
package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Region;
import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.SequentialLayout.SequentialParameters;
import jtxt.emulator.tui.TextBox;

public class Main {
    public static void main(String[] args)
        throws IOException,
               InterruptedException {
        Terminal term = new Terminal.Builder("Terminal")
                                    .font("DejaVu Sans Mono")
                                    .textSize(11)
                                    .build();
        
        Component text = new TextBox(new SequentialParameters(15, 5),
                                     "Hello, O beautiful world!",
                                     TextBox.Position.CENTER),
                  border = new Border(Border.Type.DOTTED,
                                      Color.CYAN,
                                      text);
        term.add(border);
        
        BufferedImage source = ImageIO.read(
            ClassLoader.getSystemResource("app.jpg")
        );
        Component image = new ASCIImage(new SequentialParameters(40, 15),
                                        source);
        term.add(image);
        term.update();
        
        Thread.sleep(1000);
        
        // Should this be exposed? It doesn't update the parameter variable.
        image.setBounds(Region.fromLocation(image.getBounds().start,
                                            50,
                                            15));
        term.update();
    }
}
