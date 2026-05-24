package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.data.FocusDatabase
import com.example.data.FocusRepository
import com.example.ui.TimerAppContent
import com.example.ui.TimerViewModel
import com.example.ui.TimerViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Focus notifications configured successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Silent mode: Alerts will trigger in-app only.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Local Database and Repository
        val database = FocusDatabase.getDatabase(applicationContext)
        val repository = FocusRepository(database.focusDao)

        // 2. Initialize ViewModel with Simple Constructor injection factory
        val factory = TimerViewModelFactory(repository, applicationContext)
        val timerViewModel = ViewModelProvider(this, factory)[TimerViewModel::class.java]

        // 3. Request runtime notification permissions for Android 13+ (API 33+)
        checkAndRequestPushPermissions()

        // 4. Set Jetpack Compose UI Content
        setContent {
            MyApplicationTheme {
                TimerAppContent(viewModel = timerViewModel)
            }
        }
    }

    private fun checkAndRequestPushPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
