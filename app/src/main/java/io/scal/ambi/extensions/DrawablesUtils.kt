package io.scal.ambi.extensions

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import java.util.*

object DrawablesUtils {

    private const val RGB_MAX = 255
    private const val PRESSED_COLOR = 0.20
    private const val FOCUSED_COLOR = 0.40
    private val STATE_SET = intArrayOf()

    fun getSelectableDrawableFor(color: Int): Drawable =
        if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(lightenOrDarken(color, PRESSED_COLOR)))
            stateListDrawable.addState(intArrayOf(android.R.attr.state_focused), ColorDrawable(lightenOrDarken(color, FOCUSED_COLOR)))
            stateListDrawable.addState(STATE_SET, ColorDrawable(color))
            stateListDrawable
        } else {
            val pressedColor = ColorStateList.valueOf(lightenOrDarken(color, 0.2))
            val defaultColor = ColorDrawable(color)
            val rippleColor = getRippleColor(color)
            RippleDrawable(pressedColor, defaultColor, rippleColor)
        }

    private fun getRippleColor(color: Int): Drawable {
        val outerRadii = FloatArray(8)
        Arrays.fill(outerRadii, 3f)
        val roundRectShape = RoundRectShape(outerRadii, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape)
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    private fun lightenOrDarken(color: Int, fraction: Double): Int {
        return if (canLighten(color, fraction)) {
            lighten(color, fraction)
        } else {
            darken(color, fraction)
        }
    }

    private fun lighten(color: Int, fraction: Double): Int {
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        red = lightenColor(red, fraction)
        green = lightenColor(green, fraction)
        blue = lightenColor(blue, fraction)
        val alpha = Color.alpha(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun darken(color: Int, fraction: Double): Int {
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        red = darkenColor(red, fraction)
        green = darkenColor(green, fraction)
        blue = darkenColor(blue, fraction)
        val alpha = Color.alpha(color)

        return Color.argb(alpha, red, green, blue)
    }

    private fun canLighten(color: Int, fraction: Double): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return canLightenComponent(red, fraction) && canLightenComponent(green, fraction) &&
            canLightenComponent(blue, fraction)
    }

    private fun canLightenComponent(colorComponent: Int, fraction: Double): Boolean {
        val red = Color.red(colorComponent)
        val green = Color.green(colorComponent)
        val blue = Color.blue(colorComponent)
        return RGB_MAX > red + red * fraction && RGB_MAX > green + green * fraction && RGB_MAX > blue + blue * fraction
    }

    private fun darkenColor(color: Int, fraction: Double): Int {
        val value = Math.max(color - color * fraction, 0.0)
        return value.toInt()
    }

    private fun lightenColor(color: Int, fraction: Double): Int {
        val value = Math.min(color + color * fraction, RGB_MAX.toDouble())
        return value.toInt()
    }
}
