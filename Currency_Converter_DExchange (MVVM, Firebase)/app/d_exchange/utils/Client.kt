package dev.daryl.d_exchange.utils

import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import dev.daryl.d_exchange.model.Exchange
import dev.daryl.d_exchange.view.ConHelp
import dev.daryl.d_exchange.viewModel.Market

abstract class Client{

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun setDataBaseData(exchange: Exchange){
        database.child("exchange_rates").setValue(exchange)
    }
    fun getDataBaseData(callBack: (result: Boolean)-> Unit){
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(ds: DatabaseError) {}
            override fun onDataChange(ds: DataSnapshot) = if(ds.exists()) {
                val data = ds.child("exchange_rates").getValue(Exchange::class.java)
                Market.exchange.setExchange(data!!.base, data.date ,data.rates)
                callBack.invoke(true)
            }else{
                callBack.invoke(false)
            }
        })
    }
    fun apiCall(callBack: (result: Boolean) -> Unit) {
        val queue = Volley.newRequestQueue(ConHelp.conGet?.invoke())
        val url = "https://api.exchangeratesapi.io/latest"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Market.exchange.renderCurrencies(response)
                callBack.invoke(true)
            },
            Response.ErrorListener { Toast.makeText(ConHelp.conGet?.invoke(), "Error Retrieving data", Toast.LENGTH_SHORT).show()
                                    callBack.invoke(false)})
        queue.add(jsonRequest)
    }
}