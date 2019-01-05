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

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Location;

public class TextBox extends DefaultComponent {
    /**
     * The text to draw onto the screen.
     */
    private GString text;
    
    public static enum Position { LEFT,
                                  CENTER,
                                  RIGHT }
    
    private Position justification;
    
    public TextBox(Layout.Parameters parameters,
                   String text,
                   Position justification) {
        this.parameters = parameters;
        this.text = GString.of(text);
        this.justification = justification;
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        GString[] lines = text.wrap(bounds.getWidth());
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
