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

val KB_DE_TYPESPLITBEN_NUMERIC =
    KeyboardC(
        listOf(
            listOf(
                textEditKeyItem(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                            action = ToggleClipboardMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                ).copy(right = TRANSCRIPT_HISTORY_KEYC),
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    bottomRight = KeyC("!"),
                    top = KeyC("¯\\_(ツ)_/¯", size = SMALLEST),
                    bottom = KeyC("~"),
                    left = KeyC("{"),
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    bottom = KeyC("@"),
                    topLeft = KeyC("`"),
                    topRight = KeyC("´"),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    right = KeyC("}"),
                    topRight = KeyC("°"),
                    bottomLeft = KeyC("#"),
                ),
                KeyItemC(
                    center = KeyC(".", size = LARGE),
                ),
            ),
            listOf(
                KeyItemC(
                    center =
                        SPACEBAR_CENTER_KEYC.copy(
                            display = SWITCH_IME_VOICE_KEYC.display,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    top = KeyC("+"),
                    bottom = KeyC("="),
                    left = KeyC("-"),
                    right = KeyC("_"),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = SwitchIMEVoice,
                ),
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    top = KeyC("\""),
                    bottom = KeyC(":"),
                    left = KeyC("("),
                    right = KeyC("$"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    left = KeyC("€"),
                    right = KeyC("£"),
                    bottom = KeyC("%"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    top = KeyC("'"),
                    bottom = KeyC(";"),
                    left = KeyC("^"),
                    right = KeyC(")"),
                ),
                KeyItemC(
                    center = KeyC(",", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Mood),
                            action = ToggleEmojiMode(true),
                            color = MUTED,
                        ),
                ),
            ),
            listOf(
                SPACEBAR_SKINNY_KEY_ITEM,
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    topLeft = KeyC("["),
                    topRight = KeyC("&"),
                    bottomLeft = KeyC("<"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    top = KeyC("*"),
                    bottom = KeyC("?"),
                    left = KeyC("/"),
                    right = KeyC("\\"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    left = KeyC("|"),
                    topRight = KeyC("]"),
                    bottomRight = KeyC(">"),
                ),
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                ),
            ),
            listOf(
                ABC_KEY_ITEM_ALT,
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
