/* 
 * Copyright 2018 Lane W. Surface 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jtxt.emulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

/**
 * Similar to a video frame, this is a frame of {@code Glyphs} that represents
 * the entirety of characters that are available to be fetched and painted to
 * the screen by the renderer at a specific point in time. TUI components are
 * passed an instance of this class, and may paint themselves within the bounds
 * that their parent container's layout has allocated to them. After all
 * components in the terminal have finished painting, the Glyphs belonging to
 * this frame are rasterized and rendered on the screen.
 * 
 * TODO: Make this class serializable so that it may be passed over a network
 *       connection, making way for SSH and other useful utilities.
 */
@SuppressWarnings("serial")
public class BufferedFrame extends JComponent
                           implements ResizeSubscriber,
                                      java.io.Serializable {
    /**
     * <P>
     *  The rows of {@code GString}s which make up this frame. Each GString
     *  represents a line in the buffer, where a line spans the width of the
     *  frame specified in the initial context when the terminal that this
     *  frame belongs to is constructed, or whenever this frame is resized.
     * </P>
     * <P>
     *  This buffer is a dynamic list so that the frame is readily adaptable
     *  when the frame is requested to resize; although writing outside the
     *  bounds defined by the context is considered an error (and will throw
     *  a {@code LocationOutOfBoundsException}).
     * </P>
     */
    private java.util.List<GString> buffer;
    
    /**
     * Holds information about the way to render the text on the screen.
     */
    private Context context;
    
    /**
     * The region which spans from the origin (0,&nbsp;0) in the upper-left
     * corner to the maximum dimensions on the x- and y-axis. Components
     * which attempt to place characters outside of these bounds will trigger
     * an exception.
     */
    private Region bounds;
    
    /**
     * Creates a new frame.
     * 
     * @param config The properties used for rendering the text.
     */
    public BufferedFrame(Context config) {
        this.context = config;
        bounds = new Region(0,
                            0,
                            context.getNumberOfLines(),
                            context.getLineSize());
        buffer = new ArrayList<>();
        // FIXME: Do NOT call this method ourselves; the subject should resize
        //        us once the frame is instantiated.
        resize(context.getNumberOfLines(), context.getLineSize());
    }
    
    /**
     * Updates the character at the specified index. This will overwrite the
     * character that occupied this location beforehand.
     * 
     * @param glyph The glyph to insert.
     * @param location The location to place this glyph.
     */
    public void update(Glyph glyph, Location location) {
        if (!location.inside(bounds))
            throw new LocationOutOfBoundsException(location);
        
        buffer.set(location.line,
                   buffer.get(location.line)
                         .set(location.position, glyph));
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
        GString line = buffer.get(start.line);
        
        for (int c = 0; c < glyphs.length(); c++)
            line = line.set(c + start.position, glyphs.get(c));
        
        buffer.set(start.line, line.substring(0, bounds.getWidth()));
    }
    
    /**
     * Update the glyphs in the given region, such that the region is defined 
     * as the bounding box for these glyphs. Glyphs that overflow the region
     * will be discarded.
     * 
     * @param glyphs The glyphs to place within the given region.
     * @param region The bounding box for these glyphs, such that all glyphs
     *               are guaranteed to be within it. Glyphs may not take up the
     *               full region, but will never overflow it.
     */
    public void update(GString glyphs, Region region) {
        int width = region.getWidth(),
            height = region.getHeight();
        Location start = region.getStart();
        
        GString[] lines = glyphs.wrap(width);
        for (int l = 0; l < height && l < lines.length; l++) {
            for (int p = 0; p < width && p < lines[l].length(); p++) {
                Location local = start.add(new Location(l, p));
                update(lines[l].get(p), local);
            }
        }
    }

    /**
     * Updates the region in the renderer bounded in the upper-right corner by
     * start, assuming that the renderer has allocated enough space to store
     * this array in it's internal buffer.
     * 
     * @param glyphs
     * @param start
     */
    public void update(GString[] glyphs, Location start) {
        for (int l = 0; l < glyphs.length; l++)
            for (int p = 0; p < glyphs[l].length(); p++)
                update(glyphs[l].get(p), start.add(new Location(l, p)));
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
            throw new LocationOutOfBoundsException("The given region is " +
                                                   "outside the bounds of " +
                                                   "the frame.");
        
        int height = region.getHeight();
        GString[] glyphs = new GString[height];
        for (int line = 0; line < height; line++)
            glyphs[line] = buffer.get(region.start.line + line)
                                 .substring(region.start.position,
                                            region.end.position);
        
        return glyphs;
    }
    
    /**
     * Clear all characters out of this frame's buffer.
     */
    public void clear() {
        for (int line = 0; line < bounds.getHeight(); line++)
            buffer.set(line, GString.blank(bounds.getWidth()));
    }
    
    @Override
    public void resize(int lines, int lineSize) {
        /*
         * The context has been resized, and so we need to update the buffer
         * accordingly; otherwise, components which are passed this frame may
         * unintentionally throw errors while attempting to draw themselves
         * within the bounds that the layout has allocated for them.
         */
        for (int line = 0; line < lines; line++)
            buffer.add(GString.blank(lineSize));
        
        bounds = new Region(0,
                            0,
                            lines,
                            lineSize);
    }
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 
                     0, 
                     context.windowSize.width, 
                     context.windowSize.height);
        
        int baseline = g2d.getFontMetrics().getHeight();
        for (int i = 0; i < bounds.getHeight(); i++) {
            for (int j = 0; j < bounds.getWidth(); j++) {
                Glyph glyph = buffer.get(i)
                                    .get(j);
                g2d.setColor(glyph.getColor());
                g2d.drawString(glyph.getCharacter() + "",
                               j * context.charSize.width,
                               i * context.charSize.height + baseline);
            }
        }
    }
}
