package com.dessalines.thumbkey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.Abbreviation
import com.dessalines.thumbkey.db.AbbreviationRepository
import com.dessalines.thumbkey.db.AbbreviationViewModel
import com.dessalines.thumbkey.db.AbbreviationViewModelFactory
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.utils.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbbreviationsScreen(
    navController: NavController,
    abbreviationViewModel: AbbreviationViewModel =
        viewModel(
            factory =
                AbbreviationViewModelFactory(
                    AbbreviationRepository(
                        AppDB.getDatabase(LocalContext.current).abbreviationDao(),
                    ),
                ),
        ),
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var abbreviationToEdit by remember { mutableStateOf<Abbreviation?>(null) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.abbreviations),
                navController = navController,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add abbreviation")
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.abbreviations_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            val abbreviations by abbreviationViewModel.allAbbreviations.observeAsState(initial = emptyList())
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // Add padding for FAB
            ) {
                items(abbreviations) { abbreviation ->
                    AbbreviationItem(
                        abbreviation = abbreviation,
                        onEdit = { abbreviationToEdit = it },
                        onDelete = { abbreviationViewModel.delete(it.id) },
                    )
                }
            }
        }

        if (showAddDialog) {
            AddEditAbbreviationDialog(
                abbreviation = null,
                onDismiss = { showAddDialog = false },
                onSave = { abbr, expansion ->
                    abbreviationViewModel.insertOrUpdate(abbr, expansion)
                    showAddDialog = false
                },
            )
        }

        abbreviationToEdit?.let { abbreviation ->
            AddEditAbbreviationDialog(
                abbreviation = abbreviation,
                onDismiss = { abbreviationToEdit = null },
                onSave = { abbr, expansion ->
                    abbreviationViewModel.insertOrUpdate(abbr, expansion, abbreviation.id)
                    abbreviationToEdit = null
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbbreviationItem(
    abbreviation: Abbreviation,
    onEdit: (Abbreviation) -> Unit,
    onDelete: (Abbreviation) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        onClick = { onEdit(abbreviation) },
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = abbreviation.abbreviation,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = abbreviation.expansion,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = { onDelete(abbreviation) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddEditAbbreviationDialog(
    abbreviation: Abbreviation?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var abbr by remember { mutableStateOf(abbreviation?.abbreviation ?: "") }
    var expansion by remember { mutableStateOf(abbreviation?.expansion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text =
                    if (abbreviation == null) {
                        stringResource(R.string.add_abbreviation)
                    } else {
                        stringResource(R.string.edit_abbreviation)
                    },
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = abbr,
                    onValueChange = { abbr = it },
                    label = { Text(stringResource(R.string.abbreviation)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expansion,
                    onValueChange = { expansion = it },
                    label = { Text(stringResource(R.string.expansion)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (abbr.isNotBlank() && expansion.isNotBlank()) {
                        onSave(abbr, expansion)
                    }
                },
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
