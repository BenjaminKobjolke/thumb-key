package com.dessalines.thumbkey.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.DEFAULT_KEYBOARD_LAYOUT
import com.dessalines.thumbkey.db.LayoutsUpdate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.text.NumberFormat

// Unicode category Mn (Mark, nonspacing): accents that attach to the preceding letter.
private val COMBINING_MARKS_REGEX = Regex("""\p{Mn}+""")

// Decompose characters (é -> e + ◌́) and drop the marks, so "francais" matches "Français".
fun stripCombiningMarks(s: String): String = COMBINING_MARKS_REGEX.replace(Normalizer.normalize(s, Normalizer.Form.NFD), "")

const val TAG = "com.thumbkey"

const val IME_ACTION_CUSTOM_LABEL = EditorInfo.IME_MASK_ACTION + 1
const val ANIMATION_SPEED = 300

fun keyboardPositionToAlignment(position: KeyboardPosition): Alignment =
    when (position) {
        KeyboardPosition.Right -> Alignment.BottomEnd
        KeyboardPosition.Center -> Alignment.BottomCenter
        KeyboardPosition.Left -> Alignment.BottomStart
        KeyboardPosition.Dual -> Alignment.BottomStart
    }

/**
 * Returns the correct keyboard mode
 */
fun getKeyboardMode(
    ime: IMEService,
    autoCapitalize: Boolean,
): KeyboardMode {
    val inputType = ime.currentInputEditorInfo.inputType and (InputType.TYPE_MASK_CLASS)

    return if (listOf(
            InputType.TYPE_CLASS_NUMBER,
            InputType.TYPE_CLASS_PHONE,
        ).contains(inputType)
    ) {
        Log.d(TAG, "getKeyboardMode: Setting NUMERIC mode due to number/phone input type")
        KeyboardMode.NUMERIC
    } else {
        val isUriOrEmail = isUriOrEmailOrPasswordField(ime)
        val shouldAutoCapitalize = autoCapitalizeCheck(ime)
        Log.d(TAG, "getKeyboardMode: Is URI/Email/Password field: $isUriOrEmail")
        Log.d(TAG, "getKeyboardMode: Should auto capitalize: $shouldAutoCapitalize")

        if (autoCapitalize && !isUriOrEmail && shouldAutoCapitalize) {
            Log.d(TAG, "getKeyboardMode: Setting SHIFTED mode due to auto capitalize")
            KeyboardMode.SHIFTED
        } else {
            Log.d(TAG, "getKeyboardMode: Setting MAIN mode")
            KeyboardMode.MAIN
        }
    }
}

fun getCurrentLayoutColumnCount(keyboardLayout: Int): Int {
    val currentLayout = KeyboardLayout.entries[keyboardLayout]
    val keyboardDefinition = currentLayout.keyboardDefinition
    val mainKeyboard = keyboardDefinition.modes.main
    val columnCount = mainKeyboard.arr.maxOf { it.size }
    return columnCount
}

fun getCurrentDisplayWidth(ctx: Context): Int {
    val displayMetrics = ctx.resources.displayMetrics
    return (displayMetrics.widthPixels / displayMetrics.density).toInt()
}

fun getAutoKeyWidth(
    keyboardLayout: Int,
    keyPadding: Int,
    position: KeyboardPosition,
    ctx: Context,
): Int {
    val screenWidth = getCurrentDisplayWidth(ctx)
    val availableWidth = screenWidth - (keyPadding * 2)
    val multiplier =
        when (position) {
            KeyboardPosition.Dual -> 2
            else -> 1
        }
    val columns = getCurrentLayoutColumnCount(keyboardLayout) * multiplier
    return (availableWidth / columns)
}

fun autoCapitalizeCheck(ime: IMEService): Boolean {
    val connection = ime.currentInputConnection ?: return false
    val editorInfo = ime.currentInputEditorInfo ?: return false
    return connection.getCursorCapsMode(editorInfo.inputType) > 0
}

/**
 * Avoid capitalizing or switching to shifted mode in certain edit boxes
 */
fun isUriOrEmailOrPasswordField(ime: IMEService): Boolean {
    val editorInfo = ime.currentInputEditorInfo ?: return false
    val inputType = editorInfo.inputType and (InputType.TYPE_MASK_VARIATION)
    return listOf(
        InputType.TYPE_TEXT_VARIATION_URI,
        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_NUMBER_VARIATION_PASSWORD,
    ).contains(inputType) ||
        ime.currentInputEditorInfo.inputType == EditorInfo.TYPE_NULL
}

fun isPasswordField(ime: IMEService): Boolean {
    val editorInfo = ime.currentInputEditorInfo ?: return false
    val inputType = editorInfo.inputType and (InputType.TYPE_MASK_VARIATION)
    return listOf(
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
        InputType.TYPE_NUMBER_VARIATION_PASSWORD,
    ).contains(inputType)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    text: String,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showBack: Boolean = true,
) {
    val activity = LocalActivity.current
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = text,
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = {
                    // If there's no previous destination, finish the activity
                    // This handles the case when navigating directly to a screen via intent
                    if (navController.previousBackStackEntry == null) {
                        activity?.finish()
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.settings),
                    )
                }
            }
        },
    )
}

fun openLink(
    url: String,
    ctx: Context,
) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    ctx.startActivity(intent)
}

fun Int.toBool() = this == 1

fun Boolean.toInt() = this.compareTo(false)

/**
 * The layouts there are whats stored in the DB, a string comma set of title index numbers
 */
fun keyboardLayoutsSetFromDbIndexString(layouts: String?): Set<KeyboardLayout> =
    layouts?.split(",")?.map { KeyboardLayout.entries[it.trim().toInt()] }?.toSet()
        ?: setOf(
            KeyboardLayout.entries[DEFAULT_KEYBOARD_LAYOUT],
        )

fun Context.getPackageInfo(): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }

fun Context.getVersionCode(): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        getPackageInfo().longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        getPackageInfo().versionCode
    }

/**
 * The debug and app IME names act strange, so you need to check both
 */
fun Context.getImeNames(): List<String> =
    listOf(
        "$packageName/com.dessalines.thumbkey.IMEService",
        "$packageName/.IMEService",
    )

fun getLocalCurrency(): String? =
    ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]?.let {
        NumberFormat
            .getCurrencyInstance(
                it,
            ).currency
            ?.symbol
    }

fun lastColKeysToFirst(board: KeyboardC): KeyboardC {
    val newArr =
        board.arr.map { row ->
            if (row.isNotEmpty()) {
                listOf(row.last()) + row.dropLast(1)
            } else {
                row
            }
        }
    return KeyboardC(newArr)
}

/**
 * drop all first elements of a list that satisfy a given predicate
 */
fun updateLayouts(
    appSettingsViewModel: AppSettingsViewModel,
    layoutsState: Set<KeyboardLayout>,
) {
    appSettingsViewModel.updateLayouts(
        LayoutsUpdate(
            id = 1,
            // Set the current to the first
            keyboardLayout = layoutsState.first().ordinal,
            keyboardLayouts =
                layoutsState
                    .map { it.ordinal }
                    .joinToString(),
        ),
    )
}
