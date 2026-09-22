package com.dessalines.thumbkey

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.AppSettingsRepository
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.AppSettingsViewModelFactory
import com.dessalines.thumbkey.db.ClipboardDB
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.summera.SUMMERA_LOGIN_HOST
import com.dessalines.thumbkey.summera.SUMMERA_LOGIN_SCHEME
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.ui.components.common.ShowChangelog
import com.dessalines.thumbkey.ui.components.settings.SettingsScreen
import com.dessalines.thumbkey.ui.components.settings.about.AboutScreen
import com.dessalines.thumbkey.ui.components.settings.backupandrestore.BackupAndRestoreScreen
import com.dessalines.thumbkey.ui.components.settings.behavior.BehaviorScreen
import com.dessalines.thumbkey.ui.components.settings.clipboard.ClipboardSettingsScreen
import com.dessalines.thumbkey.ui.components.settings.lookandfeel.LookAndFeelScreen
import com.dessalines.thumbkey.ui.components.settings.modifykeys.ModifyKeysScreen
import com.dessalines.thumbkey.ui.components.settings.other.OtherSettingsScreen
import com.dessalines.thumbkey.ui.components.settings.summera.SummeraHistoryScreen
import com.dessalines.thumbkey.ui.components.settings.summera.SummeraPromptsScreen
import com.dessalines.thumbkey.ui.components.settings.summera.SummeraSettingsScreen
import com.dessalines.thumbkey.ui.components.setup.SetupScreen
import com.dessalines.thumbkey.ui.screens.AbbreviationsScreen
import com.dessalines.thumbkey.ui.theme.ThumbkeyTheme
import com.dessalines.thumbkey.utils.ANIMATION_SPEED
import com.dessalines.thumbkey.utils.getImeNames
import com.dessalines.thumbkey.utils.getVersionCode
import org.woheller69.freeDroidWarn.FreeDroidWarn
import splitties.systemservices.inputMethodManager

class ThumbkeyApplication : Application() {
    private val database by lazy { AppDB.getDatabase(this) }
    private val clipboardDatabase by lazy { ClipboardDB.getDatabase(this) }
    val appSettingsRepository by lazy { AppSettingsRepository(database.appSettingsDao()) }
    val clipboardRepository by lazy {
        ClipboardRepository(
            clipboardDatabase.clipboardItemDao(),
            database.appSettingsDao(),
        )
    }

    override fun onCreate() {
        super.onCreate()
        // ponytail: debug-only breadcrumb for the Summera sign-in, shown on the Summera AI screen.
        // Delete it together with the lastCrash functions once the sign-in is confirmed working
        if (BuildConfig.DEBUG) {
            val previous = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                val frames = throwable.stackTrace.take(CRASH_BREADCRUMB_FRAMES).joinToString("\n")
                SummeraAccount.saveLastCrash(this, "${throwable::class.java.name}: ${throwable.message}\n$frames")
                previous?.uncaughtException(thread, throwable)
            }
        }
    }
}

private const val CRASH_BREADCRUMB_FRAMES = 10

class MainActivity : AppCompatActivity() {
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        AppSettingsViewModelFactory((application as ThumbkeyApplication).appSettingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FreeDroidWarn.showWarningOnUpgrade(this, getVersionCode())

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settings by appSettingsViewModel.appSettings.observeAsState()
            val ctx = LocalContext.current
            val imeNames = ctx.getImeNames()

            val thumbkeyEnabled =
                inputMethodManager.enabledInputMethodList.any {
                    imeNames.contains(it.id)
                }
            val selectedName =
                Settings.Secure.getString(
                    ctx.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD,
                )
            val thumbkeySelected = imeNames.contains(selectedName)

            val startDestination by remember {
                mutableStateOf(
                    // First, so the login page gets back to the Summera AI screen even before the
                    // keyboard is enabled
                    if (isSummeraLoginLink(intent)) {
                        "summera"
                    } else if (!thumbkeyEnabled) {
                        "setup"
                    } else {
                        intent.extras?.getString("startRoute") ?: "settings"
                    },
                )
            }

            ThumbkeyTheme(
                settings = settings,
            ) {
                val navController = rememberNavController()

                if (startDestination == "settings") {
                    ShowChangelog(appSettingsViewModel = appSettingsViewModel)
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIMATION_SPEED),
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(ANIMATION_SPEED),
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIMATION_SPEED),
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(ANIMATION_SPEED),
                        )
                    },
                ) {
                    composable(
                        route = "setup",
                    ) {
                        SetupScreen(
                            navController = navController,
                            thumbkeyEnabled = thumbkeyEnabled,
                            thumbkeySelected = thumbkeySelected,
                        )
                    }
                    composable(route = "settings") {
                        SettingsScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                            thumbkeyEnabled = thumbkeyEnabled,
                            thumbkeySelected = thumbkeySelected,
                        )
                    }
                    composable(route = "lookAndFeel") {
                        LookAndFeelScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(route = "behavior") {
                        BehaviorScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(route = "clipboardSettings") {
                        ClipboardSettingsScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                            clipboardRepository = (application as ThumbkeyApplication).clipboardRepository,
                        )
                    }
                    composable(route = "summera") {
                        SummeraSettingsScreen(navController = navController)
                    }
                    composable(route = "summeraPrompts") {
                        SummeraPromptsScreen(navController = navController)
                    }
                    composable(route = "summeraHistory") {
                        SummeraHistoryScreen(navController = navController)
                    }
                    composable(route = "modifyKeys") {
                        ModifyKeysScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(
                        route = "about",
                    ) {
                        AboutScreen(
                            navController = navController,
                        )
                    }
                    composable(
                        route = "backupAndRestore",
                    ) {
                        BackupAndRestoreScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(
                        route = "abbreviations",
                    ) {
                        AbbreviationsScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                    composable(route = "otherSettings") {
                        OtherSettingsScreen(
                            navController = navController,
                            appSettingsViewModel = appSettingsViewModel,
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // ponytail: recreate() instead of navigating, fine because the settings screens hold no
        // unsaved state. The Summera AI screen then resumes the pending sign-in by itself
        recreate()
    }
}

// The register_code the link carries is ignored on purpose: the pending code in the preferences is
// the one the poll trusts, a link can come from anywhere
private fun isSummeraLoginLink(intent: Intent): Boolean =
    intent.data?.let { it.scheme == SUMMERA_LOGIN_SCHEME && it.host == SUMMERA_LOGIN_HOST } == true
