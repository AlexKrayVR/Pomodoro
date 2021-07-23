package alex.com.pomodoro

import alex.com.pomodoro.common.displayTime
import alex.com.pomodoro.common.logDebug
import alex.com.pomodoro.databinding.ItemTimerBinding
import alex.com.pomodoro.model.PomodoroTimer
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class PomodoroTimerViewHolder(
    val listener: PomodoroTimerListener,
    val binding: ItemTimerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(timer: PomodoroTimer, position: Int) {
        logDebug("\nbind: position: $position")
        binding.time.text = timer.currentTime.displayTime()
        binding.progress.setPeriod(timer.startTime)

        if (timer.isFinished) {
            binding.layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color1
                )
            )
            binding.progress.setCurrent(0L)
        } else {
            binding.layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.white
                )
            )
            binding.progress.setCurrent(timer.currentTime)
        }

        timer.countDownTimer?.cancel()
        if (timer.isActive) {
            timer.countDownTimer = getCountDownTimer(timer, position)
            timer.countDownTimer?.start()
            binding.startStopButton.text = itemView.context.getString(R.string.stop)
            binding.indicator.isVisible = true
            (binding.indicator.background as? AnimationDrawable)?.start()
            binding.layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.white
                )
            )
        } else {
            binding.startStopButton.text = itemView.context.getString(R.string.start)
            binding.indicator.isInvisible = true
            (binding.indicator.background as? AnimationDrawable)?.stop()
        }

        binding.delete.setOnClickListener {
            timer.countDownTimer?.cancel()
            listener.delete(position)
        }

        binding.startStopButton.setOnClickListener {
            binding.layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.white
                )
            )
            if (timer.isActive) {
                timer.isActive = false
                timer.countDownTimer?.cancel()
                binding.startStopButton.text = itemView.context.getString(R.string.start)
                binding.indicator.isInvisible = true
                (binding.indicator.background as? AnimationDrawable)?.stop()
                listener.stop(position, timer.currentTime)
            } else {
                timer.isActive = true
                timer.countDownTimer?.cancel()
                timer.countDownTimer = getCountDownTimer(timer, position)
                timer.countDownTimer?.start()
                binding.startStopButton.text = itemView.context.getString(R.string.stop)
                binding.indicator.isVisible = true
                (binding.indicator.background as? AnimationDrawable)?.start()
                listener.start(position)
            }
        }
    }


    private fun getCountDownTimer(
        pomodoroTimer: PomodoroTimer, position: Int
    ): CountDownTimer {

        return object : CountDownTimer(pomodoroTimer.currentTime, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                pomodoroTimer.currentTime = millisUntilFinished
                binding.progress.setCurrent(pomodoroTimer.currentTime)
                binding.time.text = millisUntilFinished.displayTime()
                logDebug("\nposition:$position \ttime:${millisUntilFinished.displayTime()}")
            }

            override fun onFinish() {
                logDebug("\ntimer: onFinish")

                binding.time.text = "00:00:00"
                binding.progress.setCurrent(0L)
                pomodoroTimer.isFinished = true
                pomodoroTimer.isActive = false
                binding.indicator.isInvisible = true
                (binding.indicator.background as? AnimationDrawable)?.stop()
                binding.layout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color1
                    )
                )
                pomodoroTimer.currentTime = pomodoroTimer.startTime
                binding.time.text = pomodoroTimer.currentTime.displayTime()

                binding.startStopButton.text = itemView.context.getString(R.string.start)
                listener.finishTimer()
            }
        }


    }

}