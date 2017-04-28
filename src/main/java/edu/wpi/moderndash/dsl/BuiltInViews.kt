package edu.wpi.moderndash.dsl

import edu.wpi.moderndash.data.DataType
import javafx.collections.MapChangeListener
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class BuiltInViews {

    fun init() {

        // Basic text view
        view<Any> {
            name = "Text View"
            dataTypes.addAll(DataType.String, DataType.Number, DataType.Boolean)
            preferredSize = 2 by 1
            addView(1 by 1) {
                padding(8, 8, 8, 8)
                textfield {
                    textProperty().bind(source.dataProperty().stringBinding { it.toString() })
                }
            }
            addView(2 by 1, ::BorderPane) {
                padding(8, 8, 8, 8)
                top {
                    stackpane {
                        label(source.name)
                    }
                }
                center {
                    textfield {
                        textProperty().bind(source.dataProperty().stringBinding { it.toString() })
                    }
                }
            }
        }

        // Toggle button view
        view<Boolean> {
            name = "Toggle Button"
            dataTypes.addAll(DataType.Boolean)
            userInput = true
            addView(1 by 1) {
                togglebutton {
                    text = source.name
                    selectedProperty().bindBidirectional(source)
                }
            }
        }

        // Toggle switch view
        view<Boolean> {
            name = "Toggle Switch"
            dataTypes.addAll(DataType.Boolean)
            userInput = true
            addView(1 by 1) {
                alignment = Pos.CENTER
                toggleswitch {
                    text = source.name
                    selectedProperty().bindBidirectional(source)
                }
            }
        }

        // Motor controller
        compositeView {
            name = "Motor Controller"
            dataTypes.addAll(DataType.MotorController)
            userInput = true
            addView(2 by 1, ::VBox) {
                vgrow = Priority.ALWAYS
                alignment = Pos.CENTER
                padding(12)
                spacing = 8.0

                // Title
                label(source.name)
                separator(Orientation.HORIZONTAL)

                // Speed and current draw
                vbox {
                    label {
                        textProperty().bind(source.data, "Speed") {
                            String.format("Speed: %.3f", it)
                        }
                    }
                    slider(orientation = Orientation.HORIZONTAL) {
                        // TODO add a "motor slider" control to library
                        styleClass += "motor-slider"
                        range(min = -1, max = 1)
                        blockIncrement = 0.5
                        value = source.data["Speed"] as Double
                        source.data.addListener(MapChangeListener { change ->
                            if (change.wasAdded() && change.key == "Speed") {
                                value = change.valueAdded as Double
                            }
                        })
                        valueProperty().addListener { _, _, value ->
                            source.data["Speed"] = value
                        }
                    }
                }
                label {
                    textProperty().bind(source.data, "CurrentDraw") {
                        String.format("Current draw: %.3f Amps", it as Double? ?: 0.0)
                    }
                }
            }
        }

    }

}
