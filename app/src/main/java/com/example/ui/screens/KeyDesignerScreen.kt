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
import com.example.keyboard.ui.KeyView

@Composable
fun KeyDesignerScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val theme by repository.currentTheme.collectAsState()
    val individualKeys by repository.individualKeyStyles.collectAsState()

    var selectedKeyChar by remember { mutableStateOf("Z") }
    var currentShape by remember { mutableStateOf(KeyShapeType.ROUNDED_RECT) }
    var cornerRadius by remember { mutableFloatStateOf(8f) }
    var borderWidth by remember { mutableFloatStateOf(1.5f) }
    var glowRadius by remember { mutableFloatStateOf(8f) }
    var selectedBorderColor by remember { mutableStateOf(0xFF00E5FF) }
    var selectedKeyBgColor by remember { mutableStateOf(0xDD1B1242) }
    var selectedLabelColor by remember { mutableStateOf(0xFFFFFFFF) }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Key Designer", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Per-Key Independent Customization", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    val customStyle = KeyStyleConfig(
                        keyShape = currentShape,
                        cornerRadiusDp = cornerRadius,
                        borderWidthDp = borderWidth,
                        glowRadiusDp = glowRadius,
                        keyBorderColor = selectedBorderColor,
                        keyBgColor = selectedKeyBgColor,
                        labelColor = selectedLabelColor
                    )
                    repository.setKeyStyle(selectedKeyChar, customStyle)
                    Toast.makeText(context, "Saved Key [$selectedKeyChar] style!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Key", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Key Selector Carousel
        Text("Select Key to Customize:", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Z", "A", "SPACE", "ENTER", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { char ->
                val isSelected = selectedKeyChar == char
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedKeyChar = char },
                    label = { Text(char, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE SINGLE KEY PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D1226))
                .border(1.dp, Color(0xFF26325C), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val previewStyle = KeyStyleConfig(
                keyShape = currentShape,
                cornerRadiusDp = cornerRadius,
                borderWidthDp = borderWidth,
                glowRadiusDp = glowRadius,
                keyBorderColor = selectedBorderColor,
                keyBgColor = selectedKeyBgColor,
                labelColor = selectedLabelColor
            )

            KeyView(
                label = selectedKeyChar,
                keyStyle = previewStyle,
                theme = theme,
                modifier = Modifier.width(80.dp),
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KEY SHAPE & GEOMETRY
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Shape & Geometry", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        KeyShapeType.ROUNDED_RECT to "Rounded",
                        KeyShapeType.SQUIRCLE to "Squircle",
                        KeyShapeType.PILL to "Pill",
                        KeyShapeType.CUT_CORNER to "Cyber Cut"
                    ).forEach { (sh, name) ->
                        FilterChip(
                            selected = currentShape == sh,
                            onClick = { currentShape = sh },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Corner Radius (${cornerRadius.toInt()}dp)", color = Color.Gray, fontSize = 12.sp)
                Slider(
                    value = cornerRadius,
                    onValueChange = { cornerRadius = it },
                    valueRange = 2f..24f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Border Thickness (${String.format("%.1f", borderWidth)}dp)", color = Color.Gray, fontSize = 12.sp)
                Slider(
                    value = borderWidth,
                    onValueChange = { borderWidth = it },
                    valueRange = 0.5f..4.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Neon Glow Radius (${glowRadius.toInt()}dp)", color = Color.Gray, fontSize = 12.sp)
                Slider(
                    value = glowRadius,
                    onValueChange = { glowRadius = it },
                    valueRange = 0f..20f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // BORDER & BACKGROUND COLORS
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Glowing Border Color:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        0xFF00E5FF, 0xFFFF1744, 0xFFA855F7, 0xFFF59E0B,
                        0xFF10B981, 0xFF38BDF8, 0xFFFF007F, 0xFFFFFFFF
                    ).forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .border(
                                    width = if (selectedBorderColor == col) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedBorderColor = col }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Key Background Tone:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        0xEE14172B, 0xEE2A0C13, 0xEE0D2719, 0xEE231044,
                        0xEE291E08, 0xEE090D1E, 0xFF18181B
                    ).forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .border(
                                    width = if (selectedKeyBgColor == col) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedKeyBgColor = col }
                        )
                    }
                }
            }
        }
    }
}
