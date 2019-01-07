
package test;

import java.awt.Color;
import java.io.IOException;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.TextBox;

public class Main {
    public static void main(String[] args) throws IOException {
        Terminal term = new Terminal.Builder("Terminal")
                                    .font("DejaVu Sans Mono")
                                    .textSize(11)
                                    .build();

//        Container root = term.getRootContainer();
//        root.setLayout(new SequentialLayout(SequentialLayout.Axis.X));
//
//        TextBox nother = new TextBox("\\e[000;255;255mDoes this box also " +
//                                     "paint itself correctly?",
//                                     TextBox.Position.CENTER);
//        Border bnother = new Border(nother,
//                                    Border.Type.DOTTED,
//                                    Color.GREEN);
//        bnother.setSize(15, 6);
//        term.add(bnother);
//
//        BufferedImage source = ImageIO.read(
//            ClassLoader.getSystemResource("app.jpg")
//        );
//        ASCIImage image = new ASCIImage(source);
//        Border border = new Border(image,
//                                   Border.Type.DASHED,
//                                   Color.GRAY);
//        border.setSize(40, 15);
//        term.add(border);
//
//        TextBox box = new TextBox("Hello, O Beautiful World!",
//                                  TextBox.Position.RIGHT);
//        Border bbox = new Border(box,
//                                 Border.Type.CROSS,
//                                 Color.MAGENTA);
//        bbox.setSize(25, 4);
//        term.add(bbox);

        // Button button = new Button("Okay", Color.GRAY);
        // button.setSize(10, 4);
        // term.add(button);
        
        // Oh, wow, I don't think I quite like it like this.... :/
        term.add(
            new RootContainer(
                term.context,
                new SequentialLayout(Axis.X),
                new Border(
                    Border.Type.DOTTED,
                    Color.CYAN,
                    new SequentialLayout.SequentialParameters(-1, -1), // Hmm?
                    new TextBox(
                        new SequentialLayout.SequentialParameters(10, 4),
                        "Hallo, world!",
                        TextBox.Position.CENTER
                    )
                )
            )
        );

        term.update();
    }
}
