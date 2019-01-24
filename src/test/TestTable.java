package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.SequentialLayout.SequentialParameters;
import jtxt.emulator.tui.Table;
import jtxt.emulator.tui.TextBox;

public class TestTable {
    public static void main(String[] args)
        throws IOException
    {
        Terminal terminal = new Terminal.Builder("Table Test")
                                        .font("DejaVu Sans Mono")
                                        .build();
        terminal.createRootContainer(new SequentialLayout(Axis.X));
        
        Table table = new Table(new SequentialParameters(40, 40),
                                4,
                                4);
        
        BufferedImage source = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        table.insert(0, 1, new ASCIImage(null, source));
        
        Component text = new TextBox(null,
                                     "Hello, O Beautiful World!",
                                     TextBox.Position.CENTER);
        table.insert(0, 2, new Border(Border.Type.DASHED,
                                      Color.GREEN,
                                      text));
        
        terminal.add(table);
    }
}
