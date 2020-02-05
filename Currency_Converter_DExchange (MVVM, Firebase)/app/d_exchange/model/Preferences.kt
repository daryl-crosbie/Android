package dev.daryl.d_exchange.model

import java.io.Serializable

data class Preferences (
    var fromCurrency: String,
    var toCurrency: String,
    // key : code - value : currency
    var prefCodes: MutableMap<String, String>,
    var darkMode: Boolean
): Serializable{

    fun defaultCurrency(){
        fromCurrency = prefCodes.keys.elementAt(0)
        toCurrency = prefCodes.keys.elementAt(1)
    }
}


