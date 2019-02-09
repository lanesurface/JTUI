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
import java.util.Optional;

import jtxt.GlyphBuffer;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class Border extends Decorator {
    /**
     * The type of border to draw. Each type defines the characters that it 
     * will use when being rendered.
     */
    public static enum Type {
        DASHED('-', '|', '+'),
        DOTTED('.'),
        CROSS('+');
        
        private final char span,
                           edge,
                           corner;
        
        Type(char character) {
            /*
             * If only one character is specified for this border, use it for
             * drawing the span and edges.
             */
            this(character,
                 character,
                 character);
        }
        
        Type(char span, char edge, char corner) {
            this.span = span;
            this.edge = edge;
            this.corner = corner;
        }
    }
    
    private Type type;
    
    private Color color;
    
    /**
     * Initializes a border for the given component, using the character
     * defined by the type and the color for drawing the border.
     * 
     * @param component The component to draw this border around.
     * @param type The type of character to use for drawing the border.
     * @param color The color of the border.
     */
    public Border(Type type,
                  Color color,
                  Component component) {
        this.type = type;
        this.color = color;
        this.component = component;
    }
    
    @Override
    public void setBounds(Region bounds) {
        super.setBounds(bounds);
        
        component.setBounds(new Region(bounds.start.line + 1,
                                       bounds.start.position + 1,
                                       bounds.end.line - 1,
                                       bounds.end.position - 1));
    }
    
    @Override
    public void draw(GlyphBuffer buffer) {
        super.draw(buffer);
        
        Glyph[] glyphs = new Glyph[width];
        Arrays.fill(glyphs, new Glyph(type.span,
                                      color,
                                      background));
        glyphs[0] = glyphs[width - 1] = new Glyph(type.corner,
                                                  color,
                                                  background);
        GString border = new GString(glyphs);
        
        for (int line = bounds.start.line; line < bounds.end.line; line++) {
            /* 
             * Only fill the line when this is the top or bottom edge; 
             * otherwise, add the border at the left and rightmost positions.
             */
            if (line == bounds.start.line || line == bounds.end.line - 1) {
                for (int position = bounds.start.position;
                     position < bounds.end.position;
                     position++)
                {
                    buffer.update(border, Location.at(bounds,
                                                      line,
                                                      bounds.start.position));
                }
                
                continue;
            }
            
            Glyph edge = new Glyph(type.edge, color, background);
            buffer.update(edge, Location.at(bounds,
                                            line,
                                            bounds.start.position));
            buffer.update(edge, Location.at(bounds,
                                            line,
                                            bounds.end.position - 1));
        }
    }
}
