package com.example.data

object TransliterationEngine {

    private val hinglishMap = mapOf(
        "kya" to listOf("क्या", "kya", "kyun"),
        "kyun" to listOf("क्यों", "kyun"),
        "namaste" to listOf("नमस्ते", "namaste"),
        "dhanyawad" to listOf("धन्यवाद", "dhanyawad"),
        "shukriya" to listOf("शुक्रिया", "shukriya"),
        "kaise" to listOf("कैसे", "kaisa", "kaisi"),
        "kaisa" to listOf("कैसा"),
        "kaisi" to listOf("कैसी"),
        "ho" to listOf("हो"),
        "hai" to listOf("है", "hain"),
        "hain" to listOf("हैं"),
        "theek" to listOf("ठीक", "theek"),
        "bhai" to listOf("भाई", "bhaiya"),
        "dost" to listOf("दोस्त", "mitr"),
        "accha" to listOf("अच्छा", "achha"),
        "achha" to listOf("अच्छा"),
        "bahut" to listOf("बहुत"),
        "khana" to listOf("खाना"),
        "pani" to listOf("पानी"),
        "ghar" to listOf("घर"),
        "kaam" to listOf("काम"),
        "kal" to listOf("कल"),
        "aaj" to listOf("आज"),
        "abhi" to listOf("अभी"),
        "kahan" to listOf("कहाँ", "kaha"),
        "yaar" to listOf("यार"),
        "ha" to listOf("हाँ", "haan"),
        "nahi" to listOf("नहीं", "na"),
        "chalo" to listOf("चलो"),
        "alvida" to listOf("अलविदा")
    )

    private val romanUrduMap = mapOf(
        "salam" to listOf("سلام", "السلام علیکم", "salam"),
        "shukriya" to listOf("شکریہ", "shukriya"),
        "kaise" to listOf("کیسے", "kaisa"),
        "kaisa" to listOf("کیسا"),
        "kaisi" to listOf("کیسی"),
        "theek" to listOf("ٹھیک", "theek"),
        "aap" to listOf("آپ", "ap"),
        "tum" to listOf("تم"),
        "main" to listOf("میں"),
        "hum" to listOf("ہم"),
        "hai" to listOf("ہے"),
        "hain" to listOf("ہیں"),
        "hoon" to listOf("ہوں"),
        "kya" to listOf("کیا"),
        "kyun" to listOf("کیوں"),
        "bhai" to listOf("بھائی"),
        "dost" to listOf("دوست"),
        "accha" to listOf("اچھا"),
        "bohot" to listOf("بہت", "bahut"),
        "ghar" to listOf("گھر"),
        "kaam" to listOf("کام"),
        "khuda" to listOf("خدا", "الله"),
        "hafiz" to listOf("حافظ"),
        "khuda hafiz" to listOf("خدا حافظ"),
        "mashallah" to listOf("ماشاءاللہ"),
        "inshallah" to listOf("انشاءاللہ"),
        "alhamdulillah" to listOf("الحمدللہ"),
        "subhanallah" to listOf("سبحان اللہ"),
        "haan" to listOf("ہاں"),
        "nahi" to listOf("نہیں")
    )

    private val englishWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "keyboard", "studio", "custom", "design", "theme", "character", "touchpad",
        "awesome", "unique", "zyron", "welcome", "writing", "emoji", "sticker", "beautiful"
    )

    fun getSuggestions(
        input: String,
        language: String,
        customWords: List<String> = emptyList()
    ): List<String> {
        val clean = input.trim().lowercase()
        if (clean.isEmpty()) return emptyList()

        val results = mutableListOf<String>()

        when (language) {
            "Hinglish" -> {
                hinglishMap[clean]?.let { results.addAll(it) }
            }
            "Roman Urdu" -> {
                romanUrduMap[clean]?.let { results.addAll(it) }
            }
            "Hindi" -> {
                hinglishMap[clean]?.firstOrNull()?.let { results.add(it) }
            }
            "Urdu" -> {
                romanUrduMap[clean]?.firstOrNull()?.let { results.add(it) }
            }
        }

        // Custom user dictionary matches
        for (word in customWords) {
            if (word.lowercase().startsWith(clean) && !results.contains(word)) {
                results.add(word)
            }
        }

        // English prefix & fuzzy matching
        for (word in englishWords) {
            if (word.startsWith(clean) && !results.contains(word)) {
                results.add(word)
                if (results.size >= 5) break
            }
        }

        // Levenshtein fuzzy match if no exact prefix found
        if (results.isEmpty() && clean.length >= 3) {
            for (word in englishWords) {
                if (levenshteinDistance(clean, word) <= 1) {
                    results.add(word)
                    if (results.size >= 3) break
                }
            }
        }

        // If no suggestions, return the current text
        if (results.isEmpty()) {
            results.add(input)
        }

        return results.distinct().take(4)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
