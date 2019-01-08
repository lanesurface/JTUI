package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.ASCIImage;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.RootContainer;

public class TestGridLayout {
    public static void main(String[] args) {
        Terminal terminal = new Terminal.Builder("Grid Test")
                                        .dimensions(80, 20)
                                        .font("DejaVu Sans Mono")
                                        .textSize(11)
                                        .build();
        /* 
         * This layout takes an array of integers, where each element in the
         * array represents the number of cells in the respective row in the
         * layout. The number of rows is inferred from the number of elements
         * in the array. A model of this `GridLayout` is shown below:
         * 
         * ROW -----------------------------
         *     |                           |
         *  0  |                           |
         *     |---------------------------|
         *     |             |             |
         *  1  |             |             |
         *     |---------------------------|
         *     |       |          |        |
         *  2  |       |          |        |
         *     -----------------------------
         */
        GridLayout layout = new GridLayout(new int[] { 1,
                                                       2,
                                                       3 });
        RootContainer root = new RootContainer(terminal.getContext(), layout);
        terminal.setRootContainer(root);
        
        try {
            BufferedImage source = ImageIO.read(
                ClassLoader.getSystemResource("app.jpg")
            );
            
            Component photo = new ASCIImage(layout.getParametersForCellsFrom(0,
                                                                             0,
                                                                             2,
                                                                             2),
                                            source),
                      border = new Border(Border.Type.DASHED,
                                          Color.GRAY,
                                          photo);
            terminal.add(border);
        }
        catch (IOException ie) {
            System.err.println("The resource could not be loaded from the " +
                               "classpath. Make sure the resource exists " +
                               "and the classpath is configured correctly.");
        }
        terminal.update();
    }
}
