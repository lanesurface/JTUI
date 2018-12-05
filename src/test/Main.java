package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import jtxt.emulator.Context;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Terminal;
import jtxt.emulator.util.Glyphs;
import jtxt.emulator.util.image.ASCIImage;
import jtxt.emulator.util.image.ColorSamplingStrategy;
import jtxt.emulator.util.image.Images;

public class Main {
    public static void main(String[] args) {
        Context context = new Context("Terminal",
                                      80, 20,
                                      "Consolas", 12);
        Terminal term = new Terminal(context);
        
        term.putLine("This text demonstrates the wrapping features of the " +
                     "terminal. Text can be easily wrapped based on " +
                     "location and a boundary.",
                     new Location(0, 20),
                     35);
        
        term.putLine("Hello, world!");
        
        String filename = "mt-st-helens.";
        ASCIImage image = new ASCIImage(context, 
                                        filename + "jpg", 
                                        ColorSamplingStrategy.MODAL,
                                        400);
        image.writeToFile(filename + "png");
        
        term.putNewLine("Does \\e[255;000;000mthis \\e[255;255;255mwork?");
        
        String text = Glyphs.colorize("How's this?", Color.BLUE);
        System.out.println(text);
        
        Glyph[] gs = Glyphs.of("\\e[016;229;165mTeal text is awesome!");
        term.putLine(gs);
        
        // Is there a printf like function?
        // Use String.format(...) and pass as the argument to putLine(...)
        int age = Integer.parseInt(term.requestInput("Age:"));
        String msg = String.format("Your look %d.", age + 10);
        term.clear();
        term.putLine(msg);
    }
}
