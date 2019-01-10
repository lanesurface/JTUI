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
package jtxt.emulator.tui;

import jtxt.Canvas;
import jtxt.emulator.GString;
import jtxt.emulator.Location;

public class TextBox extends DefaultComponent {
    /**
     * The text to draw onto the screen.
     */
    private GString text;
    
    /**
     * The position within the bounds of this component that the text should
     * be justified in.
     */
    public static enum Position { LEFT,
                                  CENTER,
                                  RIGHT }
    
    private Position justification;
    
    /**
     * Creates a new {@code TextBox} containing the given string in the
     * given position (see {@link Position}). The string is interpreted before
     * the component is drawn, so the presence of escapes within the string
     * will be applied to their respective characters. (In other words, there 
     * is no need to convert an escaped string into a {@link GString}.
     * 
     * @param parameters The parameters for the layout that this component's
     *                   parent container has defined. Note passing an
     *                   incompatible parameter type into this argument may
     *                   cause the layout to throw an exception when this
     *                   component is added.
     * @param text The text which this component should contain.
     * @param justification The position within this component in which the
     *                      text should be placed.
     */
    public TextBox(Object parameters,
                   String text,
                   Position justification) {
        this.parameters = parameters;
        this.text = GString.of(text);
        this.justification = justification;
    }
    
    @Override
    public void draw(Canvas frame) {
        GString[] lines = text.wrap(width);
        for (int line = 0; line < lines.length; line++) {
            int spos = bounds.start.position;
            
            switch (justification) {
            case RIGHT:
                spos += bounds.getWidth() - lines[line].length();
                break;
            case CENTER:
                spos += (bounds.getWidth() - lines[line].length()) / 2;
                break;
            default: break;
            }
            
            frame.update(lines[line], new Location(bounds.start.line + line,
                                                   spos));
        }
    }
}
