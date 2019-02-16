package jtxt;

import java.util.ArrayList;
import java.util.List;

import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.LocationOutOfBoundsException;
import jtxt.emulator.Region;

public class GlyphBuffer {
    /**
     * <P>
     *  The rows of {@code GString}s which make up this frame. Each GString
     *  represents a line in the buffer, where a line spans the width of the
     *  frame specified in the initial context when the terminal that this
     *  frame belongs to is constructed, or whenever this frame is resized.
     * </P>
     * <P>
     *  This buffer is a dynamic list so that the frame is readily adaptable
     *  when the frame is requested to resize; though characters added to the
     *  buffer outside of the current bounds will be ignored.
     * </P>
     */
    protected List<GString> buffer;
    
    /**
     * The region which spans from the origin (0,&nbsp;0) in the upper-left
     * corner to the maximum dimensions on the x- and y-axis. Components
     * which attempt to place characters outside of these bounds will trigger
     * an exception.
     */
    protected Region bounds;
    
    public GlyphBuffer(Region bounds) {
        this.bounds = bounds;
        buffer = new ArrayList<>();
        
        int numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();
        for (int line = 0; line < numLines; line++)
            buffer.add(GString.blank(lineSize));
    }
    
    /**
     * Updates the character at the specified index. This will overwrite the
     * character that occupied this location beforehand.
     * 
     * @param glyph The glyph to insert.
     * @param location The location to place this glyph.
     */
    public void update(Glyph glyph, Location location) {
        if (!location.inside(bounds)) return;
        
        buffer.set(location.line,
                   buffer.get(location.line).set(location.position,
                                                 glyph));
    }

    /**
     * Updates the each of the {@code Location}s within this buffer so that
     * they contain the given glyph.
     * 
     * @param glyph The {@code Glyph} to insert into each of the given
     *              locations.
     * @param locations The Locations to insert the Glyph into.
     */
    public void update(Glyph glyph,
                       Location... locations) {
        for (int i = 0; i < locations.length; i++)
            update(glyph, locations[i]);
    }

    /**
     * Starting at the given {@code Location}, this method will will replace
     * the glyphs from start to the length of the string, assuming that this
     * string is within the bounds of the buffer.
     * 
     * @param glyphs The string to place in the buffer.
     * @param start The {@code Location} of the first glyph.
     */
    public void update(GString glyphs, Location start) {
        if (!start.inside(bounds)) return;
        
        GString line = buffer.get(start.line);
        for (int c = 0;
             c + start.position < bounds.getWidth() && c < glyphs.length();
             c++) line = line.set(c + start.position,
                                  glyphs.get(c));
        
        buffer.set(start.line,
                   line.substring(0, bounds.getWidth()));
    }

    /**
     * For each {@code GString} in the array, this method updates the frame at
     * the start location, starting at the given line and incrementing by one
     * for each subsequent element in the array, and retaining the position for
     * all lines added to the buffer.
     * 
     * @param lines The strings to place in the frame.
     * @param start The location for the first character in the first line of
     *              the array.
     */
    public void update(GString[] lines, Location start) {
        for (int line = 0; line < lines.length; line++)
            update(lines[line], new Location(start.line + line,
                                             start.position));
    }
    
    /**
     * Returns the glyph which occupies the given location.
     * 
     * @param location The location in this buffer where the glyph is stored.
     * 
     * @return The glyph in the given location.
     */
    public Glyph getGlyph(Location location) {
        if (!location.inside(bounds))
            throw new LocationOutOfBoundsException(location);
        
        return buffer.get(location.line)
                     .get(location.position);
    }

    public GString getString(int line) {
        return buffer.get(line);
    }
    
    /**
     * Given a region, this method will return all glyphs within that region as
     * an array of strings.
     * 
     * @param region The region in which the glyphs are contained in.
     * 
     * @return The glyphs in the given region as an array of {@code GString}s,
     *         where each string in the array represents one line in the
     *         buffer from the start to the end position.
     */
    public GString[] getGlyphs(Region region) {
        if (!region.inside(bounds))
            throw new LocationOutOfBoundsException("The given region is "
                                                   + "outside the bounds of "
                                                   + "this buffer.");
        
        int height = region.getHeight();
        GString[] glyphs = new GString[height];
        for (int line = 0; line < height; line++)
            glyphs[line] = buffer.get(region.start.line + line)
                                 .substring(region.start.position,
                                            region.end.position);
        
        return glyphs;
    }
    
    /**
     * Creates a new {@code GlyphBuffer}, copying the data which this one
     * contains within the given region, where the coordinates of the start of
     * this region become (0,&nbsp;0) in the new GlyphBuffer.
     * 
     * @param region A {@code Region} with coordinates which are within this
     *               buffer.
     * 
     * @return A new {@code GlyphBuffer} that contains a copy of the data
     *         within the given Region in this buffer.
     */
    public GlyphBuffer createClippedBuffer(Region region) {
        GString[] lines = getGlyphs(region);
        GlyphBuffer buffer = new GlyphBuffer(region);
        buffer.update(lines,
                      region.getStart());
        
        return buffer;
    }
    
    /**
     * Clear all characters out of this frame's buffer.
     */
    public void clear() {
        for (int line = 0; line < bounds.getHeight(); line++)
            buffer.set(line,
                       GString.blank(bounds.getWidth()));
    }
    
    public Region getBounds() {
        return bounds;
    }
}
