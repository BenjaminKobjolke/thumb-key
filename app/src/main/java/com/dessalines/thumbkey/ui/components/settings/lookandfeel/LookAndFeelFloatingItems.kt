package com.dessalines.thumbkey.ui.components.settings.lookandfeel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.LinearScale
import androidx.compose.material.icons.outlined.Opacity
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
import com.dessalines.thumbkey.db.MAX_KEYBOARD_OPACITY
import com.dessalines.thumbkey.db.MIN_KEYBOARD_OPACITY
import com.dessalines.thumbkey.utils.FloatingHandlePosition
import com.dessalines.thumbkey.utils.KeyAnimationType
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
internal fun LookAndFeelFloatingItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    KeyboardOpacityPreference(form, onChange)
    val resources = LocalResources.current
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.floatingHandlePositionState,
        onValueChange = {
            form.floatingHandlePositionState = it
            onChange()
        },
        values = FloatingHandlePosition.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.floating_handle_position)) },
        summary = { Text(stringResource(form.floatingHandlePositionState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.LinearScale, contentDescription = null) },
    )
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.keyAnimationTypeState,
        onValueChange = {
            form.keyAnimationTypeState = it
            onChange()
        },
        values = KeyAnimationType.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.key_animation)) },
        summary = { Text(stringResource(form.keyAnimationTypeState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.Animation, contentDescription = null) },
    )
    SwitchPreference(
        value = form.swipeTrailEnabledState,
        onValueChange = {
            form.swipeTrailEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.swipe_trail)) },
        summary = { Text(stringResource(R.string.swipe_trail_summary)) },
        icon = { Icon(imageVector = Icons.Outlined.LinearScale, contentDescription = null) },
    )
}

@Composable
private fun KeyboardOpacityPreference(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(form.keyboardOpacityState) }
    SliderPreference(
        value = form.keyboardOpacityState,
        sliderValue = sliderValue,
        onValueChange = {
            form.keyboardOpacityState = it
            onChange()
        },
        onSliderValueChange = { sliderValue = it },
        valueRange = MIN_KEYBOARD_OPACITY.toFloat()..MAX_KEYBOARD_OPACITY.toFloat(),
        title = { Text(stringResource(R.string.keyboard_opacity, sliderValue.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.Opacity, contentDescription = null) },
    )
}
