package test;

import java.awt.Color;

import jtxt.emulator.Context;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.SequentialLayout;
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
        
        Container root = term.getRootContainer();
        root.setLayout(new SequentialLayout(root,
                                            SequentialLayout.Axis.X));
        
        TextBox hello = new TextBox("Hello, O Beautiful world!");
        term.add(hello);
        hello.inflate(20, 10);
        
        TextBox nother = new TextBox("\\e[000;255;255mDoes this box also " +
                                     "paint itself correctly?");
        term.add(nother);
        nother.inflate(15, 4);
        
        term.update();
        
        GString string = GString.of("\\e[255;000;000mHallo!");
        string = string.concat(GString.of("Toodles!"));
        
        GString sub = string.insert(2, new Glyph('R', Color.BLUE));
        sub.forEach(System.out::println);
        
        String filename = "me2.";
        ASCIImage image = new ASCIImage(context,
                                        filename + "png",
                                        ColorSamplingStrategy.MODAL,
                                        50);
//        image.writeToFile("../out/" +  filename + "png");
    }
}
