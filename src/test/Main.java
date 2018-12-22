package test;

import java.awt.Color;

import jtxt.emulator.*;
import jtxt.emulator.tui.TextBox;
import jtxt.emulator.util.image.ASCIImage;
import jtxt.emulator.util.image.ColorSamplingStrategy;

public class Main {
    public static void main(String[] args) {
        Terminal term = new Terminal.Builder("Terminal")
                                    .font("DejaVu Sans Mono")
                                    .textSize(11)
                                    .build();
        Context context = term.getContext();
        
        TextBox box = new TextBox("Hello, O Beautiful world!");
        box.inflate(20, 10);
        // term.add(box, ...);
        
        // TODO: Make GString the default way to pass around a collection of
        // Glyphs. (Make GlyphRenderer compatible and Glyphs.of(...) return 
        // GString, as well as moving Glyphs methods to GString itself.)
        GString string = GString.of("\\e[255;000;000mHallo!");
        string = string.concat(GString.of("Toodles!"));
        
        GString sub = string.insert(2, new Glyph('R', Color.BLUE));
//        sub.forEach(System.out::println);
        
        String filename = "me2.";
        ASCIImage image = new ASCIImage(context,
                                        filename + "png",
                                        ColorSamplingStrategy.MODAL,
                                        50);
//        image.writeToFile("../out/" +  filename + "png");
    }
}
