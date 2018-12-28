package jtxt.emulator.tui;

import java.util.ArrayList;

import jtxt.emulator.Context;
import jtxt.emulator.Region;

public class RootContainer extends Container {
    /**
     * Creates a new container which occupies the entire area which was
     * given when the context was created. The size of this container will
     * match the dimensions passed to terminal's builder.
     */
    public RootContainer(Context context) {
        this.context = context;
        bounds = new Region(0,
                            0,
                            context.numLines,
                            context.lineSize);
        children = new ArrayList<>();
    }
    
    @Override
    public void setParent(Container parent) {
        throw new UnsupportedOperationException("Cannot set the parent of a " +
                                                "root container.");
    }
}
