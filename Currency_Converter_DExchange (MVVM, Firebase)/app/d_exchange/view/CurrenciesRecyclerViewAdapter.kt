package dev.daryl.d_exchange.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.view.CurrenciesFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_currencies.view.*

class CurrenciesRecyclerViewAdapter(
    private val allCodes: MutableMap<String, String>,
    private var prefCodes: MutableMap<String, String>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<CurrenciesRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var count: Int = 0
    init{
        for(c in allCodes){
            if(prefCodes.containsKey(c.key))
                count++
        }
    }
    init {
        mOnClickListener = View.OnClickListener { v ->
            val tag = v.tag as String
            if(count > 2) {
                if (!prefCodes.containsKey(tag)) {
                    setSelected(v.item_code,true, tag)
                } else {
                   setSelected(v.item_code,false, tag)
                }
            }else if(count == 2 && !prefCodes.containsKey(tag)){
                setSelected(v.item_code, true, tag)
            }
        }
    }

    private fun setSelected(v: View, add: Boolean, tag: String){
        if(add) {
             v.background = ContextCompat.getDrawable(ConHelp.conGet!!.invoke(), R.drawable.selected)
            count++
        }else{
            v.background = ContextCompat.getDrawable(ConHelp.conGet!!.invoke(), R.drawable.unselected)
            count--
        }
        mListener?.onListFragmentInteraction(allCodes[tag]+tag)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val code = allCodes.keys.elementAt(position)
        val currency = allCodes[code]

        holder.currencyView.text = currency
        holder.codeView.text = code

        if(prefCodes.contains(code)) {
            holder.codeView.background = ContextCompat.getDrawable(ConHelp.conGet!!.invoke(), R.drawable.selected)
        }else{
            holder.codeView.background = ContextCompat.getDrawable(ConHelp.conGet!!.invoke(), R.drawable.unselected)
        }
        with(holder.mView) {
            tag = code
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_currencies, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = allCodes.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val currencyView: TextView = mView.item_currency
        val codeView: TextView = mView.item_code

        override fun toString(): String {
            return super.toString() + " '" + codeView.text + "'"
        }
    }
}
