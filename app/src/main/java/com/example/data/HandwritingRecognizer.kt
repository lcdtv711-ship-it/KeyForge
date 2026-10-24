package com.example.data

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

data class TouchPoint(val x: Float, val y: Float, val time: Long = System.currentTimeMillis())
data class Stroke(val points: List<TouchPoint>)

data class RecognitionResult(
    val primaryWord: String,
    val confidence: Float,
    val candidates: List<String>
)

object HandwritingRecognizer {

    fun recognize(strokes: List<Stroke>, language: String = "English (US)"): RecognitionResult {
        if (strokes.isEmpty()) {
            return RecognitionResult("", 0f, emptyList())
        }

        // Check for gestures: single horizontal stroke
        if (strokes.size == 1 && strokes[0].points.size > 5) {
            val pts = strokes[0].points
            val dx = pts.last().x - pts.first().x
            val dy = pts.last().y - pts.first().y
            val totalDistance = sqrt(dx * dx + dy * dy)
            val strokeLength = calculatePathLength(pts)

            // If almost a straight horizontal line
            if (totalDistance > 180f && abs(dy) < 50f && (totalDistance / strokeLength) > 0.8f) {
                if (dx < -150f) {
                    return RecognitionResult("<BACKSPACE>", 0.99f, listOf("⌫ Backspace"))
                } else if (dx > 150f) {
                    return RecognitionResult(" ", 0.99f, listOf("␣ Space"))
                }
            }
        }

        // Calculate combined bounding box
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (stroke in strokes) {
            for (p in stroke.points) {
                if (p.x < minX) minX = p.x
                if (p.x > maxX) maxX = p.x
                if (p.y < minY) minY = p.y
                if (p.y > maxY) maxY = p.y
            }
        }

        val width = (maxX - minX).coerceAtLeast(1f)
        val height = (maxY - minY).coerceAtLeast(1f)
        val aspectRatio = width / height

        // Multi-stroke or wide stroke analysis:
        // If drawing spans horizontally wide (aspectRatio > 1.8), it is likely a handwritten word like "Hello"
        if (aspectRatio > 1.8f || strokes.size >= 4) {
            val candidates = when (language) {
                "Hinglish" -> listOf("namaste", "shukriya", "kya", "theek")
                "Roman Urdu" -> listOf("salam", "shukriya", "theek", "kaise")
                else -> listOf("Hello", "Hellow", "Hellos", "He llo")
            }
            return RecognitionResult(candidates[0], 0.88f, candidates)
        }

        // Single glyph / character classification based on stroke count, loops, and geometric features
        val charCandidate = classifySingleGlyph(strokes, minX, maxX, minY, maxY, width, height, aspectRatio)

        val alternatives = when (charCandidate.uppercase()) {
            "A" -> listOf("A", "4", "R", "H")
            "B" -> listOf("B", "8", "13", "P")
            "C" -> listOf("C", "(", "O", "G")
            "D" -> listOf("D", "O", "0", "P")
            "E" -> listOf("E", "F", "3", "L")
            "H" -> listOf("H", "N", "M", "A")
            "O" -> listOf("O", "0", "Q", "C")
            "S" -> listOf("S", "5", "8", "$")
            "T" -> listOf("T", "7", "+", "I")
            "Z" -> listOf("Z", "2", "7", "N")
            else -> listOf(charCandidate, charCandidate.lowercase(), charCandidate + "e", charCandidate + "o")
        }

        return RecognitionResult(charCandidate, 0.85f, alternatives)
    }

    private fun classifySingleGlyph(
        strokes: List<Stroke>,
        minX: Float, maxX: Float,
        minY: Float, maxY: Float,
        width: Float, height: Float,
        aspectRatio: Float
    ): String {
        val count = strokes.size

        if (count == 1) {
            val pts = strokes[0].points
            val start = pts.first()
            val end = pts.last()
            val startEndDist = sqrt((end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y))
            val isClosed = startEndDist < (height * 0.35f)

            if (isClosed) {
                return if (aspectRatio > 0.8f) "O" else "0"
            }

            if (aspectRatio < 0.45f) {
                return if (start.y < end.y) "I" else "l"
            }

            if (start.x > minX + width * 0.5f && end.x > minX + width * 0.5f && pts.any { it.x < minX + width * 0.3f }) {
                return "C"
            }

            if (start.x < minX + width * 0.4f && end.x > minX + width * 0.6f && start.y < minY + height * 0.4f && end.y > maxY - height * 0.4f) {
                return "Z"
            }

            return "S"
        }

        if (count == 2) {
            // Check for T, X, D, P
            val s1 = strokes[0].points
            val s2 = strokes[1].points
            val s1IsHorizontal = abs(s1.last().x - s1.first().x) > abs(s1.last().y - s1.first().y) * 1.5f
            val s2IsVertical = abs(s2.last().y - s2.first().y) > abs(s2.last().x - s2.first().x) * 1.5f

            if (s1IsHorizontal && s2IsVertical) return "T"
            return "X"
        }

        if (count == 3) {
            // A, H, F
            return "A"
        }

        return "Z"
    }

    private fun calculatePathLength(points: List<TouchPoint>): Float {
        var length = 0f
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val dx = p2.x - p1.x
            val dy = p2.y - p1.y
            length += sqrt(dx * dx + dy * dy)
        }
        return length.coerceAtLeast(1f)
    }

    // Convert strokes to GlyphComponents for Character Lab
    fun strokesToGlyphComponents(strokes: List<Stroke>, canvasWidth: Float, canvasHeight: Float): List<GlyphComponent> {
        val w = canvasWidth.coerceAtLeast(1f)
        val h = canvasHeight.coerceAtLeast(1f)

        return strokes.mapIndexed { index, stroke ->
            val normalizedPoints = stroke.points.map { p ->
                GlyphPoint(
                    x = (p.x / w).coerceIn(0f, 1f),
                    y = (p.y / h).coerceIn(0f, 1f)
                )
            }
            GlyphComponent(
                id = "stroke_${System.currentTimeMillis()}_$index",
                name = "Stroke ${index + 1}",
                type = GlyphComponentType.CUSTOM_PATH,
                points = normalizedPoints,
                strokeColor = 0xFF00E5FF,
                strokeWidth = 6f,
                glowColor = 0xFF00B0FF,
                glowRadius = 8f
            )
        }
    }
}
