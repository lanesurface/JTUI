package test;

import jtxt.SystemConsole;
import jtxt.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.TextBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TestConsoleOutput {
    public static void main(String[] args) throws IOException {
        Terminal console = new SystemConsole();
        GridLayout layout = GridLayout.initializeForDimensions(1, 2);
        console.createRootContainer(layout);
        
        TextBox text = new TextBox(layout.getParametersForCell(0, 0),
                                   "Hello world!",
                                   TextBox.Position.CENTER);
        
        BufferedImage image = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        ASCIImage ascii = new ASCIImage(layout.getParametersForCell(0, 1),
                                        image);

        console.add(text,
                    ascii);
    }
}
