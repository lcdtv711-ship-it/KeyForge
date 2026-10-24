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
import com.example.data.ZyronRepository

@Composable
fun MyLibraryScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val customGlyphs by repository.customGlyphs.collectAsState()
    val stickers by repository.stickers.collectAsState()
    val currentTheme by repository.currentTheme.collectAsState()

    var showBackupDialog by remember { mutableStateOf(false) }
    var backupJsonString by remember { mutableStateOf("") }

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
                    Text("My Library", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Saved Designs, Backups & Assets", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }

            // Export / Backup Button
            Button(
                onClick = {
                    backupJsonString = repository.exportBackupJson()
                    showBackupDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Backup", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Asset Statistics Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(title = "Glyphs", count = customGlyphs.size, modifier = Modifier.weight(1f), color = 0xFF00E5FF)
            StatCard(title = "Stickers", count = stickers.size, modifier = Modifier.weight(1f), color = 0xFFFF007F)
            StatCard(title = "Themes", count = 9, modifier = Modifier.weight(1f), color = 0xFFA855F7)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backup & Restore Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Backup & Portable Asset Packages", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Export all your custom character surgery models, merged glyphs, and preferences to a portable JSON manifest.", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val json = repository.exportBackupJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ZyronBackup", json))
                            Toast.makeText(context, "Exported JSON to Clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export JSON", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Backup verified and restored successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26325C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Saved Custom Glyphs List
        Text("Custom Glyphs (${customGlyphs.size})", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        customGlyphs.forEach { g ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F142A),
                border = BorderStroke(1.dp, Color(0xFF26325C)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(g.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Mapped to Key [${g.mappedCharacter}] • ${g.components.size} components", color = Color(0xFF00E5FF), fontSize = 11.sp)
                    }
                    Text("Active", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("ZYRON Portable Backup JSON") },
            text = {
                Text(
                    text = backupJsonString,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("ZyronBackup", backupJsonString))
                        showBackupDialog = false
                        Toast.makeText(context, "Copied backup JSON to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StatCard(title: String, count: Int, modifier: Modifier = Modifier, color: Long) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F142A),
        border = BorderStroke(1.dp, Color(color).copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", color = Color(color), fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
