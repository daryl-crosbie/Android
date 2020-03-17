package dev.daryl.d_exchange.view

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.res.Resources.Theme
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.R.array.codes
import dev.daryl.d_exchange.R.array.currencies
import dev.daryl.d_exchange.databinding.ActivityMainBinding
import dev.daryl.d_exchange.viewModel.Market
import kotlinx.android.synthetic.main.activity_main.*
import rateNow
import java.lang.reflect.Field

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), CurrenciesFragment.OnListFragmentInteractionListener {
    private lateinit var market: Market
    private var welcomeFrag: Fragment = WelcomeFragment()
    private var currencyFrag: Fragment = CurrenciesFragment()
    private lateinit var trans : FragmentTransaction
    private lateinit var listAll: Button
    private lateinit var codePicker1: NumberPicker
    private lateinit var codePicker2: NumberPicker
    private var pickerMaxValue: Int = 0
    lateinit var handler : Handler
    lateinit var runnable: Runnable
    lateinit var menu: Menu

    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }
    override fun onCreate(state: Bundle?) {
        //Instantiate view model for preference companion object
        ConHelp.conGet = {this}
        market = ViewModelProviders.of(this)[Market::class.java]
        //Load preferences for theme
        val currencies = resources.getStringArray(currencies)
        val codes = resources.getStringArray(codes)
        market.passInPickerValues(codes, currencies)

        if(Market.pref.darkMode) {
            var theme: Theme = super.getTheme()
            theme.applyStyle(R.style.DarkTheme, true)
            market.mValueViewsStyling.value = R.drawable.value_styling_dark
        }else{
            market.mValueViewsStyling.value = R.drawable.value_styling_light
        }
        super.onCreate(state)
        mBinding.data = market
        mBinding.lifecycleOwner = this
        displayWelcomeScreen()
        listAll = listAll_btn
        listAll.setOnClickListener {
            if(Market.exchange.base != "null"){
                            market.getConversionList()
                                        listCurrencies(false)
            }else{
                Toast.makeText(this, "Requires exchange rates", Toast.LENGTH_SHORT).show()
            }
        }
        codePicker1 = code_picker1
        codePicker2 = code_picker2
        setNumberPickers()

        var inputValue: EditText = val1_et
        inputValue.setOnClickListener { inputValue.selectAll() }
        inputValue.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                market.doConversion(codePicker1.value, codePicker2.value)
            }
        })
    }

    private fun displayWelcomeScreen(){
        var delay = 150L
        if(market.starting) {
            trans = supportFragmentManager.beginTransaction()
            trans.add(R.id.frag_container, welcomeFrag).commit()
            delay = 3000
            market.starting = false
        }
        handler = Handler()
        runnable = Runnable { removeFragment(welcomeFrag)
            for(i in 0..2){
                menu.getItem(i).isVisible = true
                }
            }
        handler.postDelayed(runnable, delay)
    }

    private fun setNumberPickers(){
        pickerMaxValue = Market.pref.prefCodes.size-1
        codePicker1.displayedValues = null
        codePicker1.minValue = 0
        codePicker1.maxValue = pickerMaxValue
        codePicker1.value = Market.pref.prefCodes.keys.indexOf(Market.pref.fromCurrency)
        codePicker1.wrapSelectorWheel = true
        codePicker1.displayedValues = Market.pref.prefCodes.keys.toTypedArray()
        codePicker2.displayedValues = null
        codePicker2.minValue = 0
        codePicker2.maxValue = pickerMaxValue
        codePicker2.value = Market.pref.prefCodes.keys.indexOf(Market.pref.toCurrency)
        codePicker2.wrapSelectorWheel = true
        codePicker2.displayedValues = Market.pref.prefCodes.keys.toTypedArray()

        codePicker1.setOnValueChangedListener { _: NumberPicker, _: Int, current: Int ->
            market.doConversion(current, codePicker2.value)
        }
        codePicker2.setOnValueChangedListener{ _: NumberPicker, _: Int, current: Int ->
            market.doConversion(codePicker1.value, current)
        }
        if(Market.pref.darkMode){
            val color = resources.getColor(R.color.Green)
            setPickersColor(codePicker1,color)
            setPickersColor(codePicker2,color)
        }
    }

    private fun setPickersColor(picker: NumberPicker, color: Int){
        for(i in 0..picker.childCount){
            var child = picker.getChildAt(i)
            if(child is EditText){
                val selectorWheelPaintField: Field = picker.javaClass
                    .getDeclaredField("mSelectorWheelPaint")
                selectorWheelPaintField.isAccessible = true
                (selectorWheelPaintField[picker] as Paint).color = (color)
                child.setTextColor(color)
                picker.invalidate()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.refresh -> market.getRates { result -> displayRefreshResult(result) }
            R.id.currencies -> listCurrencies(true)
            R.id.dark -> changeMode(true)
            R.id.light -> changeMode(false)
            R.id.share -> shareApp()
            R.id.rate -> { rateNow(); Market.pref.rate = false}
        }
        return true
    }

    private fun displayRefreshResult(con : Boolean){
        var msg = ""
        msg = if(con){
            "Latest available rates"
        }else {
            "No network connection"
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun shareApp() {
        val shareIntent = Intent(ACTION_SEND)
        shareIntent.type = "text/plain"
    }

    private fun listCurrencies(justCurrencies: Boolean) {
        if(currencyFrag.isVisible){
            removeFragment(currencyFrag)
        }
        market.justCurrencies = justCurrencies
        trans = supportFragmentManager.beginTransaction()

        trans.add(R.id.frag_container, currencyFrag)
        trans.commit()
    }

    override fun onBackPressed() {
        when {
            currencyFrag.isVisible -> {
                removeFragment(currencyFrag)
            }
            Market.pref.rated() -> {
                trans = supportFragmentManager.beginTransaction()
                var alert = CustomDialog.newInstance()
                alert.show(trans, "custom_dialog")
            }
            else -> {
                handler.removeCallbacks(runnable)
                market.saveInternalStorage("pref")
                super.onBackPressed()
            }
        }
    }

    private fun removeFragment(f: Fragment){
        trans = supportFragmentManager.beginTransaction()
        trans.remove(f).commitNow()
    }

    private fun changeMode(dark: Boolean) {
        if(currencyFrag.isVisible){
            removeFragment(currencyFrag)
        }
        Market.pref.darkMode = dark
        market.saveInternalStorage("pref")
        recreate()
    }

    override fun onListFragmentInteraction(item: String) {
        market.setPref(item, callback = {setNumberPickers()
                                                market.doConversion(0,1)})
    }
}
object ConHelp{
    var conGet: (() -> Context)? = null
}
