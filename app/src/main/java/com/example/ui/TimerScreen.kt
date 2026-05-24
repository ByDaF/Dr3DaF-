package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekContainerBg
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPillActiveBg
import com.example.ui.theme.SleekPillActiveText
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.SleekTertiary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTrackBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerAppContent(viewModel: TimerViewModel) {
    var activeTab by remember { mutableStateOf("timer") } // timer, stats, settings

    val context = LocalContext.current
    val currentMode by viewModel.currentMode.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsState()
    val sessionNumber by viewModel.sessionNumber.collectAsState()
    val skipUpcomingBreak by viewModel.skipUpcomingBreak.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val lastCompletedQuote by viewModel.lastCompletedQuote.collectAsState()
    val focusDurationSetting by viewModel.focusDurationSetting.collectAsState()
    val breakDurationSetting by viewModel.breakDurationSetting.collectAsState()
    val focusHistory by viewModel.focusHistory.collectAsState()

    // Main Scaffold with Background Color
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SleekBackground,
        bottomBar = {
            // Elegant Sleek Standard M3 Navigation Bar component
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider(color = SleekDivider, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(SleekBackground)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer Tab
                    Column(
                        modifier = Modifier
                            .clickable { activeTab = "timer" }
                            .padding(vertical = 8.dp)
                            .testTag("timer_tab_button"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val active = activeTab == "timer"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (active) SleekPillActiveBg else Color.Transparent)
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = "Timer",
                                tint = if (active) SleekPillActiveText else SleekTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Timer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (active) SleekPillActiveText else SleekTertiary
                        )
                    }

                    // Stats Tab
                    Column(
                        modifier = Modifier
                            .clickable { activeTab = "stats" }
                            .padding(vertical = 8.dp)
                            .testTag("stats_tab_button"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val active = activeTab == "stats"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (active) SleekPillActiveBg else Color.Transparent)
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = "Stats",
                                tint = if (active) SleekPillActiveText else SleekTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stats",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (active) SleekPillActiveText else SleekTertiary
                        )
                    }

                    // Settings Tab
                    Column(
                        modifier = Modifier
                            .clickable { activeTab = "settings" }
                            .padding(vertical = 8.dp)
                            .testTag("profile_tab_button"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val active = activeTab == "settings"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (active) SleekPillActiveBg else Color.Transparent)
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Profile",
                                tint = if (active) SleekPillActiveText else SleekTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Profile",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (active) SleekPillActiveText else SleekTertiary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "timer" -> TimerTabScreen(
                    viewModel = viewModel,
                    currentMode = currentMode,
                    isRunning = isRunning,
                    secondsRemaining = secondsRemaining,
                    totalDurationSeconds = totalDurationSeconds,
                    sessionNumber = sessionNumber,
                    skipUpcomingBreak = skipUpcomingBreak,
                    focusDurationSetting = focusDurationSetting,
                    breakDurationSetting = breakDurationSetting
                )

                "stats" -> StatsTabScreen(
                    viewModel = viewModel,
                    focusHistory = focusHistory
                )

                "settings" -> SettingsTabScreen(
                    viewModel = viewModel,
                    focusDurationSetting = focusDurationSetting,
                    breakDurationSetting = breakDurationSetting,
                    soundEnabled = soundEnabled,
                    notificationEnabled = notificationEnabled
                )
            }

            // Beautiful Self-Motivational Quote Dialog Overlay
            AnimatedVisibility(
                visible = lastCompletedQuote.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Dialog(onDismissRequest = { viewModel.dismissLastQuote() }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(24.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = SleekBackground)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(SleekPillActiveBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Quote Badge",
                                    tint = SleekPillActiveText,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "FOCUS MILESTONE COMPLETED!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekPrimary,
                                letterSpacing = 1.5.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = lastCompletedQuote,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = SleekTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your effort has been registered in focus metrics history log.",
                                fontSize = 11.sp,
                                color = SleekSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.dismissLastQuote() },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                            ) {
                                Text(
                                    text = "Continue Journey",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerTabScreen(
    viewModel: TimerViewModel,
    currentMode: FocusMode,
    isRunning: Boolean,
    secondsRemaining: Int,
    totalDurationSeconds: Int,
    sessionNumber: Int,
    skipUpcomingBreak: Boolean,
    focusDurationSetting: Int,
    breakDurationSetting: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Focus Time Symbol Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SleekPillActiveBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = "Timer Symbol",
                        tint = SleekPillActiveText,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Focus Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Center Clock Ring Widget
        Box(
            modifier = Modifier
                .size(260.dp)
                .shadow(elevation = 2.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas to draw the progress track beautifully
            val progressFraction = if (totalDurationSeconds > 0) {
                secondsRemaining.toFloat() / totalDurationSeconds.toFloat()
            } else 1f

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Background Track Grey-Light-Purple Ring
                drawCircle(
                    color = SleekTrackBorder,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Foreground Active Pomodoro Pink-Purple Ring
                drawArc(
                    color = SleekPrimary,
                    startAngle = -90f,
                    sweepAngle = 360f * progressFraction,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Inside Ring Text Layout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val remMinutes = secondsRemaining / 60
                val remSeconds = secondsRemaining % 60
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", remMinutes, remSeconds)

                Text(
                    text = timeString,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Light,
                    color = SleekTextDark,
                    letterSpacing = (-1.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (currentMode == FocusMode.FOCUS) "Focusing" else "Breaking",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSecondary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.testTag("timer_subtext")
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Session of 4 badge
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekContainerBg)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SleekPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Session $sessionNumber of 4",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$focusDurationSetting min work followed by a $breakDurationSetting min break",
            fontSize = 13.sp,
            color = SleekTertiary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.15f))

        // Buttons for EXTENSION (Add time to ongoing Work session)
        if (currentMode == FocusMode.FOCUS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Extend session:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSecondary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Button(
                    onClick = { viewModel.extendWorkSession(5) },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekContainerBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag("extend_5_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = SleekPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+5 m", fontSize = 11.sp, color = SleekPrimary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.extendWorkSession(10) },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekContainerBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag("extend_10_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = SleekPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+10 m", fontSize = 11.sp, color = SleekPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Action controls (Reset, Play/Pause, Skip Break / Next Session)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button (Left)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekTrackBorder)
                    .clickable { viewModel.resetTimer() }
                    .testTag("reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset Timer",
                    tint = SleekTextDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Play / Pause Override Button (Center)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SleekPrimary)
                    .clickable { viewModel.togglePlayPause() }
                    .testTag("play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Skip Active Break Button (Right) — only active or highlighted during BREAK
            val skipButtonAlpha = if (currentMode == FocusMode.BREAK) 1f else 0.45f
            val skipBg = if (currentMode == FocusMode.BREAK) SleekPillActiveBg else SleekTrackBorder
            val skipIconColor = if (currentMode == FocusMode.BREAK) SleekPillActiveText else SleekTertiary

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(skipBg)
                    .clickable(enabled = currentMode == FocusMode.BREAK) {
                        viewModel.skipCurrentBreak()
                    }
                    .testTag("skip_break_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Skip Break Mode",
                    tint = skipIconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip Upcoming Break Pre-toggle switch (For Focus Mode)
        if (currentMode == FocusMode.FOCUS) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekContainerBg)
                    .clickable { viewModel.toggleSkipUpcomingBreak() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Switch(
                    checked = skipUpcomingBreak,
                    onCheckedChange = { viewModel.toggleSkipUpcomingBreak() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SleekPrimary,
                        uncheckedThumbColor = SleekTertiary,
                        uncheckedTrackColor = SleekTrackBorder
                    ),
                    modifier = Modifier.testTag("skip_break_toggle")
                )
                Column {
                    Text(
                        text = "Skip upcoming break",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "Start next focus session instantly next",
                        fontSize = 10.sp,
                        color = SleekTertiary
                    )
                }
            }
        } else {
            // Under Break Mode, show quick note
            Text(
                text = "Enjoy your break! Click Skip (♻️/⏭️) to return early.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SleekPrimary
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
fun StatsTabScreen(
    viewModel: TimerViewModel,
    focusHistory: List<com.example.data.FocusSession>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Focus Diagnostics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Summary Statistics Cards Grid Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Sessions Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "SESSIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${focusHistory.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = SleekPrimary)
                    Text(text = "Completed", fontSize = 11.sp, color = SleekTertiary)
                }
            }

            // Total Minutes Card
            val totalSeconds = focusHistory.sumOf { it.durationSeconds }
            val totalMinutes = totalSeconds / 60
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "FOCUS MINUTES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$totalMinutes", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = SleekPrimary)
                    Text(text = "Total Minutes", fontSize = 11.sp, color = SleekTertiary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Motivation Quote Logs (${focusHistory.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextDark
            )
            if (focusHistory.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearMotivationalHistory() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Clear History Logs",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (focusHistory.isEmpty()) {
            // Elegant placeholder empty state layout complying with "frontend-design" instructions.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = SleekBackground),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekTrackBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SleekContainerBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Check Placeholder",
                            tint = SleekSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your screen is beautifully clean",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ready to focus? Start your first pomodoro countdown. As you complete focus rounds, self-motivational quotes and chronological milestones will populate here.",
                        fontSize = 12.sp,
                        color = SleekTertiary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            // History list view of completed intervals with received self-motivational quotes
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(focusHistory) { item ->
                    val dateFormatted = remember(item.timestamp) {
                        try {
                            val sdf = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
                            sdf.format(Date(item.timestamp))
                        } catch (e: Exception) {
                            "Earlier focusing"
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Round completed — $dateFormatted",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SleekSecondary
                                )

                                if (item.extendedSeconds > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SleekPillActiveBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Extended +${item.extendedSeconds / 60}m",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekPillActiveText
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.motivationalQuote.ifEmpty { "Keep going, great job focusing!" },
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = SleekTextDark,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTabScreen(
    viewModel: TimerViewModel,
    focusDurationSetting: Int,
    breakDurationSetting: Int,
    soundEnabled: Boolean,
    notificationEnabled: Boolean
) {
    var notificationPermissionRequested by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Profile & Customization",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Work Interval Duration Preference Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Work Session Interval",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "$focusDurationSetting min",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = focusDurationSetting.toFloat(),
                    onValueChange = { viewModel.updateFocusDuration(it.toInt()) },
                    valueRange = 1f..60f,
                    colors = SliderDefaults.colors(
                        thumbColor = SleekPrimary,
                        activeTrackColor = SleekPrimary,
                        inactiveTrackColor = SleekTrackBorder
                    )
                )
                Text(
                    text = "Slide to customize. Default is 25 minutes.",
                    fontSize = 11.sp,
                    color = SleekTertiary
                )
            }
        }

        // Break Interval Duration Preference Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Short Break Interval",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "$breakDurationSetting min",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = breakDurationSetting.toFloat(),
                    onValueChange = { viewModel.updateBreakDuration(it.toInt()) },
                    valueRange = 1f..30f,
                    colors = SliderDefaults.colors(
                        thumbColor = SleekPrimary,
                        activeTrackColor = SleekPrimary,
                        inactiveTrackColor = SleekTrackBorder
                    )
                )
                Text(
                    text = "Slide to customize. Default is 5 minutes.",
                    fontSize = 11.sp,
                    color = SleekTertiary
                )
            }
        }

        // Sound Alerts preferences
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
                            contentDescription = "Sound Preference Icon",
                            tint = SleekSecondary
                        )
                        Column {
                            Text(
                                text = "Tick & Completion Sounds",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                            Text(
                                text = "Plays a gentle alarm when work or break finishes",
                                fontSize = 11.sp,
                                color = SleekTertiary
                            )
                        }
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.soundEnabled.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SleekPrimary,
                            uncheckedThumbColor = SleekTertiary,
                            uncheckedTrackColor = SleekTrackBorder
                        )
                    )
                }
            }
        }

        // Push Notifications preferences
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = CardDefaults.cardColors(containerColor = SleekContainerBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notification Switch Icon",
                            tint = SleekSecondary
                        )
                        Column {
                            Text(
                                text = "Work/Break Notifications",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                            Text(
                                text = "Dispatches push alerts in status bar background",
                                fontSize = 11.sp,
                                color = SleekTertiary
                            )
                        }
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { viewModel.notificationEnabled.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SleekPrimary,
                            uncheckedThumbColor = SleekTertiary,
                            uncheckedTrackColor = SleekTrackBorder
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Device Signature
        Text(
            text = "Focus Time Client App — build 1.0 (PRO)",
            fontSize = 11.sp,
            color = SleekTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}
