package test;

import jtxt.ConsoleWriter;
import jtxt.emulator.BufferedFrame;
import jtxt.emulator.Region;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.RootContainer;
import jtxt.emulator.tui.TextBox;

public class TestConsoleOutput {
    public static void main(String[] args) {
        Region bounds = new Region(0,
                                   0,
                                   20,
                                   80);
        GridLayout layout = GridLayout.initializeForDimensions(1, 1);
        RootContainer root = new RootContainer(bounds,
                                               layout);
        
        TextBox text = new TextBox(layout.getParametersForCell(0, 0),
                                   "Hello world!",
                                   TextBox.Position.CENTER);
        root.add(text);
        
        BufferedFrame frame = new BufferedFrame(bounds);
        root.draw(frame);
        
        ConsoleWriter writer = new ConsoleWriter(System.out);
        writer.print(frame);
    }
}
