package com.dessalines.thumbkey.textprocessors

internal object AvroConditionEvaluator {
    private const val VOWELS = "aeiou"
    private const val CONSONANTS = "bcdfghjklmnpqrstvwxyz"

    fun passes(
        cond: AvroEngine.Cond,
        fixed: String,
        start: Int,
        end: Int,
        prev: Int,
    ): Boolean {
        val negative = cond.scope.startsWith("!")
        val scope = if (negative) cond.scope.drop(1) else cond.scope
        val chk = if (cond.type == "suffix") end else prev
        val context = Context(fixed, start, end)
        val hit =
            when (scope) {
                "punctuation" -> isPunctuationHit(cond, context, chk)
                "vowel" -> isCharacterHit(context, chk, cond.type, ::isVowel)
                "consonant" -> isCharacterHit(context, chk, cond.type, ::isConsonant)
                "exact" -> isExactHit(cond, context)
                else -> true
            }
        return hit xor negative
    }

    private data class Context(
        val fixed: String,
        val start: Int,
        val end: Int,
    )

    private fun isPunctuationHit(
        cond: AvroEngine.Cond,
        context: Context,
        chk: Int,
    ): Boolean {
        val atBoundary =
            (chk < 0 && cond.type == "prefix") || (chk >= context.fixed.length && cond.type == "suffix")
        return atBoundary || (chk in context.fixed.indices && isPunctuation(context.fixed[chk]))
    }

    private fun isCharacterHit(
        context: Context,
        chk: Int,
        type: String,
        predicate: (Char) -> Boolean,
    ): Boolean {
        val inRange =
            (chk >= 0 && type == "prefix") || (chk < context.fixed.length && type == "suffix")
        return inRange && predicate(context.fixed[chk])
    }

    private fun isExactHit(
        cond: AvroEngine.Cond,
        context: Context,
    ): Boolean {
        val len = context.fixed.length
        val (start, end) =
            if (cond.type == "suffix") {
                context.end to (context.end + cond.value.length)
            } else {
                (context.start - cond.value.length) to context.start
            }
        return start >= 0 && end <= len && context.fixed.substring(start, end) == cond.value
    }

    private fun isPunctuation(ch: Char) = !isVowel(ch) && !isConsonant(ch)

    private fun isVowel(ch: Char) = ch.lowercaseChar() in VOWELS

    private fun isConsonant(ch: Char) = ch.lowercaseChar() in CONSONANTS
}
