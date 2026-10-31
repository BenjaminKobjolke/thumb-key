package com.dessalines.thumbkey.textprocessors

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.utils.TAG

private const val UNICODE_BASE = 0xAC00
private const val LEADING_MULTIPLIER = 588
private const val VOWEL_MULTIPLIER = 28

internal fun KoreanTextProcessor.resetState() {
    state = CompositionState.EMPTY
    leading = ""
    medialVowel = ""
    trailing = ""
    standaloneVowel = ""
    composedText = ""
}

internal fun KoreanTextProcessor.deleteKeyAction(
    ime: IMEService,
    ev: KeyEvent,
) {
    val ic = ime.currentInputConnection

    when (state) {
        CompositionState.EMPTY -> {
            ic.sendKeyEvent(ev)
        }

        CompositionState.LEADING_CONSONANT -> {
            resetState()
        }

        CompositionState.MEDIAL_VOWEL -> {
            if (medialVowel.length == 2) {
                medialVowel = medialVowel.dropLast(1)
                val unicode = calculateBlockUnicode(leading, medialVowel)
                composedText = unicode.toChar().toString()
            } else {
                state = CompositionState.LEADING_CONSONANT
                composedText = leading
            }
        }

        CompositionState.TRAILING_CONSONANT -> {
            state = CompositionState.MEDIAL_VOWEL
            val unicode = calculateBlockUnicode(leading, medialVowel)
            composedText = unicode.toChar().toString()
        }

        CompositionState.TRAILING_COMPLEX_CONSONANT -> {
            state = CompositionState.TRAILING_CONSONANT
            trailing = trailing.dropLast(1)
            val unicode = calculateBlockUnicode(leading, medialVowel, trailing)
            composedText = unicode.toChar().toString()
        }

        CompositionState.STANDALONE_VOWEL -> {
            if (standaloneVowel.length > 1) {
                standaloneVowel = standaloneVowel.dropLast(1)
                composedText = standaloneVowel
            } else {
                resetState()
            }
        }

        CompositionState.STANDALONE_COMPLEX_CONSONANT -> {
            leading = leading.dropLast(1)
            composedText = leading
        }
    }

    ic.setComposingText(composedText, 1)
}

internal fun KoreanTextProcessor.processConsonant(
    ic: InputConnection,
    consonant: Char,
) {
    when (state) {
        CompositionState.EMPTY -> {
            state = CompositionState.LEADING_CONSONANT
            leading = consonant.toString()
            composedText = leading

            selectionStart += 1
            selectionEnd += 1
        }

        CompositionState.LEADING_CONSONANT -> {
            if (KoreanLetters.isComplexConsonant(leading[0], consonant)) {
                state = CompositionState.STANDALONE_COMPLEX_CONSONANT
                leading += consonant
                composedText = KoreanLetters.getComplexConsonant(leading[0], consonant).toString()
            } else {
                commitCurrentBlock(ic)
                state = CompositionState.LEADING_CONSONANT
                leading = consonant.toString()
                composedText = leading
            }
        }

        CompositionState.MEDIAL_VOWEL -> {
            if (KoreanLetters.isTrailing(consonant)) {
                state = CompositionState.TRAILING_CONSONANT
                trailing = consonant.toString()
                val unicode = calculateBlockUnicode(leading, medialVowel, trailing)
                composedText = unicode.toChar().toString()
            } else {
                commitCurrentBlock(ic)

                state = CompositionState.LEADING_CONSONANT
                leading = consonant.toString()
                composedText = leading
            }
        }

        CompositionState.TRAILING_CONSONANT -> {
            if (KoreanLetters.isComplexConsonant(trailing[0], consonant)) {
                state = CompositionState.TRAILING_COMPLEX_CONSONANT
                trailing += consonant
                val unicode = calculateBlockUnicode(leading, medialVowel, trailing)
                composedText = unicode.toChar().toString()
            } else {
                commitCurrentBlock(ic)

                state = CompositionState.LEADING_CONSONANT
                leading = consonant.toString()
                composedText = leading
            }
        }

        CompositionState.TRAILING_COMPLEX_CONSONANT,
        CompositionState.STANDALONE_COMPLEX_CONSONANT,
        CompositionState.STANDALONE_VOWEL,
        -> {
            commitCurrentBlock(ic)

            state = CompositionState.LEADING_CONSONANT
            leading = consonant.toString()
            composedText = leading
        }
    }

    ic.setComposingText(composedText, 1)
}

internal fun KoreanTextProcessor.processVowel(
    ic: InputConnection,
    vowel: Char,
) {
    when (state) {
        CompositionState.EMPTY -> {
            state = CompositionState.STANDALONE_VOWEL
            standaloneVowel = vowel.toString()
            composedText = standaloneVowel

            selectionStart += 1
            selectionEnd += 1
        }

        CompositionState.LEADING_CONSONANT -> {
            state = CompositionState.MEDIAL_VOWEL
            medialVowel = vowel.toString()
            val unicode = calculateBlockUnicode(leading, medialVowel)
            composedText = unicode.toChar().toString()
        }

        CompositionState.MEDIAL_VOWEL -> {
            if (KoreanLetters.isDiphthong(medialVowel[0], vowel) && medialVowel.length < 2) {
                // state is the same
                medialVowel += vowel
                val unicode = calculateBlockUnicode(leading, medialVowel)
                composedText = unicode.toChar().toString()
            } else {
                commitCurrentBlock(ic)

                state = CompositionState.STANDALONE_VOWEL
                standaloneVowel = vowel.toString()
                composedText = standaloneVowel
            }
        }

        CompositionState.TRAILING_CONSONANT,
        CompositionState.TRAILING_COMPLEX_CONSONANT,
        -> {
            val lastConsonant = trailing.last()
            trailing = trailing.dropLast(1)
            var unicode =
                if (trailing.isNotEmpty()) {
                    calculateBlockUnicode(leading, medialVowel, trailing)
                } else {
                    calculateBlockUnicode(leading, medialVowel)
                }
            composedText = unicode.toChar().toString()
            commitCurrentBlock(ic)

            state = CompositionState.MEDIAL_VOWEL
            leading = lastConsonant.toString()
            medialVowel = vowel.toString()
            unicode = calculateBlockUnicode(leading, medialVowel)
            composedText = unicode.toChar().toString()
        }

        CompositionState.STANDALONE_COMPLEX_CONSONANT -> {
            val lastConsonant = leading.last().toString()
            composedText = leading.dropLast(1)
            commitCurrentBlock(ic)

            state = CompositionState.MEDIAL_VOWEL
            leading = lastConsonant
            medialVowel = vowel.toString()
            val unicode = calculateBlockUnicode(leading, medialVowel)
            composedText = unicode.toChar().toString()
        }

        CompositionState.STANDALONE_VOWEL -> {
            if (standaloneVowel.length == 1 && KoreanLetters.isDiphthong(standaloneVowel[0], vowel)) {
                standaloneVowel += vowel
                composedText = KoreanLetters.getDiphthong(standaloneVowel[0], vowel).toString()
            } else {
                commitCurrentBlock(ic)
                state = CompositionState.STANDALONE_VOWEL
                standaloneVowel = vowel.toString()
                composedText = standaloneVowel
            }
        }
    }

    ic.setComposingText(composedText, 1)
}

internal fun KoreanTextProcessor.commitCurrentBlock(ic: InputConnection) {
    Log.d(TAG, "TextProcessor: committing text: $composedText")
    ic.commitText(composedText, 1)
    resetState()
    selectionStart += 1
    selectionEnd += 1
}

internal fun KoreanTextProcessor.calculateBlockUnicode(
    lCons: String,
    medVow: String,
    tCons: String? = null,
): Int {
    var codepoint = UNICODE_BASE
    codepoint += KoreanLetters.getLeadingIndex(lCons[0]) * LEADING_MULTIPLIER

    if (medVow.length == 2) {
        val diphthong = KoreanLetters.getDiphthong(medVow[0], medVow[1]) ?: return 0xFFFD
        codepoint += KoreanLetters.getVowelIndex(diphthong) * VOWEL_MULTIPLIER
    } else {
        codepoint += KoreanLetters.getVowelIndex(medVow[0]) * VOWEL_MULTIPLIER
    }

    val tLength = tCons?.length ?: return codepoint

    if (tLength == 2) {
        val complex = KoreanLetters.getComplexConsonant(tCons[0], tCons[1]) ?: return 0xFFFD
        codepoint += KoreanLetters.getTrailingIndex(complex)
    } else {
        codepoint += KoreanLetters.getTrailingIndex(tCons[0])
    }

    return codepoint
}
