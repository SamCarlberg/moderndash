package edu.wpi.moderndash.dsl

import edu.wpi.moderndash.data.DataType
import edu.wpi.moderndash.data.sources.DataSource
import edu.wpi.moderndash.exception.InvalidViewException
import edu.wpi.moderndash.views.Size
import javafx.collections.ObservableMap
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level

/**
 * A view defines
 */
class View<T> {

    constructor(init: View<T>.() -> Unit) {
        this.init()
    }

    constructor(init: Consumer<View<T>>) {
        init.accept(this)
    }

    /**
     * The name of the view. This should be unique.
     */
    var name: String = ""

    /**
     * Whether or not this view accepts user input. Controls will be disabled
     * if this flag is `false`, which is the default value, and controls that are bound
     * to properties will not be enabled even if this flag is `true`.
     */
    var userInput: Boolean = false

    /**
     * Enables user input. Note that this will not affect any controls that are bound to
     * properties.
     */
    fun enableUserInput() {
        userInput = true
    }

    /**
     * The preferred size of the view. If not set, this will default to the smallest size defined in
     * [views].
     */
    var preferredSize: Size? = null
        get() = field ?: views.keys.elementAt(0)

    /**
     * Map of sizes to suppliers for UI elements for those sizes. This is used to define
     * in a granular manner the possible sizes of this view and how it should be displayed
     * in each of those sizes. Note that the sizes defined here will be the _only_ supported
     * sizes for this view. This map is sorted in ascending size order.
     */
    val views: MutableMap<Size, () -> Pane> = TreeMap()

    /**
     * A set of the data types this view is able to show.
     */
    val dataTypes: MutableSet<DataType> = HashSet()

    /**
     * The source for the data displayed in the view. This can be set manually if a custom data
     * source is required (such as for a custom protocol). If not set manually, it will be set
     * internally by the dashboard after this view is instantiated but before UI elements are
     * created.
     */
    var source: DataSource<T> = DataSource.none()

    /**
     * Sets the UI view supplier used when the view is the given size. The view will be a pane
     * of type [P] initialized with and populated by the function [init].
     */
    fun <P : Pane> addView(size: Size, paneSupplier: () -> P, init: P.() -> Unit) {
        views[size] = {
            val pane = paneSupplier()
            pane.init()
            pane
        }
    }

    /**
     * Sets the UI view supplier used when the view is the given size. Note that the UI
     * elements will be embedded in a [StackPane], so there should generally only be one
     * top-level node function, eg
     *
     * ```
     * addView(n by m) {
     *   label {
     *     text = "..."
     *   }
     * }
     * ```
     *
     * If a different pane is required, consider using [size] or using a pane function
     * at the top level, eg
     *
     * ```
     * addView(n by m) {
     *   hbox {
     *     ...
     *   }
     * }
     * ```
     */
    fun addView(size: Size, init: StackPane.() -> Unit) = addView(size, ::StackPane, init)

}


/**
 * Defines the properties of a generic view with the given initialization function.
 */
fun <T> view(init: View<T>.() -> Unit) {
    try {
        Views.add<T> {
            it.init()
        }
    } catch (ex: InvalidViewException) {
        log.log(Level.WARNING, "Illegal or incomplete view definition", ex)
    }
}

/**
 * Defines the properties of a composite view with the given configuration function. A composite view
 * displays the contents of a composite data type such a motor controller, a subsystem, or a command.
 * The data is in a flat namespace. For example, if the view is showing a network table tree, there
 * are _no_ nested maps; rather, the data is in the format
 *
 * ```
 * foo="bar"
 * a/b="c"
 * a/b/c="d"
 * ```
 */
fun compositeView(configure: View<ObservableMap<String, Any>>.() -> Unit) = view(configure)
