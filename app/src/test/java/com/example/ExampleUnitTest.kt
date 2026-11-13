package com.example

import com.example.util.MathEvaluator
import com.example.util.VietnameseUnaccent
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testVietnameseUnaccent() {
        assertEquals("thoi tiet", VietnameseUnaccent.removeAccent("Thời tiết"))
        assertEquals("cai dat", VietnameseUnaccent.removeAccent("Cài đặt"))
        assertEquals("dien thoai", VietnameseUnaccent.removeAccent("Điện thoại"))

        assertTrue(VietnameseUnaccent.matchesFuzzy("Thời tiết", "thoi tiet"))
        assertTrue(VietnameseUnaccent.matchesFuzzy("Trình duyệt Chrome", "trinh duyet"))
        assertTrue(VietnameseUnaccent.matchesFuzzy("Tin nhắn SMS", "tin nhan"))
    }

    @Test
    fun testMathEvaluator() {
        val calc1 = MathEvaluator.evaluate("25 + 75")
        assertNotNull(calc1)
        assertEquals("100", calc1?.result)

        val calc2 = MathEvaluator.evaluate("10 * 5 - 2")
        assertNotNull(calc2)
        assertEquals("48", calc2?.result)

        val calcPercent = MathEvaluator.evaluate("15% of 200")
        assertNotNull(calcPercent)
        assertEquals("30", calcPercent?.result)

        val unitKm = MathEvaluator.evaluate("10 km to m")
        assertNotNull(unitKm)
        assertTrue(unitKm?.result?.contains("10,000") == true || unitKm?.result?.contains("10000") == true)

        val unitTemp = MathEvaluator.evaluate("100 c to f")
        assertNotNull(unitTemp)
        assertTrue(unitTemp?.result?.contains("212") == true)
    }
}
