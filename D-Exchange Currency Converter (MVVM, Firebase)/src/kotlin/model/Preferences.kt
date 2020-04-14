package dev.daryl.d_exchange.model

import java.io.Serializable

data class Preferences (
    var fromCurrency: String,
    var toCurrency: String,
    // key : code - value : currency
    var prefCodes: MutableMap<String, String>,
    var darkMode: Boolean,
    var rate: Boolean,
    var suggestRate: Int
): Serializable{

    fun defaultCurrency(){
        fromCurrency = prefCodes.keys.elementAt(0)
        toCurrency = prefCodes.keys.elementAt(1)
    }
    fun rated(): Boolean{
        suggestRate +=1
        return rate && suggestRate % 2 != 0
    }
}


