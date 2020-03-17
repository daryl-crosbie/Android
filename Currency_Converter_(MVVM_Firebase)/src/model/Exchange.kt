package dev.daryl.d_exchange.model

import android.widget.Toast
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.daryl.d_exchange.view.ConHelp
import dev.daryl.d_exchange.viewModel.Market
import org.json.JSONObject
import java.io.Serializable
import kotlin.collections.HashMap

@IgnoreExtraProperties
data class Exchange(
    var base: String,
    var date: String,
    var currentDate: String,
    // key: code - value: exchange rate
    var rates: HashMap<String, String>
): Serializable {
    constructor() : this("", "", "", hashMapOf())

    fun setExchange(base: String, date: String, currentDate: String, rates: HashMap<String, String>){
        this.base = base
        this.date = date
        this.currentDate = currentDate
        this.rates = rates
    }
    fun renderCurrencies(json: JSONObject){
        try{
            Market.exchange.setExchange(json.getString("base"), json.getString("date"),
                Market.currentDate,
                Gson().fromJson(
                    json.getJSONObject("rates").toString(), object : TypeToken<HashMap<String?, String?>?>() {}.type))
            Toast.makeText(ConHelp.conGet?.invoke(), "Latest Available Rates", Toast.LENGTH_SHORT).show()
        }catch(e:Exception){
            Toast.makeText(ConHelp.conGet?.invoke(), "Render Error", Toast.LENGTH_SHORT).show()
        }
    }
}


