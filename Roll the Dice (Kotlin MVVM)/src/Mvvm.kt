package dev.daryl.roll_the_dice


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class Mvvm : ViewModel(){

    val yourGuess = MutableLiveData<String>()

    private val _diceResult = MutableLiveData<String>(" ")
    val diceResult: LiveData<String> get() = _diceResult

    private val _diceHistory = MutableLiveData<String>(" ")
    val diceHistory: LiveData<String> get()=_diceHistory

    private val _guessResult = MutableLiveData<String>(" ")
    val guessResult: LiveData<String> get()= _guessResult

    private val _diceOne = MutableLiveData<Int>(R.drawable.bluedie1)
    val diceOne: LiveData<Int> get()= _diceOne
    private val _diceTwo = MutableLiveData<Int>(R.drawable.bluedie1)
    val diceTwo: LiveData<Int> get()= _diceTwo

    private val _shaking = MutableLiveData<Boolean>(false)
    val shaking: LiveData<Boolean> get()=_shaking

    private var randomNumber = 0
    private var randomNumber2 = 0
    private var diceTotal = 0

    fun rollDice(){
        var guess = 0
        if(yourGuess.value != null )
            guess = yourGuess.value!!.toInt()

        randomNumber = Random.nextInt(1,6)
        randomNumber2 = Random.nextInt(1,6)
        diceTotal = randomNumber + randomNumber2

        if(validateGuess(guess)){
            _shaking.value = true
            Handler().postDelayed({
            _diceOne.value = setDice(randomNumber)
            _diceTwo.value = setDice(randomNumber2)

            _diceResult.value = diceTotal.toString()
            _diceHistory.value = diceTotal.toString() +", "+ _diceHistory.value
            if(guess == diceTotal){
                _guessResult.value = "Correct, well done"
            }else{
                _guessResult.value = "Better luck next time"
            }
                _shaking.value = false
            },1850)
        }else{
            _guessResult.value = "Invalid Guess"
        }

    }

    private fun validateGuess(g: Int): Boolean{
        return (g in 2..12)
    }

    private fun setDice(num: Int): Int{
            when(num){
                1 -> return R.drawable.bluedie1
                2 -> return R.drawable.bluedie2
                3 -> return R.drawable.bluedie3
                4 -> return R.drawable.bluedie4
                5 -> return R.drawable.bluedie5
                6 -> return R.drawable.bluedie6
                else -> return R.drawable.bluedie1
            }
    }
}