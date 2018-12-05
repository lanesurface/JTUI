package jtxt.emulator.util;

import java.awt.Color;
import java.util.ArrayList;

import jtxt.emulator.Glyph;

public class Glyphs {
    private static String escapeColor(Color color) {
        int rgb = color.getRGB(),
            red = (rgb >> 16) & 0xFF,
            green = (rgb >> 8) & 0xFF,
            blue = rgb & 0xFF;
        
        String escape = "\\e[";
        escape += String.format("%03d;%03d;%03dm", red, green, blue);
        
        return escape;
    }
    
    public static String colorize(String text, Color color) {
        String colorized = escapeColor(color);
        colorized += text;
        colorized += escapeColor(Color.WHITE);
        
        return colorized;
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
                    components[ci++] = Integer.parseInt(s);
                    
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
    
    /**
     * Finds the length of the largest word in a string of text.
     * 
     * @param text The text to search.
     * 
     * @return The maximum size of a word in the string of text.
     */
    private int findMaxWordLength(String text) {
        int max = 0;
        
        String[] words = text.split("\\s+");
        
        for (String word : words)
            if (word.length() > max) max = word.length();
        
        return max;
    }
    
    /**
     * <p>
     * Given a string of characters, this method will wrap the characters onto
     * separate lines, based on their position in the line and the specified
     * edge.
     * </p>
     * 
     * <p>
     * <i>Implementation Note</i>: This algorithm is greedy and makes no
     * attempt to balance the distribution of characters between lines.
     * </p>
     * 
     * @param line The line of text to wrap.
     * @param position The position for the first character in the line within
     *                 the terminal's buffer.
     * @param edge The right bounding position for the line.
     * 
     * @return An array of Strings, where each String's length is guaranteed to
     *         be no greater than the amount of characters between the position
     *         of the text and the edge, and spaces between words at the bounds
     *         of a line are discarded.
     */
    private String[] wrap(String line,
                          int size) {
        int len = line.length();
        
        int maxWordSize = findMaxWordLength(line);
        if (maxWordSize > size)
            throw new IllegalArgumentException("Cannot break the line, as " +
                                               "the word size is too large.");
        
        if (len > size) {
            /* 
             * Keeps track of the index of the first character in the line that
             * still needs to be wrapped. 
             */
            int index = 0;
            
            /*
             * The difference between the number of characters that have been
             * wrapped into lines and the number of characters that still need 
             * to be processed.
             */
            int delta = len;
            
            java.util.List<String> lines = new ArrayList<>();
            
            while (delta > size) {
                for (int i = index + size; i > index; i--) {
                    if (line.charAt(i) == ' ') {
                        lines.add(line.substring(index, i));
                        delta -= ++i - index;
                        index = i;
                        
                        break;
                    }
                }
            }
            lines.add(line.substring(index));
            
            return lines.toArray(new String[lines.size()]);
        }
        
        return new String[] { line };
    }
}
