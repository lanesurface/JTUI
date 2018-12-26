package jtxt.emulator.tui;

import java.util.ArrayList;

import jtxt.emulator.Context;
import jtxt.emulator.Region;

public class RootContainer extends Container {
    /**
     * Creates a new container which occupies the given region.
     * 
     * @param bounds The region that this container occupy.
     */
    public RootContainer(Context context) {
        this.context = context;
        this.bounds = new Region(0,
                                 0,
                                 context.numLines,
                                 context.lineSize);
        children = new ArrayList<>();
    }
}
