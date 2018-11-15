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
    
    /**
     * Constructs an array of Glyphs for the given string of text. The presence
     * of escape sequences in the string (delimited by <code>\e[...m</code>) is
     * taken into account when constructing these Glyphs; therefore, do not
     * necessarily expect that the length of the given text and the length of
     * the array returned by this method will be the same.
     * 
     * @param text ...
     * 
     * @return ...
     */
    public static Glyph[] of(String text) {
        /*
         * Support extracting color data in the form of 
         * "\e[<0..255>;<0..255>;<0..255>m".
         */
        char[] chars = text.toCharArray();
        
        ArrayList<Glyph> glyphs = new ArrayList<>();
        Color current = Color.WHITE;
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\\' && chars[i+1] == 'e') {
                i += 2;
                
                int ci = 0;
                int[] components = new int[3];
                do {
                    char[] comp = { chars[++i],
                                    chars[++i],
                                    chars[++i] };
                    
                    String s = new String(comp);
                    components[ci++] = Integer.valueOf(s);
                    
                    i++;
                } while (chars[i] != 'm');
                
                current = new Color(components[0], 
                                    components[1], 
                                    components[2]);
                i++;
            }
            
            glyphs.add(new Glyph(chars[i], current));
        }
        
        return glyphs.toArray(new Glyph[glyphs.size()]);
    }
}
