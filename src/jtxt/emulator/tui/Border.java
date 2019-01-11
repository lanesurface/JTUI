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

public class Border extends DefaultComponent {
    /**
     * The component that this border will draw itself around. This component
     * is inflated with the border, so that the border may be displayed on its
     * outer edge.
     */
    private Component component;
    
    /**
     * The type of border to draw. Each type defines the characters that it 
     * will use when being rendered.
     */
    public static enum Type {
        SOLID('\u2501', '\u2503'),
        DASHED('-', '|'),
        DOTTED('.'),
        CROSS('+');
        
        private final char spanCharacter,
                           edgeCharacter;
        
        Type(char character) {
            /*
             * If only one character is specified for this border, use it for
             * drawing the span and edges.
             */
            this(character, character);
        }
        
        Type(char spanCharacter, char edgeCharacter) {
            this.spanCharacter = spanCharacter;
            this.edgeCharacter = edgeCharacter;
        }
    }
    
    private Glyph span,
                  edge;
    
    /**
     * Initializes a border for the given component using the specified glyph.
     * 
     * @param component The component to draw this border around.
     * @param border The glyph to use when drawing the border.
     */
    public Border(Glyph span,
                  Optional<Glyph> edge,
                  Component component) {
        this.component = component;
        this.parameters = component.getLayoutParameters();
        this.span = span;
        this.edge = edge.isPresent()
                    ? edge.get()
                    : span;
    }
    
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
        this(new Glyph(type.spanCharacter, color),
             Optional.of(new Glyph(type.edgeCharacter, color)),
             component);
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
                    Glyph[] glyphs = new Glyph[width];
                    Arrays.fill(glyphs, span);
                    GString border = new GString(glyphs);
                    
                    buffer.update(border, new Location(line,
                                                       bounds.start.position));
                }
                
                continue;
            }
            
            buffer.update(edge, new Location(line, bounds.start.position));
            buffer.update(edge, new Location(line, bounds.end.position - 1));
        }
        
        component.draw(buffer);
    }
}
