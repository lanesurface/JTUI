package jtxt.emulator;

import java.awt.Color;
import java.util.ArrayList;

public class Glyph {
    /**
     * The character that this {@code Glyph} represents.
     */
    public final char character;
    
    /**
     * The color of this Glyph (white by default).
     */
    public final Color color;
    
    public Glyph(char character, Color color) {
        this.character = character;
        this.color = color;
    }
    
    public Glyph(char c) {
        this(c, Color.WHITE);
    }
    
    @Override
    public String toString() {
        return String.format("Glyph: [char='%c',color=%s]", 
                             character,
                             color);
    }
}
