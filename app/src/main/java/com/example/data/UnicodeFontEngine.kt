package com.example.data

object UnicodeFontEngine {

    val styles: List<UnicodeFontStyle> = listOf(
        UnicodeFontStyle(
            id = "normal",
            name = "Standard Default",
            category = "Basic",
            sampleText = "ZYRON Keyboard Studio",
            transformFunction = { it }
        ),
        UnicodeFontStyle(
            id = "math_bold",
            name = "Mathematical Bold",
            category = "Modern",
            sampleText = "𝐙𝐘𝐑𝐎𝐍 𝐊𝐞𝐲𝐛𝐨𝐚𝐫𝐝",
            transformFunction = { transformMathBold(it) }
        ),
        UnicodeFontStyle(
            id = "script_cursive",
            name = "Royal Script Cursive",
            category = "Handwriting",
            sampleText = "𝒵𝓎𝓇ℴ𝓃 𝒦ℯ𝓎𝒷ℴ𝒶𝓇𝒹",
            transformFunction = { transformScript(it) }
        ),
        UnicodeFontStyle(
            id = "gothic_fraktur",
            name = "Cyber Gothic Fraktur",
            category = "Gothic",
            sampleText = "ℨ𝔶𝔯𝔬𝔫 𝔎𝔢𝔶𝔟𝔬𝔞𝔯𝔡",
            transformFunction = { transformFraktur(it) }
        ),
        UnicodeFontStyle(
            id = "double_struck",
            name = "Double-Struck Blackboard",
            category = "Futuristic",
            sampleText = "ℤ𝕐ℝ𝕆ℕ 𝕂𝕖𝕪𝕓𝕠𝕒𝕣𝕕",
            transformFunction = { transformDoubleStruck(it) }
        ),
        UnicodeFontStyle(
            id = "monospace_code",
            name = "Monospace Terminal",
            category = "Gaming",
            sampleText = "𝚉𝚈𝚁𝙾𝙽 𝙺𝚎𝚢𝚋𝚘𝚊𝚛𝚍",
            transformFunction = { transformMonospace(it) }
        ),
        UnicodeFontStyle(
            id = "bubble_circled",
            name = "Bubble Circled",
            category = "Rounded",
            sampleText = "ⓏⓎⓇⓄⓃ Ⓚⓔⓨⓑⓞⓐⓡⓓ",
            transformFunction = { transformCircled(it) }
        ),
        UnicodeFontStyle(
            id = "small_caps",
            name = "Titanium Small Caps",
            category = "Decorative",
            sampleText = "ᴢʏʀᴏɴ ᴋᴇʏʙᴏᴀʀᴅ",
            transformFunction = { transformSmallCaps(it) }
        ),
        UnicodeFontStyle(
            id = "fullwidth_cyber",
            name = "Cyber Fullwidth",
            category = "Futuristic",
            sampleText = "ＺＹＲＯＮ　Ｋｅｙｂｏａｒｄ",
            transformFunction = { transformFullwidth(it) }
        ),
        UnicodeFontStyle(
            id = "neon_bracketed",
            name = "Neon Crown Aesthetic",
            category = "Decorative",
            sampleText = "【 𝐙 𝐘 𝐑 𝐎 𝐍 】",
            transformFunction = { "【 " + transformMathBold(it).replace("", " ").trim() + " 】" }
        ),
        UnicodeFontStyle(
            id = "italic_sans",
            name = "Slanted Cyber",
            category = "Modern",
            sampleText = "𝘡𝘠𝘙𝘖𝘕 𝘒𝘦𝘺𝘣𝘰𝘢𝘳𝘥",
            transformFunction = { transformItalicSans(it) }
        )
    )

    fun applyStyle(text: String, styleId: String): String {
        val style = styles.find { it.id == styleId } ?: return text
        return style.transformFunction(text)
    }

    private fun transformMathBold(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                in 'A'..'Z' -> sb.appendCodePoint(0x1D400 + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D41A + (ch - 'a'))
                in '0'..'9' -> sb.appendCodePoint(0x1D7CE + (ch - '0'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformScript(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                'B' -> sb.append('ℬ')
                'E' -> sb.append('ℰ')
                'F' -> sb.append('ℱ')
                'H' -> sb.append('ℋ')
                'I' -> sb.append('ℐ')
                'L' -> sb.append('ℒ')
                'M' -> sb.append('ℳ')
                'R' -> sb.append('ℛ')
                'e' -> sb.append('ℯ')
                'g' -> sb.append('ℊ')
                'o' -> sb.append('ℴ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D49C + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D4B6 + (ch - 'a'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformFraktur(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                'C' -> sb.append('ℭ')
                'H' -> sb.append('ℌ')
                'I' -> sb.append('ℑ')
                'R' -> sb.append('ℜ')
                'Z' -> sb.append('ℨ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D504 + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D51E + (ch - 'a'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformDoubleStruck(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                'C' -> sb.append('ℂ')
                'H' -> sb.append('ℍ')
                'N' -> sb.append('ℕ')
                'P' -> sb.append('ℙ')
                'Q' -> sb.append('ℚ')
                'R' -> sb.append('ℝ')
                'Z' -> sb.append('ℤ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D538 + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D552 + (ch - 'a'))
                in '0'..'9' -> sb.appendCodePoint(0x1D7D8 + (ch - '0'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformMonospace(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                in 'A'..'Z' -> sb.appendCodePoint(0x1D670 + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D68A + (ch - 'a'))
                in '0'..'9' -> sb.appendCodePoint(0x1D7F6 + (ch - '0'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformCircled(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                in 'A'..'Z' -> sb.appendCodePoint(0x24B6 + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x24D0 + (ch - 'a'))
                in '1'..'9' -> sb.appendCodePoint(0x2460 + (ch - '1'))
                '0' -> sb.append('⓪')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformSmallCaps(text: String): String {
        val map = mapOf(
            'a' to 'ᴀ', 'b' to 'ʙ', 'c' to 'ᴄ', 'd' to 'ᴅ', 'e' to 'ᴇ',
            'f' to 'ꜰ', 'g' to 'ɢ', 'h' to 'ʜ', 'i' to 'ɪ', 'j' to 'ᴊ',
            'k' to 'ᴋ', 'l' to 'ʟ', 'm' to 'ᴍ', 'n' to 'ɴ', 'o' to 'ᴏ',
            'p' to 'ᴘ', 'q' to 'ǫ', 'r' to 'ʀ', 's' to 's', 't' to 'ᴛ',
            'u' to 'ᴜ', 'v' to 'ᴠ', 'w' to 'ᴡ', 'x' to 'x', 'y' to 'ʏ', 'z' to 'ᴢ'
        )
        val sb = StringBuilder()
        for (ch in text.lowercase()) {
            sb.append(map[ch] ?: ch)
        }
        return sb.toString()
    }

    private fun transformFullwidth(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                ' ' -> sb.append('　')
                in '!'..'~' -> sb.appendCodePoint(ch.code + 0xFEE0)
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun transformItalicSans(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                in 'A'..'Z' -> sb.appendCodePoint(0x1D63C + (ch - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D656 + (ch - 'a'))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }
}
