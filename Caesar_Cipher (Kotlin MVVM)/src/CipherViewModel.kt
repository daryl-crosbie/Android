package dev.daryl.caesar_cipher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CipherViewModel : ViewModel() {

    val msg = MutableLiveData<String>()
    val key = MutableLiveData<String>()

    private val _res = MutableLiveData<String>()
    val res: LiveData<String> get() = _res

    private var temp = ""
    private lateinit var m: String
    private var k = 0
    val noChange = MutableLiveData<Boolean>()

    fun encrypt(){
        if(checkInput()) {
            setValues()
            for(i in this.m.indices){
                if(m[i] == ' '){
                    temp += m[i]
                }else {
                    this.temp += if (m[i].isLowerCase()) {
                        ((((m[i].toInt() - 97) + k) % 26) + 97).toChar()
                    } else {
                        ((((m[i].toInt() - 65) + k) % 26) + 65).toChar()
                    }
                }
            }
            _res.value = temp
        }
    }
    fun decrypt(){
        if(checkInput()) {
            setValues()
            for (i in this.m.indices) {
                if(m[i] == ' '){
                    temp += m[i]
                }else {
                    if (m[i].isLowerCase()) {
                        var a = m[i].toInt() - (k % 26)
                        if (a < 97) temp += (a + 122 - 97 + 1).toChar() else {
                            temp += a.toChar()
                        }
                    } else {
                        var a = m[i].toInt() - (k % 26)
                        if (a < 65) temp += (a + 90 - 65 + 1).toChar() else {
                            temp += a.toChar()
                        }
                    }
                }
                _res.value = temp
            }
        }
    }
    private fun setValues(){
        this.k = key.value!!.toInt()
        this.m = msg.value!!
        noChange.value = k % 26 == 0
    }
    private fun checkInput(): Boolean{
        temp = ""
        _res.value = ""
        return(msg.value != null && key.value != null)
    }
}
