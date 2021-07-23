package alex.com.pomodoro.service

import alex.com.pomodoro.*
import alex.com.pomodoro.common.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null
    private var countDownTimer: CountDownTimer? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simple Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_alarm)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(SERVICE_TIME_CODE) ?: return
                logDebug("startTime: $startTime")
                commandStart(startTime)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long) {
        if (isServiceStarted) {
            return
        }
        logDebug("commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime)
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer(startTime: Long) {
        countDownTimer = object : CountDownTimer(startTime, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                logDebug("onTick, millisUntilFinished: $millisUntilFinished")

                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(millisUntilFinished.displayTime())
                )
            }

            override fun onFinish() {
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(getString(R.string.timeIsOver))
                )
                RingtoneManager
                    .getRingtone(
                        applicationContext,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ).play()
            }
        }.start()

    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        logDebug("commandStop()")
        try {
            countDownTimer?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            logDebug("moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            logDebug("moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        startForeground(NOTIFICATION_ID, getNotification("content"))
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

}