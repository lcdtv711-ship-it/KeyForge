package com.example.keyboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke as CanvasStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.*

enum class KeyboardMode {
    ALPHA,
    NUMERIC,
    SYMBOLS,
    EMOJI,
    TOUCHPAD_HANDWRITING
}

@Composable
fun ZyronKeyboardLayout(
    repository: ZyronRepository,
    modifier: Modifier = Modifier,
    onTextInput: (String) -> Unit = {},
    onDelete: () -> Unit = {},
    onEnter: () -> Unit = {},
    onSpace: () -> Unit = { onTextInput(" ") },
    onStickerSelected: (StickerItem) -> Unit = {},
    onCustomGlyphSelected: (CustomGlyph) -> Unit = {},
    onOpenStudio: () -> Unit = {}
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    val theme by repository.currentTheme.collectAsState()
    val settings by repository.typingSettings.collectAsState()
    val customGlyphs by repository.customGlyphs.collectAsState()
    val stickers by repository.stickers.collectAsState()
    val keyCustomizations by repository.individualKeyStyles.collectAsState()

    var keyboardMode by remember { mutableStateOf(KeyboardMode.ALPHA) }
    var isShifted by remember { mutableStateOf(false) }
    var isCapsLock by remember { mutableStateOf(false) }
    var currentComposingWord by remember { mutableStateOf("") }
    var showFontMenu by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (settings.hapticFeedback && vibrator != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(20)
                }
            } catch (_: Exception) {}
        }
    }

    fun handleCharInput(rawChar: String) {
        triggerHaptic()
        val char = if (isShifted || isCapsLock) rawChar.uppercase() else rawChar.lowercase()
        val styled = if (settings.activeFancyFontId != "normal") {
            UnicodeFontEngine.applyStyle(char, settings.activeFancyFontId)
        } else {
            char
        }
        currentComposingWord += char
        onTextInput(styled)
        if (isShifted && !isCapsLock) {
            isShifted = false
        }
    }

    fun handleCustomWordCommit(word: String) {
        triggerHaptic()
        // Replace currently composing word with selected candidate
        if (currentComposingWord.isNotEmpty()) {
            for (i in 0 until currentComposingWord.length) {
                onDelete()
            }
        }
        val styled = if (settings.activeFancyFontId != "normal") {
            UnicodeFontEngine.applyStyle(word, settings.activeFancyFontId)
        } else {
            word
        }
        onTextInput(styled + " ")
        currentComposingWord = ""
    }

    val suggestions = remember(currentComposingWord, settings.activeLanguage, settings.autocorrectEnabled) {
        if (!settings.autocorrectEnabled || currentComposingWord.isEmpty()) {
            emptyList()
        } else {
            TransliterationEngine.getSuggestions(
                input = currentComposingWord,
                language = settings.activeLanguage
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(theme.bgSolidColor))
    ) {
        // Theme Background Image / Gradient
        if (theme.bgDrawableRes != null) {
            Image(
                painter = painterResource(id = theme.bgDrawableRes!!),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(1.dp)
            )
            // Overlay dimming
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(theme.bgSolidColor).copy(alpha = theme.bgOpacity))
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            theme.bgGradientColors.map { Color(it) }
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            // 1. TOP TOOLBAR
            KeyboardTopToolbar(
                theme = theme,
                activeFontId = settings.activeFancyFontId,
                activeLanguage = settings.activeLanguage,
                isTouchpadActive = keyboardMode == KeyboardMode.TOUCHPAD_HANDWRITING,
                onToggleTouchpad = {
                    triggerHaptic()
                    keyboardMode = if (keyboardMode == KeyboardMode.TOUCHPAD_HANDWRITING) KeyboardMode.ALPHA else KeyboardMode.TOUCHPAD_HANDWRITING
                },
                onToggleEmoji = {
                    triggerHaptic()
                    keyboardMode = if (keyboardMode == KeyboardMode.EMOJI) KeyboardMode.ALPHA else KeyboardMode.EMOJI
                },
                onToggleFonts = { showFontMenu = !showFontMenu },
                onToggleLanguage = { showLanguageMenu = !showLanguageMenu },
                onOpenStudio = onOpenStudio,
                onQuickThemeSwitch = {
                    val nextIdx = (ThemePresets.allThemes.indexOfFirst { it.id == theme.id } + 1) % ThemePresets.allThemes.size
                    repository.setTheme(ThemePresets.allThemes[nextIdx])
                }
            )

            // Popup Font Selector
            if (showFontMenu) {
                Surface(
                    color = Color(theme.toolbarBgColor),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(theme.accentColor)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cross-App Fancy Fonts",
                                color = Color(theme.accentColor),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showFontMenu = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UnicodeFontEngine.styles.forEach { style ->
                                val isSelected = settings.activeFancyFontId == style.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        repository.setActiveFancyFont(style.id)
                                        showFontMenu = false
                                    },
                                    label = {
                                        Text(
                                            text = style.sampleText.take(10),
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else Color(theme.primaryTextColor)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(theme.accentColor)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Popup Language Selector
            if (showLanguageMenu) {
                Surface(
                    color = Color(theme.toolbarBgColor),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(theme.accentColor)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Select Language & Transliteration",
                            color = Color(theme.accentColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("English (US)", "Hinglish", "Roman Urdu", "Hindi", "Urdu").forEach { lang ->
                                val isSelected = settings.activeLanguage == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        repository.setActiveLanguage(lang)
                                        showLanguageMenu = false
                                    },
                                    label = { Text(lang, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(theme.accentColor)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 2. SUGGESTION / CANDIDATE STRIP
            if (keyboardMode != KeyboardMode.TOUCHPAD_HANDWRITING && suggestions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(Color(theme.suggestionBgColor))
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    suggestions.forEach { candidate ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(theme.keyStyle.keyBgColor),
                            border = BorderStroke(0.8.dp, Color(theme.keyStyle.keyBorderColor).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable { handleCustomWordCommit(candidate) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = candidate,
                                color = Color(theme.primaryTextColor),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 3. KEYBOARD BODY (Modes: Alpha, Numeric, Emoji, Touchpad Handwriting)
            when (keyboardMode) {
                KeyboardMode.TOUCHPAD_HANDWRITING -> {
                    TouchpadHandwritingView(
                        theme = theme,
                        language = settings.activeLanguage,
                        onRecognizedText = { text ->
                            triggerHaptic()
                            if (text == "<BACKSPACE>") {
                                onDelete()
                            } else {
                                val styled = if (settings.activeFancyFontId != "normal") {
                                    UnicodeFontEngine.applyStyle(text, settings.activeFancyFontId)
                                } else text
                                onTextInput(styled)
                            }
                        },
                        onCloseTouchpad = { keyboardMode = KeyboardMode.ALPHA },
                        onSaveAsCustomGlyph = { strokes, w, h ->
                            val components = HandwritingRecognizer.strokesToGlyphComponents(strokes, w, h)
                            val custom = CustomGlyph(
                                id = "drawn_${System.currentTimeMillis()}",
                                name = "Touchpad Drawing",
                                mappedCharacter = "✍️",
                                components = components
                            )
                            repository.saveCustomGlyph(custom)
                        }
                    )
                }
                KeyboardMode.EMOJI -> {
                    EmojiStickerPalette(
                        theme = theme,
                        stickers = stickers,
                        customGlyphs = customGlyphs,
                        onEmojiSelected = { emoji ->
                            triggerHaptic()
                            onTextInput(emoji)
                        },
                        onStickerSelected = { sticker ->
                            triggerHaptic()
                            onStickerSelected(sticker)
                        },
                        onCustomGlyphSelected = { glyph ->
                            triggerHaptic()
                            onCustomGlyphSelected(glyph)
                        },
                        onBackToKeyboard = { keyboardMode = KeyboardMode.ALPHA }
                    )
                }
                KeyboardMode.NUMERIC, KeyboardMode.SYMBOLS -> {
                    NumericAndSymbolsLayout(
                        theme = theme,
                        isSymbols = keyboardMode == KeyboardMode.SYMBOLS,
                        onKey = { key ->
                            triggerHaptic()
                            onTextInput(key)
                        },
                        onDelete = {
                            triggerHaptic()
                            if (currentComposingWord.isNotEmpty()) {
                                currentComposingWord = currentComposingWord.dropLast(1)
                            }
                            onDelete()
                        },
                        onSpace = {
                            triggerHaptic()
                            currentComposingWord = ""
                            onSpace()
                        },
                        onEnter = {
                            triggerHaptic()
                            currentComposingWord = ""
                            onEnter()
                        },
                        onToggleMode = {
                            keyboardMode = if (keyboardMode == KeyboardMode.NUMERIC) KeyboardMode.SYMBOLS else KeyboardMode.NUMERIC
                        },
                        onBackToAlpha = { keyboardMode = KeyboardMode.ALPHA }
                    )
                }
                KeyboardMode.ALPHA -> {
                    AlphaKeyboardLayout(
                        theme = theme,
                        isShifted = isShifted || isCapsLock,
                        isCapsLock = isCapsLock,
                        activeLanguage = settings.activeLanguage,
                        customGlyphs = customGlyphs,
                        keyCustomizations = keyCustomizations,
                        onKey = { char -> handleCharInput(char) },
                        onShift = {
                            triggerHaptic()
                            if (isShifted && !isCapsLock) {
                                isCapsLock = true
                            } else if (isCapsLock) {
                                isCapsLock = false
                                isShifted = false
                            } else {
                                isShifted = true
                            }
                        },
                        onDelete = {
                            triggerHaptic()
                            if (currentComposingWord.isNotEmpty()) {
                                currentComposingWord = currentComposingWord.dropLast(1)
                            }
                            onDelete()
                        },
                        onSpace = {
                            triggerHaptic()
                            currentComposingWord = ""
                            onSpace()
                        },
                        onEnter = {
                            triggerHaptic()
                            currentComposingWord = ""
                            onEnter()
                        },
                        onSwitchToNumeric = {
                            triggerHaptic()
                            keyboardMode = KeyboardMode.NUMERIC
                        },
                        onSwitchToEmoji = {
                            triggerHaptic()
                            keyboardMode = KeyboardMode.EMOJI
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyboardTopToolbar(
    theme: KeyboardTheme,
    activeFontId: String,
    activeLanguage: String,
    isTouchpadActive: Boolean,
    onToggleTouchpad: () -> Unit,
    onToggleEmoji: () -> Unit,
    onToggleFonts: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenStudio: () -> Unit,
    onQuickThemeSwitch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color(theme.toolbarBgColor))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App / Studio launcher icon
        IconButton(
            onClick = onOpenStudio,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "ZYRON Studio",
                tint = Color(theme.accentColor),
                modifier = Modifier.size(20.dp)
            )
        }

        // Touchpad Handwriting button
        IconButton(
            onClick = onToggleTouchpad,
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isTouchpadActive) Color(theme.accentColor).copy(alpha = 0.3f) else Color.Transparent,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Gesture,
                contentDescription = "Touchpad Handwriting",
                tint = if (isTouchpadActive) Color(theme.accentColor) else Color(theme.secondaryTextColor),
                modifier = Modifier.size(20.dp)
            )
        }

        // Fancy Font transformer
        IconButton(
            onClick = onToggleFonts,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "Fonts",
                tint = if (activeFontId != "normal") Color(theme.accentColor) else Color(theme.secondaryTextColor),
                modifier = Modifier.size(20.dp)
            )
        }

        // Emoji & Stickers
        IconButton(
            onClick = onToggleEmoji,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mood,
                contentDescription = "Emojis & Stickers",
                tint = Color(theme.secondaryTextColor),
                modifier = Modifier.size(20.dp)
            )
        }

        // Theme Switcher
        IconButton(
            onClick = onQuickThemeSwitch,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Switch Theme",
                tint = Color(theme.secondaryTextColor),
                modifier = Modifier.size(20.dp)
            )
        }

        // Language indicator chip
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(theme.keyStyle.keyBgColor),
            border = BorderStroke(0.8.dp, Color(theme.accentColor).copy(alpha = 0.6f)),
            modifier = Modifier.clickable { onToggleLanguage() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color(theme.accentColor),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = activeLanguage.split(" ").first(),
                    fontSize = 11.sp,
                    color = Color(theme.primaryTextColor),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AlphaKeyboardLayout(
    theme: KeyboardTheme,
    isShifted: Boolean,
    isCapsLock: Boolean,
    activeLanguage: String,
    customGlyphs: List<CustomGlyph>,
    keyCustomizations: Map<String, KeyStyleConfig>,
    onKey: (String) -> Unit,
    onShift: () -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToNumeric: () -> Unit,
    onSwitchToEmoji: () -> Unit
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")
    val row1Hints = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // ROW 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row1.forEachIndexed { index, char ->
                val displayChar = if (isShifted) char.uppercase() else char
                val customGlyph = customGlyphs.find { it.mappedCharacter.equals(char, ignoreCase = true) && it.assignedToKey }
                val customStyle = keyCustomizations[char.uppercase()] ?: theme.keyStyle

                KeyView(
                    label = displayChar,
                    secondaryHint = row1Hints[index],
                    customGlyph = customGlyph,
                    keyStyle = customStyle,
                    theme = theme,
                    modifier = Modifier.weight(1f),
                    onClick = { onKey(char) }
                )
            }
        }

        // ROW 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row2.forEach { char ->
                val displayChar = if (isShifted) char.uppercase() else char
                val customGlyph = customGlyphs.find { it.mappedCharacter.equals(char, ignoreCase = true) && it.assignedToKey }
                val customStyle = keyCustomizations[char.uppercase()] ?: theme.keyStyle

                KeyView(
                    label = displayChar,
                    customGlyph = customGlyph,
                    keyStyle = customStyle,
                    theme = theme,
                    modifier = Modifier.weight(1f),
                    onClick = { onKey(char) }
                )
            }
        }

        // ROW 3: Shift, Z-M, Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key
            SpecialKeyView(
                icon = if (isCapsLock) Icons.Default.KeyboardCapslock else Icons.Default.ArrowUpward,
                isActive = isShifted || isCapsLock,
                activeColor = Color(theme.accentColor),
                theme = theme,
                modifier = Modifier.weight(1.4f),
                onClick = onShift
            )

            row3.forEach { char ->
                val displayChar = if (isShifted) char.uppercase() else char
                val customGlyph = customGlyphs.find { it.mappedCharacter.equals(char, ignoreCase = true) && it.assignedToKey }
                val customStyle = keyCustomizations[char.uppercase()] ?: theme.keyStyle

                KeyView(
                    label = displayChar,
                    customGlyph = customGlyph,
                    keyStyle = customStyle,
                    theme = theme,
                    modifier = Modifier.weight(1f),
                    onClick = { onKey(char) }
                )
            }

            // Backspace Key
            SpecialKeyView(
                icon = Icons.AutoMirrored.Filled.Backspace,
                theme = theme,
                modifier = Modifier.weight(1.4f),
                onClick = onDelete
            )
        }

        // ROW 4: ?123, Emoji, Space, Period, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ?123 switch
            SpecialKeyView(
                label = "?123",
                theme = theme,
                modifier = Modifier.weight(1.3f),
                onClick = onSwitchToNumeric
            )

            // Emoji / Sticker
            SpecialKeyView(
                icon = Icons.Default.SentimentSatisfiedAlt,
                theme = theme,
                modifier = Modifier.weight(1.1f),
                onClick = onSwitchToEmoji
            )

            // Spacebar with language indicator
            SpacebarView(
                theme = theme,
                language = activeLanguage,
                modifier = Modifier.weight(4.5f),
                onClick = onSpace
            )

            // Period Key
            KeyView(
                label = ".",
                keyStyle = theme.keyStyle,
                theme = theme,
                modifier = Modifier.weight(1.1f),
                onClick = { onKey(".") }
            )

            // Enter Key
            SpecialKeyView(
                icon = Icons.AutoMirrored.Filled.Send,
                isActive = true,
                activeColor = Color(theme.accentColor),
                theme = theme,
                modifier = Modifier.weight(1.4f),
                onClick = onEnter
            )
        }
    }
}

@Composable
private fun NumericAndSymbolsLayout(
    theme: KeyboardTheme,
    isSymbols: Boolean,
    onKey: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onToggleMode: () -> Unit,
    onBackToAlpha: () -> Unit
) {
    val row1 = if (!isSymbols) listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    else listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")

    val row2 = if (!isSymbols) listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    else listOf("£", "¢", "€", "¥", "^", "°", "=", "{", "}", "\\")

    val row3 = if (!isSymbols) listOf("*", "\"", "'", ":", ";", "!", "?")
    else listOf("%", "_", "<", ">", "[", "]", "©", "®")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            row1.forEach { char ->
                KeyView(label = char, keyStyle = theme.keyStyle, theme = theme, modifier = Modifier.weight(1f), onClick = { onKey(char) })
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            row2.forEach { char ->
                KeyView(label = char, keyStyle = theme.keyStyle, theme = theme, modifier = Modifier.weight(1f), onClick = { onKey(char) })
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            SpecialKeyView(
                label = if (isSymbols) "123" else "=/<",
                theme = theme,
                modifier = Modifier.weight(1.4f),
                onClick = onToggleMode
            )
            row3.forEach { char ->
                KeyView(label = char, keyStyle = theme.keyStyle, theme = theme, modifier = Modifier.weight(1f), onClick = { onKey(char) })
            }
            SpecialKeyView(
                icon = Icons.AutoMirrored.Filled.Backspace,
                theme = theme,
                modifier = Modifier.weight(1.4f),
                onClick = onDelete
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            SpecialKeyView(label = "ABC", theme = theme, modifier = Modifier.weight(1.4f), onClick = onBackToAlpha)
            SpacebarView(theme = theme, language = "Space", modifier = Modifier.weight(4.5f), onClick = onSpace)
            KeyView(label = ".", keyStyle = theme.keyStyle, theme = theme, modifier = Modifier.weight(1.1f), onClick = { onKey(".") })
            SpecialKeyView(icon = Icons.AutoMirrored.Filled.Send, isActive = true, activeColor = Color(theme.accentColor), theme = theme, modifier = Modifier.weight(1.4f), onClick = onEnter)
        }
    }
}

@Composable
fun KeyView(
    label: String,
    modifier: Modifier = Modifier,
    secondaryHint: String? = null,
    customGlyph: CustomGlyph? = null,
    keyStyle: KeyStyleConfig,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = when (keyStyle.keyShape) {
        KeyShapeType.ROUNDED_RECT -> RoundedCornerShape(keyStyle.cornerRadiusDp.dp)
        KeyShapeType.SQUIRCLE -> RoundedCornerShape(percent = 35)
        KeyShapeType.PILL -> RoundedCornerShape(percent = 50)
        KeyShapeType.CUT_CORNER -> androidx.compose.foundation.shape.CutCornerShape(8.dp)
        KeyShapeType.HEXAGON -> RoundedCornerShape(keyStyle.cornerRadiusDp.dp)
    }

    val glowAlpha = (theme.glowIntensity * 0.9f).coerceIn(0f, 1f)
    val bgColor = if (isPressed) Color(keyStyle.pressedColor) else Color(keyStyle.keyBgColor)

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = if (isPressed) 1.dp else keyStyle.glowRadiusDp.dp,
                shape = shape,
                ambientColor = Color(keyStyle.glowColor).copy(alpha = glowAlpha),
                spotColor = Color(keyStyle.glowColor).copy(alpha = glowAlpha)
            )
            .border(
                width = keyStyle.borderWidthDp.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(keyStyle.keyBorderColor),
                        Color(keyStyle.glowColor).copy(alpha = 0.5f)
                    )
                ),
                shape = shape
            )
            .background(bgColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Optional secondary hint top-right
        if (secondaryHint != null) {
            Text(
                text = secondaryHint,
                color = Color(keyStyle.labelSecondaryColor),
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp)
            )
        }

        // Custom Glyph Vector Render or standard text
        if (customGlyph != null) {
            Canvas(modifier = Modifier.size(24.dp)) {
                drawCustomGlyphPreview(customGlyph)
            }
        } else {
            Text(
                text = label,
                color = Color(keyStyle.labelColor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SpecialKeyView(
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isActive: Boolean = false,
    activeColor: Color? = null,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = RoundedCornerShape(theme.keyStyle.cornerRadiusDp.dp)
    val bg = if (isActive && activeColor != null) {
        activeColor.copy(alpha = if (isPressed) 0.6f else 0.85f)
    } else {
        if (isPressed) Color(theme.keyStyle.pressedColor) else Color(theme.keyStyle.keyBgSecondaryColor)
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = theme.keyStyle.borderWidthDp.dp,
                color = if (isActive && activeColor != null) activeColor else Color(theme.keyStyle.keyBorderColor).copy(alpha = 0.6f),
                shape = shape
            )
            .background(bg, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive && activeColor != null) Color.White else Color(theme.primaryTextColor),
                modifier = Modifier.size(20.dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                color = Color(theme.primaryTextColor),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SpacebarView(
    theme: KeyboardTheme,
    language: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(theme.keyStyle.cornerRadiusDp.dp)

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = 2.dp,
                shape = shape,
                spotColor = Color(theme.accentColor).copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = Color(theme.keyStyle.keyBorderColor),
                shape = shape
            )
            .background(
                if (isPressed) Color(theme.keyStyle.pressedColor) else Color(theme.keyStyle.keyBgColor),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = language,
            color = Color(theme.secondaryTextColor),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TouchpadHandwritingView(
    theme: KeyboardTheme,
    language: String,
    onRecognizedText: (String) -> Unit,
    onCloseTouchpad: () -> Unit,
    onSaveAsCustomGlyph: (List<Stroke>, Float, Float) -> Unit
) {
    val strokes = remember { mutableStateListOf<Stroke>() }
    var currentPoints by remember { mutableStateOf<List<TouchPoint>>(emptyList()) }
    var recognitionResult by remember { mutableStateOf<RecognitionResult?>(null) }
    var canvasSize by remember { mutableStateOf(Offset(1f, 1f)) }
    var modeIsHandwriting by remember { mutableStateOf(true) } // true: Handwriting, false: Draw Character

    fun runRecognition() {
        if (strokes.isNotEmpty()) {
            recognitionResult = HandwritingRecognizer.recognize(strokes.toList(), language)
        } else {
            recognitionResult = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color(theme.bgSolidColor))
            .padding(6.dp)
    ) {
        // Mode switch and action toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = modeIsHandwriting,
                    onClick = { modeIsHandwriting = true },
                    label = { Text("Handwriting", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Gesture, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(theme.accentColor))
                )
                FilterChip(
                    selected = !modeIsHandwriting,
                    onClick = { modeIsHandwriting = false },
                    label = { Text("Draw Character", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(theme.accentColor))
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Undo last stroke
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            strokes.removeAt(strokes.lastIndex)
                            runRecognition()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                // Clear canvas
                IconButton(
                    onClick = {
                        strokes.clear()
                        currentPoints = emptyList()
                        recognitionResult = null
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                }

                // Switch back to normal keyboard
                IconButton(
                    onClick = onCloseTouchpad,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Keyboard", tint = Color(theme.accentColor), modifier = Modifier.size(20.dp))
                }
            }
        }

        // TOUCHPAD DRAWING SURFACE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.2.dp, Color(theme.accentColor).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .background(Color(0xFF090C1A))
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

                // Optional writing guide lines
                val guideColor = Color(0x2238BDF8)
                drawLine(
                    color = guideColor,
                    start = Offset(0f, size.height * 0.5f),
                    end = Offset(size.width, size.height * 0.5f),
                    strokeWidth = 1f
                )

                // Render saved strokes
                for (stroke in strokes) {
                    drawHandwritingStroke(stroke.points, Color(0xFF00E5FF))
                }
                // Render ongoing stroke
                if (currentPoints.isNotEmpty()) {
                    drawHandwritingStroke(currentPoints, Color(0xFF80D8FF))
                }
            }

            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Text(
                    text = if (modeIsHandwriting) "Write with finger here (e.g. Hello, A, 1)..." else "Draw custom glyph/symbol with finger...",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // RECOGNITION CANDIDATES BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (modeIsHandwriting) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val candidates = recognitionResult?.candidates ?: emptyList()
                    if (candidates.isEmpty()) {
                        Text(
                            text = "Candidates will appear here",
                            color = Color(theme.secondaryTextColor),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    } else {
                        candidates.forEachIndexed { idx, candidate ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (idx == 0) Color(theme.accentColor) else Color(theme.keyStyle.keyBgColor),
                                border = BorderStroke(1.dp, Color(theme.accentColor)),
                                modifier = Modifier.clickable {
                                    onRecognizedText(candidate)
                                    strokes.clear()
                                    recognitionResult = null
                                }
                            ) {
                                Text(
                                    text = candidate,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Insert Button
                Button(
                    onClick = {
                        val word = recognitionResult?.primaryWord ?: ""
                        if (word.isNotEmpty()) {
                            onRecognizedText(word)
                            strokes.clear()
                            recognitionResult = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(theme.accentColor)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Insert", fontSize = 12.sp)
                }
            } else {
                // Mode 2: Save as Custom Character
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            onSaveAsCustomGlyph(strokes.toList(), canvasSize.x, canvasSize.y)
                            strokes.clear()
                            recognitionResult = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(theme.accentColor)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save to Character Lab", fontSize = 12.sp)
                }
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
        drawCircle(color, radius = 4f, center = Offset(points[0].x, points[0].y))
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
    drawPath(path, color = color, style = CanvasStroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun EmojiStickerPalette(
    theme: KeyboardTheme,
    stickers: List<StickerItem>,
    customGlyphs: List<CustomGlyph>,
    onEmojiSelected: (String) -> Unit,
    onStickerSelected: (StickerItem) -> Unit,
    onCustomGlyphSelected: (CustomGlyph) -> Unit,
    onBackToKeyboard: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Smileys", "Anime & Cyber", "Stickers", "Custom Glyphs")

    val smileys = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥹", "😊", "😇", "🙂", "🙃", "😉",
        "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐",
        "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣",
        "🔥", "⚡", "✨", "👑", "🚀", "💎", "⭐", "🎉", "💯", "🦾", "👾", "🤖", "⚔️", "🛡️"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color(theme.bgSolidColor))
    ) {
        // Tab header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(theme.toolbarBgColor),
            contentColor = Color(theme.accentColor)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(6.dp)
        ) {
            when (selectedTab) {
                0, 1 -> {
                    // Standard / Cyber Emojis
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(42.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(smileys.size) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onEmojiSelected(smileys[idx]) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(smileys[idx], fontSize = 22.sp)
                            }
                        }
                    }
                }
                2 -> {
                    // Stickers from Emoji Studio
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(72.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(stickers.size) { idx ->
                            val s = stickers[idx]
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF141A33),
                                border = BorderStroke(1.dp, Color(theme.accentColor).copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .size(72.dp)
                                    .clickable { onStickerSelected(s) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (s.imageDrawableRes != null) {
                                        Image(
                                            painter = painterResource(s.imageDrawableRes),
                                            contentDescription = s.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    if (s.textOverlay != null) {
                                        Text(
                                            text = s.textOverlay,
                                            color = Color(s.textColor),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Custom Glyphs from Character Lab
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(64.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customGlyphs.size) { idx ->
                            val g = customGlyphs[idx]
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10162B),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable { onCustomGlyphSelected(g) }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(36.dp)) {
                                        drawCustomGlyphPreview(g)
                                    }
                                    Text(g.name, fontSize = 8.sp, color = Color.White, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom return button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(Color(theme.toolbarBgColor))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBackToKeyboard,
                colors = ButtonDefaults.buttonColors(containerColor = Color(theme.keyStyle.keyBgColor)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ABC Keyboard", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCustomGlyphPreview(glyph: CustomGlyph) {
    val w = size.width
    val h = size.height

    for (comp in glyph.components.sortedBy { it.zIndex }) {
        if (!comp.isVisible) continue

        val strokeColor = Color(comp.strokeColor)
        val strokeWidthPx = comp.strokeWidth * (w / 100f).coerceAtLeast(1f)

        when (comp.type) {
            GlyphComponentType.LINE -> {
                if (comp.points.size >= 2) {
                    drawLine(
                        color = strokeColor,
                        start = Offset(comp.points[0].x * w, comp.points[0].y * h),
                        end = Offset(comp.points[1].x * w, comp.points[1].y * h),
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                }
            }
            GlyphComponentType.CURVE, GlyphComponentType.CROWN_SPIKE, GlyphComponentType.BOLT, GlyphComponentType.CUSTOM_PATH -> {
                if (comp.points.size >= 2) {
                    val path = Path()
                    path.moveTo(comp.points[0].x * w, comp.points[0].y * h)
                    for (i in 1 until comp.points.size) {
                        path.lineTo(comp.points[i].x * w, comp.points[i].y * h)
                    }
                    if (comp.fillColor != null) {
                        drawPath(path, color = Color(comp.fillColor))
                    }
                    drawPath(
                        path,
                        color = strokeColor,
                        style = CanvasStroke(width = strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
            else -> {
                if (comp.points.size >= 2) {
                    drawLine(
                        color = strokeColor,
                        start = Offset(comp.points[0].x * w, comp.points[0].y * h),
                        end = Offset(comp.points[1].x * w, comp.points[1].y * h),
                        strokeWidth = strokeWidthPx
                    )
                }
            }
        }
    }
}
