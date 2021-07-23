package alex.com.pomodoro.model

import android.os.CountDownTimer

data class PomodoroTimer(
    var startTime: Long,
    var currentTime: Long,
    var isActive: Boolean = false,
    var isFinished: Boolean = false,
    var countDownTimer: CountDownTimer? = null
)