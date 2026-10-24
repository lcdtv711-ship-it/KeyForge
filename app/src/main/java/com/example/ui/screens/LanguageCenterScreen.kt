package com.example.ui.screens

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
fun LanguageCenterScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by repository.typingSettings.collectAsState()
    val customWords by repository.customWords.collectAsState()

    var newWordInput by remember { mutableStateOf("") }
    val languages = listOf("English (US)", "Hinglish", "Roman Urdu", "Hindi", "Urdu")

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
                Text("Language & Autocorrect", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Multilingual Typing & Smart Predictions", color = Color(0xFF00E5FF), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LANGUAGE SELECTION
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Active Typing Language", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                languages.forEach { lang ->
                    val isSelected = settings.activeLanguage == lang
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF1E2850) else Color(0xFF0A0E22),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                repository.setActiveLanguage(lang)
                                Toast.makeText(context, "Switched language to $lang", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(lang, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                val desc = when (lang) {
                                    "Hinglish" -> "Type in Latin script -> Suggests Hindi words (kya -> क्या)"
                                    "Roman Urdu" -> "Type in Latin script -> Suggests Urdu script (salam -> سلام)"
                                    "Hindi" -> "Devanagari keyboard layout"
                                    "Urdu" -> "Nastaliq & Urdu layout"
                                    else -> "Standard QWERTY layout with prediction"
                                }
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color(0xFF00E5FF))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AUTOCORRECT PREFERENCES
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Autocorrect Preferences", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                // Autocorrect ON/OFF Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Autocorrect", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Automatically suggest and fix typos on space", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.autocorrectEnabled,
                        onCheckedChange = { repository.toggleAutocorrect(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Aggressiveness
                Text("Correction Aggressiveness", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High").forEach { agg ->
                        FilterChip(
                            selected = settings.correctionAggressiveness == agg,
                            onClick = { repository.setTypingSettings(settings.copy(correctionAggressiveness = agg)) },
                            label = { Text(agg, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto-Capitalization Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Capitalization", color = Color.White, fontSize = 14.sp)
                        Text("Capitalize first word of sentences", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.autoCapitalization,
                        onCheckedChange = { repository.setTypingSettings(settings.copy(autoCapitalization = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Double Space Period
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Double-Space Period", color = Color.White, fontSize = 14.sp)
                        Text("Insert '.' followed by space on double tap", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.doubleSpacePeriod,
                        onCheckedChange = { repository.setTypingSettings(settings.copy(doubleSpacePeriod = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PERSONAL USER DICTIONARY
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Personal User Dictionary", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Add custom slang, acronyms, or names to suggestion engine", color = Color.Gray, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newWordInput,
                        onValueChange = { newWordInput = it },
                        label = { Text("New Custom Word") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newWordInput.isNotBlank()) {
                                repository.addCustomWord(newWordInput.trim())
                                newWordInput = ""
                                Toast.makeText(context, "Added to dictionary!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                if (customWords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        customWords.forEach { word ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E2850),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF))
                            ) {
                                Text(
                                    text = word,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PRIVACY & SECURITY BANNER
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0A181C),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Privacy & Security Guarantee", color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "ZYRON never logs or transmits typed text to servers. Password and credit card fields automatically disable all suggestion caching.",
                        color = Color(0xFFD1FAE5),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
