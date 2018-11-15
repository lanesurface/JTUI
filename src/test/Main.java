package test;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import jtxt.emulator.Context;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Terminal;
import jtxt.emulator.util.image.Images;

public class Main {
    public static void main(String[] args) {
        Context context = new Context("Terminal",
                                      200, 70,
                                      "DejaVu Sans Mono", 10);
        Terminal term = new Terminal(context);
        
        term.putLine("This text demonstrates the wrapping features of the " +
                     "terminal. Text can be easily wrapped based on " +
                     "location and a boundary.", 
                     new Location(5, 70),
                     85);
        
        term.putLine("Hello, world!");
        
        try {
            long start = System.nanoTime();
            Image img = ImageIO.read(ClassLoader.getSystemResource("coke.jpg"));
            long end = System.nanoTime();
            System.out.printf("Resource loading took %d ms.%n", 
                (int)(end - start) / 1_000_000);
            
//            String[] lines = Images.convertToASCII(img, 110);
//            
//            File f = new File("output.txt");
//            if (!f.exists()) f.createNewFile();
//            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//            
//            for (String line : lines)
//                writer.write(line + "\r\n");
//            
//            writer.close();
//            
//            term.putLines(lines, new Location(0, 0));
//            end = System.nanoTime();
            
            BufferedImage bi = Images.toBufferedImage(img);
            Glyph[][] chars = Images.mapToCharacters(bi, 100);
            
            term.cursor.setLocation(0, 0);
            for (Glyph[] glyphs : chars)
                term.putLine(glyphs);
            
            System.out.printf("TOTAL FETCH AND RENDER TIME: %d ms.%n",
                (int)(end - start) / 1_000_000);
        } catch (java.io.IOException ie) { }
        
        term.cursor.setLocation(0, 80);
        term.putNewLine("Does \\e[255;000;000mthis \\e[255;255;255mwork?");
        
        Glyph[] gs = Glyph.of("\\e[016;229;165mTeal text is awesome!");
        term.putLine(gs);
        
        // Is there a printf like function?
        // Use String.format(...) and pass as the argument to putLine(...)
        int age = Integer.parseInt(term.requestInput("Age:"));
        String msg = String.format("Your look %d.", age + 10);
        term.clear();
        term.putLine(msg);
    }
}
