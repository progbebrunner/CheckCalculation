package com.example.whatcheckcalculation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.whatcheckcalculation.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, Timer::class.java)
        registerReceiver(updateTime, IntentFilter(Timer.timer_updated))
    }

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var all_time = 0.0
    private var avg_sec = 0.0
    private var max_sec = 0.0
    private var min_sec = 99999.0
    private var time = 0.0

    var correctAnsw = 0
    var wrongAnsw = 0
    var allExamples = 0
    var percentage = 0.0
    var choice = true

    fun checkAnsw(operand:String, operatorOne:Int, operatorTwo:Int, playerResult: Int):Boolean
    {
        var result = 0
        when (operand){
            "+" -> result = operatorOne+operatorTwo
            "-" -> result = operatorOne-operatorTwo
            "*" -> result = operatorOne*operatorTwo
            "/" -> result = operatorOne/operatorTwo
        }
        return (result == playerResult)
    }

    fun onClickStartBtn(view: View)
    {
        var result = 0
        val operands = arrayOf("+","-","*","/")
        val operand = operands.random()
        var oneOperant = (10..99).random()
        var twoOperator = (10..99).random()
        if (operand == "/")
        {
            while (oneOperant%twoOperator!=0) //целочисленное
            {
                oneOperant = (10..99).random()
                twoOperator = (10..99).random()
            }
        }
        var chance = (1..2).random() //шанс правильного ответа
        if (chance == 1)
        {
            choice = true //верно
            when (operand){
                "+" -> result = oneOperant+twoOperator
                "-" -> result = oneOperant-twoOperator
                "*" -> result = oneOperant*twoOperator
                "/" -> result = oneOperant/twoOperator
            }
        }
        else
        {
            choice = false //ложь
            result = (-100..100).random()
        }
        binding.TxtNumber1.text = oneOperant.toString()
        binding.TxtNumber2.text = twoOperator.toString()
        binding.TxtSign.text = operand
        binding.TxtResult.text = result.toString()
        binding.BtFalse.isEnabled = true
        binding.BtTrue.isEnabled = true
        binding.BtRun.isEnabled = false
        binding.NumberWrongTxt.setBackgroundColor(Color.WHITE)
        binding.NumberRigthTxt.setBackgroundColor(Color.WHITE)
        startTimer()
    }

    fun onClickRightBtn(view: View)
    {
        if (choice)
        {
            correctAnsw++
            allExamples++
            binding.NumberRigthTxt.setBackgroundColor(Color.GREEN)
        }
        else
        {
            wrongAnsw++
            allExamples++
            binding.NumberWrongTxt.setBackgroundColor(Color.RED)
        }
        percentage = (correctAnsw/allExamples*100).toDouble()
        binding.NumberRigthTxt.text = correctAnsw.toString()
        binding.NumberWrongTxt.text = wrongAnsw.toString()
        binding.AllExamplesTxt.text = allExamples.toString()
        binding.TxtPercent.text = ("%.2f".format(percentage)).toString()  + "%"
        binding.BtFalse.isEnabled = false
        binding.BtTrue.isEnabled = false
        binding.BtRun.isEnabled = true
        resetTimer()
    }

    fun onClickWrongBtn(view: View)
    {
        if (!choice)
        {
            correctAnsw++
            allExamples++
            binding.NumberRigthTxt.setBackgroundColor(Color.GREEN)
        }
        else
        {
            wrongAnsw++
            allExamples++
            binding.NumberWrongTxt.setBackgroundColor(Color.RED)
        }
        percentage = (correctAnsw/allExamples*100).toDouble()
        binding.NumberRigthTxt.text = correctAnsw.toString()
        binding.NumberWrongTxt.text = wrongAnsw.toString()
        binding.AllExamplesTxt.text = allExamples.toString()
        binding.TxtPercent.text = ("%.2f".format(percentage)).toString()  + "%"
        binding.BtFalse.isEnabled = false
        binding.BtTrue.isEnabled = false
        binding.BtRun.isEnabled = true
        resetTimer()
    }

    private fun resetTimer()
    {
        stopTimer()
        all_time += time
        if (time < min_sec) {
            min_sec = time
            binding.MinSecTxt.text = time.toString()
        }
        if (time > max_sec) {
            max_sec = time
            binding.MaxSecTxt.text = time.toString()
        }
        avg_sec = all_time / allExamples
        binding.AvgSecTxt.text = avg_sec.toString()
        time = 0.0
        binding.TimerTxt.text = getTimeStringFromDouble(time)
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(Timer.timer_extra, time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(Timer.timer_extra, 0.0)
            binding.TimerTxt.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val seconds = resultInt

        return seconds.toString()
    }
}

