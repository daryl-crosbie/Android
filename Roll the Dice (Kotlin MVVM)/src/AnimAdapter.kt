package dev.daryl.roll_the_dice

import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData


@BindingAdapter("rotate")
fun ImageView.spin(spinning: LiveData<Boolean>){
    if(spinning.value == true) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.spin)
        this.startAnimation(anim)
    }
}