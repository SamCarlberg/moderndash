package edu.wpi.moderndash.dsl

import javafx.scene.Node
import org.controlsfx.control.ToggleSwitch
import tornadofx.opcr

/**
 * Creates a ControlsFX [ToggleSwitch] that gets configured with the operation [op].
 *
 * @param op an operation to configure the resulting toggle switch
 */
fun Node.toggleswitch(op: ToggleSwitch.() -> Unit) = opcr(this, ToggleSwitch(), op)
