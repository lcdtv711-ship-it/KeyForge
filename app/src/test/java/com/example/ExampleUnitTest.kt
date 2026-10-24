package com.example

import com.example.data.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCharacterSurgery_deleteStroke() {
        val glyphA = GlyphPresets.getInitialGlyphFor("A")
        val initialCount = glyphA.components.size
        assertTrue(initialCount >= 3)

        // Surgery: find left diagonal stroke and delete it
        val leftStroke = glyphA.components.find { it.id == "stroke_left_diag" }
        assertNotNull(leftStroke)

        val modifiedComponents = glyphA.components.filterNot { it.id == "stroke_left_diag" }
        val modifiedGlyph = glyphA.copy(
            id = "custom_A_surgical",
            name = "Surgically Modified A",
            components = modifiedComponents
        )

        assertEquals(initialCount - 1, modifiedGlyph.components.size)
        assertFalse(modifiedGlyph.components.any { it.id == "stroke_left_diag" })

        // Original preset must not be mutated
        val freshA = GlyphPresets.getInitialGlyphFor("A")
        assertEquals(initialCount, freshA.components.size)
    }

    @Test
    fun testCharacterMergeEngine() {
        val glyphA = GlyphPresets.getInitialGlyphFor("A")
        val glyphB = GlyphPresets.getInitialGlyphFor("B")

        // Extract curved loop component from B
        val loopComp = glyphB.components.find { it.id == "loop_top" }
        assertNotNull(loopComp)

        // Merge loop into A
        val mergedComp = loopComp!!.copy(
            id = "merged_loop_from_B",
            name = "B Curve Loop in A",
            zIndex = glyphA.components.size + 1
        )
        val hybridGlyph = glyphA.copy(
            id = "hybrid_A_B",
            name = "Fusion A+B",
            components = glyphA.components + mergedComp
        )

        assertEquals(glyphA.components.size + 1, hybridGlyph.components.size)
        assertTrue(hybridGlyph.components.any { it.id == "merged_loop_from_B" })
        assertEquals("B Curve Loop in A", hybridGlyph.components.last().name)
    }

    @Test
    fun testTouchpadGesturesAndRecognition() {
        // Horizontal left swipe gesture -> Backspace
        val leftPoints = listOf(
            TouchPoint(200f, 100f),
            TouchPoint(150f, 102f),
            TouchPoint(100f, 101f),
            TouchPoint(50f, 99f),
            TouchPoint(10f, 100f),
            TouchPoint(0f, 100f)
        )
        val backspaceResult = HandwritingRecognizer.recognize(listOf(Stroke(leftPoints)))
        assertEquals("<BACKSPACE>", backspaceResult.primaryWord)

        // Horizontal right swipe gesture -> Space
        val rightPoints = listOf(
            TouchPoint(10f, 100f),
            TouchPoint(60f, 101f),
            TouchPoint(110f, 99f),
            TouchPoint(160f, 102f),
            TouchPoint(210f, 100f),
            TouchPoint(250f, 100f)
        )
        val spaceResult = HandwritingRecognizer.recognize(listOf(Stroke(rightPoints)))
        assertEquals(" ", spaceResult.primaryWord)

        // Multi-stroke wide drawing -> word candidates
        val wordStrokes = listOf(
            Stroke(listOf(TouchPoint(10f, 50f), TouchPoint(10f, 150f), TouchPoint(10f, 250f))),
            Stroke(listOf(TouchPoint(40f, 150f), TouchPoint(70f, 150f))),
            Stroke(listOf(TouchPoint(70f, 50f), TouchPoint(70f, 250f))),
            Stroke(listOf(TouchPoint(90f, 200f), TouchPoint(140f, 200f))),
            Stroke(listOf(TouchPoint(170f, 50f), TouchPoint(170f, 250f)))
        )
        val wordResult = HandwritingRecognizer.recognize(wordStrokes)
        assertEquals("Hello", wordResult.primaryWord)
        assertTrue(wordResult.candidates.contains("Hello"))
        assertTrue(wordResult.candidates.contains("Hellow"))
    }

    @Test
    fun testUnicodeFontEngineStyles() {
        val original = "ZYRON"
        val mathBold = UnicodeFontEngine.applyStyle(original, "math_bold")
        assertEquals("𝐙𝐘𝐑𝐎𝐍", mathBold)

        val script = UnicodeFontEngine.applyStyle(original, "script_cursive")
        assertTrue(script.contains("𝒵") || script.contains("𝒴"))

        val fraktur = UnicodeFontEngine.applyStyle(original, "gothic_fraktur")
        assertTrue(fraktur.contains("ℨ") || fraktur.contains("𝔜"))

        val doubleStruck = UnicodeFontEngine.applyStyle(original, "double_struck")
        assertTrue(doubleStruck.contains("ℤ") || doubleStruck.contains("𝕐"))
    }

    @Test
    fun testTransliterationEngine() {
        val hinglishSuggestions = TransliterationEngine.getSuggestions("kya", "Hinglish")
        assertTrue(hinglishSuggestions.contains("क्या"))

        val romanUrduSuggestions = TransliterationEngine.getSuggestions("salam", "Roman Urdu")
        assertTrue(romanUrduSuggestions.contains("سلام"))

        val englishSuggestions = TransliterationEngine.getSuggestions("keyb", "English (US)")
        assertTrue(englishSuggestions.contains("keyboard"))
    }

    @Test
    fun testThemePresetsIntegrity() {
        assertEquals(9, ThemePresets.allThemes.size)
        assertNotNull(ThemePresets.getThemeById("anime_cyber"))
        assertNotNull(ThemePresets.getThemeById("neon_red"))
        assertNotNull(ThemePresets.getThemeById("sky_dream"))
        assertNotNull(ThemePresets.getThemeById("forest_green"))
        assertNotNull(ThemePresets.getThemeById("purple_night"))
        assertNotNull(ThemePresets.getThemeById("golden"))
        assertNotNull(ThemePresets.getThemeById("minimal_dark"))
        assertNotNull(ThemePresets.getThemeById("light_mode"))
        assertNotNull(ThemePresets.getThemeById("galaxy"))
    }
}
