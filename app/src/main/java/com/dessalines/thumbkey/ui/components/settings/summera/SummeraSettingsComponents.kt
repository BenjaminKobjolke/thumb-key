package com.dessalines.thumbkey.ui.components.settings.summera

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FindReplace
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import de.xida.aichatapi.Credentials
import me.zhanghai.compose.preference.Preference

@Composable
internal fun SummeraSignedInItems(
    credentials: Credentials?,
    activePrompt: String?,
    navController: NavController,
    onSignOut: () -> Unit,
) {
    Preference(
        title = { Text(stringResource(R.string.summera_signed_in_as, credentials?.email.orEmpty())) },
        icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
    )
    Preference(
        title = { Text(stringResource(R.string.summera_sign_out)) },
        icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
        onClick = onSignOut,
    )
    Preference(
        title = { Text(stringResource(R.string.summera_prompts)) },
        summary = { Text(activePrompt ?: stringResource(R.string.summera_no_prompt)) },
        icon = { Icon(Icons.Outlined.Tune, contentDescription = null) },
        onClick = { navController.navigate("summeraPrompts") },
    )
    Preference(
        title = { Text(stringResource(R.string.summera_replacements)) },
        summary = { Text(stringResource(R.string.summera_replacements_summary)) },
        icon = { Icon(Icons.Outlined.FindReplace, contentDescription = null) },
        onClick = { navController.navigate("summeraReplacements") },
    )
    Preference(
        title = { Text(stringResource(R.string.summera_history)) },
        icon = { Icon(Icons.Outlined.History, contentDescription = null) },
        onClick = { navController.navigate("summeraHistory") },
    )
}
