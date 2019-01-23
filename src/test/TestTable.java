package test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.SequentialLayout.SequentialParameters;
import jtxt.emulator.tui.Table;

public class TestTable {
    public static void main(String[] args)
        throws IOException
    {
        Terminal terminal = new Terminal.Builder("Table Test")
                                        .build();
        terminal.createRootContainer(new SequentialLayout(Axis.X));
        
        Table table = new Table(new SequentialParameters(40, 40),
                                4,
                                4);
        
        BufferedImage source = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        table.insert(0, 1, new ASCIImage(null, source));
        
        terminal.add(table);
    }
}
