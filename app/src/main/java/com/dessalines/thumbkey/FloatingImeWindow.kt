package com.dessalines.thumbkey

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Region
import android.graphics.drawable.ColorDrawable
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.compose.ui.unit.IntRect
import com.dessalines.thumbkey.utils.TAG

/** Owns the floating keyboard's IME window sizing and touchable region. */
internal class FloatingImeWindow(
    private val ime: IMEService,
    private val isFullscreen: () -> Boolean,
) {
    var isFloatingMode: Boolean = false
        private set

    private var panelBounds: Rect? = null
    private var navBarBottomPx: Int = 0

    fun setFloatingMode(enabled: Boolean): Boolean {
        if (enabled == isFloatingMode) return false
        isFloatingMode = enabled
        if (!enabled) panelBounds = null
        return true
    }

    fun onFloatingBounds(bounds: IntRect) {
        val rect = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
        if (rect != panelBounds) {
            panelBounds = rect
            ime.window
                ?.window
                ?.decorView
                ?.requestLayout()
        }
    }

    fun onNavBarBottomInsetChanged(bottomPx: Int) {
        if (bottomPx != navBarBottomPx) {
            navBarBottomPx = bottomPx
            if (isFloatingMode) {
                ime.window
                    ?.window
                    ?.decorView
                    ?.requestLayout()
            }
        }
    }

    fun applyWindowLayout(
        win: Window,
        fullscreen: Boolean = isFullscreen(),
        candidatesOnly: Boolean = false,
    ) {
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        try {
            val inputArea = win.decorView.findViewById<View>(android.R.id.inputArea)
            if (isFloatingMode && !fullscreen && !candidatesOnly) {
                applyFloatingLayout(win, inputArea)
            } else {
                applyRegularLayout(win, inputArea, fullscreen)
            }
        } catch (detached: IllegalArgumentException) {
            Log.d(TAG, "IME window not attached while sizing it: ${detached.message}")
        }
    }

    private fun applyFloatingLayout(
        win: Window,
        inputArea: View?,
    ) {
        inputArea?.let { area ->
            val params = area.layoutParams
            if (params != null && params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                area.layoutParams = params
            }
            val panel = area.parent as? LinearLayout
            if (panel != null &&
                panel.orientation == LinearLayout.VERTICAL &&
                (panel.gravity and Gravity.VERTICAL_GRAVITY_MASK) != Gravity.BOTTOM
            ) {
                panel.gravity = Gravity.BOTTOM
            }
        }
        if (win.attributes.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun applyRegularLayout(
        win: Window,
        inputArea: View?,
        fullscreen: Boolean,
    ) {
        inputArea?.let { area ->
            val params = area.layoutParams
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                area.layoutParams = params
            }
        }
        val targetHeight = if (fullscreen) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        if (win.attributes.height != targetHeight) {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight)
        }
    }

    fun computeInsets(outInsets: InputMethodService.Insets) {
        if (!isFloatingMode) return
        val decorView = ime.window?.window?.decorView ?: return
        val decorHeight = decorView.height
        if (decorHeight <= 0) return
        val band = navBarBottomPx.coerceIn(0, decorHeight)
        val top = decorHeight - band
        outInsets.contentTopInsets = top
        outInsets.visibleTopInsets = top
        panelBounds?.let { bounds ->
            outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_REGION
            outInsets.touchableRegion.setEmpty()
            outInsets.touchableRegion.set(bounds)
            if (band > 0) outInsets.touchableRegion.op(0, top, decorView.width, decorHeight, Region.Op.UNION)
        }
    }

    fun onConfigurationChanged() {
        panelBounds = null
        ime.window?.window?.let { applyWindowLayout(it) }
        ime.window?.window?.decorView?.let { decorView ->
            decorView.requestApplyInsets()
            decorView.requestLayout()
        }
    }

    fun shouldEvaluateFullscreenMode(superResult: Boolean): Boolean = !isFloatingMode && superResult
}
