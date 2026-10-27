package com.dessalines.thumbkey.ui.components.settings.summera

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.FindReplace
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import de.xida.aichatapi.Credentials
import kotlinx.coroutines.Job
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

internal data class SummeraSignInResumeState(
    val context: Context,
    val credentials: Credentials?,
    val signInJob: Job?,
    val onCredentialsChanged: (Credentials?) -> Unit,
    val onActivePromptChanged: (String?) -> Unit,
    val onResumeSignIn: (String) -> Unit,
)

@Composable
internal fun RestoreSummeraSignInOnResume(state: SummeraSignInResumeState) {
    // On each return, restore sign-in state and resume polling after lifecycle cancellation.
    LifecycleResumeEffect(Unit) {
        var currentCredentials = state.credentials
        if (currentCredentials == null) {
            currentCredentials = SummeraAccount.credentials(state.context)
            state.onCredentialsChanged(currentCredentials)
        }
        state.onActivePromptChanged(SummeraAccount.activePromptText(state.context))
        if (currentCredentials == null && state.signInJob == null) {
            SummeraAccount.pendingCode(state.context)?.let(state.onResumeSignIn)
        }
        onPauseOrDispose { }
    }
}

internal data class SummeraAccountPreferenceState(
    val credentials: Credentials?,
    val signInJob: Job?,
    val signInError: String?,
    val activePrompt: String?,
    val navController: NavController,
    val onSignIn: () -> Unit,
    val onCancelSignIn: () -> Unit,
    val onSignOut: () -> Unit,
)

internal data class SummeraSettingsPreferenceState(
    val account: SummeraAccountPreferenceState,
    val microphoneGranted: Boolean,
    val onRequestMicrophone: () -> Unit,
    val navController: NavController,
)

@Composable
internal fun SettingsPreferences(
    modifier: Modifier,
    state: SummeraSettingsPreferenceState,
) {
    Column(modifier = modifier) {
        ProvidePreferenceTheme {
            if (state.account.credentials != null) {
                SignedInPreferences(state.account)
            } else {
                SignInPreference(state.account)
            }
            MicrophonePreference(state.microphoneGranted, state.onRequestMicrophone)
            DebugLogPreference(state.navController)
        }
    }
}

@Composable
private fun SignInPreference(state: SummeraAccountPreferenceState) {
    if (state.signInJob != null) {
        Preference(
            title = { Text(stringResource(R.string.summera_signing_in)) },
            summary = { Text(stringResource(R.string.summera_cancel)) },
            icon = { Icon(Icons.Outlined.HourglassTop, contentDescription = null) },
            onClick = state.onCancelSignIn,
        )
    } else {
        Preference(
            title = { Text(stringResource(R.string.summera_sign_in)) },
            summary = state.signInError?.let { { Text(stringResource(R.string.summera_failed, it)) } },
            icon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = null) },
            onClick = state.onSignIn,
        )
    }
}

@Composable
private fun MicrophonePreference(
    microphoneGranted: Boolean,
    onRequestMicrophone: () -> Unit,
) {
    Preference(
        title = { Text(stringResource(R.string.summera_microphone_title)) },
        summary = {
            val resource =
                if (microphoneGranted) {
                    R.string.summera_microphone_granted
                } else {
                    R.string.summera_microphone_not_granted
                }
            Text(stringResource(resource))
        },
        icon = { Icon(Icons.Outlined.Mic, contentDescription = null) },
        onClick = onRequestMicrophone,
    )
}

@Composable
private fun DebugLogPreference(navController: NavController) {
    Preference(
        title = { Text(stringResource(R.string.summera_debug_log)) },
        icon = { Icon(Icons.Outlined.BugReport, contentDescription = null) },
        onClick = { navController.navigate("summeraLog") },
    )
}

@Composable
private fun SignedInPreferences(state: SummeraAccountPreferenceState) {
    SignedInAccountPreferences(state.credentials?.email.orEmpty(), state.onSignOut)
    SummeraFeaturePreferences(state.activePrompt, state.navController)
}

@Composable
private fun SignedInAccountPreferences(
    email: String,
    onSignOut: () -> Unit,
) {
    Preference(
        title = { Text(stringResource(R.string.summera_signed_in_as, email)) },
        icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
    )
    Preference(
        title = { Text(stringResource(R.string.summera_sign_out)) },
        icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
        onClick = onSignOut,
    )
}

@Composable
private fun SummeraFeaturePreferences(
    activePrompt: String?,
    navController: NavController,
) {
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
