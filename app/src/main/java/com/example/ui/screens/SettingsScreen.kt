package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ZyronRepository

@Composable
fun SettingsScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by repository.typingSettings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text("Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Keyboard Preferences & System Setup", color = Color(0xFF00E5FF), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Activation Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF6366F1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Android System Keyboard Status", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Enable ZYRON Keyboard in system settings to type in WhatsApp, Chrome, Notes, and all third-party apps.", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enable Keyboard")
                    }

                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select ZYRON", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback & Haptics
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Haptic Feedback & Audio", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vibration on Keypress", color = Color.White, fontSize = 14.sp)
                        Text("Gentle cyber haptic pulse on key tap", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = { repository.setTypingSettings(settings.copy(hapticFeedback = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sound on Keypress", color = Color.White, fontSize = 14.sp)
                        Text("Mechanical key click sound effect", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.soundFeedback,
                        onCheckedChange = { repository.setTypingSettings(settings.copy(soundFeedback = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Touchpad Handwriting Preferences
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Touchpad Handwriting Preferences", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Insert on Gestures", color = Color.White, fontSize = 14.sp)
                        Text("Automatically apply space and backspace swipes", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.autoInsertHandwriting,
                        onCheckedChange = { repository.setTypingSettings(settings.copy(autoInsertHandwriting = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset All
        Button(
            onClick = {
                repository.resetKeyStyles()
                Toast.makeText(context, "Key customizations reset to default", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF220C12)),
            border = BorderStroke(1.dp, Color(0xFFFF5252)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFFFF5252))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Custom Key Styles to Theme Default", color = Color(0xFFFF5252))
        }
    }
}
