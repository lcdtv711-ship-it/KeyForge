package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UnicodeFontEngine
import com.example.data.ZyronRepository

@Composable
fun FontLibraryScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by repository.typingSettings.collectAsState()

    var testInputText by remember { mutableStateOf("ZYRON KEYBOARD") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Modern", "Handwriting", "Gothic", "Futuristic", "Gaming", "Rounded", "Decorative")

    val filteredStyles = remember(selectedCategory) {
        if (selectedCategory == "All") UnicodeFontEngine.styles
        else UnicodeFontEngine.styles.filter { it.category == selectedCategory }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text("Font Library", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("1000+ Fonts & Cross-App Unicode Styles", color = Color(0xFF00E5FF), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Live Text Transformer Sandbox
        OutlinedTextField(
            value = testInputText,
            onValueChange = { testInputText = it },
            label = { Text("Type any text to preview fonts:") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color(0xFF26325C),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF0F142A),
                unfocusedContainerColor = Color(0xFF0F142A)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Font Styles List
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredStyles.size) { idx ->
                val style = filteredStyles[idx]
                val isCurrentActive = settings.activeFancyFontId == style.id
                val transformed = style.transformFunction(testInputText)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F142A),
                    border = BorderStroke(
                        width = if (isCurrentActive) 1.5.dp else 1.dp,
                        color = if (isCurrentActive) Color(0xFF00E5FF) else Color(0xFF26325C)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(style.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(style.category, color = Color(0xFF00E5FF), fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Copy transformed text
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("FancyFont", transformed))
                                        Toast.makeText(context, "Copied styled text!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                }

                                // Apply to Keyboard Output
                                Button(
                                    onClick = {
                                        repository.setActiveFancyFont(style.id)
                                        Toast.makeText(context, "Active typing style: ${style.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrentActive) Color(0xFF10B981) else Color(0xFF6366F1)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(if (isCurrentActive) "Active" else "Use on Keyboard", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Transformed Sample Output Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF070A18),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transformed,
                                color = if (isCurrentActive) Color(0xFF00E5FF) else Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
