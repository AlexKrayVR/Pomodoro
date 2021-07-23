package alex.com.pomodoro.common

import java.util.*

const val NOTIFICATION_ID = 101
const val CHANNEL_ID = "pomodoro"
const val CHANNEL_NAME = "Pomodoro Service"
const val INTERVAL = 1000L
const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val STARTED_TIMER_TIME = "00:00:00"
const val SERVICE_TIME_CODE = "11"


fun Long.displayTime(): String {
    if (this <= 0L) {
        return STARTED_TIMER_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"

}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}

