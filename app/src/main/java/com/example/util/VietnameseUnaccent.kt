package com.example.util

import java.text.Normalizer
import java.util.regex.Pattern

object VietnameseUnaccent {
    private val DIACRITICS_PATTERN: Pattern =
        Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

    fun removeAccent(input: String?): String {
        if (input.isNullOrBlank()) return ""
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val withoutMarks = DIACRITICS_PATTERN.matcher(normalized).replaceAll("")
        return withoutMarks
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .lowercase()
            .trim()
    }

    fun matchesFuzzy(text: String, query: String): Boolean {
        if (query.isBlank()) return true
        val cleanText = removeAccent(text)
        val cleanQuery = removeAccent(query)
        return cleanText.contains(cleanQuery)
    }
}
