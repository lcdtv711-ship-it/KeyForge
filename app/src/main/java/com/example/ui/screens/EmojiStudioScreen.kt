package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.R
import com.example.data.StickerItem
import com.example.data.ZyronRepository

@Composable
fun EmojiStudioScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val stickers by repository.stickers.collectAsState()

    var stickerText by remember { mutableStateOf("HUNTER") }
    var selectedTextColor by remember { mutableStateOf(0xFF00E5FF) }
    var selectedDrawableRes by remember { mutableIntStateOf(R.drawable.avatar_anime_hero) }
    var stickerPackName by remember { mutableStateOf("Cyber Hunters") }
    var isCircularCut by remember { mutableStateOf(true) }

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
                    Text("Emoji & Sticker Studio", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Photo-to-Emoji & Custom Badges", color = Color(0xFF00E5FF), fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    val newSticker = StickerItem(
                        id = "sticker_${System.currentTimeMillis()}",
                        packName = stickerPackName,
                        name = "Custom $stickerText",
                        imageDrawableRes = selectedDrawableRes,
                        textOverlay = stickerText,
                        textColor = selectedTextColor,
                        textGlowColor = selectedTextColor
                    )
                    repository.addSticker(newSticker)
                    Toast.makeText(context, "Added to Keyboard Stickers!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE STICKER PREVIEW CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0E1328))
                .border(1.2.dp, Color(0xFF26325C), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Cutout sticker shape
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(if (isCircularCut) CircleShape else RoundedCornerShape(20.dp))
                    .border(3.dp, Color(selectedTextColor), if (isCircularCut) CircleShape else RoundedCornerShape(20.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(selectedDrawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Text Overlay
                if (stickerText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stickerText,
                            color = Color(selectedTextColor),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // STICKER EDITING CONTROLS
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Sticker Text & Badge Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stickerText,
                    onValueChange = { stickerText = it.take(15) },
                    label = { Text("Badge Text Overlay") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Source Image Chooser
                Text("Choose Base Portrait / Art:", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        R.drawable.avatar_anime_hero to "Anime Hero",
                        R.drawable.zyron_logo to "Crown Z",
                        R.drawable.bg_anime_cyber to "Cyber City"
                    ).forEach { (res, label) ->
                        val isSelected = selectedDrawableRes == res
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black,
                            border = BorderStroke(2.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
                            modifier = Modifier
                                .size(64.dp)
                                .clickable { selectedDrawableRes = res }
                        ) {
                            Image(
                                painter = painterResource(res),
                                contentDescription = label,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cutout Shape: Circular vs Rounded Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Circular Badge Cutout", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isCircularCut,
                        onCheckedChange = { isCircularCut = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Text Color Palette
                Text("Text & Border Glow Color:", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        0xFF00E5FF, 0xFFFF007F, 0xFFFF1744, 0xFFA855F7,
                        0xFFF59E0B, 0xFF10B981, 0xFFFFFFFF
                    ).forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .border(
                                    width = if (selectedTextColor == col) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedTextColor = col }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Existing Stickers in Keyboard
        Text(
            text = "Active Stickers in Keyboard (${stickers.size})",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stickers.forEach { s ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F142A),
                    border = BorderStroke(1.dp, Color(0xFF26325C)),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (s.imageDrawableRes != null) {
                            Image(
                                painter = painterResource(s.imageDrawableRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        if (s.textOverlay != null) {
                            Text(
                                text = s.textOverlay,
                                color = Color(s.textColor),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
