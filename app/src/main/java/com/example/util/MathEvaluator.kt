package com.example.util

import java.text.DecimalFormat
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.sqrt

data class CalcResult(
    val expression: String,
    val result: String,
    val type: String // "Tính toán" or "Đổi đơn vị"
)

object MathEvaluator {
    private val df = DecimalFormat("#,##0.##")

    fun evaluate(query: String): CalcResult? {
        val trimmed = query.trim()
        if (trimmed.length < 2) return null

        // Check unit conversion first
        val unitResult = evaluateUnitConversion(trimmed)
        if (unitResult != null) return unitResult

        // Check math expression
        return evaluateMath(trimmed)
    }

    private fun evaluateUnitConversion(input: String): CalcResult? {
        val lower = input.lowercase(Locale.ROOT).trim()

        // Currency: e.g., "10 usd to vnd", "50 eur to vnd", "100000 vnd to usd"
        val currencyRegex = Regex("""^([\d.,]+)\s*(usd|vnd|eur|jpy)\s*(to|sang|=|qua)\s*(usd|vnd|eur|jpy)$""")
        currencyRegex.find(lower)?.let { match ->
            val num = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val from = match.groupValues[2]
            val to = match.groupValues[4]
            if (from == to) return CalcResult(input, "$num ${to.uppercase()}", "Đổi đơn vị")

            // Base rate approximation in VND
            val ratesToVnd = mapOf(
                "vnd" to 1.0,
                "usd" to 25400.0,
                "eur" to 27500.0,
                "jpy" to 168.0
            )
            val vndVal = num * (ratesToVnd[from] ?: 1.0)
            val finalVal = vndVal / (ratesToVnd[to] ?: 1.0)
            return CalcResult(input, "${df.format(finalVal)} ${to.uppercase()}", "Tỷ giá tiền tệ")
        }

        // Distance & Length: km, m, cm, mm, inch, ft, mile
        val lengthRegex = Regex("""^([\d.,]+)\s*(km|m|cm|mm|inch|in|ft|mile|dam|hai ly)\s*(to|sang|=|qua)\s*(km|m|cm|mm|inch|in|ft|mile|dam|hai ly)$""")
        lengthRegex.find(lower)?.let { match ->
            val num = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val rawFrom = match.groupValues[2]
            val rawTo = match.groupValues[4]
            val from = if (rawFrom == "in") "inch" else rawFrom
            val to = if (rawTo == "in") "inch" else rawTo
            val toMeters = mapOf(
                "km" to 1000.0, "m" to 1.0, "cm" to 0.01, "mm" to 0.001,
                "inch" to 0.0254, "ft" to 0.3048, "mile" to 1609.34, "dam" to 1609.34, "hai ly" to 1852.0
            )
            val mVal = num * (toMeters[from] ?: 1.0)
            val finalVal = mVal / (toMeters[to] ?: 1.0)
            return CalcResult(input, "${df.format(finalVal)} $to", "Đổi độ dài")
        }

        // Weight: kg, g, mg, lb, oz, tan, ta, yen
        val weightRegex = Regex("""^([\d.,]+)\s*(kg|g|mg|lb|oz|tan|ta)\s*(to|sang|=|qua)\s*(kg|g|mg|lb|oz|tan|ta)$""")
        weightRegex.find(lower)?.let { match ->
            val num = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val from = match.groupValues[2]
            val to = match.groupValues[4]
            val toKg = mapOf(
                "kg" to 1.0, "g" to 0.001, "mg" to 0.000001,
                "lb" to 0.45359237, "oz" to 0.0283495, "tan" to 1000.0, "ta" to 100.0
            )
            val kgVal = num * (toKg[from] ?: 1.0)
            val finalVal = kgVal / (toKg[to] ?: 1.0)
            return CalcResult(input, "${df.format(finalVal)} $to", "Đổi khối lượng")
        }

        // Temperature: c to f, f to c
        val tempRegex = Regex("""^([\d.,]+)\s*(c|f|k)\s*(to|sang|=|qua)\s*(c|f|k)$""")
        tempRegex.find(lower)?.let { match ->
            val num = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
            val from = match.groupValues[2]
            val to = match.groupValues[4]
            val result = when {
                from == "c" && to == "f" -> (num * 9 / 5) + 32
                from == "f" && to == "c" -> (num - 32) * 5 / 9
                from == "c" && to == "k" -> num + 273.15
                from == "k" && to == "c" -> num - 273.15
                else -> num
            }
            return CalcResult(input, "${df.format(result)} °${to.uppercase()}", "Đổi nhiệt độ")
        }

        return null
    }

    private fun evaluateMath(input: String): CalcResult? {
        var clean = input.lowercase(Locale.ROOT)
            .replace("x", "*")
            .replace(":", "/")
            .replace("×", "*")
            .replace("÷", "/")
            .replace(" ", "")

        // Handle percent: "15%of200" or "15%*200"
        val percentOf = Regex("""([\d.]+)%of([\d.]+)""")
        percentOf.find(clean)?.let {
            val p = it.groupValues[1].toDoubleOrNull() ?: return null
            val v = it.groupValues[2].toDoubleOrNull() ?: return null
            val res = (p / 100.0) * v
            return CalcResult(input, df.format(res), "Phần trăm")
        }

        // Handle square root: "sqrt(144)" or "can(144)"
        if (clean.startsWith("sqrt(") || clean.startsWith("can(")) {
            val inside = clean.substringAfter("(").substringBefore(")")
            val num = inside.toDoubleOrNull() ?: return null
            if (num < 0) return null
            return CalcResult(input, df.format(sqrt(num)), "Căn bậc hai")
        }

        // Must contain at least one math operator: +, -, *, /, ^
        if (!clean.any { it in "+-*/^" }) return null

        return try {
            val res = Parser(clean).parse()
            if (res.isNaN() || res.isInfinite()) null
            else CalcResult(input, df.format(res), "Máy tính nhanh")
        } catch (e: Exception) {
            null
        }
    }

    private class Parser(private val str: String) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected character: " + ch.toChar())
            }

            if (eat('^'.code)) x = x.pow(parseFactor())
            if (eat('%'.code)) x = x / 100.0

            return x
        }
    }
}
