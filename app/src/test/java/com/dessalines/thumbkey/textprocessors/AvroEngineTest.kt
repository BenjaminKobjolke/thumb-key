package com.dessalines.thumbkey.textprocessors

import org.junit.Assert.assertEquals
import org.junit.Test

class AvroEngineTest {
    @Test
    fun convertsRepresentativeAvroInputs() {
        mapOf(
            "ami" to "আমি",
            "bangla" to "বাংলা",
            "kemon" to "কেমন",
            "ami bhalo achi" to "আমি ভাল আছি",
            "OI" to "ঐ",
            "rri" to "ঋ",
            "krri" to "কৃ",
            "kOI" to "কৈ",
            "shoshur" to "শশুr",
            "x" to "এক্স",
            "wa" to "ওযা",
            "ry" to "র‍্য",
        ).forEach { (input, expected) -> assertEquals(input, expected, AvroEngine.convert(input)) }
    }
}
