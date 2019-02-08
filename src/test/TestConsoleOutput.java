package test;

import jtxt.PlainTextWriter;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.TextBox;

public class TestConsoleOutput {
    public static void main(String[] args) {
        GridLayout layout = GridLayout.initializeForDimensions(1, 1);
        TextBox text = new TextBox(layout.getParametersForCell(0, 0),
                                   "Hello world!",
                                   TextBox.Position.CENTER);
        
        PlainTextWriter writer = new PlainTextWriter(System.out);
        writer.draw(80,
                    20,
                    layout,
                    text);
    }
}
