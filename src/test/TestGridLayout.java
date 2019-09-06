package test;

import jtxt.emulator.EmulatedTerminal;
import jtxt.Terminal;
import jtxt.emulator.tui.*;
import jtxt.emulator.tui.GridLayout.GridParameters;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TestGridLayout {
  public static void main(String[] args) {
    Terminal terminal = new EmulatedTerminal(
      "GridLayout",
      80,
      20,
      "Consolas",
      12,
      Color.BLACK,
      0.8f);
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
    GridLayout layout = new GridLayout(
      1,
      2,
      3);
    terminal.createRootContainer(layout);

    Component photo = null;
    try {
      BufferedImage source = ImageIO.read(
        ClassLoader.getSystemResource("coke.jpg")
      );

      GridParameters picParams = layout.getParametersForCellsInRange(
        0,
        0,
        1,
        1);
      photo = new Border(
        Border.Type.DASHED,
        Color.GRAY,
        new ASCIImage(
          picParams,
          source));
    } catch (IOException ie) {
      System.err.println("The resource could not be loaded from the " +
                         "classpath. Make sure the resource exists " +
                         "and the classpath is configured correctly.");
      ie.printStackTrace();

      System.exit(-1);
    }

    GridParameters textParams = layout.getParametersForCellsInRange(
      2,
      0,
      2,
      1);
    Component text = new TextBox(
      textParams,
      "Hello, O Beautiful World!",
      TextBox.Position.CENTER);

    GridParameters buttonParams = layout.getParametersForCell(
      2,
      2);
    Button button = new Button(
      "PRESS ME TO SEE CONSOLE OUTPUT...",
      Color.GREEN,
      buttonParams);
    button.addCallback(() -> System.out.println("pressed!"));

    terminal.add(
      photo,
      text,
      button);
  }
}
