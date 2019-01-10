package jtxt;

import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;

/**
 * An object which has the ability to be drawn to by the components in the
 * {@link jtxt.emulator.tui} package. It must be noted that a {@code Canvas}
 * must be sure to conform to the guidelines provided to the {@code Layout}
 * which manages these components, otherwise the components may not know where
 * the bounds of this canvas is, and could overflow it and/or position
 * themselves incorrectly.
 * 
 * @see jtxt.emulator.tui.Component
 * @see jtxt.emulator.tui.Layout
 */
public interface Canvas {
    /**
     * Updates the {@code Glyph} at the given location, possibly overwriting
     * the glyph that occupied the location beforehand (if there was one).
     * 
     * @param glyph The glyph to update.
     * @param location The location to place this glyph at.
     */
    void update(Glyph glyph, Location location);
    
    /**
     * Updates the glyphs from the start location to the length of the string,
     * or the edge of this canvas (if there is any).
     * 
     * @param string The string of glyphs to place
     * @param start The position for the first glyph in the string.
     */
    void update(GString string, Location start);
    
    /**
     * For each line in the array, the interface is updated from the start
     * postion to the end of the string or the edge of this canvas (if there is
     * any).
     * 
     * @param lines The strings to update.
     * @param start The location for the first glyph in the first string in the
     *              array.
     */
    void update(GString[] lines, Location start);
}
