package com.example.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardTheme
import com.example.data.ThemePresets
import com.example.data.ZyronRepository

@Composable
fun ThemeStudioScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by repository.currentTheme.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Anime", "Cyberpunk", "Dark Premium", "Minimalist", "Space", "Nature")

    val filteredThemes = remember(selectedCategory) {
        if (selectedCategory == "All") ThemePresets.allThemes
        else ThemePresets.allThemes.filter { it.category == selectedCategory }
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
                Text("Theme Studio", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Theme Variants & Cyber Presets", color = Color(0xFF00E5FF), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        // Themes Grid (matching mockup: Sky Dream, Neon Red, Forest Green, Purple Night, Golden, Minimal Dark, Anime Style, Light Mode, Galaxy)
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredThemes.size) { idx ->
                val theme = filteredThemes[idx]
                val isSelected = currentTheme.id == theme.id

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(theme.bgSolidColor),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF26325C)
                    ),
                    modifier = Modifier
                        .height(150.dp)
                        .clickable {
                            repository.setTheme(theme)
                            Toast.makeText(context, "Applied theme: ${theme.name}", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (theme.bgDrawableRes != null) {
                            Image(
                                painter = painterResource(theme.bgDrawableRes!!),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                            )
                        }

                        // Selected checkmark badge
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(bottomStart = 8.dp),
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.Black,
                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                )
                            }
                        }

                        // Mini simulated keys row
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                listOf("Q", "W", "E", "R", "T").forEach { ch ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 24.dp, height = 28.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .border(1.dp, Color(theme.keyStyle.keyBorderColor), RoundedCornerShape(4.dp))
                                            .background(Color(theme.keyStyle.keyBgColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(ch, color = Color(theme.keyStyle.labelColor), fontSize = 10.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                listOf("A", "S", "D", "F").forEach { ch ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 24.dp, height = 28.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .border(1.dp, Color(theme.keyStyle.keyBorderColor), RoundedCornerShape(4.dp))
                                            .background(Color(theme.keyStyle.keyBgColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(ch, color = Color(theme.keyStyle.labelColor), fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        // Bottom Theme Info
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = theme.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = theme.category,
                                color = Color(theme.accentColor),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
