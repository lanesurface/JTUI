package jtxt.emulator.tui;

import jtxt.emulator.Location;

/**
 * The relative location that a component occupies within a container.
 * Components that occupy the same location within the layout will be rendered
 * in the order that they were added to that container. (If two components are
 * added to the NORTH in the layout, the first that was added will appear above
 * the second.)
 */
public interface Layout {
    public static enum BorderPosition {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        CENTER;
    }
    
    Location getLocation(BorderPosition position);
}
