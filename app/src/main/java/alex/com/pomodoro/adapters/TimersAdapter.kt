package alex.com.pomodoro.adapters


import alex.com.pomodoro.model.PomodoroTimer
import alex.com.pomodoro.PomodoroTimerListener
import alex.com.pomodoro.PomodoroTimerViewHolder
import alex.com.pomodoro.common.logDebug
import alex.com.pomodoro.databinding.ItemTimerBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class TimersAdapter(
    private var pomodoroTimers: MutableList<PomodoroTimer>,
    private val listener: PomodoroTimerListener
) :
    RecyclerView.Adapter<PomodoroTimerViewHolder>() {

    var isTimerActivated: Boolean = false

    fun isActivated() = isTimerActivated

    fun addTimer(timer: PomodoroTimer) {
        pomodoroTimers.add(timer)
        notifyItemInserted(pomodoroTimers.size)
    }

    fun getTime(): Long {
        for (item in pomodoroTimers) {
            if (item.isActive) {
                return item.currentTime
            }
        }
        return 0
    }

    fun stopTimer() {
        isTimerActivated = false
        for (item in pomodoroTimers) {
            item.isActive = false
        }
    }

    fun startTimer(position: Int) {
        logDebug("\npomodoroTimers:$pomodoroTimers")
        isTimerActivated = true
        for (item in pomodoroTimers) {
            item.isActive = false
            item.countDownTimer?.cancel()
        }
        pomodoroTimers[position].isActive = true
        //pomodoroTimers[position].isActive = true
        notifyDataSetChanged()
    }

    fun deleteTimer(position: Int) {
        if (pomodoroTimers[position].isActive) {
            isTimerActivated = false
        }

        pomodoroTimers.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pomodoroTimers.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PomodoroTimerViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PomodoroTimerViewHolder(listener, binding)
    }

    override fun onBindViewHolder(holder: PomodoroTimerViewHolder, position: Int) {
        val currentTimer = pomodoroTimers[position]
        logDebug("\n" + currentTimer.toString())
        holder.bind(currentTimer, position)
    }

    override fun getItemCount(): Int {
        return pomodoroTimers.size
    }
}



