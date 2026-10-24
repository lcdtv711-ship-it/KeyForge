package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class ZyronRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("zyron_keyboard_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow<KeyboardTheme>(ThemePresets.allThemes[0])
    val currentTheme: StateFlow<KeyboardTheme> = _currentTheme.asStateFlow()

    private val _customGlyphs = MutableStateFlow<List<CustomGlyph>>(emptyList())
    val customGlyphs: StateFlow<List<CustomGlyph>> = _customGlyphs.asStateFlow()

    private val _typingSettings = MutableStateFlow(TypingSettings())
    val typingSettings: StateFlow<TypingSettings> = _typingSettings.asStateFlow()

    private val _stickers = MutableStateFlow<List<StickerItem>>(GlyphPresets.sampleStickers)
    val stickers: StateFlow<List<StickerItem>> = _stickers.asStateFlow()

    private val _customWords = MutableStateFlow<List<String>>(emptyList())
    val customWords: StateFlow<List<String>> = _customWords.asStateFlow()

    private val _installedMarketplaceIds = MutableStateFlow<Set<String>>(setOf("market_theme_anime", "market_glyph_pack", "market_sticker_anime"))
    val installedMarketplaceIds: StateFlow<Set<String>> = _installedMarketplaceIds.asStateFlow()

    // Individual Key Customizations: Key label -> KeyStyleConfig
    private val _individualKeyStyles = MutableStateFlow<Map<String, KeyStyleConfig>>(emptyMap())
    val individualKeyStyles: StateFlow<Map<String, KeyStyleConfig>> = _individualKeyStyles.asStateFlow()

    init {
        loadSettings()
        loadTheme()
        loadGlyphs()
    }

    private fun loadSettings() {
        val autocorrect = prefs.getBoolean("autocorrect", true)
        val agg = prefs.getString("aggressiveness", "Medium") ?: "Medium"
        val lang = prefs.getString("language", "English (US)") ?: "English (US)"
        val font = prefs.getString("fancy_font", "normal") ?: "normal"
        val haptic = prefs.getBoolean("haptic", true)
        val sound = prefs.getBoolean("sound", false)
        val autoInsert = prefs.getBoolean("auto_insert_hw", true)

        _typingSettings.value = TypingSettings(
            autocorrectEnabled = autocorrect,
            correctionAggressiveness = agg,
            activeLanguage = lang,
            activeFancyFontId = font,
            hapticFeedback = haptic,
            soundFeedback = sound,
            autoInsertHandwriting = autoInsert
        )
    }

    private fun loadTheme() {
        val themeId = prefs.getString("selected_theme_id", "anime_cyber") ?: "anime_cyber"
        val found = ThemePresets.allThemes.find { it.id == themeId } ?: ThemePresets.allThemes[0]
        _currentTheme.value = found
    }

    private fun loadGlyphs() {
        val defaultGlyphs = listOf(
            GlyphPresets.getInitialGlyphFor("A"),
            GlyphPresets.getInitialGlyphFor("B"),
            GlyphPresets.getInitialGlyphFor("Z")
        )
        _customGlyphs.value = defaultGlyphs
    }

    fun setTheme(theme: KeyboardTheme) {
        _currentTheme.value = theme
        prefs.edit().putString("selected_theme_id", theme.id).apply()
    }

    fun updateThemeOpacityAndGlow(opacity: Float, glow: Float) {
        val current = _currentTheme.value
        val updated = current.copy(bgOpacity = opacity, glowIntensity = glow)
        _currentTheme.value = updated
    }

    fun setTypingSettings(settings: TypingSettings) {
        _typingSettings.value = settings
        prefs.edit()
            .putBoolean("autocorrect", settings.autocorrectEnabled)
            .putString("aggressiveness", settings.correctionAggressiveness)
            .putString("language", settings.activeLanguage)
            .putString("fancy_font", settings.activeFancyFontId)
            .putBoolean("haptic", settings.hapticFeedback)
            .putBoolean("sound", settings.soundFeedback)
            .putBoolean("auto_insert_hw", settings.autoInsertHandwriting)
            .apply()
    }

    fun setActiveLanguage(lang: String) {
        setTypingSettings(_typingSettings.value.copy(activeLanguage = lang))
    }

    fun setActiveFancyFont(fontId: String) {
        setTypingSettings(_typingSettings.value.copy(activeFancyFontId = fontId))
    }

    fun toggleAutocorrect(enabled: Boolean) {
        setTypingSettings(_typingSettings.value.copy(autocorrectEnabled = enabled))
    }

    fun saveCustomGlyph(glyph: CustomGlyph) {
        val current = _customGlyphs.value.toMutableList()
        val index = current.indexOfFirst { it.id == glyph.id || (it.mappedCharacter == glyph.mappedCharacter && it.name == glyph.name) }
        if (index >= 0) {
            current[index] = glyph
        } else {
            current.add(0, glyph)
        }
        _customGlyphs.value = current
    }

    fun deleteCustomGlyph(glyphId: String) {
        _customGlyphs.value = _customGlyphs.value.filterNot { it.id == glyphId }
    }

    fun addSticker(sticker: StickerItem) {
        _stickers.value = listOf(sticker) + _stickers.value
    }

    fun addCustomWord(word: String) {
        val clean = word.trim().lowercase()
        if (clean.isNotEmpty() && !_customWords.value.contains(clean)) {
            _customWords.value = _customWords.value + clean
        }
    }

    fun setKeyStyle(keyChar: String, style: KeyStyleConfig) {
        val current = _individualKeyStyles.value.toMutableMap()
        current[keyChar.uppercase()] = style
        _individualKeyStyles.value = current
    }

    fun resetKeyStyles() {
        _individualKeyStyles.value = emptyMap()
    }

    fun installMarketplaceItem(itemId: String) {
        _installedMarketplaceIds.value = _installedMarketplaceIds.value + itemId
        val item = GlyphPresets.marketplaceShowcase.find { it.id == itemId }
        if (item?.associatedThemeId != null) {
            val theme = ThemePresets.allThemes.find { it.id == item.associatedThemeId }
            if (theme != null) {
                setTheme(theme)
            }
        }
    }

    fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("selected_theme", _currentTheme.value.id)
        root.put("active_language", _typingSettings.value.activeLanguage)
        root.put("autocorrect", _typingSettings.value.autocorrectEnabled)
        val glyphArray = JSONArray()
        for (g in _customGlyphs.value) {
            val gObj = JSONObject()
            gObj.put("id", g.id)
            gObj.put("name", g.name)
            gObj.put("char", g.mappedCharacter)
            gObj.put("components_count", g.components.size)
            glyphArray.put(gObj)
        }
        root.put("glyphs", glyphArray)
        return root.toString(2)
    }

    companion object {
        @Volatile
        private var instance: ZyronRepository? = null

        fun getInstance(context: Context): ZyronRepository {
            return instance ?: synchronized(this) {
                instance ?: ZyronRepository(context).also { instance = it }
            }
        }
    }
}
