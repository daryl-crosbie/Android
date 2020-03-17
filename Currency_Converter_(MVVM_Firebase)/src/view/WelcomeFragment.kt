package dev.daryl.d_exchange.view

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView

import dev.daryl.d_exchange.R

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_welcome, container, false)
        val circle = v.findViewById<ImageView>(R.id.circle_view)
        Handler().postDelayed({
            val rotate = AnimationUtils.loadAnimation(activity, R.anim.rotate_logo)
            rotate.fillAfter = true
            circle.startAnimation(rotate)
        },500)
        return v
    }
}
