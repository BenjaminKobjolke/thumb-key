package com.dessalines.thumbkey.textprocessors

object KoreanLetters {
    private val leadingConsonants: Set<Char> =
        setOf(
            'ㄱ',
            'ㄲ',
            'ㄴ',
            'ㄷ',
            'ㄸ',
            'ㄹ',
            'ㅁ',
            'ㅂ',
            'ㅃ',
            'ㅅ',
            'ㅆ',
            'ㅇ',
            'ㅈ',
            'ㅉ',
            'ㅊ',
            'ㅋ',
            'ㅌ',
            'ㅍ',
            'ㅎ',
        )
    private val vowels: Set<Char> =
        setOf(
            'ㅏ',
            'ㅐ',
            'ㅑ',
            'ㅒ',
            'ㅓ',
            'ㅔ',
            'ㅕ',
            'ㅖ',
            'ㅗ',
            'ㅘ',
            'ㅙ',
            'ㅚ',
            'ㅛ',
            'ㅜ',
            'ㅝ',
            'ㅞ',
            'ㅟ',
            'ㅠ',
            'ㅡ',
            'ㅢ',
            'ㅣ',
        )
    private val trailingConsonants: Set<Char> =
        setOf(
            '\u0000',
            'ㄱ',
            'ㄲ',
            'ㄳ',
            'ㄴ',
            'ㄵ',
            'ㄶ',
            'ㄷ',
            'ㄹ',
            'ㄺ',
            'ㄻ',
            'ㄼ',
            'ㄽ',
            'ㄾ',
            'ㄿ',
            'ㅀ',
            'ㅁ',
            'ㅂ',
            'ㅄ',
            'ㅅ',
            'ㅆ',
            'ㅇ',
            'ㅈ',
            'ㅊ',
            'ㅋ',
            'ㅌ',
            'ㅍ',
            'ㅎ',
        )
    private val diphthongs: Map<Pair<Char, Char>, Char> =
        mapOf(
            ('ㅗ' to 'ㅏ') to 'ㅘ',
            ('ㅗ' to 'ㅐ') to 'ㅙ',
            ('ㅗ' to 'ㅣ') to 'ㅚ',
            ('ㅜ' to 'ㅓ') to 'ㅝ',
            ('ㅜ' to 'ㅔ') to 'ㅞ',
            ('ㅜ' to 'ㅣ') to 'ㅟ',
            ('ㅡ' to 'ㅣ') to 'ㅢ',
        )
    private val complexConsonants: Map<Pair<Char, Char>, Char> =
        mapOf(
            ('ㄱ' to 'ㅅ') to 'ㄳ',
            ('ㄴ' to 'ㅈ') to 'ㄵ',
            ('ㄴ' to 'ㅎ') to 'ㄶ',
            ('ㄹ' to 'ㄱ') to 'ㄺ',
            ('ㄹ' to 'ㅁ') to 'ㄻ',
            ('ㄹ' to 'ㅂ') to 'ㄼ',
            ('ㄹ' to 'ㅅ') to 'ㄽ',
            ('ㄹ' to 'ㅌ') to 'ㄾ',
            ('ㄹ' to 'ㅍ') to 'ㄿ',
            ('ㄹ' to 'ㅎ') to 'ㅀ',
            ('ㅂ' to 'ㅅ') to 'ㅄ',
        )

    fun isConsonant(c: Char): Boolean = c in leadingConsonants

    fun isVowel(c: Char): Boolean = c in vowels

    fun isTrailing(c: Char): Boolean = c in trailingConsonants

    fun isComplexConsonant(
        cons1: Char,
        cons2: Char,
    ): Boolean = Pair(cons1, cons2) in complexConsonants

    fun isDiphthong(
        vowel1: Char,
        vowel2: Char,
    ): Boolean = Pair(vowel1, vowel2) in diphthongs

    fun getLeadingIndex(leading: Char): Int = leadingConsonants.indexOf(leading)

    fun getVowelIndex(vowel: Char): Int = vowels.indexOf(vowel)

    fun getTrailingIndex(trailing: Char): Int = trailingConsonants.indexOf(trailing)

    fun getDiphthong(
        vowel1: Char,
        vowel2: Char,
    ): Char? = diphthongs[vowel1 to vowel2]

    fun getComplexConsonant(
        cons1: Char,
        cons2: Char,
    ): Char? = complexConsonants[cons1 to cons2]
}

enum class CompositionState {
    EMPTY,
    LEADING_CONSONANT,
    MEDIAL_VOWEL,
    TRAILING_CONSONANT,
    TRAILING_COMPLEX_CONSONANT,
    STANDALONE_COMPLEX_CONSONANT,
    STANDALONE_VOWEL,
}
