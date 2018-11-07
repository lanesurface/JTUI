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
class BufferedTextPane extends JComponent {
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
    private char[][] buffer;
    
    /**
     * Holds information about the way to render the text on the screen.
     */
    private Configuration config;
    
    /**
     * Creates a new text area with an editable text buffer.
     * 
     * @param config The properties used for rendering the text.
     */
    public BufferedTextPane(Configuration config) {
        this.config = config;
        buffer = new char[config.numLines][config.lineSize];
    }
    
    /**
     * Updates a character in the text buffer.
     * 
     * @param c The character to replace in the buffer.
     * @param line The line in the buffer to place the character.
     * @param position The position in the buffer to place the character.
     */
    public void update(char c, int line, int position) {
        buffer[line][position] = c;
    }
    
    /**
     * Inserts a string into the text buffer.
     * 
     * @param s The string to insert into the buffer.
     * @param line The line in the buffer to place the string.
     * @param position The position in the buffer for the first character in
     *                 the string.
     */
    public void update(String s, int line, int position) {
        // TODO: Account for out-of-bounds exception here?
        for (int i = 0; i < s.length(); i++)
            buffer[line][position+i] = s.charAt(i);
    }
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        
        g2d.setColor(new Color(0, 0, 172));
        g2d.fillRect(0, 0, config.windowSize.width, config.windowSize.height);
        
        // TODO: Allow characters to carry color information.
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < buffer.length; i++)
            for (int j = 0; j < buffer[i].length; j++)
                g2d.drawString(buffer[i][j]+"", 
                               (j+1)*config.charSize.width, 
                               (i+1)*config.charSize.height);
    }
}
