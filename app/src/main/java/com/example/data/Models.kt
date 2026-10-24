package com.example.data

import androidx.compose.ui.graphics.Color

enum class KeyShapeType {
    ROUNDED_RECT,
    SQUIRCLE,
    PILL,
    CUT_CORNER,
    HEXAGON
}

enum class KeyTarget {
    ALL,
    SPACE,
    ENTER,
    SHIFT,
    BACKSPACE,
    SPECIFIC
}

data class KeyStyleConfig(
    val keyShape: KeyShapeType = KeyShapeType.ROUNDED_RECT,
    val cornerRadiusDp: Float = 8f,
    val keyBgColor: Long = 0xFF14172B,
    val keyBgSecondaryColor: Long = 0xFF1D223D,
    val keyBorderColor: Long = 0xFF2F3965,
    val borderWidthDp: Float = 1f,
    val glowColor: Long = 0xFF4D7CFF,
    val glowRadiusDp: Float = 4f,
    val labelColor: Long = 0xFFE2E8F0,
    val labelSecondaryColor: Long = 0xFF64748B,
    val pressedScale: Float = 0.95f,
    val pressedColor: Long = 0xFF2A335E,
    val customImageUri: String? = null
)

data class KeyboardTheme(
    val id: String,
    val name: String,
    val category: String,
    val isDark: Boolean = true,
    val bgDrawableRes: Int? = null,
    val bgSolidColor: Long = 0xFF0A0C16,
    val bgGradientColors: List<Long> = listOf(0xFF070913, 0xFF0E1326),
    val accentColor: Long = 0xFF6366F1,
    val primaryTextColor: Long = 0xFFF8FAFC,
    val secondaryTextColor: Long = 0xFF94A3B8,
    val keyStyle: KeyStyleConfig = KeyStyleConfig(),
    val toolbarBgColor: Long = 0xFF0D1020,
    val suggestionBgColor: Long = 0xFF111529,
    val bgOpacity: Float = 0.90f,
    val glowIntensity: Float = 0.65f,
    val isUserCreated: Boolean = false
)

enum class GlyphComponentType {
    LINE,
    CURVE,
    RECT,
    OVAL,
    CROWN_SPIKE,
    WING,
    CIRCUIT_LINE,
    STAR,
    BOLT,
    CUSTOM_PATH
}

data class GlyphPoint(
    val x: Float, // Normalized 0.0 to 1.0 on canvas
    val y: Float
)

data class GlyphComponent(
    val id: String,
    val name: String,
    val type: GlyphComponentType,
    val points: List<GlyphPoint> = emptyList(),
    val strokeColor: Long = 0xFF00E5FF,
    val fillColor: Long? = null,
    val strokeWidth: Float = 4f,
    val glowColor: Long = 0xFF00B0FF,
    val glowRadius: Float = 6f,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val rotationDeg: Float = 0f,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val zIndex: Int = 0
)

data class CustomGlyph(
    val id: String,
    val name: String,
    val mappedCharacter: String, // e.g. "A", "Z", "1", "CROWN"
    val components: List<GlyphComponent>,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val collectionName: String = "My Glyphs",
    val assignedToKey: Boolean = true
)

data class StickerItem(
    val id: String,
    val packName: String,
    val name: String,
    val imageDrawableRes: Int? = null,
    val localUri: String? = null,
    val textOverlay: String? = null,
    val textColor: Long = 0xFFFFFFFF,
    val textGlowColor: Long = 0xFF8A2BE2,
    val rotationDeg: Float = 0f,
    val scale: Float = 1f
)

data class UnicodeFontStyle(
    val id: String,
    val name: String,
    val category: String,
    val sampleText: String,
    val transformFunction: (String) -> String
)

data class TypingSettings(
    val autocorrectEnabled: Boolean = true,
    val correctionAggressiveness: String = "Medium", // "Low", "Medium", "High"
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val hapticFeedback: Boolean = true,
    val soundFeedback: Boolean = false,
    val activeLanguage: String = "English (US)", // "English (US)", "Hinglish", "Roman Urdu", "Hindi", "Urdu"
    val activeFancyFontId: String = "normal",
    val touchpadSensitivity: Float = 1.0f,
    val autoInsertHandwriting: Boolean = true,
    val showNumberRow: Boolean = true,
    val keyboardHeightFactor: Float = 1.0f
)

data class MarketplaceItem(
    val id: String,
    val title: String,
    val creator: String,
    val category: String, // "Themes", "Character Packs", "Stickers", "Fonts", "Key Styles"
    val description: String,
    val downloads: Int,
    val rating: Float,
    val tags: List<String>,
    val isInstalled: Boolean = false,
    val previewColor: Long = 0xFF6366F1,
    val associatedThemeId: String? = null
)
