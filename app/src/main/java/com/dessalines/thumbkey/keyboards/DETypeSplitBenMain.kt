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

val KB_DE_TYPESPLITBEN_MAIN =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("e", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("q"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("q"),
                        ),
                    bottom = KeyC("w"),
                ),
                KeyItemC(
                    center = KeyC("r", size = LARGE),
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
                    center = KeyC("t", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("z"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("z"),
                        ),
                    bottom = KeyC("u"),
                    top = KeyC("ü", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("i", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("p"),
                        ),
                    left = KeyC("p"),
                    bottom = KeyC("o"),
                    top = KeyC("ö", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("a", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = null,
                            action = CommitText("ä"),
                        ),
                    top = KeyC("ä", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("s", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("ß", color = MUTED),
                    top =
                        KeyC(
                            display = null,
                            action = CommitText("ß"),
                        ),
                ),
                SPACEBAR_MIC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("d", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("f"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("f"),
                        ),
                    bottom = KeyC("g"),
                ),
                KeyItemC(
                    center = KeyC("h", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("k"),
                        ),
                    left = KeyC("k"),
                    bottom = KeyC("l"),
                    top = KeyC("j"),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("c", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("x"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("x"),
                        ),
                    bottom = KeyC("y"),
                ),
                KeyItemC(
                    center = KeyC("b", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("v"),
                ),
                SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM.copy(
                    center = SPACEBAR_MIC_CENTER_KEYC,
                    longPress = SwitchIMEVoice,
                ),
                KeyItemC(
                    center = KeyC("n", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("m", size = LARGE),
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
                cursorControlKeyItem(shiftMode = true),
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
