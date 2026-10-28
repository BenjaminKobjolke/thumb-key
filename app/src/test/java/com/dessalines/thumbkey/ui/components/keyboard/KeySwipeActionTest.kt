package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.ui.geometry.Offset
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyItemC
import org.junit.Assert.assertSame
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class KeySwipeActionTest {
    @Test
    fun tapLikeDragReturnsCenterAction() {
        val key = key("center")

        val action = resolveSwipeAction(keys(key), gesture(positions = listOf(Offset.Zero)), settings())

        assertSame(key.center.action, action)
    }

    @Test
    fun swipeUsesKeyActionThenGhostFallback() {
        val directKey = key("center").copy(right = key("right").center)
        val directAction = resolveSwipeAction(keys(directKey), rightSwipe(), settings())
        assertSame(directKey.right!!.action, directAction)

        val ghostKey = key("ghost").copy(right = key("ghost right").center)
        val ghostAction = resolveSwipeAction(keys(key("center"), ghostKey = ghostKey), rightSwipe(), settings())
        assertSame(ghostKey.right!!.action, ghostAction)
    }

    @Test
    fun dragReturnUsesKeyThenOppositeCaseThenGhostFallbacks() {
        val swipeReturnAction = action("return")
        val returningKey = key("center").copy(right = KeyC(action("right"), swipeReturnAction))
        assertSame(
            swipeReturnAction,
            resolveSwipeAction(keys(returningKey), returnedRightSwipe(), settings().copy(dragReturnEnabled = true)),
        )

        val oppositeAction = action("opposite")
        val oppositeKey = key("opposite center").copy(right = key("opposite right").center)
        val ghostReturnAction = action("ghost return")
        val ghostKey = key("ghost center").copy(right = KeyC(action("ghost right"), ghostReturnAction))
        val keyWithoutReturn = key("center").copy(right = key("right").center)
        assertSame(
            oppositeAction,
            resolveSwipeAction(
                keys(
                    keyWithoutReturn,
                    ghostKey = ghostKey,
                    oppositeCaseKey = oppositeKey.copy(right = KeyC(oppositeAction)),
                ),
                returnedRightSwipe(),
                settings().copy(dragReturnEnabled = true),
            ),
        )

        assertSame(
            ghostReturnAction,
            resolveSwipeAction(
                keys(keyWithoutReturn, ghostKey = ghostKey),
                returnedRightSwipe(),
                settings().copy(dragReturnEnabled = true),
            ),
        )
    }

    @Test
    fun disabledDragReturnFallsBackToCenterAction() {
        val key = key("center").copy(right = KeyC(action("right"), action("return")))

        val result = resolveSwipeAction(keys(key), returnedRightSwipe(), settings())

        assertSame(key.center.action, result)
    }

    @Test
    fun circularGesturesUseConfiguredDirectionActionsAndFallback() {
        val oppositeCaseAction = action("opposite")
        val numericAction = action("numeric")
        val oppositeCaseKey = key("opposite").copy(center = KeyC(oppositeCaseAction))
        val numericKey = key("numeric").copy(center = KeyC(numericAction))
        val clockwiseSettings =
            settings().copy(
                circularDragEnabled = true,
                clockwiseDragAction = CircularDragAction.OppositeCase,
                counterclockwiseDragAction = CircularDragAction.Numeric,
            )
        assertSame(
            oppositeCaseAction,
            resolveSwipeAction(
                keys(key("center"), oppositeCaseKey = oppositeCaseKey, numericKey = numericKey),
                circularSwipe(clockwise = true),
                clockwiseSettings,
            ),
        )
        assertSame(
            numericAction,
            resolveSwipeAction(
                keys(key("center"), oppositeCaseKey = oppositeCaseKey, numericKey = numericKey),
                circularSwipe(clockwise = false),
                clockwiseSettings,
            ),
        )

        val returnAction = action("return")
        val keyWithReturn = key("center").copy(bottom = KeyC(action("bottom"), returnAction))
        val missingMappedKey =
            clockwiseSettings.copy(
                dragReturnEnabled = true,
                clockwiseDragAction = CircularDragAction.Numeric,
            )
        assertSame(
            returnAction,
            resolveSwipeAction(
                keys(keyWithReturn, oppositeCaseKey = oppositeCaseKey),
                circularSwipe(clockwise = true),
                missingMappedKey,
            ),
        )
    }

    private fun keys(
        key: KeyItemC,
        ghostKey: KeyItemC? = null,
        oppositeCaseKey: KeyItemC? = null,
        numericKey: KeyItemC? = null,
    ) = SwipeKeys(key, ghostKey, oppositeCaseKey, numericKey)

    private fun key(text: String) = KeyItemC(center = KeyC(text))

    private fun action(text: String) = KeyAction.CommitText(text)

    private fun settings() =
        SwipeSettings(
            20,
            false,
            false,
            CircularDragAction.OppositeCase,
            CircularDragAction.OppositeCase,
        )

    private fun gesture(
        positions: List<Offset>,
        maxOffset: Offset = Offset.Zero,
        offsetX: Float = 0f,
        offsetY: Float = 0f,
    ) = SwipeGesture(positions, maxOffset, offsetX, offsetY)

    private fun rightSwipe() = gesture(listOf(Offset.Zero, Offset(40f, 0f)), Offset(40f, 0f), 40f)

    private fun returnedRightSwipe() = gesture(listOf(Offset.Zero, Offset(40f, 0f), Offset(0f, 0f)), Offset(40f, 0f))

    private fun circularSwipe(clockwise: Boolean): SwipeGesture {
        val direction = if (clockwise) 1 else -1
        val positions =
            (0..16).map { index ->
                val angle = direction * 2 * PI * index / 16
                Offset(60f + 50f * cos(angle).toFloat(), 50f * sin(angle).toFloat())
            }
        return gesture(positions, maxOffset = Offset(0f, 110f))
    }
}
