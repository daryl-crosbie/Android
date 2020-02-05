package dev.daryl.d_exchange.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
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
                private val mDateColor: MutableLiveData<Int> = MutableLiveData(R.color.Red),
                val val1: MutableLiveData<String> = MutableLiveData("1")
) : ViewModel(){
    init {
        exchange = Exchange("null", "No available rates", HashMap())
        readInternalStorage("rates")
        when {
            ConHelp.conGet?.invoke()!!.isConnectedToNetwork() -> {
                Market.getDataBaseData { run{
                    if (exchange.base == "null" || !checkDate(exchange.date)) {
                        Market.apiCall { run{
                            checkDate(exchange.date, true)
                            Market.setDataBaseData(exchange)
                        }
                        }
                    }
                }
                    saveInternalStorage("rates")
                }
            }
            exchange.base == "null" -> {
                mDate.value = exchange.date
            }
            else -> {
                mDate.value = alterDate(exchange.date)
            }
        }
    }

    private var decimalFormat = DecimalFormat("###,###,###.00")

    companion object: Client(){
        lateinit var exchange: Exchange
        lateinit var pref: Preferences
        val allCodes: MutableMap<String, String> = mutableMapOf()
    }

    var justCurrencies = false
    // key: code - value: exchange
    lateinit var conversionList: MutableMap<String, String>
    lateinit var baseOneConversionList: MutableMap<String, String>
    lateinit var fromOneConversionList: MutableMap<String, String>

    val date: LiveData<String> get() = mDate
    val dateColor: LiveData<Int> get() = mDateColor

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

    fun passInPickerValues(codes: Array<String>, currencies: Array<String>){
        for(c in codes){
            allCodes[c] = currencies[codes.indexOf(c)]
        }
        if(!readInternalStorage("pref")){
            pref = Preferences("EUR", "USD", mutableMapOf(),false)
            var i = 0
            while(i < 6) {
                var key = allCodes.keys.elementAt(i)
                pref.prefCodes[key] = allCodes.getValue(key)
                i++
            }
        }
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
            Toast.makeText(ConHelp.conGet?.invoke(), "Error saving data", Toast.LENGTH_SHORT).show()
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
                    exchange.setExchange(ex.base, ex.date, ex.rates)
                    Toast.makeText(ConHelp.conGet?.invoke(), "Loaded", Toast.LENGTH_SHORT).show()
                }
                "pref"-> {
                    val p = ois.readObject() as Preferences
                    pref = Preferences(p.fromCurrency,p.toCurrency,p.prefCodes,p.darkMode)
                }
            }
            return true
        }catch(e: Exception){
            e.printStackTrace()
            Toast.makeText(con, "Error loading saved data", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkDate(date: String, api: Boolean = false): Boolean{

        //Date is always a day behind, change it to check it with current date
        val correctDate = alterDate(date)
        mDate.value = correctDate
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val c = Calendar.getInstance()
        val current = sdf.format(c.time)
        mDateColor.value = R.color.Green
        saveInternalStorage("rates")
        if (current == correctDate || api) {
            return true
        }
        return false
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
