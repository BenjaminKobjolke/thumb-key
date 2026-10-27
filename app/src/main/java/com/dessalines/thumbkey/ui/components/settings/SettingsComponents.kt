package com.dessalines.thumbkey.ui.components.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.KeyboardAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.ShortText
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.ui.components.common.SearchableMultiSelectListPreference
import com.dessalines.thumbkey.ui.components.settings.about.USER_GUIDE_URL
import com.dessalines.thumbkey.utils.KeyboardLayout
import com.dessalines.thumbkey.utils.openLink
import com.dessalines.thumbkey.utils.stripCombiningMarks
import me.zhanghai.compose.preference.Preference

/** Values and callbacks used to render the main settings navigation. */
internal data class SettingsFeatureState(
    val navController: NavController,
    val thumbkeyEnabled: Boolean,
    val thumbkeySelected: Boolean,
    val layoutsState: Set<KeyboardLayout>,
    val onLayoutsChange: (Set<KeyboardLayout>) -> Unit,
)

@Composable
internal fun SettingsFeatureItems(state: SettingsFeatureState) {
    SettingsLayoutItems(state)
    SettingsNavigationItems(state.navController)
}

@Composable
private fun SettingsLayoutItems(state: SettingsFeatureState) {
    if (!(state.thumbkeyEnabled || state.thumbkeySelected)) {
        Preference(
            title = { Text(stringResource(R.string.setup)) },
            icon = { Icon(Icons.Outlined.InstallMobile, contentDescription = null) },
            onClick = { state.navController.navigate("setup") },
        )
    }

    SearchableMultiSelectListPreference(
        value = state.layoutsState,
        values = KeyboardLayout.entries.sortedBy { it.keyboardDefinition.title },
        valueToText = { AnnotatedString(it.keyboardDefinition.title) },
        searchFilter = ::matchesSearchQuery,
        searchPlaceholder = stringResource(R.string.search_layouts),
        emptySearchText = stringResource(R.string.no_layouts_found),
        onValueChange = state.onLayoutsChange,
        icon = { Icon(Icons.Outlined.KeyboardAlt, contentDescription = null) },
        title = { Text(stringResource(R.string.layouts)) },
        summary = {
            val layoutsStr = state.layoutsState.joinToString(", ") { it.keyboardDefinition.title }
            Text(layoutsStr)
        },
    )
}

@Composable
private fun SettingsNavigationItems(navController: NavController) {
    SettingsMainNavigationItems(navController)
    SettingsProductNavigationItems(navController)
}

@Composable
private fun SettingsMainNavigationItems(navController: NavController) {
    Preference(
        title = { Text(stringResource(R.string.look_and_feel)) },
        icon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
        onClick = { navController.navigate("lookAndFeel") },
    )
    Preference(
        title = { Text(stringResource(R.string.behavior)) },
        icon = { Icon(Icons.Outlined.TouchApp, contentDescription = null) },
        onClick = { navController.navigate("behavior") },
    )
}

@Composable
private fun SettingsProductNavigationItems(navController: NavController) {
    Preference(
        title = { Text(stringResource(R.string.abbreviations)) },
        icon = {
            @Suppress("DEPRECATION")
            Icon(Icons.Outlined.ShortText, contentDescription = null)
        },
        onClick = { navController.navigate("abbreviations") },
    )
    Preference(
        title = { Text(stringResource(R.string.word_suggestions_title)) },
        icon = { Icon(Icons.Outlined.Spellcheck, contentDescription = null) },
        onClick = { navController.navigate("wordSuggestions") },
    )
    Preference(
        title = { Text(stringResource(R.string.clipboard_history)) },
        icon = { Icon(Icons.Outlined.ContentPaste, contentDescription = null) },
        onClick = { navController.navigate("clipboardSettings") },
    )
    Preference(
        title = { Text(stringResource(R.string.summera_title)) },
        icon = { Icon(Icons.Outlined.Mic, contentDescription = null) },
        onClick = { navController.navigate("summera") },
    )
}

@Composable
internal fun SettingsAppItems(navController: NavController) {
    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(R.string.modify_keys)) },
        icon = { Icon(Icons.Outlined.AppRegistration, contentDescription = null) },
        onClick = { navController.navigate("modifyKeys") },
    )
    Preference(
        title = { Text(stringResource(R.string.backup_and_restore)) },
        icon = { Icon(Icons.Outlined.Restore, contentDescription = null) },
        onClick = { navController.navigate("backupAndRestore") },
    )
    Preference(
        title = { Text(stringResource(R.string.other)) },
        icon = { Icon(Icons.Outlined.Build, contentDescription = null) },
        onClick = { navController.navigate("otherSettings") },
    )
    Preference(
        title = { Text(stringResource(R.string.user_guide)) },
        icon = { Icon(Icons.AutoMirrored.Outlined.HelpCenter, contentDescription = null) },
        onClick = { openLink(USER_GUIDE_URL, context) },
    )
    Preference(
        title = { Text(stringResource(R.string.about)) },
        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
        onClick = { navController.navigate("about") },
    )
}

/** Decides whether a keyboard layout matches the text entered in the searchable list. */
private fun matchesSearchQuery(
    keyboardLayout: KeyboardLayout,
    text: String,
    query: String,
): Boolean {
    if (query.isBlank()) return true
    return stripCombiningMarks(keyboardLayout.name).contains(stripCombiningMarks(query), ignoreCase = true)
}
