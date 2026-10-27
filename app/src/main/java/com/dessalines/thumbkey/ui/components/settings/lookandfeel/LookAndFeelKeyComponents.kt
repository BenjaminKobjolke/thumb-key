package com.dessalines.thumbkey.ui.components.settings.lookandfeel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.BorderOuter
import androidx.compose.material.icons.outlined.Crop75
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Padding
import androidx.compose.material.icons.outlined.RoundedCorner
import androidx.compose.material.icons.outlined.VerticalAlignTop
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
internal fun LookAndFeelKeySizingItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.autoSizeKeysState,
        onValueChange = {
            form.autoSizeKeysState = it
            onChange()
        },
        title = { Text(stringResource(R.string.auto_size_keys)) },
        icon = { Icon(imageVector = Icons.Outlined.Fullscreen, contentDescription = null) },
    )
    if (!form.autoSizeKeysState) {
        KeyWidthItem(form, onChange)
    }
    SwitchPreference(
        value = form.nonSquareKeysState,
        onValueChange = {
            form.nonSquareKeysState = it
            onChange()
        },
        title = { Text(stringResource(R.string.key_non_square)) },
        icon = { Icon(imageVector = Icons.Outlined.Crop75, contentDescription = null) },
    )
    if (form.nonSquareKeysState) {
        KeyHeightItem(form, onChange)
    }
}

@Composable
private fun KeyWidthItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.keyWidthState) }
    SliderPreference(
        value = form.keyWidthState,
        sliderValue = sliderState,
        onValueChange = {
            form.keyWidthState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 10f..200f,
        title = {
            val titleId = if (form.nonSquareKeysState) R.string.key_width else R.string.key_size
            Text(stringResource(titleId, sliderState.toInt().toString()))
        },
        icon = { Icon(imageVector = Icons.Outlined.FormatSize, contentDescription = null) },
    )
}

@Composable
private fun KeyHeightItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.keyHeightState) }
    SliderPreference(
        value = form.keyHeightState,
        sliderValue = sliderState,
        onValueChange = {
            form.keyHeightState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 10f..200f,
        title = { Text(stringResource(R.string.key_height, sliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.Crop75, contentDescription = null) },
    )
}

@Composable
internal fun LookAndFeelKeyAppearanceItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var keyPaddingSliderState by remember { mutableFloatStateOf(form.keyPaddingState) }
    var keyBorderWidthSliderState by remember { mutableFloatStateOf(form.keyBorderWidthState) }
    var keyRadiusSliderState by remember { mutableFloatStateOf(form.keyRadiusState) }
    SliderPreference(
        value = form.keyPaddingState,
        sliderValue = keyPaddingSliderState,
        onValueChange = {
            form.keyPaddingState = it
            onChange()
        },
        onSliderValueChange = { keyPaddingSliderState = it },
        valueRange = 0f..10f,
        title = { Text(stringResource(R.string.key_padding, keyPaddingSliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.Padding, contentDescription = null) },
    )
    SliderPreference(
        value = form.keyBorderWidthState,
        sliderValue = keyBorderWidthSliderState,
        onValueChange = {
            form.keyBorderWidthState = it
            onChange()
        },
        onSliderValueChange = { keyBorderWidthSliderState = it },
        valueRange = 0f..50f,
        title = { Text(stringResource(R.string.key_border_width, keyBorderWidthSliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.BorderOuter, contentDescription = null) },
    )
    SliderPreference(
        value = form.keyRadiusState,
        sliderValue = keyRadiusSliderState,
        onValueChange = {
            form.keyRadiusState = it
            onChange()
        },
        onSliderValueChange = { keyRadiusSliderState = it },
        valueRange = 0f..100f,
        title = { Text(stringResource(R.string.key_radius, keyRadiusSliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.RoundedCorner, contentDescription = null) },
    )
}

@Composable
internal fun LookAndFeelAnimationItems(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    BottomOffsetItem(form, onChange)
    AnimationSpeedItem(form, onChange)
    AnimationHelperSpeedItem(form, onChange)
}

@Composable
private fun BottomOffsetItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.pushupSizeState) }
    SliderPreference(
        value = form.pushupSizeState,
        sliderValue = sliderState,
        onValueChange = {
            form.pushupSizeState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 0f..250f,
        title = { Text(stringResource(R.string.bottom_offset, sliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.VerticalAlignTop, contentDescription = null) },
    )
}

@Composable
private fun AnimationSpeedItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var sliderState by remember { mutableFloatStateOf(form.animationSpeedState) }
    SliderPreference(
        value = form.animationSpeedState,
        sliderValue = sliderState,
        onValueChange = {
            form.animationSpeedState = it
            onChange()
        },
        onSliderValueChange = { sliderState = it },
        valueRange = 0f..500f,
        title = { Text(stringResource(R.string.animation_speed, sliderState.toInt().toString())) },
        icon = { Icon(imageVector = Icons.Outlined.Animation, contentDescription = null) },
    )
}

@Composable
private fun AnimationHelperSpeedItem(
    form: LookAndFeelForm,
    onChange: () -> Unit,
) {
    var animationHelperSpeedSliderState by remember { mutableFloatStateOf(form.animationHelperSpeedState) }
    SliderPreference(
        value = form.animationHelperSpeedState,
        sliderValue = animationHelperSpeedSliderState,
        onValueChange = {
            form.animationHelperSpeedState = it
            onChange()
        },
        onSliderValueChange = { animationHelperSpeedSliderState = it },
        valueRange = 0f..500f,
        title = {
            val animationHelperSpeedStr =
                stringResource(
                    R.string.animation_helper_speed,
                    animationHelperSpeedSliderState.toInt().toString(),
                )
            Text(animationHelperSpeedStr)
        },
        icon = { Icon(imageVector = Icons.Outlined.Visibility, contentDescription = null) },
    )
}
