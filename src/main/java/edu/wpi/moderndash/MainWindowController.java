package edu.wpi.moderndash;

import com.google.inject.Inject;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.data.DataType;
import edu.wpi.moderndash.data.sources.CompositeNetworkTableSource;
import edu.wpi.moderndash.data.sources.DataSource;
import edu.wpi.moderndash.data.sources.SingleKeyNetworkTableSource;
import edu.wpi.moderndash.dsl.View;
import edu.wpi.moderndash.dsl.Views;
import edu.wpi.moderndash.views.Size;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for the main UI window.
 */
@Log
public class MainWindowController implements FxmlController {

    @FXML
    private BorderPane root;
    @FXML
    private GridPane views;
    @FXML
    private TreeTableView<NetworkTableEntry> networkTables;
    @FXML
    private TreeTableColumn<NetworkTableEntry, String> keyColumn;
    @FXML
    private TreeTableColumn<NetworkTableEntry, String> valueColumn;
    @FXML
    private TreeItem<NetworkTableEntry> networktableRoot;
    @FXML
    private HBox footer;
    private AutoHideDialog aboutDialog;

    @Inject
    private Preferences preferences;

    private ITable rootTable = NetworkTable.getTable("");

    private int numCols;
    private int numRows;
    private static final int colWidth = 128;
    private static final int rowHeight = colWidth; // square tiles

    @Data
    private static final class ViewHandle {
        private final View view;
        private @NonNull Size currentSize;
        private String sourceName;
        private Node uiElement;

        public ViewHandle(@NonNull View view) {
            this.view = view;
            sourceName = view.getSource().getName();
        }

    }

    private final List<ViewHandle> viewHandles = new ArrayList<>();

    @FXML
    @Override
    public void initialize() throws IOException {
        // Show network table data in the sidebar
        NetworkTablesJNI.addEntryListener(
                "",
                (uid, key, value, flags) -> makeBranches(key, value, flags),
                ITable.NOTIFY_IMMEDIATE | ITable.NOTIFY_LOCAL | ITable.NOTIFY_NEW | ITable.NOTIFY_DELETE | ITable.NOTIFY_UPDATE
        );

        // init the about dialog stuff
        aboutDialog = new AutoHideDialog("About FRC moderndash");
        aboutDialog.setContent(FXMLLoader.load(MainWindowController.class.getResource("About.fxml")));

        // init grid
        numCols = preferences.getNumColumns();
        numRows = preferences.getNumRows();
        for (int i = 0; i < numCols; i++) {
            views.getColumnConstraints().add(new ColumnConstraints(colWidth, colWidth, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
        }
        for (int i = 0; i < numRows; i++) {
            views.getRowConstraints().add(new RowConstraints(rowHeight, rowHeight, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
        views.setGridLinesVisible(false);

        // NetworkTable view init
        keyColumn.setCellValueFactory(f -> new ReadOnlyStringWrapper(simpleKey(f.getValue().getValue().getKey())));
        valueColumn.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getValue().getValue())); // lol

        networkTables.setSortPolicy(view -> {
            sort(networktableRoot);
            return true;
        });

        networkTables.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, prev, cur) -> {
                    // Highlight the tiles for the currently selected data item
                    if (prev != null) {
                        highlight(prev, false);
                    }
                    if (cur != null) {
                        highlight(cur, true);
                    }
                });

        networkTables.setOnContextMenuRequested(e -> {
            TreeItem<NetworkTableEntry> selectedItem = networkTables.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }

            String key = normalizeKey(selectedItem.getValue().getKey()).substring(1);
            if (!findViews(key).isEmpty()) {
                log.info("Already have a view for key " + key);
                //return;
            }

            if (viewHandles.stream().anyMatch(h -> key.startsWith(h.getSourceName()))) {
                log.info("Already have a composite view for a super table of " + key);
                //return;
            }

            List<String> viewNames = viewNamesFor(key);
            if (viewNames.isEmpty()) {
                // No known views that can show this data
                return;
            }

            ContextMenu menu = new ContextMenu();
            for (String viewName : viewNames) {
                MenuItem mi = new MenuItem("Show as: " + viewName);
                mi.setOnAction(a -> {
                    Views.createView(viewName).ifPresent(v -> {
                        if (rootTable.containsSubTable(key)) {
                            // It's a composite data type like a motor controller
                            ITable table = rootTable.getSubTable(key);
                            View raw = v;
                            raw.setSource(new CompositeNetworkTableSource(table, DataType.valueOf(table.getString("~METADATA~/Type", null))));
                        } else {
                            // It's a single key-value pair
                            v.setSource(new SingleKeyNetworkTableSource<>(rootTable, key, rootTable.getValue(key).getClass()));
                        }
                        addView(v);
                    });
                });
                menu.getItems().add(mi);
            }
            menu.show(root.getScene().getWindow(), e.getScreenX(), e.getScreenY());
        });
    }

    private void highlight(TreeItem<NetworkTableEntry> prev, boolean doHighlight) {
        findViews(prev.getValue().getKey())
                .forEach(handle -> handle.getUiElement().pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), doHighlight));
        if (!prev.isLeaf()) {
            // Highlight all child views
            viewHandles.stream()
                    .filter(h -> h.getSourceName().startsWith(prev.getValue().getKey().substring(1)))
                    .forEach(h -> h.getUiElement().pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), doHighlight));
        }
    }

    private List<ViewHandle> findViews(String fullTableKey) {
        String k = normalizeKey(fullTableKey).substring(1);
        return viewHandles.stream()
                .filter(h -> h.sourceName.equals(k))
                .collect(Collectors.toList());
    }

    public List<String> viewNamesFor(String fullTableKey) {
        fullTableKey = normalizeKey(fullTableKey).substring(1);
        if (rootTable.containsKey(fullTableKey)) {
            // Queried a key-value
            return Views.viewNamesForType(
                    DataType.valueOf(NetworkTable.getTable("").getValue(fullTableKey).getClass()));
        } else if (rootTable.containsSubTable(fullTableKey)) {
            // Queried a subtable (composite type)
            ITable table = rootTable.getSubTable(fullTableKey);
            if (table.containsSubTable("~METADATA~")) {
                // check for metadata that describes the type
                String type = table.getSubTable("~METADATA~").getString("Type", null);
                DataType dataType = DataType.valueOf(type);
                if (type == null) {
                    log.warning("No type specified in metadata table");
                    dataType = DataType.Composite;
                } else if (dataType == DataType.Unknown) {
                    log.warning("Unknown data type '" + type + "'");
                    dataType = DataType.Composite;
                }
                return Views.viewNamesForType(dataType);
            } else {
                log.warning("No metadata table for table " + fullTableKey);
                return Collections.emptyList();
            }
        } else {
            // No possible views
            log.warning("No table element corresponding to key '" + fullTableKey + "'");
            return Collections.emptyList();
        }
    }

    /**
     * Sorts tree nodes recursively in order of branches before leaves, then alphabetically.
     *
     * @param node the root node to sort
     */
    private void sort(TreeItem<NetworkTableEntry> node) {
        if (!node.isLeaf()) {
            FXCollections.sort(node.getChildren(),
                    ((Comparator<TreeItem<NetworkTableEntry>>) (a, b) -> a.isLeaf() ? b.isLeaf() ? 0 : 1 : -1)
                            .thenComparing(Comparator.comparing(item -> item.getValue().getKey()))
            );
            node.getChildren().forEach(this::sort);
        }
    }

    private String simpleKey(String key) {
        if (!key.contains("/")) {
            return key;
        }
        return key.substring(key.lastIndexOf('/') + 1);
    }

    /**
     * Normalizes a network table key to start with exactly one leading slash ("/").
     */
    private static String normalizeKey(String key) {
        while (key.startsWith("//")) {
            key = key.substring(1);
        }
        if (!key.startsWith("/")) {
            key = "/" + key;
        }
        return key;
    }

    private void makeBranches(String key, Object value, int flags) {
        key = normalizeKey(key);
        boolean deleted = (flags & ITable.NOTIFY_DELETE) != 0;
        List<String> pathElements = Stream.of(key.split("/"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        TreeItem<NetworkTableEntry> current = networktableRoot;
        TreeItem<NetworkTableEntry> parent;
        StringBuilder k = new StringBuilder();
        for (int i = 0; i < pathElements.size(); i++) {
            String pathElement = pathElements.get(i);
            k.append("/").append(pathElement);
            parent = current;
            current = current.getChildren().stream().filter(item -> item.getValue().getKey().equals(k.toString())).findFirst().orElse(null);
            if (deleted) {
                if (current == null) {
                    break;
                } else if (i == pathElements.size() - 1) {
                    // last
                    parent.getChildren().remove(current);
                }
            } else if (i == pathElements.size() - 1) {
                if (current == null) {
                    current = new TreeItem<>(new NetworkTableEntry(key, asString(value)));
                    parent.getChildren().add(current);
                } else {
                    current.getValue().setValue(asString(value));
                }
            } else if (current == null) {
                current = new TreeItem<>(new NetworkTableEntry(k.toString(), ""));
                parent.getChildren().add(current);
            }
        }
        Platform.runLater(this::refreshTableView);
    }

    private void refreshTableView() {
        keyColumn.setVisible(false);
        keyColumn.setVisible(true);
    }

    private String asString(@NonNull Object o) {
        String s = o.toString();
        if (o.getClass().isArray()) {
            if (!s.equals("[]")) {
                s = s.substring(1, s.length());
            }
        }
        return s;
    }

    @FXML
    public void close() {
        log.info("Close button pressed, exiting app");
    }

    @FXML
    public void about() {
        aboutDialog.showAndWait();
    }

    private void makeReadOnly(Parent root) {
        root.getChildrenUnmodifiable().forEach(c -> {
            if (c instanceof Pane) {
                makeReadOnly((Pane) c);
            }
            if (c instanceof Control) {
                c.setDisable(true);
                c.setStyle("-fx-opacity: 1;");
            }
        });
    }

    public void addView(View<?> view) {
        addView(view, view.getPreferredSize());
    }

    public void addView(View<?> view, Size size) {
        ViewHandle handle = new ViewHandle(view);
        handle.setCurrentSize(size);
        viewHandles.add(handle);
        Pane control = view.getViews().get(size).invoke();
        if (!view.getUserInput()) {
            makeReadOnly(control);
        }
        AtomicReference<Node> uiElement = new AtomicReference<>(addTile(control, size));
        control.setOnContextMenuRequested(e -> {
            ContextMenu menu = new ContextMenu();
            MenuItem remove = new MenuItem("Remove");
            remove.setOnAction(a -> {
                views.getChildren().remove(uiElement.get());
                viewHandles.remove(handle);
            });
            if (view.getViews().size() > 1) {
                // Add menu items for changing the size
                Menu changeSize = new Menu("Resize...");
                for (Size s : view.getViews().keySet()) {
                    MenuItem sizeItem = new MenuItem(s.width + " by " + s.height);
                    if (handle.getCurrentSize().equals(s)) {
                        sizeItem.setGraphic(new Label("✓"));
                    }
                    sizeItem.setOnAction(a -> {
                        viewHandles.remove(handle);
                        views.getChildren().remove(uiElement.get());
                        addView(view, s);
                    });
                    changeSize.getItems().add(sizeItem);
                }
                menu.getItems().add(changeSize);
            }
            Menu changeView = new Menu("Show as...");
            Views.viewNamesForType(DataType.valueOf(view.getSource().getData().getClass()))
                    .forEach(name -> {
                        MenuItem changeItem = new MenuItem(name);
                        if (name.equals(view.getName())) {
                            changeItem.setGraphic(new Label("✓"));
                        } else {
                            // only need to change if it's to another type
                            changeItem.setOnAction(a -> {
                                viewHandles.remove(handle);
                                views.getChildren().remove(uiElement.get());
                                Views.createView(name).ifPresent(v -> {
                                    //noinspection unchecked - This is safe because the view is guaranteed to be able to use the same type of data
                                    v.setSource((DataSource) view.getSource());
                                    addView(v);
                                });
                            });
                        }
                        changeView.getItems().add(changeItem);
                    });
            menu.getItems().add(changeView);
            menu.getItems().add(new SeparatorMenuItem());
            menu.getItems().add(remove);
            menu.show(root.getScene().getWindow(), e.getScreenX(), e.getScreenY());
        });
        handle.setUiElement(uiElement.get());
        handle.getUiElement().pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
    }

    private Node addTile(Node node, Size size) {
        return addTile(node, size.width, size.height);
    }

    /**
     * Adds a node in the first available spot. The node will be wrapped in a pane to make it easier to add
     * single controls (buttons, labels, etc). This will fail (return {@code null}) iff:
     * <ul>
     * <li>{@code node} is {@code null}; or</li>
     * <li>{@code width} is zero or negative; or</li>
     * <li>{@code height} is zero or negative; or</li>
     * <li>there is no available space for a tile with the given dimensions</li>
     * </ul>
     *
     * @param node   the node to add
     * @param width  the width of the tile for the node. Must be >= 1
     * @param height the height of the tile for the node. Must be >= 1
     * @return the node added to the view
     */
    private Node addTile(Node node, int width, int height) {
        if (node == null) {
            // Can't add a null tile
            return null;
        }
        if (width < 1 || height < 1) {
            // Illegal dimensions
            return null;
        }
        Point placement = firstPoint(width, height);
        if (placement == null) {
            // Nowhere to place the node
            return null;
        }

        StackPane wrapper = new StackPane(node);
        wrapper.getStyleClass().add("tile");

        views.add(wrapper, placement.col, placement.row, width, height);
        return wrapper;
    }

    /**
     * Finds the first point where a tile with the given dimensions can be added, or {@code null} if no such point exists.
     *
     * @param width  the width of the tile trying to be added
     * @param height the height of the tile trying to be added
     */
    private Point firstPoint(int width, int height) {
        // outer row, inner col to add tiles left-to-right in the upper rows
        // outer col, inner row would add tiles top-to-bottom from the left-hand columns (not intuitive)
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (isOpen(col, row, width, height)) {
                    return new Point(col, row);
                }
            }
        }
        return null;
    }

    /**
     * Checks if a tile with the given width and height can be added at the point {@code (col, row)}.
     *
     * @param col        the column index of the point to check
     * @param row        the row index of the point to check
     * @param tileWidth  the width of the tile
     * @param tileHeight the height of the tile
     */
    private boolean isOpen(int col, int row, int tileWidth, int tileHeight) {
        if (col + tileWidth > numCols || row + tileHeight > numRows) {
            return false;
        }

        int x;
        int y;
        int width;
        int height;

        for (Node tile : views.getChildren()) {
            try {
                x = GridPane.getColumnIndex(tile);
                y = GridPane.getRowIndex(tile);
                width = GridPane.getColumnSpan(tile);
                height = GridPane.getRowSpan(tile);
            } catch (NullPointerException e) {
                // Not a real child (Geppetto pls)
                continue;
            }

            if (x + width > col && y + height > row
                    && x < col + tileWidth && y < row + tileHeight) {
                // Check intersection
                return false;
            }

        }

        return true;
    }

    @Value
    private static class Point {
        int col, row;
    }

}
