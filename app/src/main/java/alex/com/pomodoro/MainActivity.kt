package alex.com.pomodoro

import alex.com.pomodoro.adapters.TimersAdapter
import alex.com.pomodoro.common.*
import alex.com.pomodoro.databinding.ActivityMainBinding
import alex.com.pomodoro.model.PomodoroTimer
import alex.com.pomodoro.service.ForegroundService
import android.content.Intent
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class MainActivity : AppCompatActivity(), LifecycleObserver, PomodoroTimerListener {
    private lateinit var binding: ActivityMainBinding
    private var toast: Toast? = null
    private val adapter = TimersAdapter(mutableListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding.apply {
            recyclerTimers.adapter = adapter
            addTimer.setOnClickListener {
                if (minutes.text.isNotEmpty() or seconds.text.isNotEmpty()) {
                    var minutesLong: Long = 0
                    var secondsLong: Long = 0
                    if (minutes.text.isNotEmpty()) {
                        minutesLong = minutes.text.toString().toLong() * 1000L * 60L
                    }
                    if (seconds.text.isNotEmpty()) {
                        secondsLong = seconds.text.toString().toLong() * 1000L
                    }

                    adapter.addTimer(
                        PomodoroTimer(
                            minutesLong +
                                    secondsLong,
                            minutesLong +
                                    secondsLong,
                            false
                        )
                    )
                } else {
                    showToast(getString(R.string.timeRequired))
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // Background
        if (adapter.isActivated()) {
            logDebug("\nadapter.getTime(): " + adapter.getTime())
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(SERVICE_TIME_CODE, adapter.getTime())
            startService(startIntent)
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // Foreground
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }


    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun start(id: Int) {
        adapter.startTimer(id)
    }

    override fun stop(position: Int, currentMs: Long) {
        adapter.stopTimer()
    }

    override fun reset(position: Int, startMs: Long) {
        TODO("Not yet implemented")
    }

    override fun delete(position: Int) {
        adapter.deleteTimer(position)
    }

    override fun finishTimer() {
        RingtoneManager
            .getRingtone(
                this,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ).play()
        showToast(getString(R.string.timeIsOver))
        adapter.stopTimer()
    }

}