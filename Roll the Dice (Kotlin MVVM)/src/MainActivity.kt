package dev.daryl.roll_the_dice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.daryl.roll_the_dice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var vm : Mvvm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        vm = ViewModelProviders.of(this)[Mvvm::class.java]
        binding.vm = vm
        binding.lifecycleOwner = this
    }
}
