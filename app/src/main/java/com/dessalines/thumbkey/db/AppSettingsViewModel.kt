package com.dessalines.thumbkey.db

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val repository: AppSettingsRepository,
) : ViewModel() {
    val appSettings = repository.appSettings
    val changelog = repository.changelog

    fun update(appSettings: AppSettings) =
        viewModelScope.launch {
            repository.update(appSettings)
        }

    fun updateLayouts(layouts: LayoutsUpdate) =
        viewModelScope.launch {
            repository.updateLayouts(layouts)
        }

    fun updateLookAndFeel(lookAndFeel: LookAndFeelUpdate) =
        viewModelScope.launch {
            repository.updateLookAndFeel(lookAndFeel)
        }

    fun updateBehavior(behavior: BehaviorUpdate) =
        viewModelScope.launch {
            repository.updateBehavior(behavior)
        }

    fun updateKeyModifications(behavior: KeyModificationsUpdate) =
        viewModelScope.launch {
            repository.updateKeyModifications(behavior)
        }

    fun updateClipboardSettings(clipboardSettings: ClipboardSettingsUpdate) =
        viewModelScope.launch {
            repository.updateClipboardSettings(clipboardSettings)
        }

    fun updateOtherSettings(otherSettings: OtherSettingsUpdate) =
        viewModelScope.launch {
            repository.updateOtherSettings(otherSettings)
        }

    fun updateLastVersionCodeViewed(versionCode: Int) =
        viewModelScope.launch {
            repository.updateLastVersionCodeViewed(versionCode)
        }

    fun updateAbbreviationBufferEnabled(enabled: Int) =
        viewModelScope.launch {
            repository.updateAbbreviationBufferEnabled(enabled)
        }

    fun updateWordSuggestionsEnabled(enabled: Int) =
        viewModelScope.launch {
            repository.updateWordSuggestionsEnabled(enabled)
        }

    fun updateWordCombinationsEnabled(enabled: Int) =
        viewModelScope.launch {
            repository.updateWordCombinationsEnabled(enabled)
        }

    fun updateChangelog(ctx: Context) =
        viewModelScope.launch {
            repository.updateChangelog(ctx)
        }
}

class AppSettingsViewModelFactory(
    private val repository: AppSettingsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
