package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.ColorVariant.SURFACE_VARIANT
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardC

fun reverseKeyItemC(item: KeyItemC): KeyItemC {
    val revItem =
        item.copy(
            left = item.right,
            right = item.left,
            topLeft = item.topRight,
            topRight = item.topLeft,
            bottomLeft = item.bottomRight,
            bottomRight = item.bottomLeft,
        )
    return revItem
}

fun reverseRow(l: List<KeyItemC>): List<KeyItemC> {
    val rev = mutableListOf<KeyItemC>()
    for (item in l) {
        val revItem = reverseKeyItemC(item)
        rev.add(0, revItem)
    }
    return rev
}

fun reverseRowExceptControl(l: List<KeyItemC>): List<KeyItemC> {
    val rev = mutableListOf<KeyItemC>()
    for (item in l) {
        if (item.backgroundColor == SURFACE_VARIANT) {
            rev.add(item)
        } else {
            val revItem = reverseKeyItemC(item)
            rev.add(0, revItem)
        }
    }
    return rev
}

fun reverseRowOnlyControl(l: List<KeyItemC>): List<KeyItemC> {
    val grid = mutableListOf<KeyItemC>()
    for (item in l) {
        if (item.backgroundColor == SURFACE_VARIANT) {
            val revItem = reverseKeyItemC(item)
            grid.add(0, revItem)
        } else {
            grid.add(item)
        }
    }
    return grid
}

fun makeControlLeftSide(kb: KeyboardC): KeyboardC {
    val kbArrNew = mutableListOf<List<KeyItemC>>()
    for (row in kb.arr) {
        kbArrNew.add(reverseRowOnlyControl(row))
    }
    return KeyboardC(kbArrNew)
}

fun makeLeftHanded(kb: KeyboardC): KeyboardC {
    val kbArrNew = mutableListOf<List<KeyItemC>>()
    for (row in kb.arr) {
        kbArrNew.add(reverseRowExceptControl(row))
    }
    return KeyboardC(kbArrNew)
}

fun makeLeftHandedControlLeftSide(kb: KeyboardC): KeyboardC {
    val kbArrNew = mutableListOf<List<KeyItemC>>()
    for (row in kb.arr) {
        kbArrNew.add(reverseRow(row))
    }
    return KeyboardC(kbArrNew)
}
