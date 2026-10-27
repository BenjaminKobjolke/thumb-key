package com.dessalines.thumbkey.ui.components.settings.summera

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SUMMERA_LOGIN_URL
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.summera.hasMicrophonePermission
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var credentials by remember { mutableStateOf(SummeraAccount.credentials(ctx)) }
    var signInJob by remember { mutableStateOf<Job?>(null) }
    var signInError by remember { mutableStateOf<String?>(null) }
    var microphoneGranted by remember { mutableStateOf(hasMicrophonePermission(ctx)) }
    var activePrompt by remember { mutableStateOf(SummeraAccount.activePromptText(ctx)) }

    val microphoneLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            microphoneGranted = it
        }

    fun logDebug(line: String) = SummeraAccount.log(ctx, line)

    // resumeCode: a stored pending code to keep polling with, null starts a fresh sign-in
    fun signIn(resumeCode: String? = null) {
        signInError = null
        signInJob =
            scope.launch {
                try {
                    logDebug("sign-in start, resume=${resumeCode != null}")
                    val client = SummeraAccount.client(ctx)
                    val code =
                        resumeCode ?: run {
                            val registerCode =
                                withContext(Dispatchers.IO) { client.auth.createRegisterCode(SUMMERA_LOGIN_URL) }
                            SummeraAccount.savePending(ctx, registerCode.code)
                            logDebug("code ${registerCode.code.take(6)}…, opening browser")
                            CustomTabsIntent.Builder().build().launchUrl(ctx, registerCode.url.toUri())
                            registerCode.code
                        }

                    val result = awaitSignIn(client, code, ::logDebug)
                    SummeraAccount.clearPending(ctx)
                    logDebug("poll finished: ${if (result == null) "timeout" else "signed in"}")
                    if (result == null) {
                        signInError = ctx.getString(R.string.summera_timeout)
                    } else {
                        SummeraAccount.save(ctx, result)
                        credentials = result
                        // A resumed sign-in already has this screen in front
                        if (resumeCode == null) bringToFront(ctx)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Anything escaping this job would take the whole process down
                    SummeraAccount.clearPending(ctx)
                    Log.e(TAG, "Summera sign-in failed", e)
                    logDebug("sign-in failed: ${e.message}")
                    signInError = e.message ?: e::class.java.simpleName
                } finally {
                    // Not clearing the pending code here: a cancellation by the lifecycle has to
                    // leave it for the resume
                    signInJob = null
                }
            }
    }

    RestoreSummeraSignInOnResume(
        state =
            SummeraSignInResumeState(
                context = ctx,
                credentials = credentials,
                signInJob = signInJob,
                onCredentialsChanged = { credentials = it },
                onActivePromptChanged = { activePrompt = it },
                onResumeSignIn = { signIn(it) },
            ),
    )

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_title),
                navController = navController,
            )
        },
        content = { padding ->
            SettingsPreferences(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .background(color = MaterialTheme.colorScheme.surface),
                state =
                    SummeraSettingsPreferenceState(
                        account =
                            SummeraAccountPreferenceState(
                                credentials = credentials,
                                signInJob = signInJob,
                                signInError = signInError,
                                activePrompt = activePrompt,
                                navController = navController,
                                onSignIn = { signIn() },
                                onCancelSignIn = {
                                    SummeraAccount.clearPending(ctx)
                                    signInJob?.cancel()
                                },
                                onSignOut = {
                                    SummeraAccount.clear(ctx)
                                    credentials = null
                                },
                            ),
                        microphoneGranted = microphoneGranted,
                        onRequestMicrophone = { microphoneLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        navController = navController,
                    ),
            )
        },
    )
}
