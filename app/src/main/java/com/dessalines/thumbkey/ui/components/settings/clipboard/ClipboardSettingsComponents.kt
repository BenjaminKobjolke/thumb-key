package com.dessalines.thumbkey.ui.components.settings.clipboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DataArray
import androidx.compose.material.icons.outlined.DiscFull
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.MAX_CLIPBOARD_MAX_SIZE
import com.dessalines.thumbkey.db.MIN_CLIPBOARD_MAX_SIZE
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
internal fun ClipboardCleanupItems(
    form: ClipboardSettingsForm,
    onChange: () -> Unit,
) {
    var sliderState by remember {
        mutableFloatStateOf(form.clipboardCleanupDuration.ordinal.toFloat())
    }

    SwitchPreference(
        value = form.clipboardAutoCleanupEnabledState,
        onValueChange = {
            form.clipboardAutoCleanupEnabledState = it
            onChange()
        },
        enabled = form.clipboardHistoryEnabledState,
        title = { Text(stringResource(R.string.clipboard_auto_cleanup)) },
        summary = { Text(stringResource(R.string.clipboard_auto_cleanup_description)) },
        icon = {
            Icon(imageVector = Icons.Outlined.CleaningServices, contentDescription = null)
        },
    )
    SliderPreference(
        value = form.clipboardCleanupDuration.ordinal.toFloat(),
        sliderValue = sliderState,
        onValueChange = {
            form.clipboardCleanupDuration = CleanupDuration.fromIndex(it.toInt())
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 0f..(CleanupDuration.entries.size - 1).toFloat(),
        valueSteps = CleanupDuration.entries.size - 2,
        enabled = form.clipboardHistoryEnabledState && form.clipboardAutoCleanupEnabledState,
        title = {
            val duration = CleanupDuration.fromIndex(sliderState.toInt())
            Text(
                stringResource(
                    R.string.clipboard_cleanup_after,
                    stringResource(duration.displayNameResId),
                ),
            )
        },
        icon = { Icon(imageVector = Icons.Outlined.HourglassTop, contentDescription = null) },
    )
}

@Composable
internal fun ClipboardSizeLimitItems(
    form: ClipboardSettingsForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.clipboardMaxSizeState) }

    SwitchPreference(
        value = form.clipboardSizeLimitEnabledState,
        onValueChange = {
            form.clipboardSizeLimitEnabledState = it
            onChange()
        },
        enabled = form.clipboardHistoryEnabledState,
        title = { Text(stringResource(R.string.clipboard_size_limit)) },
        summary = { Text(stringResource(R.string.clipboard_size_limit_description)) },
        icon = { Icon(imageVector = Icons.Outlined.DiscFull, contentDescription = null) },
    )
    SliderPreference(
        value = form.clipboardMaxSizeState,
        sliderValue = sliderState,
        onValueChange = {
            form.clipboardMaxSizeState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = MIN_CLIPBOARD_MAX_SIZE.toFloat()..MAX_CLIPBOARD_MAX_SIZE.toFloat(),
        enabled = form.clipboardHistoryEnabledState && form.clipboardSizeLimitEnabledState,
        title = {
            Text(stringResource(R.string.clipboard_max_size, sliderState.toInt()))
        },
        icon = { Icon(imageVector = Icons.Outlined.DataArray, contentDescription = null) },
    )
}
