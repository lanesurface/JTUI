package test;

import jtxt.ANSIWriter;
import jtxt.emulator.Region;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.RootContainer;
import jtxt.emulator.tui.TextBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TestConsoleOutput {
    public static void main(String[] args) throws IOException {
        GridLayout layout = GridLayout.initializeForDimensions(1, 2);
        
        TextBox text = new TextBox(layout.getParametersForCell(0, 0),
                                   "Hello world!",
                                   TextBox.Position.CENTER);
        
        BufferedImage image = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        ASCIImage ascii = new ASCIImage(layout.getParametersForCell(0, 1),
                                        image);

        ANSIWriter writer = new ANSIWriter(System.out);
        Region bounds = new Region(0,
                                   0,
                                   20,
                                   80);
        RootContainer root = new RootContainer(bounds,
                                               layout,
                                               text,
                                               ascii);
        writer.draw(root.drawToBuffer());
    }
}
