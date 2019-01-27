package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Button;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.Table;
import jtxt.emulator.tui.TextBox;

public class TestTable {
    public static void main(String[] args)
        throws IOException
    {
        Terminal terminal = new Terminal.Builder("Table Test")
                                        .font("DejaVu Sans Mono")
                                        .build();
        
        GridLayout layout = GridLayout.initializeForDimensions(1, 1);
        terminal.createRootContainer(layout);
        
        Table table = new Table(layout.getParametersForCell(0, 0),
                                4,
                                4);
        
        BufferedImage source = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        table.add(0, 0, new ASCIImage(null, source));
        
        Component text = new TextBox(null,
                                     "Hello, O Beautiful World!",
                                     TextBox.Position.CENTER);
        /* 
         * FIXME: For some reason, inserting Components wrapped in a Border
         *        causes the entire Container to disappear in the terminal.
         */
//        table.add(0, 2, new Border(Border.Type.DASHED,
//                                   Color.GREEN,
//                                   text));
        table.add(0, 1, text);
        
        Component inText = new TextBox(null,
                                       "This is an inserted string!",
                                       TextBox.Position.LEFT),
                  inButton = new Button("Inserted button!",
                                        Color.CYAN,
                                        null);
        
        /*
         * FIXME: Buttons don't respond to input events (because of the way we
         *        are iterating over the Container).
         */
        table.insertIntoColumn(0,
                               0,
                               false,
                               inText,
                               inButton);
        
        terminal.add(table);
    }
}
