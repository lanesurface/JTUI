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

import java.awt.Color;
import java.util.Arrays;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class Border extends Component {
    private Component component;
    
    public static enum Type { 
        SOLID('\u2501'),
        DASHED('-'),
        DOTTED('.'),
        CROSS('+');
        
        private final char character;
        
        Type(char character) {
            this.character = character;
        }
    }
    
    private Glyph border;
    
    public Border(Component component, Type type, Color color) {
        this.component = component;
        border = new Glyph(type.character, color);
    }
    
    @Override
    public void inflate(int width, int height) {
        super.inflate(width, height);
        
        component.bounds = new Region(bounds.start.line + 1,
                                      bounds.start.position + 1,
                                      bounds.end.line - 1,
                                      bounds.end.position - 1);
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        for (int line = bounds.start.line; line < bounds.end.line; line++) {
            /* 
             * Only fill the line when this is the top or bottom edge; 
             * otherwise, only add the border at the left and rightmost
             * positions.
             */
            if (line == bounds.start.line || line == bounds.end.line - 1) {
                for (int position = bounds.start.position;
                     position < bounds.end.position;
                     position++)
                {
                    Glyph[] glyphs = new Glyph[bounds.getWidth()];
                    Arrays.fill(glyphs, border);
                    GString border = new GString(glyphs);
                    
                    frame.update(border, new Region(line,
                                                    bounds.start.position,
                                                    line + 1,
                                                    bounds.end.position));
                }
                
                continue;
            }
            
            frame.update(border, new Location(line, bounds.start.position));
            frame.update(border, new Location(line, bounds.end.position - 1));
        }
        
        component.draw(frame);
    }
}
