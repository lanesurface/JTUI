package test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.PlainTextWriter;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.TextBox;

public class TestConsoleOutput {
    public static void main(String[] args) throws IOException {
        GridLayout layout = GridLayout.initializeForDimensions(1, 2);
        
        TextBox text = new TextBox(layout.getParametersForCell(0, 0),
                                   "Hello world!",
                                   TextBox.Position.CENTER);
        
        BufferedImage image = ImageIO.read(
            ClassLoader.getSystemResource("coke.jpg")
        );
        ASCIImage coke = new ASCIImage(layout.getParametersForCell(0, 1),
                                       image);
        
        PlainTextWriter writer = new PlainTextWriter(System.out);
        writer.draw(80,
                    20,
                    layout,
                    text,
                    coke);
    }
}
