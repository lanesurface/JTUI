package jtxt.emulator;

import java.awt.Color;

public class Character {
    public final char character;
    
    public final Color color;
    
    public Character(char character, Color color) {
        this.character = character;
        this.color = color;
    }
    
    public static final Character BLOCK = new Character('\u2588', Color.WHITE);
    
    public static final Character BLOCK_SHADE = new Character('\u2592',
                                                              Color.WHITE);
}
