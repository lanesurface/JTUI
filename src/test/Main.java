package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.TextBox;

public class Main {
    public static void main(String[] args) throws IOException {
        Terminal term = new Terminal.Builder("Terminal")
                                    .font("DejaVu Sans Mono")
                                    .textSize(11)
                                    .build();
        
        Container root = term.getRootContainer();
        root.setLayout(new SequentialLayout(root,
                                            SequentialLayout.Axis.X));
        
        TextBox nother = new TextBox("\\e[000;255;255mDoes this box also " +
                                     "paint itself correctly?",
                                     TextBox.Justification.CENTER);
        Border bnother = new Border(nother,
                                    Border.Type.DOTTED,
                                    Color.GREEN);
        term.add(bnother);
        bnother.inflate(15, 6);
        
        BufferedImage source = ImageIO.read(ClassLoader.getSystemResource("arches.jpg"));
        ASCIImage image = new ASCIImage(source);
        Border border = new Border(image,
                                   Border.Type.DASHED,
                                   Color.GRAY);
        term.add(border);
        border.inflate(40, 15);
        
        term.update();
    }
}
