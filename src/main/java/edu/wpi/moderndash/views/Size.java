package edu.wpi.moderndash.views;

import lombok.Value;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 */
@Value
public class Size implements Comparable<Size> {

    public int width;
    public int height;

    public Size(int width, int height) {
        checkArgument(width > 0, "Width must be positive (was " + width + ")");
        checkArgument(height > 0, "Height must be positive (was " + height + ")");
        this.width = width;
        this.height = height;
    }

    @Override
    public int compareTo(Size o) {
        if (width < o.width) {
            return -1;
        } else if (width > o.width) {
            return 1;
        } else if (height < o.height) {
            return -1;
        } else if (height > o.height) {
            return 1;
        } else {
            return 0;
        }
    }

}
