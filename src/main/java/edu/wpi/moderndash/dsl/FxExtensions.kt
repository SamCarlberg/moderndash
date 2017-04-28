package edu.wpi.moderndash.dsl

import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.control.ButtonBase
import javafx.scene.control.Slider
import javafx.scene.layout.Region

/*
 * Extension methods for JavaFX that tornadofx doesn't include
 */

/**
 * Sets the padding on a region. Equivalent to:
 * ```
 * padding = Insets(topRightBottomLeft)
 * ```
 */
fun Region.padding(topRightBottomLeft: Number) {
    padding(topRightBottomLeft, topRightBottomLeft, topRightBottomLeft, topRightBottomLeft)
}

/**
 * Sets the padding on a region. Equivalent to:
 * ```
 * padding = Insets(top, right, bottom, left)
 * ```
 */
fun Region.padding(top: Number, right: Number, bottom: Number, left: Number) {
    padding = Insets(top.toDouble(), right.toDouble(), bottom.toDouble(), left.toDouble())
}

/**
 * Sets the function to call when this button is pressed. This allows for more fluent code like:
 *
 * ```
 * button {
 *   onAction {
 *      println("Button pressed!")
 *   }
 * }
 * ```
 *
 * instead of
 *
 * ```
 * button {
 *   setOnAction {
 *     println("Button pressed!")
 *   }
 * }
 * ```
 */
fun ButtonBase.onAction(handler: (ActionEvent) -> Unit) {
    setOnAction(handler)
}


/**
 * Represents a range of values from some minimum to some maximum. Defaults to (0, 100).
 */
class Range(var min: Number = 0, var max: Number = 100) {

    override fun toString(): String = "($min, $max)"

}

/**
 * Fluent setter for a slider's range. Instead of
 * ```kotlin
 * slider {
 *   min = -1.0
 *   max = 1.0
 * }
 * ```
 *
 * it's possible to use (with this function):
 * ```kotlin
 * slider {
 *   range {
 *      min = -1
 *      max = 1
 *   }
 * }
 * ```
 */
fun Slider.range(config: Range.() -> Unit) {
    val range = Range()
    range.config()
    min = range.min.toDouble()
    max = range.max.toDouble()
}

/**
 * ```kotlin
 * slider {
 *   range(min = -1, max = 1)
 * }
 * ```
 */
fun Slider.range(min: Number = 0, max: Number = 100) {
    this.min = min.toDouble()
    this.max = max.toDouble()
}
