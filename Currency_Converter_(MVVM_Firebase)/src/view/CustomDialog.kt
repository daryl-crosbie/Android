package dev.daryl.d_exchange.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import dev.daryl.d_exchange.R
import dev.daryl.d_exchange.viewModel.Market
import kotlinx.android.synthetic.main.custom_dialog.*
import rateNow

class CustomDialog : DialogFragment() {
    private lateinit var market: Market
    private lateinit var sure: Button
    private lateinit var later: Button

    companion object {
        fun newInstance(): CustomDialog {
            return CustomDialog()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.custom_dialog, container)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        market = ViewModelProviders.of(requireActivity()).get(Market::class.java)
        sure = sure_btn
        later = later_btn
        sure.setOnClickListener {
            activity?.rateNow()
            Market.pref.rate = false
        }
        later.setOnClickListener {
            market.saveInternalStorage("pref")
            activity?.finish()
        }
    }
}