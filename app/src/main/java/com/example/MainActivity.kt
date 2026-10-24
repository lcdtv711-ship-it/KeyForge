package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ZyronRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ZyronRepository.getInstance(applicationContext)

        setContent {
            MyApplicationTheme {
                MainAppScaffold(repository = repository)
            }
        }
    }
}

@Composable
fun MainAppScaffold(repository: ZyronRepository) {
    // Navigation state stack
    var screenStack by remember { mutableStateOf(listOf("home")) }
    val currentScreen = screenStack.lastOrNull() ?: "home"

    fun navigateTo(screen: String) {
        if (screen == "home") {
            screenStack = listOf("home")
        } else {
            screenStack = screenStack + screen
        }
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    // Handle system back button
    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    val isTopLevelScreen = currentScreen in listOf("home", "keyboard_studio", "my_library", "marketplace", "settings")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF070913),
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    containerColor = Color(0xFF0D1127),
                    contentColor = Color(0xFF00E5FF),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { screenStack = listOf("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = if (currentScreen == "home") FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            selectedTextColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E2850)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "keyboard_studio",
                        onClick = { navigateTo("keyboard_studio") },
                        icon = { Icon(Icons.Default.ColorLens, contentDescription = "Studio") },
                        label = { Text("Studio", fontSize = 11.sp, fontWeight = if (currentScreen == "keyboard_studio") FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            selectedTextColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E2850)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "my_library",
                        onClick = { navigateTo("my_library") },
                        icon = { Icon(Icons.Default.FolderSpecial, contentDescription = "Library") },
                        label = { Text("Library", fontSize = 11.sp, fontWeight = if (currentScreen == "my_library") FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            selectedTextColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E2850)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "marketplace",
                        onClick = { navigateTo("marketplace") },
                        icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Marketplace") },
                        label = { Text("Marketplace", fontSize = 11.sp, fontWeight = if (currentScreen == "marketplace") FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            selectedTextColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E2850)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "settings",
                        onClick = { navigateTo("settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 11.sp, fontWeight = if (currentScreen == "settings") FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            selectedTextColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E2850)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF070913))
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    repository = repository,
                    onNavigateTo = { navigateTo(it) }
                )
                "keyboard_studio" -> KeyboardStudioScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "character_lab" -> CharacterLabScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "touchpad" -> TouchpadTypingScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "fonts" -> FontLibraryScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "emojis" -> EmojiStudioScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "key_designer" -> KeyDesignerScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "themes" -> ThemeStudioScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "language" -> LanguageCenterScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "marketplace" -> MarketplaceScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "my_library" -> MyLibraryScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                "settings" -> SettingsScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
                else -> HomeScreen(
                    repository = repository,
                    onNavigateTo = { navigateTo(it) }
                )
            }
        }
    }
}
