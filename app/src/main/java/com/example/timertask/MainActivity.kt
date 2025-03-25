package com.example.timertask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.timertask.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isTimerRunning = false
    private var elapsedTime: Long = 0L // Tracks elapsed time in seconds
    private var handler = Handler(Looper.getMainLooper())
    private var timerJob: Job? = null // Job for the Coroutine Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btStartStop.setOnClickListener { view ->
            if (isTimerRunning) {
                stopTimerService()
            } else {
                startTimerService()
            }
        }
    }

    private fun startTimerService() {
        isTimerRunning = true
        binding.btStartStop.text = "Stop"
        val intent = Intent(this, TimerService::class.java)
        intent.action = "START"
        startService(intent)
        Log.e("com.example.timertask.TimerService", "Service started")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter("TIMER_UPDATED")
        registerReceiver(timerUpdateReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(timerUpdateReceiver)
    }



    private fun stopTimerService() {
        isTimerRunning = false
        binding.btStartStop.text = "Start"
        val intent = Intent(this, TimerService::class.java)
        intent.action = "STOP"
        stopService(intent)
    }
    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val elapsedTime = intent?.getLongExtra("elapsedTime", 0L) ?: 0L
            updateTimerDisplay(elapsedTime)
            Log.e("MainActivity1", "Elapsed time received: $elapsedTime")
        }
    }


    private fun updateTimerDisplay(timeInSeconds: Long) {
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds) % 60
        val seconds = timeInSeconds % 60
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

}



fun main(){
    // 1 2 3 4
// 1 2 3
// 1 2
// 1

        for(i in 4 downTo 1){
            for(j in 1 ..  i ){
                print("$j")
            }
            println()
        }
}

