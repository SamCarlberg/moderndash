package edu.wpi.moderndash.dsl

import edu.wpi.moderndash.data.DataType
import edu.wpi.moderndash.data.sources.DataSource
import edu.wpi.moderndash.views.Size
import javafx.application.Platform
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import tornadofx.objectBinding
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger


internal val log: Logger = Logger.getLogger("moderndash-view-dsl")

/**
 * Allows a new size to be created with the syntax `<width> by <height>`. For example, `2 by 1` will create a size
 * with width `2` and height `1`.
 */
infix fun Int.by(other: Int): Size = Size(this, other)

/**
 * Describes a view.
 */
data class ViewDescription(val name: String, val userInput: Boolean = false, val dataTypes: Set<DataType>) {

    constructor(v: View<*>) : this(v.name, v.userInput, HashSet(v.dataTypes))

}

fun <T> MutableCollection<T>.addAll(vararg elements: T) {
    this.addAll(elements)
}

/**
 * Binds a property to a value in an observable map. For example, to bind a labels text to the value of the
 * "Speed" entry in a composite data source:
 *
 * ```kotlin
 * label {
 *   textProperty().bind(source.data, "Speed") {
 *     "Speed: ${it ?: "unknown"}"
 *   }
 * }
 * ```
 *
 * @param map the map to bind to
 * @param key the key associated with the value to bind to
 * @param converter a function for converting the type of values in the map to the type the property holds
 */
fun <K, V, T> Property<T>.bind(map: ObservableMap<K, V>, key: K, converter: (V?) -> T) {
    this.bind(MapEntryBinding(map, key).objectBinding {
        converter(it)
    })
}

/**
 * Runs a task on the JavaFX application thread as soon as possible. If called from the application thread, the
 * task will run immediately; otherwise, it will be run at some later time on the application thread.
 */
fun runOnFxThread(task: Runnable) {
    runOnFxThread(task::run)
}

/**
 * Runs a task on the JavaFX application thread as soon as possible. If called from the application thread, the
 * task will run immediately; otherwise, it will be run at some later time on the application thread.
 */
fun runOnFxThread(task: () -> Unit) {
    if (Platform.isFxApplicationThread()) {
        task()
    } else {
        Platform.runLater(task)
    }
}

/**
 * A [Binding][javafx.beans.binding.Binding] to the value of a specific key in a map. Changes to that key
 * will be reflected in this binding. A change from a different thread will notify listeners on the JavaFX
 * application thread.
 */
class MapEntryBinding<K, V>(val map: ObservableMap<K, V>, val key: K) : ObjectBinding<V?>() {

    init {
        // trigger invalidation whenever the map changes
        // Invalidation will run on the JavaFX application thread.
        map.addListener(MapChangeListener {
            if (it.key == key) {
                runOnFxThread(this::invalidate)
            }
        })
    }

    override fun computeValue(): V? = map[key]

    override fun getDependencies(): ObservableList<*> {
        return FXCollections.observableArrayList(map)
    }

}

/**
 * Binds a property to a data source's data property. Shorthand for `bind(source.dataProperty())`.
 * Note that if this property is for a control (eg button selection or a label's text), that control
 * will not be able to accept user input.
 */
fun <T> Property<in T>.bind(source: DataSource<out T>) {
    bind(source.dataProperty())
}

/**
 * Binds a property bidirectionally to a data source. Changes to the property will change the data in
 * the source, and a change in the source will make a change in the property.
 */
fun <T> Property<T>.bindBidirectional(source: DataSource<T>) {
    this.bindBidirectional(source.dataProperty())
}

