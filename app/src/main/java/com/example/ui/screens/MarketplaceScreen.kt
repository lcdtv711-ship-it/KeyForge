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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GlyphPresets
import com.example.data.MarketplaceItem
import com.example.data.ZyronRepository

@Composable
fun MarketplaceScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val installedIds by repository.installedMarketplaceIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Themes", "Character Packs", "Stickers", "Fonts")

    val filteredItems = remember(searchQuery, selectedCategory) {
        GlyphPresets.marketplaceShowcase.filter { item ->
            (selectedCategory == "All" || item.category == selectedCategory) &&
                    (searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true) || item.tags.any { it.contains(searchQuery, ignoreCase = true) })
        }
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
                Text("Marketplace", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Themes, Character Packs, Fonts & Stickers", color = Color(0xFF00E5FF), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search community creations, cyber themes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color(0xFF26325C),
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

        Spacer(modifier = Modifier.height(14.dp))

        // Marketplace Items List
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems.size) { idx ->
                val item = filteredItems[idx]
                val isInstalled = installedIds.contains(item.id)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F142A),
                    border = BorderStroke(1.dp, Color(0xFF26325C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("by ${item.creator} • ${item.category}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${item.rating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.description, color = Color(0xFF94A3B8), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags & Action button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                item.tags.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF161F3D)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            color = Color(0xFF818CF8),
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    repository.installMarketplaceItem(item.id)
                                    Toast.makeText(context, "Installed ${item.title} to your keyboard!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isInstalled) Color(0xFF10B981) else Color(0xFF6366F1)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isInstalled) Icons.Default.Check else Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isInstalled) "Applied" else "Get Free", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
