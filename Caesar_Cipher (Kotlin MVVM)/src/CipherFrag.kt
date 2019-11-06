package dev.daryl.caesar_cipher

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import dev.daryl.caesar_cipher.databinding.CipherFragmentBinding

class CipherFrag : Fragment() {

    companion object {
        fun newInstance() = CipherFrag()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: CipherFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.cipher_fragment, container, false
        )
        val viewModel = ViewModelProviders.of(this).get(CipherViewModel::class.java)

        binding.cipher = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }
}
