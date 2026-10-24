package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as CanvasStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@Composable
fun TouchpadTypingScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by repository.typingSettings.collectAsState()
    val theme by repository.currentTheme.collectAsState()

    var testRecognizedSentence by remember { mutableStateOf("Typed output: ") }
    val strokes = remember { mutableStateListOf<Stroke>() }
    var currentPoints by remember { mutableStateOf<List<TouchPoint>>(emptyList()) }
    var recognitionResult by remember { mutableStateOf<RecognitionResult?>(null) }
    var isHandwritingMode by remember { mutableStateOf(true) } // true: Handwriting, false: Draw Character
    var canvasSize by remember { mutableStateOf(Offset(1f, 1f)) }

    fun runRecognition() {
        if (strokes.isNotEmpty()) {
            recognitionResult = HandwritingRecognizer.recognize(strokes.toList(), settings.activeLanguage)
            if (settings.autoInsertHandwriting && recognitionResult != null) {
                // If single character or gesture
                val res = recognitionResult!!
                if (res.primaryWord == "<BACKSPACE>") {
                    if (testRecognizedSentence.length > 14) {
                        testRecognizedSentence = testRecognizedSentence.dropLast(1)
                    }
                    strokes.clear()
                    recognitionResult = null
                } else if (res.primaryWord == " ") {
                    testRecognizedSentence += " "
                    strokes.clear()
                    recognitionResult = null
                }
            }
        } else {
            recognitionResult = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913))
            .padding(16.dp)
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
                    Text("Touchpad Typing", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Handwriting & Stroke Recognition", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }

            // Language Chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F142A),
                border = BorderStroke(1.dp, Color(0xFF00E5FF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(settings.activeLanguage, color = Color.White, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Insertion Output Sandbox Box
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Simulated App Text Field (Output):", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = testRecognizedSentence,
                    color = Color(0xFF00E5FF),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Switcher: Handwriting vs Draw Character
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = isHandwritingMode,
                    onClick = { isHandwritingMode = true },
                    label = { Text("Handwriting to Text", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Gesture, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                )
                FilterChip(
                    selected = !isHandwritingMode,
                    onClick = { isHandwritingMode = false },
                    label = { Text("Draw Character", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            strokes.removeAt(strokes.lastIndex)
                            runRecognition()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        strokes.clear()
                        currentPoints = emptyList()
                        recognitionResult = null
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFFF5252))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TOUCHPAD WRITING SURFACE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                .background(Color(0xFF080C1D))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints = listOf(TouchPoint(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints = currentPoints + TouchPoint(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                strokes.add(Stroke(currentPoints))
                                currentPoints = emptyList()
                                runRecognition()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasSize = Offset(size.width, size.height)

                // High-tech grid and writing guide lines
                val guideColor = Color(0x1F00E5FF)
                drawLine(
                    color = guideColor,
                    start = Offset(0f, size.height * 0.35f),
                    end = Offset(size.width, size.height * 0.35f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = guideColor,
                    start = Offset(0f, size.height * 0.65f),
                    end = Offset(size.width, size.height * 0.65f),
                    strokeWidth = 1f
                )

                // Render strokes
                for (s in strokes) {
                    drawHandwritingStroke(s.points, Color(0xFF00E5FF))
                }
                if (currentPoints.isNotEmpty()) {
                    drawHandwritingStroke(currentPoints, Color(0xFF80D8FF))
                }
            }

            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Draw,
                        contentDescription = null,
                        tint = Color(0x4400E5FF),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHandwritingMode) "Write words with your finger here\nSwipe left to backspace • Swipe right for space" else "Draw custom vector glyph to import into Character Lab",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CANDIDATES & INSERTION BAR
        if (isHandwritingMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val candidates = recognitionResult?.candidates ?: emptyList()
                    if (candidates.isEmpty()) {
                        Text("Draw to see real-time candidates...", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        candidates.forEachIndexed { idx, candidate ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (idx == 0) Color(0xFF6366F1) else Color(0xFF0F142A),
                                border = BorderStroke(1.dp, Color(0xFF6366F1)),
                                modifier = Modifier.clickable {
                                    testRecognizedSentence += "$candidate "
                                    strokes.clear()
                                    recognitionResult = null
                                }
                            ) {
                                Text(
                                    text = candidate,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val word = recognitionResult?.primaryWord ?: ""
                        if (word.isNotEmpty()) {
                            testRecognizedSentence += "$word "
                            strokes.clear()
                            recognitionResult = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Insert", color = Color(0xFF0A0E24), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Mode 2: Save to Character Lab
            Button(
                onClick = {
                    if (strokes.isNotEmpty()) {
                        val components = HandwritingRecognizer.strokesToGlyphComponents(strokes.toList(), canvasSize.x, canvasSize.y)
                        val newGlyph = CustomGlyph(
                            id = "glyph_drawn_${System.currentTimeMillis()}",
                            name = "Hand-Drawn Glyph",
                            mappedCharacter = "✍",
                            components = components
                        )
                        repository.saveCustomGlyph(newGlyph)
                        strokes.clear()
                        Toast.makeText(context, "Saved drawing as Custom Glyph in Character Lab!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Drawing to Character Lab", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHandwritingStroke(
    points: List<TouchPoint>,
    color: Color
) {
    if (points.isEmpty()) return
    if (points.size == 1) {
        drawCircle(color, radius = 5f, center = Offset(points[0].x, points[0].y))
        return
    }
    val path = Path()
    path.moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        val p1 = points[i - 1]
        val p2 = points[i]
        val midX = (p1.x + p2.x) / 2f
        val midY = (p1.y + p2.y) / 2f
        path.quadraticTo(p1.x, p1.y, midX, midY)
    }
    drawPath(path, color = color, style = CanvasStroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
