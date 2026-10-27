package com.dessalines.thumbkey.ui.components.settings.behavior

import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.SpaceBar
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.SwipeRight
import androidx.compose.material.icons.outlined.UTurnRight
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
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.CursorAccelerationMode
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
internal fun BehaviorGeneralItems(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.minSwipeLengthState) }

    SwitchPreference(
        value = form.autoCapitalizeState,
        onValueChange = {
            form.autoCapitalizeState = it
            onChange()
        },
        title = { Text(stringResource(R.string.auto_capitalize)) },
        icon = { Icon(imageVector = Icons.Outlined.Abc, contentDescription = null) },
    )
    SwitchPreference(
        value = form.spacebarMultiTapsState,
        onValueChange = {
            form.spacebarMultiTapsState = it
            onChange()
        },
        title = { Text(stringResource(R.string.spacebar_multitaps)) },
        icon = { Icon(imageVector = Icons.Outlined.SpaceBar, contentDescription = null) },
    )
    SwitchPreference(
        value = form.switchToLettersAfterSpaceState,
        onValueChange = {
            form.switchToLettersAfterSpaceState = it
            onChange()
        },
        title = { Text(stringResource(R.string.switch_to_letters_after_space)) },
        summary = { Text(stringResource(R.string.switch_to_letters_after_space_description)) },
        icon = { Icon(imageVector = Icons.Outlined.Abc, contentDescription = null) },
    )
    SliderPreference(
        value = form.minSwipeLengthState,
        sliderValue = sliderState,
        onValueChange = {
            form.minSwipeLengthState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 0f..200f,
        title = {
            Text(stringResource(R.string.min_swipe_length, sliderState.toInt().toString()))
        },
        icon = { Icon(imageVector = Icons.Outlined.Swipe, contentDescription = null) },
    )
}

@Composable
internal fun BehaviorSlideItems(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    val resources = LocalResources.current
    BehaviorSlideEnabledItem(form, onChange)
    BehaviorSlideControlItems(form, onChange, resources)
    BehaviorSlideDeadzoneItems(form, onChange)
}

@Composable
private fun BehaviorSlideEnabledItem(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.slideEnabledState,
        onValueChange = {
            form.slideEnabledState = it
            if (form.slideHoldEnabledState) form.slideHoldEnabledState = false
            onChange()
        },
        title = { Text(stringResource(R.string.slide_enable)) },
        icon = {
            Icon(imageVector = Icons.Outlined.SpaceBar, contentDescription = stringResource(R.string.slide_enable))
        },
    )
}

@Composable
private fun BehaviorSlideDeadzoneItems(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.slideSpacebarDeadzoneEnabledState,
        onValueChange = {
            form.slideSpacebarDeadzoneEnabledState = it
            onChange()
        },
        enabled = form.slideEnabledState,
        title = { Text(stringResource(R.string.slide_spacebar_deadzone_enable)) },
        icon = {
            Icon(
                imageVector = Icons.Outlined.SpaceBar,
                contentDescription = stringResource(R.string.slide_spacebar_deadzone_enable),
            )
        },
    )
    SwitchPreference(
        value = form.slideBackspaceDeadzoneEnabledState,
        onValueChange = {
            form.slideBackspaceDeadzoneEnabledState = it
            onChange()
        },
        enabled = form.slideEnabledState,
        title = { Text(stringResource(R.string.slide_backspace_deadzone_enable)) },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = stringResource(R.string.slide_backspace_deadzone_enable),
            )
        },
    )
}

@Composable
internal fun BehaviorSlideHoldItem(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.slideHoldEnabledState,
        onValueChange = {
            form.slideHoldEnabledState = it
            if (form.slideEnabledState) form.slideEnabledState = false
            onChange()
        },
        title = { Text(stringResource(R.string.slide_hold_enable)) },
        summary = { Text(stringResource(R.string.slide_hold_description)) },
        icon = {
            Icon(
                imageVector = Icons.Outlined.SwipeRight,
                contentDescription = stringResource(R.string.slide_hold_enable),
            )
        },
    )
}

@Composable
internal fun BehaviorDragItems(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    val resources = LocalResources.current
    BehaviorDragReturnItem(form, onChange)
    BehaviorCircularDragItems(form, onChange, resources)
}

@Composable
private fun BehaviorDragReturnItem(
    form: BehaviorForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.dragReturnEnabledState,
        onValueChange = {
            form.dragReturnEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.drag_return_enable)) },
        icon = {
            Icon(imageVector = Icons.Outlined.UTurnRight, contentDescription = stringResource(R.string.drag_return_enable))
        },
    )
}

@Composable
private fun BehaviorCircularDragItems(
    form: BehaviorForm,
    onChange: () -> Unit,
    resources: Resources,
) {
    SwitchPreference(
        value = form.circularDragEnabledState,
        onValueChange = {
            form.circularDragEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.circular_drag_enable)) },
        icon = {
            Icon(imageVector = Icons.Outlined.Circle, contentDescription = stringResource(R.string.circular_drag_enable))
        },
    )
    CircularDragDirectionItems(form, onChange, resources)
}
