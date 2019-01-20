package jtxt.emulator.util;

import java.awt.Color;

public class Glyphs {
    public static String escape(Color color) {
        int rgb = color.getRGB(),
            red = (rgb >> 16) & 0xFF,
            green = (rgb >> 8) & 0xFF,
            blue = rgb & 0xFF;
        
        String escape = "\\e[";
        escape += String.format("%03d;%03d;%03dm", red, green, blue);
        
        return escape;
    }
}
