package com.dessalines.thumbkey.textprocessors

/*
 * Avro Phonetic engine — faithful Kotlin port of jsAvroPhonetic v1.1.4
 * Original: Copyright (C) OmicronLab, Rifat Nabi (MPL 1.1)
 * https://github.com/torifat/jsAvroPhonetic
 */
object AvroEngine {
    private const val CASE_SENSITIVE = "oiudgjnrstyz"

    private val PATTERNS = AVRO_PATTERNS_B_TO_N + AVRO_PATTERNS_O_TO_SYMBOLS

    fun convert(input: String): String {
        val fixed = fixString(input)
        val out = StringBuilder()
        var cur = 0
        while (cur < fixed.length) {
            val start = cur
            var matched = false
            for (pattern in PATTERNS) {
                val end = cur + pattern.find.length
                if (end > fixed.length || fixed.substring(start, end) != pattern.find) continue
                for (rule in pattern.rules) {
                    if (rule.conds.all {
                            AvroConditionEvaluator.passes(it, fixed, start, end)
                        }
                    ) {
                        out.append(rule.replace)
                        cur = end
                        matched = true
                        break
                    }
                }
                if (matched) break
                out.append(pattern.replace) // no rule matched -> default
                cur = end
                matched = true
                break
            }
            if (!matched) {
                out.append(fixed[cur])
                cur++
            }
        }
        return out.toString()
    }

    // lowercase everything except letters whose case matters to the rules
    private fun fixString(input: String): String =
        buildString {
            for (ch in input) append(if (ch.lowercaseChar() in CASE_SENSITIVE) ch else ch.lowercaseChar())
        }
}
