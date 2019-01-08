package test;

import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.RootContainer;
import jtxt.emulator.tui.TextBox;

public class TestGridLayout {
    public static void main(String[] args) {
        Terminal terminal = new Terminal.Builder("Grid Test")
                                        .dimensions(80, 20)
                                        .build();
        /* 
         * I should note here that the root container's layout has now been
         * defined to be made up of a series of "cells" on each row, which are
         * passed in as parameters to the `GridLayout` constructor. A model is
         * shown here:
         * 
         * ROW -----------------------------
         *     |                           |
         *  1  |                           |
         *     |---------------------------|
         *     |             |             |
         *  2  |             |             |
         *     |---------------------------|
         *     |       |          |        |
         *  3  |       |          |        |
         *     -----------------------------
         */
        GridLayout layout = new GridLayout(new int[] { 1,
                                                       2,
                                                       3 });
        RootContainer root = new RootContainer(terminal.context, layout);
        terminal.setRootContainer(root);
        
        // TODO: Make sure to do some bounds checking in the GridParams class.
        Component text = new TextBox(layout.new GridParameters(0, 0, 1, 1),
                                     "Hello grid layout!",
                                     TextBox.Position.CENTER);
        terminal.add(text);
        terminal.update();
    }
}
