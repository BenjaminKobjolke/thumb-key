package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.ui.theme.fontSizeVariantToFontSize
import com.dessalines.thumbkey.ui.theme.pxToSp
import com.dessalines.thumbkey.ui.theme.toPx
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.KeyAnimationType
import com.dessalines.thumbkey.utils.ReleasedKeyFeedback
import com.dessalines.thumbkey.utils.initialTranslationFraction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class KeyReleaseAnimationConfig(
    val type: KeyAnimationType,
    val speed: Int,
    val helperSpeed: Int,
    val keySize: Double,
)

private data class KeyPressFeedbackAnimationState(
    val activeFeedback: MutableState<ReleasedKeyFeedback?>,
    val enterProgress: Animatable<Float, AnimationVector1D>,
    val exitAlpha: Animatable<Float, AnimationVector1D>,
)

@Composable
internal fun KeyReleaseAnimation(
    feedback: ReleasedKeyFeedback?,
    isPasswordField: Boolean,
    config: KeyReleaseAnimationConfig,
) {
    if (isPasswordField) return
    KeyPressFeedbackOverlay(feedback, config)
}

@Composable
private fun KeyPressFeedbackOverlay(
    feedback: ReleasedKeyFeedback?,
    config: KeyReleaseAnimationConfig,
) {
    val state = remember {
        KeyPressFeedbackAnimationState(
            activeFeedback = mutableStateOf(null),
            enterProgress = Animatable(1f),
            exitAlpha = Animatable(0f),
        )
    }
    KeyPressFeedbackAnimation(feedback, config, state)
    val current = state.activeFeedback.value ?: return
    KeyPressFeedbackVisual(current, config, state)
}

@Composable
private fun KeyPressFeedbackAnimation(
    feedback: ReleasedKeyFeedback?,
    config: KeyReleaseAnimationConfig,
    state: KeyPressFeedbackAnimationState,
) {
    LaunchedEffect(feedback?.id) {
        val current = feedback ?: return@LaunchedEffect
        if (config.speed <= 0 && config.helperSpeed <= 0) {
            state.activeFeedback.value = null
            return@LaunchedEffect
        }
        state.activeFeedback.value = current
        state.enterProgress.snapTo(if (config.speed <= 0) 1f else 0f)
        state.exitAlpha.snapTo(1f)
        val enterJob =
            if (config.speed > 0) {
                launch {
                    state.enterProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = config.speed, easing = FastOutSlowInEasing),
                    )
                }
            } else {
                null
            }
        delay(config.helperSpeed.coerceAtLeast(0).toLong())
        if (config.speed > 0) {
            state.exitAlpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = config.speed))
        }
        enterJob?.cancel()
        state.activeFeedback.value = null
    }
}

@Composable
private fun KeyPressFeedbackVisual(
    current: ReleasedKeyFeedback,
    config: KeyReleaseAnimationConfig,
    state: KeyPressFeedbackAnimationState,
) {
    val useFadeIn = config.type == KeyAnimationType.Fade || config.type == KeyAnimationType.FromOrigin
    val originFraction = config.type.initialTranslationFraction(current.originDirection)
    val spSize =
        fontSizeVariantToFontSize(
            fontSizeVariant = FontSizeVariant.LARGE,
            keySize = config.keySize.dp,
            isUpperCase = false,
        ).toPx.pxToSp
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val fadeInFactor = if (useFadeIn) state.enterProgress.value else 1f
                    alpha = (fadeInFactor * state.exitAlpha.value).coerceIn(0f, 1f)
                    compositingStrategy = CompositingStrategy.Offscreen
                }.background(color = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val remaining = 1f - state.enterProgress.value
                        translationX = size.width * originFraction.x * remaining
                        translationY = size.height * originFraction.y * remaining
                    },
        ) {
            Text(
                text = current.text,
                fontWeight = FontWeight.Bold,
                fontSize = spSize,
                lineHeight = spSize,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
