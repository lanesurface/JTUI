package jtxt.emulator.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import jtxt.emulator.GString;
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
}
