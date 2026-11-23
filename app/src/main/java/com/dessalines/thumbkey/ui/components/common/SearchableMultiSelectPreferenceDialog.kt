package com.dessalines.thumbkey.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T> SearchableMultiSelectPreferenceDialog(
    data: SearchableMultiSelectListPreferenceData<T>,
    title: @Composable () -> Unit,
    onValueChange: (Set<T>) -> Unit,
    onDismiss: () -> Unit,
) {
    var dialogValue by rememberSaveable { mutableStateOf(data.value) }
    var query by rememberSaveable { mutableStateOf("") }
    // Snapshot of the selection when the dialog opened. Unchecking an item keeps it pinned
    // until the dialog is closed and reopened.
    val pinnedValues = rememberSaveable { data.values.filter { it in data.value } }
    val filteredPinnedValues =
        remember(pinnedValues, query) {
            pinnedValues.filter { data.searchFilter(it, data.valueToText(it).text, query) }
        }
    val filteredValues =
        remember(data.values, query) {
            data.values.filter { data.searchFilter(it, data.valueToText(it).text, query) }
        }
    val onToggle: (T, Boolean) -> Unit = { itemValue, checked ->
        dialogValue = if (checked) dialogValue + itemValue else dialogValue - itemValue
    }
    val rows = SearchableMultiSelectDialogRows(filteredPinnedValues, filteredValues, dialogValue)
    val content = SearchableMultiSelectDialogContent(query, rows, onToggle)
    val actions =
        SearchableMultiSelectDialogActions(
            onQueryChange = { query = it },
            onCancel = onDismiss,
            onConfirm = {
                onValueChange(dialogValue)
                onDismiss()
            },
        )

    SearchableMultiSelectDialog(data, title, content, actions)
}

private data class SearchableMultiSelectDialogContent<T>(
    val query: String,
    val rows: SearchableMultiSelectDialogRows<T>,
    val onToggle: (T, Boolean) -> Unit,
)

private data class SearchableMultiSelectDialogActions(
    val onQueryChange: (String) -> Unit,
    val onCancel: () -> Unit,
    val onConfirm: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SearchableMultiSelectDialog(
    data: SearchableMultiSelectListPreferenceData<T>,
    title: @Composable () -> Unit,
    content: SearchableMultiSelectDialogContent<T>,
    actions: SearchableMultiSelectDialogActions,
) {
    BasicAlertDialog(onDismissRequest = actions.onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SearchableMultiSelectDialogBody(data, title, content, actions)
            }
        }
    }
}

@Composable
private fun <T> SearchableMultiSelectDialogBody(
    data: SearchableMultiSelectListPreferenceData<T>,
    title: @Composable () -> Unit,
    content: SearchableMultiSelectDialogContent<T>,
    actions: SearchableMultiSelectDialogActions,
) {
    SearchableMultiSelectDialogTitle(title)
    SearchableMultiSelectDialogSearchField(
        query = content.query,
        onQueryChange = actions.onQueryChange,
        placeholder = data.searchPlaceholder,
    )
    SearchableMultiSelectDialogValues(
        items = SearchableMultiSelectDialogItems(data, content.rows),
        onToggle = content.onToggle,
        modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
    )
    SearchableMultiSelectDialogButtons(actions)
}

@Composable
private fun SearchableMultiSelectDialogTitle(title: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides AlertDialogDefaults.titleContentColor,
            LocalTextStyle provides LocalTextStyle.current.merge(MaterialTheme.typography.headlineSmall),
            content = title,
        )
    }
}

@Composable
private fun SearchableMultiSelectDialogSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon =
            if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                        )
                    }
                }
            } else {
                null
            },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}

private data class SearchableMultiSelectDialogRows<T>(
    val pinnedValues: List<T>,
    val values: List<T>,
    val selectedValues: Set<T>,
)

private data class SearchableMultiSelectDialogItems<T>(
    val data: SearchableMultiSelectListPreferenceData<T>,
    val rows: SearchableMultiSelectDialogRows<T>,
)

@Composable
private fun <T> SearchableMultiSelectDialogValues(
    itemData: SearchableMultiSelectDialogItems<T>,
    onToggle: (T, Boolean) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (itemData.rows.values.isEmpty()) {
            item {
                Text(
                    text = itemData.data.emptySearchText,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        items(itemData.rows.pinnedValues) { value ->
            SearchableMultiSelectItem(
                text = itemData.data.valueToText(value),
                checked = value in itemData.rows.selectedValues,
                onToggle = { onToggle(value, it) },
            )
        }
        if (itemData.rows.pinnedValues.isNotEmpty()) {
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
        }
        items(itemData.rows.values) { value ->
            SearchableMultiSelectItem(
                text = itemData.data.valueToText(value),
                checked = value in itemData.rows.selectedValues,
                onToggle = { onToggle(value, it) },
            )
        }
    }
}

@Composable
private fun SearchableMultiSelectDialogButtons(actions: SearchableMultiSelectDialogActions) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
    ) {
        TextButton(onClick = actions.onCancel) { Text(text = stringResource(android.R.string.cancel)) }
        TextButton(onClick = actions.onConfirm) { Text(text = stringResource(android.R.string.ok)) }
    }
}

@Composable
private fun SearchableMultiSelectItem(
    text: AnnotatedString,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onToggle)
                .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
