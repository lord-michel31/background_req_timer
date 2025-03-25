package com.example.timertask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    private val CHANNEL_ID = "TimerChannel"
    private var elapsedTime: Long = 0L
    private var isTimerRunning = false
    private var timerJob: Job? = null


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, buildNotification("Timer started: 00:00:00"))
        }
        Log.e("ayyyyyaa","hfhf${intent?.action}")

        when (intent?.action) {
            "START" -> startTimer()
            "STOP" -> stopTimer()
        }
        return START_STICKY
    }


    private fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            timerJob = CoroutineScope(Dispatchers.Default).launch {
                while (isActive) {
                    Log.d("com.example.timertask.TimerService", "Timer started")

                    delay(1000) // Wait for 1 second
                    elapsedTime++
                    updateNotification()
                }
            }
        }
    }

    private fun stopTimer() {
        isTimerRunning = false
        timerJob?.cancel()
        stopSelf()
    }

    private fun updateNotification() {
        val timeFormatted = formatElapsedTime(elapsedTime)
        val notification = buildNotification("Elapsed Time: $timeFormatted")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
        // Send elapsed time to the MainActivity
        sendElapsedTimeBroadcast()
    }

    private fun sendElapsedTimeBroadcast() {
        val intent = Intent("TIMER_UPDATED")
        intent.putExtra("elapsedTime", elapsedTime)
        sendBroadcast(intent)
    }

    private fun formatElapsedTime(timeInSeconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds) % 60
        val seconds = timeInSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun buildNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
}
