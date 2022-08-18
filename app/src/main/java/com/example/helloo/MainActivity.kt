package com.example.helloo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.example.helloo.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManager? = null

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private lateinit var countDownTimer: CountDownTimer

    private var textToSpeech : TextToSpeech? = null

    private var channelId = "001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelId, "countdown", "Notif when countdown end.")

        binding.btnText.setOnClickListener {
            countDownTimer.start()
            binding.tv1.text = binding.edtText.text.toString()
        }

        countDownTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                displayNotification()
                speech()
            }

        }


    }

    private fun speech(){
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it==TextToSpeech.SUCCESS){

                textToSpeech?.language = Locale.KOREA
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.speak(binding.edtText.text.toString(), TextToSpeech.QUEUE_ADD, null)
            }
        })
    }

    private fun displayNotification() {
        val snoozeIntent = Intent(this, MainActivity::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("EXTRA_NOTIFICATION_ID", 0)
        }

        val snoozePendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)
        var action = NotificationCompat.Action.Builder(R.drawable.heart, "Snooze", snoozePendingIntent)
                .build()

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.heart)
            .setContentTitle("Hello")
            .setContentText("Hello")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(binding.tv1.text)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(snoozePendingIntent)
            .setAutoCancel(true)
            .addAction(action)

        notificationManager?.notify(1, builder.build())

    }

    private fun createNotificationChannel(id: String, name: String, descriptionChannel: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = descriptionChannel
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}