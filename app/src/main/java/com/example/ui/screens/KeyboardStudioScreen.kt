package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.keyboard.ui.ZyronKeyboardLayout

@Composable
fun KeyboardStudioScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by repository.currentTheme.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Keys, 1: Background, 2: Effects

    var opacity by remember { mutableFloatStateOf(currentTheme.bgOpacity) }
    var glow by remember { mutableFloatStateOf(currentTheme.glowIntensity) }
    var keyShape by remember { mutableStateOf(currentTheme.keyStyle.keyShape) }
    var keyCornerRadius by remember { mutableFloatStateOf(currentTheme.keyStyle.cornerRadiusDp) }
    var keyBorderWidth by remember { mutableFloatStateOf(currentTheme.keyStyle.borderWidthDp) }
    var selectedAccentColor by remember { mutableStateOf(currentTheme.accentColor) }

    // Synchronize updates into active theme
    fun applyLiveChanges() {
        val updatedKeyStyle = currentTheme.keyStyle.copy(
            keyShape = keyShape,
            cornerRadiusDp = keyCornerRadius,
            borderWidthDp = keyBorderWidth,
            glowRadiusDp = glow * 10f,
            keyBorderColor = selectedAccentColor
        )
        val updatedTheme = currentTheme.copy(
            bgOpacity = opacity,
            glowIntensity = glow,
            accentColor = selectedAccentColor,
            keyStyle = updatedKeyStyle
        )
        repository.setTheme(updatedTheme)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Keyboard Studio", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Live Customization & Effects", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    applyLiveChanges()
                    Toast.makeText(context, "Keyboard Theme Applied Successfully!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apply", fontSize = 13.sp)
            }
        }

        // TABS: Keys, Background, Effects
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color(0xFF0D1127),
            contentColor = Color(0xFF00E5FF)
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Keys", fontWeight = FontWeight.SemiBold) })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Background", fontWeight = FontWeight.SemiBold) })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Effects", fontWeight = FontWeight.SemiBold) })
        }

        // LIVE KEYBOARD PREVIEW AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.2.dp, Color(selectedAccentColor).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        ) {
            ZyronKeyboardLayout(
                repository = repository,
                onTextInput = {},
                onDelete = {},
                onEnter = {},
                onOpenStudio = {}
            )
        }

        // CONTROLS PANEL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // KEYS CUSTOMIZATION
                    Text("Key Shape & Geometry", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            KeyShapeType.ROUNDED_RECT to "Rounded",
                            KeyShapeType.SQUIRCLE to "Squircle",
                            KeyShapeType.PILL to "Pill",
                            KeyShapeType.CUT_CORNER to "Cyber Cut"
                        ).forEach { (shape, name) ->
                            FilterChip(
                                selected = keyShape == shape,
                                onClick = {
                                    keyShape = shape
                                    applyLiveChanges()
                                },
                                label = { Text(name, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Corner Radius (${keyCornerRadius.toInt()}dp)", color = Color.Gray, fontSize = 12.sp)
                    Slider(
                        value = keyCornerRadius,
                        onValueChange = {
                            keyCornerRadius = it
                            applyLiveChanges()
                        },
                        valueRange = 2f..24f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Border Stroke (${String.format("%.1f", keyBorderWidth)}dp)", color = Color.Gray, fontSize = 12.sp)
                    Slider(
                        value = keyBorderWidth,
                        onValueChange = {
                            keyBorderWidth = it
                            applyLiveChanges()
                        },
                        valueRange = 0.5f..3.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                    )
                }
                1 -> {
                    // BACKGROUND SELECTOR
                    Text("Background Artwork & Presets", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemePresets.allThemes.forEach { th ->
                            val isSelected = th.id == currentTheme.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(th.bgSolidColor),
                                border = BorderStroke(2.dp, if (isSelected) Color(0xFF00E5FF) else Color(0xFF26325C)),
                                modifier = Modifier
                                    .size(width = 110.dp, height = 75.dp)
                                    .clickable { repository.setTheme(th) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = th.name.take(12),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // EFFECTS & NEON GLOW
                    Text("Neon Glow & Backlight Effects", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Opacity Slider (85% in mockup)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Opacity", color = Color.White, fontSize = 13.sp)
                        Text("${(opacity * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = opacity,
                        onValueChange = {
                            opacity = it
                            repository.updateThemeOpacityAndGlow(opacity, glow)
                        },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Glow Slider (60% in mockup)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Glow", color = Color.White, fontSize = 13.sp)
                        Text("${(glow * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = glow,
                        onValueChange = {
                            glow = it
                            repository.updateThemeOpacityAndGlow(opacity, glow)
                        },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Accent Neon Color", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            0xFF00E5FF, 0xFFFF1744, 0xFFA855F7, 0xFF38BDF8,
                            0xFF10B981, 0xFFF59E0B, 0xFFFF007F
                        ).forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(col))
                                    .border(
                                        width = if (selectedAccentColor == col) 2.5.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedAccentColor = col
                                        applyLiveChanges()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
