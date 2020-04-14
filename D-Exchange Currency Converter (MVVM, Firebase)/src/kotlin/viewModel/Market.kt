package dev.daryl.d_exchange.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.model.Exchange
import dev.daryl.d_exchange.model.Preferences
import dev.daryl.d_exchange.utils.Client
import dev.daryl.d_exchange.view.ConHelp
import isConnectedToNetwork
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Market( private val mDate: MutableLiveData<String> = MutableLiveData(),
              private val mDateColor: MutableLiveData<Int> = MutableLiveData(R.color.orange),
              val val1: MutableLiveData<String> = MutableLiveData("1")
) : ViewModel(){
    init {
        getTodaysDate()
        initializeFlagViews()
        exchange = Exchange("null", "No rates available", currentDate, HashMap())
        readInternalStorage("rates")
        getRates{run{showDate()
            saveInternalStorage("rates")}}
    }

    companion object: Client(){
        lateinit var exchange: Exchange
        lateinit var pref: Preferences
        // key: code - value: country
        val allCodes: MutableMap<String, String> = mutableMapOf()
        // key: code - value: ID of drawable flag image
        val flags: MutableMap<String, Int> = mutableMapOf()
        lateinit var currentDate: String
    }

    private var decimalFormat = DecimalFormat("###,###,###.00")
    var starting: Boolean = true
    var justCurrencies = false
    // key: code - value: exchange
    lateinit var conversionList: MutableMap<String, String>
    lateinit var baseOneConversionList: MutableMap<String, String>
    lateinit var fromOneConversionList: MutableMap<String, String>
    val date: LiveData<String> get() = mDate
    val dateColor: LiveData<Int> get() = mDateColor
    val mValueViewsStyling: MutableLiveData<Int> = MutableLiveData(R.drawable.value_styling_light)
    private val mCurrency1 = MutableLiveData<String>()
    val currency1: LiveData<String> get() = mCurrency1
    private val mCurrency2 = MutableLiveData<String>()
    val currency2: LiveData<String> get() = mCurrency2
    private val mFromSymbol = MutableLiveData<String>()
    val fromSymbol: LiveData<String> get() = mFromSymbol
    private val mToSymbol = MutableLiveData<String>()
    val toSymbol: LiveData<String> get() = mToSymbol
    private val mVal2 = MutableLiveData<String>()
    val val2: LiveData<String> get() = mVal2
    private var transparentIndentifier = 0
    val mFlag1 = MutableLiveData(transparentIndentifier)
    val mFlag2 = MutableLiveData(transparentIndentifier)

    fun getRates(callback: (result: Boolean) -> Unit) {
        if (ConHelp.conGet?.invoke()!!.isConnectedToNetwork()) {
            //Database ensures a single api call per day
            Market.getDataBaseData {
                run {
                    //If data is outdated, make api call
                    if (exchange.currentDate != currentDate || exchange.base == "null") {
                        Market.apiCall {
                            run {
                                Market.setDataBaseData(exchange)
                                callback.invoke(true)
                            }
                        }
                    }else{
                        callback.invoke(true)
                    }
                }
            }
        }else{
            callback.invoke(false)}
    }

    private fun initializeFlagViews(){ //Or bindings will fail on construction as flag mapping resources cannot be generated till after
        val con = ConHelp.conGet?.invoke()
        transparentIndentifier = con!!.resources.getIdentifier(
            "transparent_flag", "drawable", con.packageName)
    }

    private fun fillFlagMap(){
        val con = ConHelp.conGet?.invoke()
        var lowerCaseCode = "_"
        for(code in allCodes){
            for(c in code.key){
                lowerCaseCode += c.toLowerCase()
            }
            flags[code.key] = con!!.resources.getIdentifier(lowerCaseCode, "drawable", con.packageName)
            lowerCaseCode = "_"
        }
    }

    fun doConversion(from: Int, to: Int){
        pref.fromCurrency = pref.prefCodes.keys.elementAt(from)
        pref.toCurrency = pref.prefCodes.keys.elementAt(to)
        setViews()
        if(!val1.value.isNullOrEmpty() && exchange.base != "null") {
            val value = calculate(pref.fromCurrency, pref.toCurrency)
            mVal2.value = value
        }
    }

    private fun setViews(){
        mCurrency1.value = pref.prefCodes[pref.fromCurrency]
        mCurrency2.value = pref.prefCodes[pref.toCurrency]
        mFromSymbol.value = Currency.getInstance(pref.fromCurrency).symbol.toString()
        mToSymbol.value = Currency.getInstance(pref.toCurrency).symbol.toString()
        mFlag1.value = flags[pref.fromCurrency]
        mFlag2.value = flags[pref.toCurrency]
    }

    fun getConversionList(){
        conversionList = mutableMapOf()
        baseOneConversionList = mutableMapOf()
        fromOneConversionList = mutableMapOf()
        for(c in pref.prefCodes){
            if(c.key != pref.fromCurrency)
                conversionList[c.key] = calculate(pref.fromCurrency, c.key)
                baseOneConversionList[c.key] = calculate(pref.fromCurrency, c.key, 1)
                fromOneConversionList[c.key] = calculate(c.key, pref.fromCurrency, 1)
        }
    }

    private fun calculate(from: String, to: String, base:Int=0): String {
        var amount = BigDecimal(val1.value!!)
        when(base){
            1 -> amount = BigDecimal(1)
        }
        if (from != exchange.base) {
            amount = amount.divide( BigDecimal(exchange.rates[from]!!), MathContext.DECIMAL128)
        }
        if (to != exchange.base) {
            amount = amount.multiply(BigDecimal(exchange.rates[to]!!))
        }
        var value = decimalFormat.format(amount)
        if(value.startsWith(".")){
            value = "0$value"
        }
        return value
    }

    // Followed by flag map fill
    fun passInPickerValues(codes: Array<String>, currencies: Array<String>){
        for(c in codes){
            allCodes[c] = currencies[codes.indexOf(c)]
        }
        if(!readInternalStorage("pref")){
            pref = Preferences("EUR", "USD", mutableMapOf(),
                darkMode = false,
                rate = true,
                suggestRate = 0
            )
            var i = 0
            while(i < 6) {
                var key = allCodes.keys.elementAt(i)
                pref.prefCodes[key] = allCodes.getValue(key)
                i++
            }
        }
        fillFlagMap()
    }

    fun setPref(currencyCode: String, callback: (result: Boolean)->Unit) {
        val currency = currencyCode.substring(0, currencyCode.length - 3)
        val code = currencyCode.substring(currency.length)

        if (pref.prefCodes.containsKey(code)) {
            addRemoveCurrency(false, currency, code)
        } else if(!pref.prefCodes.containsKey(code)) {
            addRemoveCurrency(true, currency, code)
        }
        pref.defaultCurrency()
        setViews()
        saveInternalStorage("pref")
        callback.invoke(true)
    }

    private fun addRemoveCurrency(add: Boolean, currency: String, code: String){
        if(add){
            pref.prefCodes[code] = currency
        }else{
            pref.prefCodes.remove(code)
        }
    }

     fun saveInternalStorage(fileName: String){
         val con = ConHelp.conGet?.invoke()
         val fos: FileOutputStream
         try{
            fos = con!!.openFileOutput(fileName, Context.MODE_PRIVATE)
            val ob = ObjectOutputStream(fos)
            when(fileName){
                "rates" -> ob.writeObject(exchange)
                "pref" ->  ob.writeObject(pref)
            }
            ob.close()
            fos.close()
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun readInternalStorage(fileName: String): Boolean{
        val con = ConHelp.conGet?.invoke()
        var fis: FileInputStream?
        try {
            fis = con?.openFileInput(fileName)
            val ois = ObjectInputStream(fis)
            when(fileName){
                "rates" -> {
                    val ex = ois.readObject() as Exchange
                    exchange.setExchange(ex.base, ex.date, ex.currentDate, ex.rates)
                }
                "pref"-> {
                    val p = ois.readObject() as Preferences
                    pref = Preferences(p.fromCurrency,p.toCurrency,p.prefCodes,p.darkMode,p.rate,p.suggestRate)
                }
            }
            return true
        }catch(e: Exception){
            e.printStackTrace()
            return false
        }
    }

    private fun getTodaysDate(){
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val c = Calendar.getInstance()
        currentDate = sdf.format(c.time)
    }

    private fun showDate() {
        var display = formatDate(currentDate)
        if(exchange.base != "null" && alterDate(exchange.date) == display){
            mDateColor.value = R.color.green
        }else{
            display = formatDate(exchange.date)
        }
        mDate.value = display
    }

    private fun formatDate(d: String): String{
        if(d.startsWith("N")){
            mDateColor.value = R.color.red
            return d
        }
        return d.substring(8,10) + d.substring(4,8)+d.substring(0,4)
    }

    private fun alterDate(date: String): String{
        var day = date.substring(8,10).toInt()
        val month = date.substring(5,7).toInt()
        val nDay : String
        nDay = if(month == 2){
            if(day == 29){
                setDay(day, 29)
            }else
                setDay(day, 28)
        }else if(month == 4 || month == 6 || month == 9 || month == 11 ){
            setDay(day, 30)
        }else{
            setDay(day, 31)
        }
        return nDay + date.substring(4, 8) + date.substring(0, 4)
    }

    private fun setDay(day: Int, lastDay: Int): String{
        return when {
            day == lastDay -> {
                "01"
            }
            day < 9 -> {
                val nDay = day+1
                return "0$nDay"
            }
            else -> {
                return (1+day).toString()
            }
        }
    }
}
