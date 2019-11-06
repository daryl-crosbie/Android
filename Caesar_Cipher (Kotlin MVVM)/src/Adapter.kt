package dev.daryl.caesar_cipher

import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter

@BindingAdapter("toast")
fun View.v(noChange: Boolean){
    if(noChange) {
        Toast.makeText(context, "Full loop of the alphabet", Toast.LENGTH_LONG).show()
    }
}