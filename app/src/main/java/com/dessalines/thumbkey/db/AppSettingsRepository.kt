package com.dessalines.thumbkey.db

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppSettingsRepository(
    private val appSettingsDao: AppSettingsDao,
) {
    private val _changelog = MutableStateFlow("")
    val changelog = _changelog.asStateFlow()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val appSettings = appSettingsDao.getSettings()

    @WorkerThread
    suspend fun update(appSettings: AppSettings) {
        appSettingsDao.updateAppSettings(appSettings)
    }

    @WorkerThread
    fun updateLayouts(layouts: LayoutsUpdate) {
        appSettingsDao.updateLayouts(layouts)
    }

    @WorkerThread
    fun updateLookAndFeel(lookAndFeel: LookAndFeelUpdate) {
        appSettingsDao.updateLookAndFeel(lookAndFeel)
    }

    @WorkerThread
    fun updateBehavior(behavior: BehaviorUpdate) {
        appSettingsDao.updateBehavior(behavior)
    }

    @WorkerThread
    fun updateKeyModifications(behavior: KeyModificationsUpdate) {
        appSettingsDao.updateKeyModifications(behavior)
    }

    @WorkerThread
    fun updateClipboardSettings(clipboardSettings: ClipboardSettingsUpdate) {
        appSettingsDao.updateClipboardSettings(clipboardSettings)
    }

    @WorkerThread
    fun updateOtherSettings(otherSettings: OtherSettingsUpdate) {
        appSettingsDao.updateOtherSettings(otherSettings)
    }

    @WorkerThread
    suspend fun updateLastVersionCodeViewed(versionCode: Int) {
        appSettingsDao.updateLastVersionCode(versionCode)
    }

    @WorkerThread
    fun updateAbbreviationBufferEnabled(enabled: Int) {
        appSettingsDao.updateAbbreviationBufferEnabled(enabled)
    }

    @WorkerThread
    fun updateWordSuggestionsEnabled(enabled: Int) {
        appSettingsDao.updateWordSuggestionsEnabled(enabled)
    }

    @WorkerThread
    fun updateWordCombinationsEnabled(enabled: Int) {
        appSettingsDao.updateWordCombinationsEnabled(enabled)
    }

    @WorkerThread
    suspend fun updateChangelog(ctx: Context) {
        withContext(Dispatchers.IO) {
            try {
                val releasesStr =
                    ctx.assets
                        .open("RELEASES.md")
                        .bufferedReader()
                        .use { it.readText() }
                _changelog.value = releasesStr
            } catch (e: Exception) {
                Log.e("thumb-key", "Failed to load changelog: $e")
            }
        }
    }
}
