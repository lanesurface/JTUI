package test;

import java.awt.Color;

import jtxt.emulator.Context;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Border;
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
        
        TextBox hello = new TextBox("Hello, O Beautiful world!",
                                    TextBox.Justification.RIGHT);
        Border border = new Border(hello,
                                   Border.Type.CROSS,
                                   Color.RED);
        term.add(border);
        border.inflate(15, 5);
        
        TextBox nother = new TextBox("\\e[000;255;255mDoes this box also " +
                                     "paint itself correctly?",
                                     TextBox.Justification.LEFT);
        Border bnother = new Border(nother,
                                    Border.Type.DOTTED,
                                    Color.GREEN);
        term.add(bnother);
        bnother.inflate(15, 6);
        
        term.update();
    }
}
