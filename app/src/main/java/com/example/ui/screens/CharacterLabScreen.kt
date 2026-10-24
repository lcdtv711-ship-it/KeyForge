package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.keyboard.ui.drawCustomGlyphPreview

@Composable
fun CharacterLabScreen(
    repository: ZyronRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val customGlyphs by repository.customGlyphs.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Edit (Surgery), 1: Merge Engine, 2: Glyph Library

    // Active editing glyph state
    var activeGlyph by remember { mutableStateOf(GlyphPresets.getInitialGlyphFor("A")) }
    var selectedComponentId by remember { mutableStateOf<String?>(null) }
    var activeTool by remember { mutableStateOf("select") } // "select", "brush", "eraser", "shape", "text"

    // Styling states
    var selectedStrokeColor by remember { mutableStateOf(0xFFFF1744) }
    var selectedGlowColor by remember { mutableStateOf(0xFFFF0055) }
    var strokeThickness by remember { mutableFloatStateOf(6f) }
    var glowRadius by remember { mutableFloatStateOf(8f) }
    var glowEnabled by remember { mutableStateOf(true) }

    // Merge Tool states
    var mergeBaseChar by remember { mutableStateOf("A") }
    var mergeSourceChar by remember { mutableStateOf("B") }
    var selectedSourceCompIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913))
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Character Lab",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Surgery & Merge Engine",
                        color = Color(0xFF00E5FF),
                        fontSize = 12.sp
                    )
                }
            }

            // Save button
            Button(
                onClick = {
                    repository.saveCustomGlyph(activeGlyph)
                    Toast.makeText(context, "Saved ${activeGlyph.name} to Library & Keyboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save", fontSize = 13.sp)
            }
        }

        // Mode Navigation Tabs: Edit, Merge, Library
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0D1127),
            contentColor = Color(0xFF00E5FF)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Edit (Surgery)", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Merge Engine", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Glyph Library", fontWeight = FontWeight.SemiBold) }
            )
        }

        when (selectedTab) {
            0 -> {
                // SURGERY CANVAS & LAYERS
                CharacterSurgeryContent(
                    activeGlyph = activeGlyph,
                    selectedComponentId = selectedComponentId,
                    activeTool = activeTool,
                    selectedStrokeColor = selectedStrokeColor,
                    glowEnabled = glowEnabled,
                    strokeThickness = strokeThickness,
                    onToolSelected = { activeTool = it },
                    onSelectComponent = { selectedComponentId = it },
                    onDeleteComponent = { id ->
                        activeGlyph = activeGlyph.copy(
                            components = activeGlyph.components.filterNot { it.id == id }
                        )
                        if (selectedComponentId == id) selectedComponentId = null
                        Toast.makeText(context, "Stroke deleted", Toast.LENGTH_SHORT).show()
                    },
                    onToggleVisibility = { id ->
                        activeGlyph = activeGlyph.copy(
                            components = activeGlyph.components.map {
                                if (it.id == id) it.copy(isVisible = !it.isVisible) else it
                            }
                        )
                    },
                    onColorChanged = { color ->
                        selectedStrokeColor = color
                        if (selectedComponentId != null) {
                            activeGlyph = activeGlyph.copy(
                                components = activeGlyph.components.map {
                                    if (it.id == selectedComponentId) it.copy(strokeColor = color, glowColor = color) else it
                                }
                            )
                        }
                    },
                    onThicknessChanged = { thick ->
                        strokeThickness = thick
                        if (selectedComponentId != null) {
                            activeGlyph = activeGlyph.copy(
                                components = activeGlyph.components.map {
                                    if (it.id == selectedComponentId) it.copy(strokeWidth = thick) else it
                                }
                            )
                        }
                    },
                    onGlowToggle = { enabled ->
                        glowEnabled = enabled
                        if (selectedComponentId != null) {
                            activeGlyph = activeGlyph.copy(
                                components = activeGlyph.components.map {
                                    if (it.id == selectedComponentId) it.copy(glowRadius = if (enabled) 8f else 0f) else it
                                }
                            )
                        }
                    },
                    onAddShape = { shapeType ->
                        val newComp = when (shapeType) {
                            "crown" -> GlyphComponent(
                                id = "comp_crown_${System.currentTimeMillis()}",
                                name = "Royal Crown",
                                type = GlyphComponentType.CROWN_SPIKE,
                                points = listOf(
                                    GlyphPoint(0.35f, 0.15f), GlyphPoint(0.40f, 0.22f),
                                    GlyphPoint(0.50f, 0.10f), GlyphPoint(0.60f, 0.22f),
                                    GlyphPoint(0.65f, 0.15f), GlyphPoint(0.62f, 0.25f),
                                    GlyphPoint(0.38f, 0.25f)
                                ),
                                strokeColor = 0xFFFF0055,
                                fillColor = 0x55FF0055,
                                strokeWidth = 3f,
                                glowColor = 0xFFFF0055,
                                glowRadius = 10f
                            )
                            "bolt" -> GlyphComponent(
                                id = "comp_bolt_${System.currentTimeMillis()}",
                                name = "Electric Bolt",
                                type = GlyphComponentType.BOLT,
                                points = listOf(
                                    GlyphPoint(0.52f, 0.40f), GlyphPoint(0.44f, 0.52f),
                                    GlyphPoint(0.56f, 0.52f), GlyphPoint(0.48f, 0.65f)
                                ),
                                strokeColor = 0xFF00E5FF,
                                strokeWidth = 4f,
                                glowColor = 0xFF00E5FF,
                                glowRadius = 10f
                            )
                            else -> GlyphComponent(
                                id = "comp_cross_${System.currentTimeMillis()}",
                                name = "Cyber Bar",
                                type = GlyphComponentType.LINE,
                                points = listOf(GlyphPoint(0.20f, 0.50f), GlyphPoint(0.80f, 0.50f)),
                                strokeColor = 0xFF00E5FF,
                                strokeWidth = 6f
                            )
                        }
                        activeGlyph = activeGlyph.copy(components = activeGlyph.components + newComp)
                        selectedComponentId = newComp.id
                    },
                    onSelectPresetGlyph = { char ->
                        activeGlyph = GlyphPresets.getInitialGlyphFor(char)
                        selectedComponentId = null
                    }
                )
            }
            1 -> {
                // CHARACTER MERGE ENGINE
                CharacterMergeContent(
                    baseChar = mergeBaseChar,
                    sourceChar = mergeSourceChar,
                    selectedSourceCompIndex = selectedSourceCompIndex,
                    onBaseCharChange = { mergeBaseChar = it },
                    onSourceCharChange = { mergeSourceChar = it },
                    onSelectSourceComponent = { selectedSourceCompIndex = it },
                    onMergeComponents = {
                        val basePreset = GlyphPresets.getInitialGlyphFor(mergeBaseChar)
                        val sourcePreset = GlyphPresets.getInitialGlyphFor(mergeSourceChar)
                        val compToMerge = sourcePreset.components.getOrNull(selectedSourceCompIndex)

                        if (compToMerge != null) {
                            val mergedComp = compToMerge.copy(
                                id = "merged_${System.currentTimeMillis()}",
                                name = "Merged ${compToMerge.name} from $mergeSourceChar",
                                zIndex = basePreset.components.size + 1
                            )
                            val mergedGlyph = CustomGlyph(
                                id = "merged_${mergeBaseChar}_${mergeSourceChar}_${System.currentTimeMillis()}",
                                name = "Fusion ${mergeBaseChar}+$mergeSourceChar",
                                mappedCharacter = mergeBaseChar,
                                components = basePreset.components + mergedComp
                            )
                            activeGlyph = mergedGlyph
                            repository.saveCustomGlyph(mergedGlyph)
                            selectedTab = 0 // Switch to surgery editor
                            Toast.makeText(context, "Merged component from $mergeSourceChar into $mergeBaseChar!", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
            2 -> {
                // GLYPH LIBRARY
                GlyphLibraryContent(
                    customGlyphs = customGlyphs,
                    onSelectGlyph = { glyph ->
                        activeGlyph = glyph
                        selectedTab = 0
                    },
                    onDeleteGlyph = { id ->
                        repository.deleteCustomGlyph(id)
                        Toast.makeText(context, "Deleted glyph", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun CharacterSurgeryContent(
    activeGlyph: CustomGlyph,
    selectedComponentId: String?,
    activeTool: String,
    selectedStrokeColor: Long,
    glowEnabled: Boolean,
    strokeThickness: Float,
    onToolSelected: (String) -> Unit,
    onSelectComponent: (String) -> Unit,
    onDeleteComponent: (String) -> Unit,
    onToggleVisibility: (String) -> Unit,
    onColorChanged: (Long) -> Unit,
    onThicknessChanged: (Float) -> Unit,
    onGlowToggle: (Boolean) -> Unit,
    onAddShape: (String) -> Unit,
    onSelectPresetGlyph: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Quick Character Switcher Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("A", "B", "Z", "C", "X", "M", "S", "1", "7").forEach { char ->
                val isSelected = activeGlyph.mappedCharacter.equals(char, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPresetGlyph(char) },
                    label = { Text("Letter $char", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1))
                )
            }
        }

        // MAIN CANVAS ROW: Toolbar + Canvas + Layers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Tools Sidebar (Select, Brush, Eraser, Shape, Text)
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF0F142A), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolButton(icon = Icons.Default.NearMe, label = "Select", isSelected = activeTool == "select") {
                    onToolSelected("select")
                }
                ToolButton(icon = Icons.Default.Brush, label = "Brush", isSelected = activeTool == "brush") {
                    onToolSelected("brush")
                }
                ToolButton(icon = Icons.Default.AutoFixNormal, label = "Eraser", isSelected = activeTool == "eraser") {
                    onToolSelected("eraser")
                    if (selectedComponentId != null) {
                        onDeleteComponent(selectedComponentId)
                    }
                }
                ToolButton(icon = Icons.Default.Category, label = "Shape", isSelected = activeTool == "shape") {
                    onToolSelected("shape")
                    onAddShape("crown")
                }
                ToolButton(icon = Icons.Default.ElectricBolt, label = "Bolt", isSelected = activeTool == "bolt") {
                    onToolSelected("bolt")
                    onAddShape("bolt")
                }
            }

            // 2. Central Vector Canvas
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.2.dp, Color(0xFF26325C), RoundedCornerShape(14.dp))
                    .background(Color(0xFF080B18))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Tap on canvas selects nearest component
                            val comp = activeGlyph.components.firstOrNull()
                            if (comp != null) onSelectComponent(comp.id)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(240.dp)
                        .padding(16.dp)
                ) {
                    drawCustomGlyphPreview(activeGlyph)
                }

                // Center glowing label hint
                Text(
                    text = activeGlyph.name,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }

            // 3. Layers Hierarchy Panel
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0F142A), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Layers", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${activeGlyph.components.size}", color = Color.Gray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    activeGlyph.components.forEach { comp ->
                        val isSelected = comp.id == selectedComponentId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF222B52) else Color(0xFF131934),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color(0x3300E5FF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectComponent(comp.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(comp.strokeColor))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = comp.name,
                                        color = if (comp.isVisible) Color.White else Color.Gray,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }

                                Row {
                                    // Visibility toggle
                                    IconButton(
                                        onClick = { onToggleVisibility(comp.id) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (comp.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = if (comp.isVisible) Color.White else Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    // Delete component
                                    IconButton(
                                        onClick = { onDeleteComponent(comp.id) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Stroke",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // STROKE CONTROLS & COLOR SWATCHES
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F142A),
            border = BorderStroke(1.dp, Color(0xFF26325C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Stroke & Glow Styling",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Swatches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Color Palette", color = Color.Gray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            0xFFFF1744, 0xFF00E5FF, 0xFFA855F7, 0xFFF59E0B,
                            0xFF10B981, 0xFFFFFFFF, 0xFFFF007F
                        ).forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(col))
                                    .border(
                                        width = if (selectedStrokeColor == col) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { onColorChanged(col) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stroke Width Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Stroke Thickness (${strokeThickness.toInt()}px)", color = Color.Gray, fontSize = 11.sp)
                    Slider(
                        value = strokeThickness,
                        onValueChange = onThicknessChanged,
                        valueRange = 2f..14f,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF)
                        )
                    )
                }

                // Glow Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Neon Cyber Glow", color = Color.Gray, fontSize = 11.sp)
                    Switch(
                        checked = glowEnabled,
                        onCheckedChange = onGlowToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(
                if (isSelected) Color(0xFF6366F1) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun CharacterMergeContent(
    baseChar: String,
    sourceChar: String,
    selectedSourceCompIndex: Int,
    onBaseCharChange: (String) -> Unit,
    onSourceCharChange: (String) -> Unit,
    onSelectSourceComponent: (Int) -> Unit,
    onMergeComponents: () -> Unit
) {
    val baseGlyph = GlyphPresets.getInitialGlyphFor(baseChar)
    val sourceGlyph = GlyphPresets.getInitialGlyphFor(sourceChar)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Text(
            text = "Character Fusion & Component Merge",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Extract vector components from one glyph (e.g. curved loop of B or crown) and merge into another (e.g. A) to create a completely new hybrid design.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Side by side selection cards: Target + Source
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Target Base Character Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F142A),
                border = BorderStroke(1.dp, Color(0xFF6366F1)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Target Base", color = Color(0xFF6366F1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Canvas(modifier = Modifier.size(90.dp).padding(6.dp)) {
                        drawCustomGlyphPreview(baseGlyph)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("A", "Z", "X", "1").forEach { ch ->
                            FilterChip(
                                selected = baseChar == ch,
                                onClick = { onBaseCharChange(ch) },
                                label = { Text(ch, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Source Donor Character Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F142A),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Donor Character", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Canvas(modifier = Modifier.size(90.dp).padding(6.dp)) {
                        drawCustomGlyphPreview(sourceGlyph)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("B", "Z", "A", "C").forEach { ch ->
                            FilterChip(
                                selected = sourceChar == ch,
                                onClick = { onSourceCharChange(ch) },
                                label = { Text(ch, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Component picker from Source
        Text(
            text = "Select Component from '$sourceChar' to extract:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            sourceGlyph.components.forEachIndexed { idx, comp ->
                val isSelected = idx == selectedSourceCompIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF26325C) else Color(0xFF0F142A),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color(0x3300E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSourceComponent(idx) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(comp.name, color = Color.White, fontSize = 12.sp)
                        Text(comp.type.name, color = Color(0xFF00E5FF), fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Merge Action Button
        Button(
            onClick = onMergeComponents,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CallMerge, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Merge Into '$baseChar' and Open Surgery Editor", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GlyphLibraryContent(
    customGlyphs: List<CustomGlyph>,
    onSelectGlyph: (CustomGlyph) -> Unit,
    onDeleteGlyph: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = "Saved Glyphs & Characters (${customGlyphs.size})",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(customGlyphs.size) { idx ->
                val glyph = customGlyphs[idx]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F142A),
                    border = BorderStroke(1.dp, Color(0xFF26325C)),
                    modifier = Modifier
                        .height(130.dp)
                        .clickable { onSelectGlyph(glyph) }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Canvas(modifier = Modifier.size(54.dp)) {
                            drawCustomGlyphPreview(glyph)
                        }
                        Text(
                            text = glyph.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Key [${glyph.mappedCharacter}]",
                                color = Color(0xFF00E5FF),
                                fontSize = 9.sp
                            )
                            IconButton(
                                onClick = { onDeleteGlyph(glyph.id) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
