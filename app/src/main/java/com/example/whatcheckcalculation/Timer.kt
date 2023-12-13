package com.example.whatcheckcalculation

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Timer
import java.util.TimerTask

class Timer : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer_value = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        val time = intent.getDoubleExtra(timer_extra, 0.0)
        timer_value.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy()
    {
        timer_value.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask()
    {
        override fun run()
        {
            val intent = Intent(timer_updated)
            time++
            intent.putExtra(timer_extra, time)
            sendBroadcast(intent)
        }
    }

    companion object
    {
        const val timer_updated = "timerUpdated"
        const val timer_extra = "timeExtra"
    }
}