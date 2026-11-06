package com.dessalines.thumbkey.textprocessors

// Shared data types and builders for the ordered Avro pattern tables.
internal object AvroPatternDsl {
    data class Cond(
        val type: String,
        val scope: String,
        val value: String = "",
    )

    data class Rule(
        val replace: String,
        val conds: List<Cond> = emptyList(),
    )

    data class Pattern(
        val find: String,
        val replace: String,
        val rules: List<Rule> = emptyList(),
    )

    fun c(
        type: String,
        scope: String,
        value: String = "",
    ) = Cond(type, scope, value)

    fun rule(
        replace: String,
        vararg conds: Cond,
    ) = Rule(replace, conds.toList())

    // vowel renders as full letter at word start / after vowel, kar after consonant
    fun sealRules(full: String) =
        listOf(
            rule(full, c("prefix", "!consonant"), c("suffix", "!exact", "`")),
            rule(full, c("prefix", "punctuation"), c("suffix", "!exact", "`")),
        )
}
