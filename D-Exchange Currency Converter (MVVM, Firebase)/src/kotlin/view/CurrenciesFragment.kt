package dev.daryl.d_exchange.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.viewModel.Market
import kotlinx.android.synthetic.main.fragment_list.*

class CurrenciesFragment : Fragment() {
    private lateinit var market: Market
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        market = ViewModelProvider(requireActivity()).get(Market::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = if(market.justCurrencies) {
                    CurrenciesRecyclerViewAdapter(
                        Market.allCodes,
                        Market.flags,
                        Market.pref.prefCodes,
                        listener
                    )
                }else{
                    RateRecyclerViewAdapter(
                        Market.pref.fromCurrency,
                        market.val1.value.toString(),
                        market.conversionList,
                        market.baseOneConversionList,
                        market.fromOneConversionList
                    )
                }
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if(Market.pref.darkMode){
            list_rates.background = ResourcesCompat.getDrawable(resources, R.color.background_dark, null)
        }else{
            list_rates.background = ResourcesCompat.getDrawable(resources, R.color.background_light, null)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: String){
        }
    }
}
