package com.dessalines.thumbkey.ui.components.settings.behavior

// Builds the slider and direction preferences for behavior settings.

import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.CursorAccelerationMode
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.SliderPreference

@Composable
internal fun BehaviorSlideControlItems(
    form: BehaviorForm,
    onChange: () -> Unit,
    resources: Resources,
) {
    var sliderState by remember { mutableFloatStateOf(form.slideSensitivityState) }
    ListPreference(
        enabled = form.slideEnabledState,
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.slideCursorMovementModeState,
        onValueChange = {
            form.slideCursorMovementModeState = it
            onChange()
        },
        values = CursorAccelerationMode.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.slide_cursor_movement_mode)) },
        summary = { Text(stringResource(form.slideCursorMovementModeState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.SwapHoriz, contentDescription = null) },
    )
    SliderPreference(
        valueRange = 1f..50f,
        value = form.slideSensitivityState,
        sliderValue = sliderState,
        onSliderValueChange = { sliderState = it },
        onValueChange = {
            form.slideSensitivityState = it
            onChange()
        },
        enabled = form.slideEnabledState,
        title = { Text(stringResource(R.string.slide_sensitivity, sliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.SwapHoriz, contentDescription = null) },
    )
}

@Composable
internal fun CircularDragDirectionItems(
    form: BehaviorForm,
    onChange: () -> Unit,
    resources: Resources,
) {
    ListPreference(
        enabled = form.circularDragEnabledState,
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.clockwiseDragActionState,
        onValueChange = {
            form.clockwiseDragActionState = it
            onChange()
        },
        values = CircularDragAction.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.clockwise_drag_action)) },
        summary = { Text(stringResource(form.clockwiseDragActionState.resId)) },
        icon = {
            Icon(imageVector = Icons.AutoMirrored.Outlined.RotateRight, contentDescription = stringResource(R.string.clockwise_drag_action))
        },
    )
    ListPreference(
        enabled = form.circularDragEnabledState,
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.counterclockwiseDragActionState,
        onValueChange = {
            form.counterclockwiseDragActionState = it
            onChange()
        },
        values = CircularDragAction.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.counterclockwise_drag_action)) },
        summary = { Text(stringResource(form.counterclockwiseDragActionState.resId)) },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.RotateLeft,
                contentDescription = stringResource(R.string.counterclockwise_drag_action),
            )
        },
    )
}
