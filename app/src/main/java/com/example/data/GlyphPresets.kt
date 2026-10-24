package com.example.data

object GlyphPresets {

    fun getInitialGlyphFor(char: String): CustomGlyph {
        val uppercase = char.uppercase()
        val components = when (uppercase) {
            "A" -> createGlyphA()
            "B" -> createGlyphB()
            "Z" -> createGlyphZ()
            "C" -> createGlyphC()
            "X" -> createGlyphX()
            else -> createGenericGlyph(char)
        }
        return CustomGlyph(
            id = "glyph_${uppercase}_base",
            name = "Cyber $uppercase",
            mappedCharacter = uppercase,
            components = components
        )
    }

    private fun createGlyphA(): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "comp_crown",
                name = "Crown Ornament",
                type = GlyphComponentType.CROWN_SPIKE,
                points = listOf(
                    GlyphPoint(0.35f, 0.15f),
                    GlyphPoint(0.40f, 0.22f),
                    GlyphPoint(0.50f, 0.10f),
                    GlyphPoint(0.60f, 0.22f),
                    GlyphPoint(0.65f, 0.15f),
                    GlyphPoint(0.62f, 0.25f),
                    GlyphPoint(0.38f, 0.25f)
                ),
                strokeColor = 0xFFFF0055,
                fillColor = 0x66FF0055,
                strokeWidth = 3f,
                glowColor = 0xFFFF0055,
                glowRadius = 10f,
                zIndex = 5
            ),
            GlyphComponent(
                id = "stroke_left_diag",
                name = "Left Diagonal Stroke",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.50f, 0.22f), GlyphPoint(0.18f, 0.85f)),
                strokeColor = 0xFFFF1744,
                strokeWidth = 6f,
                glowColor = 0xFFFF1744,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "stroke_right_diag",
                name = "Right Diagonal Stroke",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.50f, 0.22f), GlyphPoint(0.82f, 0.85f)),
                strokeColor = 0xFFFF1744,
                strokeWidth = 6f,
                glowColor = 0xFFFF1744,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "stroke_crossbar",
                name = "Horizontal Crossbar",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.32f, 0.58f), GlyphPoint(0.68f, 0.58f)),
                strokeColor = 0xFFFF5252,
                strokeWidth = 5f,
                glowColor = 0xFFFF5252,
                glowRadius = 6f,
                zIndex = 3
            ),
            GlyphComponent(
                id = "cyber_wing_left",
                name = "Cyber Accent Wing",
                type = GlyphComponentType.CUSTOM_PATH,
                points = listOf(
                    GlyphPoint(0.18f, 0.85f),
                    GlyphPoint(0.10f, 0.75f),
                    GlyphPoint(0.15f, 0.70f)
                ),
                strokeColor = 0xFF00E5FF,
                strokeWidth = 3f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 1
            )
        )
    }

    private fun createGlyphB(): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "stroke_spine",
                name = "Vertical Spine",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.24f, 0.18f), GlyphPoint(0.24f, 0.82f)),
                strokeColor = 0xFF00E5FF,
                strokeWidth = 6f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "loop_top",
                name = "Top Curve Loop",
                type = GlyphComponentType.CURVE,
                points = listOf(
                    GlyphPoint(0.24f, 0.18f),
                    GlyphPoint(0.70f, 0.18f),
                    GlyphPoint(0.70f, 0.50f),
                    GlyphPoint(0.24f, 0.50f)
                ),
                strokeColor = 0xFF00E5FF,
                fillColor = 0x2200E5FF,
                strokeWidth = 5f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 3
            ),
            GlyphComponent(
                id = "loop_bottom",
                name = "Bottom Curve Loop",
                type = GlyphComponentType.CURVE,
                points = listOf(
                    GlyphPoint(0.24f, 0.50f),
                    GlyphPoint(0.78f, 0.50f),
                    GlyphPoint(0.78f, 0.82f),
                    GlyphPoint(0.24f, 0.82f)
                ),
                strokeColor = 0xFF00B0FF,
                fillColor = 0x3300B0FF,
                strokeWidth = 5f,
                glowColor = 0xFF00B0FF,
                glowRadius = 8f,
                zIndex = 3
            )
        )
    }

    private fun createGlyphZ(): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "z_crown",
                name = "Royal Crown",
                type = GlyphComponentType.CROWN_SPIKE,
                points = listOf(
                    GlyphPoint(0.32f, 0.10f),
                    GlyphPoint(0.38f, 0.16f),
                    GlyphPoint(0.50f, 0.05f),
                    GlyphPoint(0.62f, 0.16f),
                    GlyphPoint(0.68f, 0.10f),
                    GlyphPoint(0.65f, 0.18f),
                    GlyphPoint(0.35f, 0.18f)
                ),
                strokeColor = 0xFFA855F7,
                fillColor = 0x55A855F7,
                strokeWidth = 3f,
                glowColor = 0xFFA855F7,
                glowRadius = 12f,
                zIndex = 5
            ),
            GlyphComponent(
                id = "bar_top",
                name = "Top Cyber Bar",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.20f, 0.22f), GlyphPoint(0.80f, 0.22f)),
                strokeColor = 0xFF00E5FF,
                strokeWidth = 7f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "bar_diagonal",
                name = "Diagonal Slash",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.80f, 0.22f), GlyphPoint(0.20f, 0.80f)),
                strokeColor = 0xFFA855F7,
                strokeWidth = 7f,
                glowColor = 0xFFA855F7,
                glowRadius = 10f,
                zIndex = 3
            ),
            GlyphComponent(
                id = "bar_bottom",
                name = "Bottom Cyber Bar",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.20f, 0.80f), GlyphPoint(0.80f, 0.80f)),
                strokeColor = 0xFF00E5FF,
                strokeWidth = 7f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "lightning_notch",
                name = "Electric Bolt Notch",
                type = GlyphComponentType.BOLT,
                points = listOf(
                    GlyphPoint(0.52f, 0.44f),
                    GlyphPoint(0.44f, 0.54f),
                    GlyphPoint(0.56f, 0.54f),
                    GlyphPoint(0.48f, 0.66f)
                ),
                strokeColor = 0xFF00FFFF,
                strokeWidth = 3f,
                glowColor = 0xFF00FFFF,
                glowRadius = 8f,
                zIndex = 4
            )
        )
    }

    private fun createGlyphC(): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "curve_c",
                name = "C-Arc Stroke",
                type = GlyphComponentType.CURVE,
                points = listOf(
                    GlyphPoint(0.75f, 0.25f),
                    GlyphPoint(0.20f, 0.20f),
                    GlyphPoint(0.20f, 0.80f),
                    GlyphPoint(0.75f, 0.75f)
                ),
                strokeColor = 0xFF38BDF8,
                strokeWidth = 6f,
                glowColor = 0xFF38BDF8,
                glowRadius = 8f,
                zIndex = 2
            )
        )
    }

    private fun createGlyphX(): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "diag_left",
                name = "Diagonal Slash 1",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.22f, 0.20f), GlyphPoint(0.78f, 0.80f)),
                strokeColor = 0xFFFF0055,
                strokeWidth = 6f,
                glowColor = 0xFFFF0055,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "diag_right",
                name = "Diagonal Slash 2",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.78f, 0.20f), GlyphPoint(0.22f, 0.80f)),
                strokeColor = 0xFF00E5FF,
                strokeWidth = 6f,
                glowColor = 0xFF00E5FF,
                glowRadius = 8f,
                zIndex = 2
            )
        )
    }

    private fun createGenericGlyph(char: String): List<GlyphComponent> {
        return listOf(
            GlyphComponent(
                id = "comp_main",
                name = "$char Center Spine",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.50f, 0.20f), GlyphPoint(0.50f, 0.80f)),
                strokeColor = 0xFF6366F1,
                strokeWidth = 6f,
                glowColor = 0xFF6366F1,
                glowRadius = 8f,
                zIndex = 2
            ),
            GlyphComponent(
                id = "comp_cross",
                name = "$char Top Cross",
                type = GlyphComponentType.LINE,
                points = listOf(GlyphPoint(0.25f, 0.20f), GlyphPoint(0.75f, 0.20f)),
                strokeColor = 0xFF818CF8,
                strokeWidth = 5f,
                glowColor = 0xFF818CF8,
                glowRadius = 6f,
                zIndex = 3
            )
        )
    }

    val sampleStickers: List<StickerItem> = listOf(
        StickerItem(
            id = "sticker_avatar_hero",
            packName = "Cyber Hunters",
            name = "Anime Hero",
            imageDrawableRes = com.example.R.drawable.avatar_anime_hero,
            textOverlay = "AWAKEN",
            textColor = 0xFF00E5FF,
            textGlowColor = 0xFF0088FF
        ),
        StickerItem(
            id = "sticker_zyron_crown",
            packName = "Cyber Hunters",
            name = "Crown Z",
            imageDrawableRes = com.example.R.drawable.zyron_logo,
            textOverlay = "UNIQUE",
            textColor = 0xFFFF007F,
            textGlowColor = 0xFFA855F7
        ),
        StickerItem(
            id = "sticker_cyber_city",
            packName = "Neon Streets",
            name = "Solo City",
            imageDrawableRes = com.example.R.drawable.bg_anime_cyber,
            textOverlay = "CYBER",
            textColor = 0xFF38BDF8,
            textGlowColor = 0xFF0284C7
        )
    )

    val marketplaceShowcase: List<MarketplaceItem> = listOf(
        MarketplaceItem(
            id = "market_theme_anime",
            title = "Shadow Monarch Cyber",
            creator = "ZYRON Studio",
            category = "Themes",
            description = "High-voltage anime aesthetic with electric blue glowing keys and animated dark aura.",
            downloads = 48200,
            rating = 4.9f,
            tags = listOf("Anime", "Glow", "Cyberpunk"),
            isInstalled = true,
            associatedThemeId = "anime_cyber"
        ),
        MarketplaceItem(
            id = "market_theme_red",
            title = "Inferno Crimson RGB",
            creator = "NeonPulse",
            category = "Themes",
            description = "Deep fiery red backlights with brushed steel keycaps and dynamic neon response.",
            downloads = 32400,
            rating = 4.8f,
            tags = listOf("Neon", "Red", "Gaming"),
            isInstalled = false,
            associatedThemeId = "neon_red"
        ),
        MarketplaceItem(
            id = "market_glyph_pack",
            title = "Cyber Glyph Pack V1",
            creator = "GlyphMaster",
            category = "Character Packs",
            description = "Complete set of merged robotic A-Z alphabet with crowns, wings, and circuit runes.",
            downloads = 19300,
            rating = 4.9f,
            tags = listOf("Characters", "Vector", "Surgery"),
            isInstalled = true
        ),
        MarketplaceItem(
            id = "market_sticker_anime",
            title = "Solo Hunter Stickers",
            creator = "AkiraArt",
            category = "Stickers",
            description = "16 ultra-HD anime stickers formatted for fast keyboard insertion in WhatsApp and Telegram.",
            downloads = 58900,
            rating = 5.0f,
            tags = listOf("Stickers", "Anime", "Reactions"),
            isInstalled = true
        ),
        MarketplaceItem(
            id = "market_font_fraktur",
            title = "Gothic Cyber Fonts",
            creator = "TypeFoundry",
            category = "Fonts",
            description = "Futuristic Fraktur & Blackboard mathematical fonts usable in all third party applications.",
            downloads = 27100,
            rating = 4.7f,
            tags = listOf("Fonts", "Gothic", "Unicode"),
            isInstalled = false
        )
    )
}
