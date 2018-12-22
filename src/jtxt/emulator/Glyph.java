package jtxt.emulator;

import java.awt.Color;

public class Glyph {
    /**
     * The character that this glyph represents.
     */
    private final char character;
    
    /**
     * The color of this glyph's character.
     */
    private final Color color;
    
    /**
     * A glyph that is guaranteed to not appear in the terminal, but will avoid
     * null pointer exceptions from being thrown by the renderer. This 
     * glyph has an alpha of <tt>0.0</tt> and is represented by the underlying
     * Unicode null character <tt>\0</tt>.
     */
    public static final Glyph BLANK = new Glyph(new Character('\0'),
                                                new Color(0, 0, 0, 0));
    
    /**
     * Constructs a new glyph with the given character; the character will be
     * rendered in the specified color.
     * 
     * @param character The character that this glyph represents.
     * @param color The color of this character.
     */
    public Glyph(char character, Color color) {
        this.character = character;
        this.color = color;
    }
    
    /**
     * Constructs a new glyph with the given character and RGB components.
     * 
     * @param character The character that this glyph represents.
     * @param red The red value of this character.
     * @param green The green value of this character.
     * @param blue The blue value of this character.
     */
    public Glyph(char character, int red, int green, int blue) {
        this(character, new Color(red, green, blue));
    }
    
    /**
     * Get the underlying character of this glyph.
     * 
     * @return The character of this glyph.
     */
    public char getCharacter() {
        return character;
    }
    
    /**
     * Get the color of this glyph.
     * 
     * @return The color of this glyph.
     */
    public Color getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return String.format("Glyph: [char='%c',color=%s]", 
                             character,
                             color);
    }
}
