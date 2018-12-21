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

import javax.swing.JComponent;

/**
 * Handles the display of text on the screen. Characters added to the buffer
 * will be rendered appropriately when this component is drawn.
 * 
 * Since this API does not aim to give full control of the way the text is 
 * drawn, the user specifies properties of the {@code Terminal} using a 
 * {@code Configuration} object, and the panel renders appropriately.
 */
@SuppressWarnings("serial")
class BufferedTextPane extends JComponent
                       implements GlyphRenderer {
    /**
     * The buffer holds all of the characters that need to be drawn in the
     * terminal window. This buffer isn't directly editable so that we can
     * make sure the window updates as soon as its content changes, though
     * all positions in the buffer can be modified at any point themselves,
     * either by positioning of the cursor or through an appropriate method
     * invocation.
     * 
     * This is implemented as a 2-dimensional character array to make it easier
     * to insert and wrap text, as text-based applications should have no way
     * to scroll (though technically we could do so).
     */
    private GString[] buffer; // TODO: Make type of ArrayList....
    
    /**
     * Holds information about the way to render the text on the screen.
     */
    private Context context;
    
    /**
     * The number of lines this buffer can support.
     */
    private int lines;
    
    /**
     * The number of characters in each line of the buffer.
     */
    private int lineSize;
    
    /**
     * Creates a new fixed size buffered-text pane.
     * 
     * @param config The properties used for rendering the text.
     */
    public BufferedTextPane(Context config) {
        this.context = config;
        lines = config.numLines;
        lineSize = config.lineSize;
        buffer = new GString[lines];
        
        // Make sure there are no null characters in the buffer.
        clear();
    }
    
    public void update(Glyph glyph, Location location) {
        if (location.outside(context))
            throw new LocationOutOfBoundsException(location);
        
        buffer[location.line].insert(location.position, glyph);
    }

    public void update(GString glyphs, Region region) {
        int width = region.getWidth(),
            height = region.getHeight();
        Location start = region.getStart();
        
        GString[] lines = glyphs.wrap(width);
        for (int l = 0; l < height && l < lines.length; l++) {
            for (int p = 0; p < width && p < lines[l].length(); p++) {
                Location local = start.add(new Location(l, p));
                buffer[local.line].insert(local.position, lines[l].get(p));
            }
        }
    }

    public void update(GString[] glyphs, Location start) {        
        for (int l = 0; l < glyphs.length; l++)
            for (int p = 0; p < glyphs[l].length(); p++)
                buffer[start.line + l].insert(start.position + p,
                                              glyphs[l].get(p));
    }

    public Glyph getGlyph(Location location) {
        if (location.outside(context))
            throw new LocationOutOfBoundsException(location);
        
        return buffer[location.line].get(location.position);
    }

    public GString[] getGlyphs(Region region) {
        if (!region.inside(new Region(0,
                                      0,
                                      lines,
                                      lineSize)))
            throw new LocationOutOfBoundsException("The given region is " +
                                                   "outside the bounds of " +
                                                   "the text pane.");
        
        Location start = region.getStart(),
                 end = region.getEnd();
        
        GString[] glyphs = new GString[region.getHeight()];
        for (int line = start.line; line < end.line; line++)
            glyphs[line] = buffer[line].substring(start.position,
                                                  end.position);
        
        return glyphs;
    }
    
    public void clear() {
        for (int i = 0; i < lines; i++)
            buffer[i] = new GString(lineSize);
    }
    
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
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < lineSize; j++) {
                Glyph glyph = buffer[i].get(j);
                g2d.setColor(glyph.getColor());
                g2d.drawString(glyph.getCharacter()+"",
                               j * context.charSize.width,
                               i * context.charSize.height + baseline);
            }
        }
    }
}
