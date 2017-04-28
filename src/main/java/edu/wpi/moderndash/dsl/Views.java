package edu.wpi.moderndash.dsl;

import edu.wpi.moderndash.data.DataType;
import edu.wpi.moderndash.exception.InvalidViewException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Keeps track of the loaded views.
 */
@UtilityClass
public class Views {

    // map descriptions to init functions to avoid muddling with view internals if they were singletons
    private final Map<ViewDescription, Consumer<? extends View<?>>> views = new HashMap<>();

    // package-private; should only be used by view() in ViewDsl.kt
    static <T> void add(Consumer<View<T>> init) {
        // Create a dummy view to 1. validate the function and 2. extract metadata from it (name, sizes, etc)
        View<T> view = new View<>(init);
        validate(view);
        views.put(new ViewDescription(view), init);
    }

    /**
     * Validates a view. The initialization function for the view will not be added if a view constructed
     * with it cannot pass validation.
     *
     * @param view the view to validate
     */
    private void validate(View<?> view) throws InvalidViewException {
        if (view.getName().isEmpty()) {
            throw new InvalidViewException("No name specified for the view");
        }
        if (views.keySet().stream().map(ViewDescription::getName).anyMatch(view.getName()::equals)) {
            throw new InvalidViewException("A view already exists with the same name: " + view.getName());
        }
        if (view.getViews().isEmpty()) {
            throw new InvalidViewException("No views specified for " + view.getName());
        }
        if (view.getViews().keySet().stream().noneMatch(view.getPreferredSize()::equals)) {
            throw new InvalidViewException("The preferred size doesn't have an associated view");
        }
    }

    /**
     * Tries to create a view from a known view with the given name.
     *
     * @param name The name of the view to create
     * @return an optional containing the created view, or an empty optional if no view could be created
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<View<T>> createView(@NonNull String name) {
        return views.entrySet().stream()
                .filter(e -> name.equals(e.getKey().getName()))
                .map(e -> new View(e.getValue()))
                .map(v -> (View<T>) v)
                .findFirst();
    }

    /**
     * Gets the names of all the possible views that can display the given type. A view can be created for these
     * with {@link #createView(String) createView}.
     *
     * @param type the type of data to get possible views for.
     * @return a list containing the names of all known views that can display data of the given type
     */
    public List<String> viewNamesForType(@NonNull DataType type) {
        return views.keySet().stream()
                .filter(d -> d.getDataTypes().contains(DataType.All) || d.getDataTypes().contains(type))
                .map(ViewDescription::getName)
                .collect(Collectors.toList());
    }

}
