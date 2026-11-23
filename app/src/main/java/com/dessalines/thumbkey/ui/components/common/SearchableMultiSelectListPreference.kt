package com.dessalines.thumbkey.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import me.zhanghai.compose.preference.Preference

/** Data and presentation options for the searchable multi-select preference. */
internal data class SearchableMultiSelectListPreferenceData<T>(
    val value: Set<T>,
    val values: List<T>,
    val searchPlaceholder: String,
    val emptySearchText: String,
    val valueToText: (T) -> AnnotatedString = { AnnotatedString(it.toString()) },
    val searchFilter: (value: T, text: String, query: String) -> Boolean = { _, text, query ->
        text.contains(query, ignoreCase = true)
    },
)

internal data class SearchableMultiSelectListPreferencePresentation(
    val title: @Composable () -> Unit,
    val modifier: Modifier = Modifier,
    val icon: (@Composable () -> Unit)? = null,
    val summary: (@Composable () -> Unit)? = null,
)

/**
 * A variant of [me.zhanghai.compose.preference.MultiSelectListPreference] whose dialog has a
 * search box that filters the list of values. Values selected when the dialog opened stay pinned.
 */
@Composable
internal fun <T> SearchableMultiSelectListPreference(
    data: SearchableMultiSelectListPreferenceData<T>,
    presentation: SearchableMultiSelectListPreferencePresentation,
    onValueChange: (Set<T>) -> Unit,
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    Preference(
        title = presentation.title,
        modifier = presentation.modifier,
        icon = presentation.icon,
        summary = presentation.summary,
    ) {
        openDialog = true
    }
    if (openDialog) {
        SearchableMultiSelectPreferenceDialog(
            data = data,
            title = presentation.title,
            onValueChange = onValueChange,
            onDismiss = { openDialog = false },
        )
    }
}
