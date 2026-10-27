package com.dessalines.thumbkey.ui.components.settings.lookandfeel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderBottom
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.LinearScale
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WebAssetOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.KeyboardPosition
import com.dessalines.thumbkey.utils.ThemeColor
import com.dessalines.thumbkey.utils.ThemeMode
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
internal fun LookAndFeelThemeAndPositionItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    val resources = LocalResources.current
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.themeState,
        onValueChange = {
            form.themeState = it
            onChange()
        },
        values = ThemeMode.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.theme)) },
        summary = { Text(stringResource(form.themeState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.Palette, contentDescription = null) },
    )
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.themeColorState,
        onValueChange = {
            form.themeColorState = it
            onChange()
        },
        values = ThemeColor.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.theme_color)) },
        summary = { Text(stringResource(form.themeColorState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.Colorize, contentDescription = null) },
    )
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.positionState,
        onValueChange = {
            form.positionState = it
            onChange()
        },
        values = KeyboardPosition.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        summary = { Text(stringResource(form.positionState.resId)) },
        title = { Text(stringResource(R.string.position)) },
        icon = { Icon(imageVector = Icons.Outlined.LinearScale, contentDescription = null) },
    )
    LookAndFeelPositionPaddingItem(form, onChange)
}

@Composable
private fun LookAndFeelPositionPaddingItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.positionPaddingState) }
    SliderPreference(
        value = form.positionPaddingState,
        sliderValue = sliderState,
        onValueChange = {
            form.positionPaddingState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = -200f..200f,
        title = {
            Text(stringResource(R.string.position_padding, sliderState.toInt().toString()))
        },
        icon = { Icon(imageVector = Icons.Outlined.LinearScale, contentDescription = null) },
    )
}

@Composable
internal fun LookAndFeelVisibilityItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    LookAndFeelVisibilityBehaviorItems(form, onChange)
    LookAndFeelVisibilityEditorItems(form, onChange)
}

@Composable
private fun LookAndFeelVisibilityBehaviorItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.hideLettersState,
        onValueChange = {
            form.hideLettersState = it
            onChange()
        },
        title = { Text(stringResource(R.string.hide_letters)) },
        icon = { Icon(imageVector = Icons.Outlined.HideImage, contentDescription = null) },
    )
    SwitchPreference(
        value = form.hideSymbolsState,
        onValueChange = {
            form.hideSymbolsState = it
            onChange()
        },
        title = { Text(stringResource(R.string.hide_symbols)) },
        icon = { Icon(imageVector = Icons.Outlined.HideImage, contentDescription = null) },
    )
    SwitchPreference(
        value = form.backdropEnabledState,
        onValueChange = {
            form.backdropEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.backdrop)) },
        icon = { Icon(imageVector = Icons.Outlined.ViewDay, contentDescription = null) },
    )
}

@Composable
private fun LookAndFeelVisibilityEditorItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.ignoreBottomPaddingState,
        onValueChange = {
            form.ignoreBottomPaddingState = it
            onChange()
        },
        title = { Text(stringResource(R.string.ignore_bottom_padding)) },
        icon = { Icon(imageVector = Icons.Outlined.BorderBottom, contentDescription = null) },
    )
    SwitchPreference(
        value = form.showToastOnLayoutSwitchState,
        onValueChange = {
            form.showToastOnLayoutSwitchState = it
            onChange()
        },
        title = { Text(stringResource(R.string.show_toast_on_layout_switch)) },
        icon = { Icon(imageVector = Icons.Outlined.Mail, contentDescription = null) },
    )
    SwitchPreference(
        value = form.disableFullscreenEditorState,
        onValueChange = {
            form.disableFullscreenEditorState = it
            onChange()
        },
        title = { Text(stringResource(R.string.disable_fullscreen_editor)) },
        icon = { Icon(imageVector = Icons.Outlined.WebAssetOff, contentDescription = null) },
    )
}

@Composable
internal fun LookAndFeelHapticItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.vibrateOnTapState,
        onValueChange = {
            form.vibrateOnTapState = it
            onChange()
        },
        title = { Text(stringResource(R.string.vibrate_on_tap)) },
        summary = { Text(stringResource(R.string.vibrate_warning)) },
        icon = { Icon(imageVector = Icons.Outlined.Vibration, contentDescription = null) },
    )
    SwitchPreference(
        enabled = form.slideEnabled,
        value = form.vibrateOnSlideState,
        onValueChange = {
            form.vibrateOnSlideState = it
            onChange()
        },
        title = { Text(stringResource(R.string.vibrate_on_slide)) },
        summary = {
            if (form.slideEnabled) {
                Text(stringResource(R.string.vibrate_slide_note))
            } else {
                Text(stringResource(R.string.vibrate_slide_warning))
            }
        },
        icon = { Icon(imageVector = Icons.Outlined.LinearScale, contentDescription = null) },
    )
    SwitchPreference(
        value = form.soundOnTapState,
        onValueChange = {
            form.soundOnTapState = it
            onChange()
        },
        title = { Text(stringResource(R.string.play_sound_on_tap)) },
        icon = { Icon(imageVector = Icons.Outlined.Visibility, contentDescription = null) },
    )
}
