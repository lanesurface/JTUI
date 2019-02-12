package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Border;
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
        table.add(0, 2, new Border(Border.Type.DASHED,
                                   Color.GREEN,
                                   text));
        
        Component inText = new TextBox(null,
                                       "\\e[255;000;150mint" +
                                       "\\e[255;255;255m x = y;",
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
                               inText,
                               inButton);
        
        terminal.add(table);
    }
}
