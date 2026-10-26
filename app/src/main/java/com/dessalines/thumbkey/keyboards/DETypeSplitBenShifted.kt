@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_DE_TYPESPLITBEN_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("E", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("Q"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("Q"),
                        ),
                    bottom = KeyC("W"),
                ),
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                            action = ToggleClipboardMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = EIGHT_WAY,
                    top = COPY_KEYC,
                    topLeft = SELECT_ALL_KEYC,
                    topRight = CUT_KEYC,
                    right = TRANSCRIPT_HISTORY_KEYC,
                    bottom = PASTE_KEYC,
                    bottomLeft = UNDO_KEYC,
                    bottomRight = REDO_KEYC,
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("T", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("Z"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("Z"),
                        ),
                    bottom = KeyC("U"),
                    top = KeyC("Ü", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("P"),
                        ),
                    left = KeyC("P"),
                    bottom = KeyC("O"),
                    top = KeyC("Ö", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("A", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = null,
                            action = CommitText("Ä"),
                        ),
                    top = KeyC("Ä", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("S", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("ẞ", color = MUTED),
                    top =
                        KeyC(
                            display = null,
                            action = CommitText("ẞ"),
                        ),
                ),
                SPACEBAR_MIC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("D", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("F"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("F"),
                        ),
                    bottom = KeyC("G"),
                ),
                KeyItemC(
                    center = KeyC("H", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("K"),
                        ),
                    left = KeyC("K"),
                    bottom = KeyC("L"),
                    top = KeyC("J"),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("C", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("X"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("X"),
                        ),
                    bottom = KeyC("Y"),
                ),
                KeyItemC(
                    center = KeyC("B", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("V"),
                ),
                SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM.copy(
                    center = SPACEBAR_MIC_CENTER_KEYC,
                    longPress = SwitchIMEVoice,
                ),
                KeyItemC(
                    center = KeyC("N", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("M", size = LARGE),
                ),
            ),
            listOf(
                KeyItemC(
                    backgroundColor = SURFACE_VARIANT,
                    swipeType = EIGHT_WAY,
                    center = NUMERIC_KEY_ITEM.center,
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Settings),
                            action = GotoSettings,
                            color = MUTED,
                        ),
                ),
                cursorControlKeyItem(shiftMode = false),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("Entf"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_FORWARD_DEL,
                                    ),
                                ),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordAfterCursor,
                ),
                RETURN_KEY_ITEM,
            ),
        ),
    )
