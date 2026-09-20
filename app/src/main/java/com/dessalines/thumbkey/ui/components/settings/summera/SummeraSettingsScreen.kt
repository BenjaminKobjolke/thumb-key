package com.dessalines.thumbkey.ui.components.settings.summera

import android.Manifest
import android.content.ActivityNotFoundException
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
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
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.summera.hasMicrophonePermission
import com.dessalines.thumbkey.summera.poll
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.openLink
import de.xida.aichatapi.XidaAiException
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

    val timeoutStr = stringResource(R.string.summera_timeout)
    val microphoneLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            microphoneGranted = it
        }

    fun signIn() {
        signInError = null
        signInJob =
            scope.launch {
                try {
                    val client = SummeraAccount.client()
                    val registerCode = withContext(Dispatchers.IO) { client.auth.createRegisterCode() }
                    openLink(registerCode.url, ctx)

                    // check() fails until the browser leg is done
                    val result =
                        poll {
                            try {
                                withContext(Dispatchers.IO) { client.auth.check(registerCode.code) }
                            } catch (_: XidaAiException) {
                                null
                            }
                        }
                    if (result == null) {
                        signInError = timeoutStr
                    } else {
                        SummeraAccount.save(ctx, result)
                        credentials = result
                    }
                } catch (e: XidaAiException) {
                    signInError = e.message
                } catch (e: ActivityNotFoundException) {
                    signInError = e.message
                } finally {
                    signInJob = null
                }
            }
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
                            onClick = { signInJob?.cancel() },
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
