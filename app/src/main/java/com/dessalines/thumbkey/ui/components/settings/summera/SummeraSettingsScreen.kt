package com.dessalines.thumbkey.ui.components.settings.summera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.dessalines.thumbkey.BuildConfig
import com.dessalines.thumbkey.MainActivity
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SUMMERA_LOGIN_URL
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.summera.hasMicrophonePermission
import com.dessalines.thumbkey.summera.poll
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.XidaAiClient
import de.xida.aichatapi.XidaAiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraSettingsScreen(navController: NavController) {
    Log.d(TAG, "Got to summera settings activity")

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var credentials by remember { mutableStateOf(SummeraAccount.credentials(ctx)) }
    var signInJob by remember { mutableStateOf<Job?>(null) }
    var signInError by remember { mutableStateOf<String?>(null) }
    var microphoneGranted by remember { mutableStateOf(hasMicrophonePermission(ctx)) }
    var lastCrash by remember { mutableStateOf(SummeraAccount.lastCrash(ctx)) }
    var debugLog by remember { mutableStateOf(SummeraAccount.debugLog(ctx)) }

    val timeoutStr = stringResource(R.string.summera_timeout)
    val microphoneLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            microphoneGranted = it
        }

    fun logDebug(line: String) {
        SummeraAccount.logSignIn(ctx, line)
    }

    // resumeCode: a stored pending code to keep polling with, null starts a fresh sign-in
    fun signIn(resumeCode: String? = null) {
        signInError = null
        signInJob =
            scope.launch {
                try {
                    logDebug("sign-in start, resume=${resumeCode != null}")
                    val client = SummeraAccount.signInClient(ctx)
                    val code =
                        resumeCode ?: run {
                            val registerCode = withContext(Dispatchers.IO) { client.auth.createRegisterCode(SUMMERA_LOGIN_URL) }
                            SummeraAccount.savePending(ctx, registerCode.code)
                            logDebug("code ${registerCode.code.take(6)}…, opening browser")
                            CustomTabsIntent.Builder().build().launchUrl(ctx, registerCode.url.toUri())
                            registerCode.code
                        }

                    val result = awaitSignIn(client, code, ::logDebug)
                    SummeraAccount.clearPending(ctx)
                    logDebug("poll finished: ${if (result == null) "timeout" else "signed in"}")
                    if (result == null) {
                        signInError = timeoutStr
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

    // On every return to the screen: shows a sign-in that finished while it was stopped, and picks
    // one up again that the screen or the activity dying cut off
    LifecycleResumeEffect(Unit) {
        if (credentials == null) credentials = SummeraAccount.credentials(ctx)
        lastCrash = SummeraAccount.lastCrash(ctx)
        // As of this resume, not live: it is read after the fact
        debugLog = SummeraAccount.debugLog(ctx)
        if (credentials == null && signInJob == null) {
            SummeraAccount.pendingCode(ctx)?.let { signIn(it) }
        }
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_title),
                navController = navController,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .background(color = MaterialTheme.colorScheme.surface),
            ) {
                ProvidePreferenceTheme {
                    val signedIn = credentials
                    if (signedIn != null) {
                        Preference(
                            title = { Text(stringResource(R.string.summera_signed_in_as, signedIn.email)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                )
                            },
                        )
                        Preference(
                            title = { Text(stringResource(R.string.summera_sign_out)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                SummeraAccount.clear(ctx)
                                credentials = null
                            },
                        )
                    } else if (signInJob != null) {
                        Preference(
                            title = { Text(stringResource(R.string.summera_signing_in)) },
                            summary = { Text(stringResource(R.string.summera_cancel)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.HourglassTop,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                SummeraAccount.clearPending(ctx)
                                signInJob?.cancel()
                            },
                        )
                    } else {
                        Preference(
                            title = { Text(stringResource(R.string.summera_sign_in)) },
                            summary =
                                signInError?.let {
                                    { Text(stringResource(R.string.summera_failed, it)) }
                                },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Login,
                                    contentDescription = null,
                                )
                            },
                            onClick = { signIn() },
                        )
                    }
                    val crash = lastCrash
                    if (BuildConfig.DEBUG && crash != null) {
                        Preference(
                            title = { Text(stringResource(R.string.summera_last_crash)) },
                            summary = { Text(crash) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.BugReport,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                SummeraAccount.clearLastCrash(ctx)
                                lastCrash = null
                            },
                        )
                    }
                    val log = debugLog
                    if (BuildConfig.DEBUG && log != null) {
                        Preference(
                            title = { Text(stringResource(R.string.summera_debug_log)) },
                            summary = { Text(log) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                SummeraAccount.clearDebugLog(ctx)
                                debugLog = null
                            },
                        )
                    }
                    Preference(
                        title = { Text(stringResource(R.string.summera_microphone_title)) },
                        summary = {
                            Text(
                                stringResource(
                                    if (microphoneGranted) {
                                        R.string.summera_microphone_granted
                                    } else {
                                        R.string.summera_microphone_not_granted
                                    },
                                ),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                            )
                        },
                        onClick = { microphoneLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    )
                }
            }
        },
    )
}

// check() fails until the browser leg is done
private suspend fun awaitSignIn(
    client: XidaAiClient,
    code: String,
    log: (String) -> Unit,
): Credentials? =
    poll {
        try {
            withContext(Dispatchers.IO) { client.auth.check(code) }
        } catch (e: XidaAiException) {
            // A wrong response shape shows up as a repeating reason instead of silence
            log("check: pending (${e.message})")
            null
        }
    }

private fun bringToFront(ctx: Context) {
    // ponytail: the return is a bonus. Android 10+ and several OEM skins block or reject an
    // activity start from the background; the sign-in is already saved and the screen shows it on
    // the next resume. NEW_TASK also covers a Custom Tab that ended up in the browser's own task
    try {
        ctx.startActivity(
            Intent(ctx, MainActivity::class.java)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP,
                ),
        )
    } catch (e: Exception) {
        // The credentials are saved, the screen shows them on the next resume
        Log.w(TAG, "Could not return from the sign-in page", e)
    }
}
