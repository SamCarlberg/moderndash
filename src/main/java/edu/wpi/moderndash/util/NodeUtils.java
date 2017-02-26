package edu.wpi.moderndash.util;

import java.util.Collection;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * A utility class for working with JavaFX {@link Node Nodes}.
 */
@UtilityClass
public class NodeUtils {

    /**
     * Finds the nearest node to a given point. The point should be relative to a
     * {@link javafx.scene.Parent Parent} and the nodes should only be children
     * of that {@code Parent}.
     *
     * @param nodes the nodes to search for the closest one.
     * @param x     the x-coordinate of the point
     * @param y     the y-coordinate of the point
     * @param <N>   the type of the node to search for
     *
     * @return the nearest node to the point, or null if no node was found.
     */
    public static <N extends Node> N findNearestNode(@NonNull Collection<N> nodes, double x, double y) {
        Point2D pClick = new Point2D(x, y);
        N nearestNode = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (N node : nodes) {
            Bounds bounds = node.getBoundsInParent();
            Point2D[] corners = new Point2D[]{
                    new Point2D(bounds.getMinX(), bounds.getMinY()),
                    new Point2D(bounds.getMaxX(), bounds.getMinY()),
                    new Point2D(bounds.getMaxX(), bounds.getMaxY()),
                    new Point2D(bounds.getMinX(), bounds.getMaxY()),
            };

            for (Point2D pCompare : corners) {
                double nextDist = pClick.distance(pCompare);
                if (nextDist < closestDistance) {
                    closestDistance = nextDist;
                    nearestNode = node;
                }
            }
        }
        return nearestNode;
    }

}
