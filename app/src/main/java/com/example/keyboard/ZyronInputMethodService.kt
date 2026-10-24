package com.example.keyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.data.CustomGlyph
import com.example.data.StickerItem
import com.example.data.ZyronRepository
import com.example.keyboard.ui.ZyronKeyboardLayout

class ZyronInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var repository: ZyronRepository
    private var isPasswordField = false

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        repository = ZyronRepository.getInstance(applicationContext)
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this)
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        composeView.setContent {
            ZyronKeyboardLayout(
                repository = repository,
                onTextInput = { text ->
                    currentInputConnection?.commitText(text, 1)
                },
                onDelete = {
                    val ic = currentInputConnection
                    if (ic != null) {
                        val selected = ic.getSelectedText(0)
                        if (selected.isNullOrEmpty()) {
                            ic.deleteSurroundingText(1, 0)
                        } else {
                            ic.commitText("", 1)
                        }
                    }
                },
                onEnter = {
                    val ic = currentInputConnection
                    val info = currentInputEditorInfo
                    if (ic != null && info != null) {
                        val action = info.imeOptions and EditorInfo.IME_MASK_ACTION
                        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                            ic.performEditorAction(action)
                        } else {
                            ic.commitText("\n", 1)
                        }
                    }
                },
                onSpace = {
                    currentInputConnection?.commitText(" ", 1)
                },
                onStickerSelected = { sticker ->
                    handleStickerCommit(sticker)
                },
                onCustomGlyphSelected = { glyph ->
                    handleCustomGlyphCommit(glyph)
                },
                onOpenStudio = {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    }
                    startActivity(intent)
                }
            )
        }

        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        // Privacy Check: Disable suggestions / recording on sensitive fields
        if (info != null) {
            val variation = info.inputType and InputType.TYPE_MASK_VARIATION
            isPasswordField = variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                    info.inputType == InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    private fun handleStickerCommit(sticker: StickerItem) {
        val ic = currentInputConnection ?: return
        val info = currentInputEditorInfo ?: return

        // Support CommitContent API for rich media insertion into WhatsApp, Telegram, etc.
        val mimeTypes = EditorInfoCompat.getContentMimeTypes(info)
        var supported = false
        for (mime in mimeTypes) {
            if (mime.startsWith("image/")) {
                supported = true
                break
            }
        }

        if (supported && sticker.localUri != null) {
            try {
                val contentUri = Uri.parse(sticker.localUri)
                val description = android.content.ClipDescription(sticker.name, arrayOf("image/png"))
                val inputContentInfo = InputContentInfoCompat(contentUri, description, null)
                val flags = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
                val success = InputConnectionCompat.commitContent(ic, info, inputContentInfo, flags, null)
                if (success) return
            } catch (_: Exception) {}
        }

        // Fallback: Copy sticker name / text to clipboard or commit text
        val textToCommit = sticker.textOverlay ?: sticker.name
        ic.commitText("[$textToCommit]", 1)
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Sticker", textToCommit))
            Toast.makeText(this, "Sticker: $textToCommit copied", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {}
    }

    private fun handleCustomGlyphCommit(glyph: CustomGlyph) {
        val ic = currentInputConnection ?: return
        // Insert the mapped character
        ic.commitText(glyph.mappedCharacter, 1)
    }
}
