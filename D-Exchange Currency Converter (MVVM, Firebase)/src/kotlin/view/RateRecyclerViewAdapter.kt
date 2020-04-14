package dev.daryl.d_exchange.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.viewModel.Market
import kotlinx.android.synthetic.main.fragment_exchange.view.*
import java.util.*

class RateRecyclerViewAdapter(
    private val base: String,
    private var baseValue: String,
    private val conversionList: MutableMap<String, String>,
    private val base1List: MutableMap<String,String>,
    private val exchange1List: MutableMap<String, String>
) : RecyclerView.Adapter<RateRecyclerViewAdapter.ViewHolder>() {

    init{
        if(!baseValue.contains(".")){
            baseValue += ConHelp.conGet?.invoke()!!.getString(R.string.two_decimal_places)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_exchange, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exchangeTo = conversionList.keys.elementAt(position)
        val exchangeFrom1 = base1List[exchangeTo]
        val rate = conversionList[exchangeTo]
        val fromOne = exchange1List[exchangeTo]
        val fromSymbol = Currency.getInstance(base).symbol.toString()
        val toSymbol = Currency.getInstance(exchangeTo).symbol.toString()
        val inputFrom = "$baseValue $base"
        holder.fromCodeView.text = inputFrom
        val inputTo = "$rate $exchangeTo"
        holder.toCodeView.text = inputTo
        val base1 = "$fromSymbol 1.00"
        holder.withBase1.text =  base1
        val exchange1 = "$toSymbol $exchangeFrom1"
        holder.withExchange1.text = exchange1
        val baseCurrencyExchanged = "$fromSymbol $fromOne"
        holder.baseExchange.text = baseCurrencyExchanged
        val exchangeWithBase1 = "$toSymbol 1.00"
        holder.exchangeBase1.text = exchangeWithBase1
    }

    override fun getItemCount(): Int = conversionList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val fromCodeView: TextView = mView.from_code
        val toCodeView: TextView = mView.to_code
        val withBase1: TextView = mView.with_base_one
        val withExchange1: TextView = mView.exchange_one
        val baseExchange: TextView = mView.base_exchange
        val exchangeBase1: TextView = mView.exchange_base_one
        private val exchangeColor: LinearLayout = mView.exchange_back_color
        private val cardBack: LinearLayout = mView.card_back
        init{
            if(Market.pref.darkMode){
                cardBack.setBackgroundResource(R.color.cardview_dark_background)
                exchangeColor.setBackgroundResource(R.color.green)
                base
            }else{
                cardBack.setBackgroundResource(R.color.cardview_light_background)
                exchangeColor.setBackgroundResource(R.color.green)
            }
        }
    }
}
