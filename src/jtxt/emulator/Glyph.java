/* 
 * Copyright (C) 2018, 2019 Lane W. Surface
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package jtxt.emulator;

import java.awt.Color;

public class Glyph {
    /**
     * The character that this Glyph represents.
     */
    public final char character;
    
    /**
     * The color of this Glyph's character.
     */
    public final Color color,
                       background;
    
    public static final Color TRANSPARENT = new Color(0,
                                                      0,
                                                      0,
                                                      0);
    
    /**
     * A Glyph that is guaranteed to not appear in the terminal, but will avoid
     * null pointer exceptions from being thrown by the renderer. This 
     * Glyph has an alpha of <tt>0.0</tt> and is represented by the underlying
     * Unicode null character <tt>\0</tt>.
     */
    public static final Glyph BLANK = new Glyph('\0',
                                                TRANSPARENT,
                                                TRANSPARENT);
    
    /**
     * Constructs a new Glyph with the given character; the character will be
     * rendered in the specified color.
     * 
     * @param character The character that this Glyph represents.
     * @param color The color of this character.
     * @param background The color which appears behind this text.
     */
    public Glyph(char character, Color color, Color background) {
        this.character = character;
        this.color = color;
        this.background = background;
    }
    
    public Glyph(char character, Color color) {
        this(character, color, TRANSPARENT);
    }
    
    /**
     * Constructs a new Glyph with the given character and RGB components.
     * 
     * @param character The character that this Glyph represents.
     * @param red The red value of this character.
     * @param green The green value of this character.
     * @param blue The blue value of this character.
     */
    public Glyph(char character, int red, int green, int blue) {
        this(character,
             new Color(red, green, blue),
             TRANSPARENT);
    }
    
    @Override
    public String toString() {
        return String.format("Glyph: [char='%c', color=%s, bg=%s]%n",
                             character,
                             color,
                             background);
    }
}
