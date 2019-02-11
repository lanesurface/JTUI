package test;

import java.awt.Color;

import jtxt.emulator.Renderer;
import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.GridLayout.GridParameters;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.SequentialLayout.SequentialParameters;
import jtxt.emulator.tui.TextBox;

public class TestColoredContainer {
    public static void main(String[] args) {
        Terminal terminal =
            new Terminal.Builder("Colored Container")
                        .rasterType(Renderer.RasterType.HARDWARE_ACCELERATED)
                        .transparency(0.6f)
                        .font("Consolas")
                        .build();
        
        GridLayout layout = GridLayout.initializeForDimensions(1, 1);
        terminal.createRootContainer(layout);
        
        GridParameters parameters = layout.getParametersForCell(0, 0);
        Container<Component> container =
            new Container<>(parameters,
                            new SequentialLayout(Axis.X),
                            Color.CYAN);

        terminal.add(new Border(Border.Type.DASHED,
                                Color.LIGHT_GRAY,
                                container));
        
        container.add(new TextBox(new SequentialParameters(20, 10),
                                  "Hello, O beautiful colored container!",
                                  TextBox.Position.CENTER));
    }
}
