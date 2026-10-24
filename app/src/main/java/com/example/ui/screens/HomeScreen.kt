package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ZyronRepository
import com.example.keyboard.ui.ZyronKeyboardLayout

@Composable
fun HomeScreen(
    repository: ZyronRepository,
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val currentTheme by repository.currentTheme.collectAsState()
    var testText by remember { mutableStateOf("") }

    // Check if keyboard is enabled/selected
    fun isKeyboardEnabled(): Boolean {
        return try {
            val enabledMethods = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_INPUT_METHODS
            ) ?: ""
            enabledMethods.contains(context.packageName)
        } catch (_: Exception) {
            false
        }
    }

    val isEnabled = remember { isKeyboardEnabled() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. TOP HEADER (Avatar, ZYRON Logo, Search)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Avatar with neon cyan ring
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF00E5FF), CircleShape)
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_anime_hero),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // ZYRON Neon Crown Logo Text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ZYRON",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            IconButton(
                onClick = { onNavigateTo("marketplace") },
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF0E142B), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. HERO BANNER: "Design Your Own Keyboard - Limitless Customization"
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0E142B),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onNavigateTo("keyboard_studio") }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Banner Image
                Image(
                    painter = painterResource(R.drawable.bg_anime_cyber),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.88f),
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Hero Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Design\nYour Own\nKeyboard",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Limitless Customization →",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Circle Arrow Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. ENABLE KEYBOARD WIZARD CARD
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0D1226),
            border = BorderStroke(1.2.dp, Color(0xFF3B82F6).copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable ZYRON Keyboard",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isEnabled) Color(0x3310B981) else Color(0x33F59E0B)
                    ) {
                        Text(
                            text = if (isEnabled) "Ready to Use" else "Setup Needed",
                            color = if (isEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Steps: 1. Go to Settings, 2. Select Languages & Input, 3. Choose ZYRON
                StepItem(step = "1", title = "Go to Settings", desc = "Open Android System Settings")
                Spacer(modifier = Modifier.height(6.dp))
                StepItem(step = "2", title = "Select Languages & Input", desc = "Tap on Manage On-Screen Keyboards")
                Spacer(modifier = Modifier.height(6.dp))
                StepItem(step = "3", title = "Choose ZYRON Keyboard", desc = "Enable toggle & select as active IME")

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Open Settings button
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Settings", fontSize = 13.sp)
                    }

                    // Switch IME button
                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Switch Keyboard", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security Badges: Safe, Private, No Tracking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SecurityBadge(icon = Icons.Default.Shield, label = "Safe")
                    SecurityBadge(icon = Icons.Default.Lock, label = "Private")
                    SecurityBadge(icon = Icons.Default.VisibilityOff, label = "No Tracking")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. QUICK TOOLS GRID (Matching Mockup exactly!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Tools",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See All >",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateTo("marketplace") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2 columns grid of 6 tools
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolCard(
                    title = "Keyboard Studio",
                    desc = "Customize Keys",
                    icon = Icons.Default.Keyboard,
                    accentColor = 0xFF6366F1,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("keyboard_studio") }
                )
                QuickToolCard(
                    title = "Character Lab",
                    desc = "Edit & Merge",
                    icon = Icons.Default.FontDownload,
                    accentColor = 0xFFFF1744,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("character_lab") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolCard(
                    title = "Touchpad Typing",
                    desc = "Handwriting Input",
                    icon = Icons.Default.Gesture,
                    accentColor = 0xFF00E5FF,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("touchpad") }
                )
                QuickToolCard(
                    title = "Font Library",
                    desc = "1000+ Fonts",
                    icon = Icons.Default.FormatSize,
                    accentColor = 0xFF38BDF8,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("fonts") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolCard(
                    title = "Emoji Studio",
                    desc = "Create Emojis",
                    icon = Icons.Default.Mood,
                    accentColor = 0xFFF59E0B,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("emojis") }
                )
                QuickToolCard(
                    title = "Theme Studio",
                    desc = "Build Themes",
                    icon = Icons.Default.Palette,
                    accentColor = 0xFFA855F7,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("themes") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. TEST TYPING SANDBOX & INTERACTIVE KEYBOARD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Interactive Test Keyboard",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clear Text",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { testText = "" }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Test Text Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F142A),
                border = BorderStroke(1.dp, Color(0xFF26325C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    if (testText.isEmpty()) {
                        Text(
                            text = "Tap on the keys below to test type, or switch to touchpad...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = testText,
                            color = Color(0xFF00E5FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Keyboard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.2.dp, Color(currentTheme.accentColor).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                ZyronKeyboardLayout(
                    repository = repository,
                    onTextInput = { testText += it },
                    onDelete = {
                        if (testText.isNotEmpty()) {
                            testText = testText.dropLast(1)
                        }
                    },
                    onEnter = { testText += "\n" },
                    onSpace = { testText += " " },
                    onStickerSelected = { testText += " [${it.textOverlay ?: it.name}] " },
                    onCustomGlyphSelected = { testText += it.mappedCharacter },
                    onOpenStudio = { onNavigateTo("keyboard_studio") }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun StepItem(step: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E2850),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(step, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SecurityBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}

@Composable
private fun QuickToolCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Long,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0D1226),
        border = BorderStroke(1.dp, Color(0xFF1E2850)),
        modifier = modifier
            .height(82.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(accentColor).copy(alpha = 0.2f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color(accentColor), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}
