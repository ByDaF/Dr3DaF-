package com.example.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.data.FocusRepository
import com.example.data.FocusSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class FocusMode {
    FOCUS, BREAK
}

class TimerViewModel(
    private val repository: FocusRepository,
    private val context: Context
) : ViewModel() {

    private val NOTIFICATION_ID = 451

    val quotesRepository = listOf(
        "\"The secret of getting ahead is getting started.\" — Mark Twain",
        "\"It always seems impossible until it's done.\" — Nelson Mandela",
        "\"Don't watch the clock; do what it does. Keep going.\" — Sam Levenson",
        "\"Focus on being productive instead of busy.\" — Tim Ferriss",
        "\"Amateurs sit and wait for inspiration, the rest of us just get up and go to work.\" — Stephen King",
        "\"Deep work is an indispensable skill in our modern economy.\" — Cal Newport",
        "\"Believe you can and you're halfway there.\" — Theodore Roosevelt",
        "\"Action is the foundational key to all success.\" — Pablo Picasso",
        "\"You don't have to be great to start, but you must start to be great.\" — Zig Ziglar",
        "\"Real master is master of himself. Focus on the breath and stay persistent.\" — Stoic Wisdom",
        "\"Success is the sum of small effort, repeated day in and day out.\" — Robert Collier",
        "\"Do what you can, with what you have, where you are.\" — Theodore Roosevelt"
    )

    // Current Pomodoro Settings (in minutes)
    val focusDurationSetting = MutableStateFlow(25)
    val breakDurationSetting = MutableStateFlow(5)

    private val _currentMode = MutableStateFlow(FocusMode.FOCUS)
    val currentMode: StateFlow<FocusMode> = _currentMode.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(25 * 60)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _sessionNumber = MutableStateFlow(1) // 1 of 4
    val sessionNumber: StateFlow<Int> = _sessionNumber.asStateFlow()

    private val _skipUpcomingBreak = MutableStateFlow(false)
    val skipUpcomingBreak: StateFlow<Boolean> = _skipUpcomingBreak.asStateFlow()

    // Sound and Notification configuration
    val soundEnabled = MutableStateFlow(true)
    val notificationEnabled = MutableStateFlow(true)

    // Track active session cumulative stats
    private val _extendedSecondsAccumulated = MutableStateFlow(0)
    val extendedSecondsAccumulated: StateFlow<Int> = _extendedSecondsAccumulated.asStateFlow()

    // Last completed focus motivational quote to display
    private val _lastCompletedQuote = MutableStateFlow("")
    val lastCompletedQuote: StateFlow<String> = _lastCompletedQuote.asStateFlow()

    // Flow for completed database history
    val focusHistory: StateFlow<List<FocusSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var countdownJob: Job? = null

    init {
        createNotificationChannel()
    }

    fun togglePlayPause() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        countdownJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(1000)
                val currentSeconds = _secondsRemaining.value
                if (currentSeconds > 0) {
                    _secondsRemaining.value = currentSeconds - 1
                } else {
                    withContext(Dispatchers.Main) {
                        onTimerFinished()
                    }
                    break
                }
            }
        }
    }

    private fun pauseTimer() {
        _isRunning.value = false
        countdownJob?.cancel()
        countdownJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _extendedSecondsAccumulated.value = 0
        if (_currentMode.value == FocusMode.FOCUS) {
            val seconds = focusDurationSetting.value * 60
            _secondsRemaining.value = seconds
            _totalDurationSeconds.value = seconds
        } else {
            val seconds = breakDurationSetting.value * 60
            _secondsRemaining.value = seconds
            _totalDurationSeconds.value = seconds
        }
    }

    // Extend current work session by minutes
    fun extendWorkSession(minutes: Int) {
        val secondsToAdd = minutes * 60
        _secondsRemaining.value += secondsToAdd
        _totalDurationSeconds.value += secondsToAdd
        if (_currentMode.value == FocusMode.FOCUS) {
            _extendedSecondsAccumulated.value += secondsToAdd
        }
    }

    // Skip the current active break or pre-toggle break skip
    fun skipCurrentBreak() {
        if (_currentMode.value == FocusMode.BREAK) {
            // Instantly transition back to focus mode
            transitionToFocusMode(incrementSession = true, wasBreakSkipped = true)
            playNotificationSound()
            sendNotification("Break Skipped", "Starting Focus Session ${sessionNumber.value} now!")
        }
    }

    fun toggleSkipUpcomingBreak() {
        _skipUpcomingBreak.value = !_skipUpcomingBreak.value
    }

    fun clearMotivationalHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun dismissLastQuote() {
        _lastCompletedQuote.value = ""
    }

    // Setters for durations to allow quick-adjustments from settings/profile
    fun updateFocusDuration(minutes: Int) {
        if (minutes in 1..180) {
            focusDurationSetting.value = minutes
            if (_currentMode.value == FocusMode.FOCUS && !_isRunning.value) {
                _secondsRemaining.value = minutes * 60
                _totalDurationSeconds.value = minutes * 60
            }
        }
    }

    fun updateBreakDuration(minutes: Int) {
        if (minutes in 1..120) {
            breakDurationSetting.value = minutes
            if (_currentMode.value == FocusMode.BREAK && !_isRunning.value) {
                _secondsRemaining.value = minutes * 60
                _totalDurationSeconds.value = minutes * 60
            }
        }
    }

    private fun onTimerFinished() {
        pauseTimer()
        playNotificationSound()

        if (_currentMode.value == FocusMode.FOCUS) {
            // Completed 25min (or custom Focus Duration)
            val quote = quotesRepository.random()
            _lastCompletedQuote.value = quote

            // Save to Database
            val sessionTotalFocused = (focusDurationSetting.value * 60) + _extendedSecondsAccumulated.value
            val savedSession = FocusSession(
                durationSeconds = sessionTotalFocused,
                sessionNumber = _sessionNumber.value,
                breakSkipped = _skipUpcomingBreak.value,
                extendedSeconds = _extendedSecondsAccumulated.value,
                motivationalQuote = quote
            )
            viewModelScope.launch {
                repository.insertSession(savedSession)
            }

            sendNotification(
                "Focus Session Completed!",
                "Great job finishing Session ${_sessionNumber.value}. " + if (_skipUpcomingBreak.value) "Starting next focus interval!" else "Enjoy your 5-minute break!"
            )

            // Reset accumulated extension info
            _extendedSecondsAccumulated.value = 0

            // Check if upcoming break is skipped
            if (_skipUpcomingBreak.value) {
                _skipUpcomingBreak.value = false // reset flag
                transitionToFocusMode(incrementSession = true, wasBreakSkipped = true)
            } else {
                transitionToBreakMode()
            }
        } else {
            // Break completed, go to focus mode
            sendNotification("Break Finished", "Time to focus! Let's start the next task.")
            transitionToFocusMode(incrementSession = true, wasBreakSkipped = false)
        }
    }

    private fun transitionToBreakMode() {
        _currentMode.value = FocusMode.BREAK
        val seconds = breakDurationSetting.value * 60
        _secondsRemaining.value = seconds
        _totalDurationSeconds.value = seconds
        startTimer() // start the break timer automatically
    }

    private fun transitionToFocusMode(incrementSession: Boolean, wasBreakSkipped: Boolean) {
        _currentMode.value = FocusMode.FOCUS
        if (incrementSession) {
            val nextVal = _sessionNumber.value + 1
            _sessionNumber.value = if (nextVal > 4) 1 else nextVal
        }
        val seconds = focusDurationSetting.value * 60
        _secondsRemaining.value = seconds
        _totalDurationSeconds.value = seconds
        startTimer() // start the focus timer automatically
    }

    private fun playNotificationSound() {
        if (!soundEnabled.value) return
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, notificationUri)
                ringtone?.play()
            } catch (e: Exception) {
                // Fallback to ToneGenerator
                try {
                    val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
                    toneG.startTone(ToneGenerator.TONE_PROP_ACK, 300)
                } catch (tr: Exception) {
                    tr.printStackTrace()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Focus Time Alerts"
            val descriptionText = "Triggers sound and alerts when work or break timers finish."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("focus_timer_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, content: String) {
        if (!notificationEnabled.value) return
        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "focus_timer_channel")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

class TimerViewModelFactory(
    private val repository: FocusRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
