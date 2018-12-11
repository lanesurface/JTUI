package jtxt.emulator;

import java.awt.Color;

public class Glyph {
    /**
     * The character that this {@code Glyph} represents.
     */
    private final char character;
    
    /**
     * The color of this Glyph (white by default).
     */
    private final Color color;
    
    public Glyph(char character, Color color) {
        this.character = character;
        this.color = color;
    }
    
    public Glyph(char c) {
        this(c, Color.WHITE);
    }
    
    public char getCharacter() {
        return character;
    }
    
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
