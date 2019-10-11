package app.game.d_tap

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import kotlin.random.Random


class MainActivity : AppCompatActivity() , WelcomeFrag.OnFragmentInteractionListener {

    private val welcome: Fragment = WelcomeFrag()
    private var btns : Array<Button?> = arrayOfNulls(18)
    private lateinit var mainBack : ConstraintLayout
    private lateinit var gameScoreTxtv: TextView
    private lateinit var timeLeftTxtv: TextView
    private lateinit var highScorev: TextView
    private lateinit var addScoreTxtv: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val initialTime: Long = 30000
    private val countDownInterval: Long = 1000
    private var timeLeftOnTimer: Long = 10000
    private var highScoreOriginal = 0
    private var highScoreEasy = 0
    private var currentButton = 30
    private var pick = 7
    private var score = 0
    private var gameStarted = false
    private var finalTen = false
    private var firstTime = true
    private var bonus = false
    private var mode = false
    private var finishedButtons = mutableSetOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFrag()
        loadData()
        gameScoreTxtv = game_score_txtv
        timeLeftTxtv = time_left_txtv
        timeLeftTxtv.text = getString(R.string.time_left, "")
        highScorev = highScore
        addScoreTxtv = addScore
        mainBack = main
        assignBtnViewArray()
    }

    private fun showFrag(){
        var man: FragmentTransaction = supportFragmentManager.beginTransaction()
        man.add(R.id.Frag_container, welcome)
        man.commit()
        Handler().postDelayed({
            man = supportFragmentManager.beginTransaction()
            man.remove(welcome)
            man.commit()
            resetGame()
        }, 2500)
    }

    private fun startGame(){
        gameStarted = true
        btns[pick]?.text = null
        countDownTimer.start()
    }

    private fun resetGame(){
        if(firstTime){
            welcomeInfo()
        }
        btns[pick]?.visibility = VISIBLE
        addScoreTxtv.text = getString(R.string.blank)
        if(mode){
            highScorev.text = getString(R.string.high_score, highScoreEasy.toString())
        }else{
            highScorev.text = getString(R.string.high_score, highScoreOriginal.toString())
        }
        score = 0
        gameScoreTxtv.text = getString(R.string.your_score, score.toString())
        val initialTimeLeft = initialTime / 1000
        timeLeftTxtv.text = getString(R.string.time_left, initialTimeLeft.toString())
        countDownTimer = object: CountDownTimer(initialTime, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTxtv.text = getString(R.string.time_left, timeLeft.toString())
                if(timeLeft < 11){
                    val blinkTime = AnimationUtils.loadAnimation(timeLeftTxtv.context, R.anim.blink)
                    timeLeftTxtv.startAnimation(blinkTime)
                    finalTen = true
                }
            }
            override fun onFinish() {
                if(!bonus) {
                    for (b in btns) {
                        if (b?.contentDescription != getString(R.string.fin)) {
                            b?.contentDescription = getString(R.string.fin)
                            val dropAnim = AnimationUtils.loadAnimation(b?.context, R.anim.drop)
                            b?.startAnimation(dropAnim)
                        }
                    }
                    Handler().postDelayed({ endGame() }, 1600)
                }
            }
        }
        finishedButtons = mutableSetOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)
        pick = 7
        gameStarted = false
        finalTen = false
        bonus = false
        showButtons()
    }

    private fun showButtons(){
        for(b in btns){
            b?.contentDescription = getString(R.string.down)
            if(mode){
                mainBack.background = ContextCompat.getDrawable(applicationContext, R.drawable.wood)
                b?.setBackgroundResource(R.drawable.down2)
            }else{
                mainBack.background = ContextCompat.getDrawable(applicationContext, R.drawable.back)
                b?.setBackgroundResource(R.drawable.down)
            }
            val emerge = AnimationUtils.loadAnimation(b?.context, R.anim.emerge)
            b?.startAnimation(emerge)
        }
        btns[pick]?.text = getString(R.string.start)
        btns[pick]?.contentDescription = getString(R.string.up)
        if(mode){
            btns[pick]?.setBackgroundResource(R.drawable.up2)
        }else {
            btns[pick]?.setBackgroundResource(R.drawable.up)
        }
    }

    private fun buttonSelect(){
        if(!gameStarted){
            startGame()
        }
        if(mode){
            setupButton(R.drawable.down2, R.drawable.up2)
        }else{
            setupButton(R.drawable.down, R.drawable.up)
        }
    }

    private fun finalButtonSet(add : Int){
        if(finishedButtons.isEmpty()){
            countDownTimer.cancel()
            addedScore(10)
            score += 5
            bonus = true
            gameScoreTxtv.text = getString(R.string.your_score, score.toString())
            val finishAnim = AnimationUtils.loadAnimation(button8.context, R.anim.finish)
            btns[pick]?.startAnimation(finishAnim)
            Handler().postDelayed({
                endGame()
            },1200)
        }else {
            addedScore(add)
            pick = finishedButtons.random()
        }
    }

    private fun addedScore(amm : Int){
        val blinkScore = AnimationUtils.loadAnimation(addScoreTxtv.context, R.anim.blink)
        addScoreTxtv.text = when(amm){
            1 -> "+1"
            2 -> "+2"
            3 -> "+3"
            4 -> "+4"
            5 -> "+5"
            else -> "+10"
        }
        addScoreTxtv.startAnimation(blinkScore)
        Handler().postDelayed({
            if(amm != 3){addScoreTxtv.text = getString(R.string.blank)}
        },700)
    }

    private fun setupButton(down: Int, up : Int){
        currentButton = pick
        if(finalTen){
            val btnLeft = finishedButtons.size
            var add : Int
            add = when{
                btnLeft < 6 -> 5
                btnLeft < 9 -> 4
                btnLeft < 12 -> 3
                btnLeft < 15 -> 2
                else -> 1
            }
            score += add
            finishedButtons.remove(currentButton)
            finalButtonSet(add)
            btns[currentButton]?.contentDescription = getString(R.string.fin)
        }else{
            addedScore(1)
            score += 1
            pick = Random.nextInt(0, 17)
            btns[currentButton]?.setBackgroundResource(down)
            btns[currentButton]?.contentDescription = getString(R.string.down)
        }
        gameScoreTxtv.text = getString(R.string.your_score, score.toString())
        btns[pick]?.setBackgroundResource(up)
        btns[pick]?.contentDescription = getString(R.string.up)

    }

    private fun changeTheme(theme : Int){
        countDownTimer.cancel()
        mainBack = main
        when(theme){
            0 -> {mainBack.background = ContextCompat.getDrawable(applicationContext,R.drawable.back)
                changeButtons(R.drawable.down)
                mode = false}
            1 -> {mainBack.background = ContextCompat.getDrawable(applicationContext,R.drawable.wood)
                changeButtons(R.drawable.down2)
                mode = true}
        }
        resetGame()
    }

    private fun changeButtons(img: Int){
        for(b in btns){
            b?.setBackgroundResource(img)
        }
    }

    private fun endGame() {
        btns[pick]?.visibility = INVISIBLE
        if (mode) {
            showResult(highScoreEasy)
            when{highScoreEasy < score -> highScoreEasy = score}
        } else {
            showResult(highScoreOriginal)
            when{highScoreOriginal < score -> highScoreOriginal = score}
        }
        saveData()
    }

    private fun showResult(highScore : Int){
        val dialogTitle: String
        var dialogMessage = getString(R.string.your_score, score.toString())
        var calc = highScore - score
        if(score > highScore){
            dialogTitle = getString(R.string.best)
            dialogMessage = getString(R.string.new_score, score.toString())
        }else if(score == highScore){
            dialogTitle = getString(R.string.nice)
        }else if(calc in 1..5){
            dialogTitle = getString(R.string.not_bad)
        }else if(calc in 1..15){
            dialogTitle = getString(R.string.faster)
        }else{
            dialogTitle = getString(R.string.slow)
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Again") { dialog, _-> dialog.dismiss()
                resetGame()}
            .setNeutralButton("Enough"){ _, _-> finish()}
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.normalMode -> changeTheme(0)
            R.id.easyMode -> changeTheme(1)
            R.id.rate -> rateApp()
            R.id.share -> shareApp()
            R.id.action_about -> showInfo()
        }
        return true
    }

    private fun rateApp(){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=app.game.d_tap")))
    }

    private fun shareApp() = try {
        val shareIntent = Intent(ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "D-Tap")
        var msg = "My best score is $highScoreOriginal.\nSee if you can beat it.\n\n"
        var link = msg + "https://play.google.com/store/apps/details?id=app.game.d_tap"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }catch(e: Exception){
        Toast.makeText(this, "Error sharing", Toast.LENGTH_SHORT).show()
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.app_name)
        val dialogMessage = getString(R.string.about_message)
        dialogBuilder(dialogTitle, dialogMessage)
    }

    private fun welcomeInfo(){
        val dialogTitle = getString(R.string.welcomeTitle)
        val dialogMessage = getString(R.string.welcomeMsg)
        dialogBuilder(dialogTitle, dialogMessage)
        firstTime = false
    }

    private fun dialogBuilder(title: String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.create().show()
    }

    private fun saveData(){
        try{
            val sharedPref = getPreferences(Context.MODE_PRIVATE)?:return
            with (sharedPref.edit()) {
                putInt(getString(R.string.highestOriginal), highScoreOriginal)
                putInt(getString(R.string.highestEasy), highScoreEasy)
                apply()
            }
        }catch (e : Exception){
            Toast.makeText(this, "Error Saving", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadData(){
        try{
            val sharedPref = getPreferences(Context.MODE_PRIVATE)?:return
            highScoreOriginal = sharedPref.getInt(getString(R.string.highestOriginal),0)
            highScoreEasy = sharedPref.getInt(getString(R.string.highestEasy), 0)
        }catch(e : Exception){
            Toast.makeText(this, "Error loading Score", Toast.LENGTH_SHORT).show()
        }
        if(highScoreEasy != 0 || highScoreOriginal != 0){
            firstTime = false
        }
    }

    private fun assignBtnViewArray(){
        btns[0] = button1
        btns[1] = button2
        btns[2] = button3
        btns[3] = button4
        btns[4] = button5
        btns[5] = button6
        btns[6] = button7
        btns[7] = button8
        btns[8] = button9
        btns[9] = button10
        btns[10] = button11
        btns[11] = button12
        btns[12] = button13
        btns[13] = button14
        btns[14] = button15
        btns[15] = button16
        btns[16] = button17
        btns[17] = button18
        for(i in btns){
            i?.setOnClickListener{ view ->
                val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
                val dropAnim = AnimationUtils.loadAnimation(this, R.anim.drop)
                if(i.contentDescription == getString(R.string.up)) {
                    if(finalTen){
                        view.startAnimation(dropAnim)
                    }else{
                        view.startAnimation(bounceAnim)
                    }
                    buttonSelect()
                }
            }
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
        countDownTimer.cancel()
        finish()
    }
    override fun onFragmentInteraction(uri: Uri) {}
}



