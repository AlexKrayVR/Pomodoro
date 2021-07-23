package alex.com.pomodoro

interface PomodoroTimerListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int, startMs: Long)

    fun delete(id: Int)

    fun finishTimer()
}