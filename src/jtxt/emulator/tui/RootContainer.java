
package jtxt.emulator.tui;

import java.util.ArrayList;

import jtxt.emulator.Context;
import jtxt.emulator.Region;
import jtxt.emulator.ResizeSubscriber;

public class RootContainer extends Container implements ResizeSubscriber {
    /**
     * Creates a new container which occupies the entire area which was given
     * when the context was created. The size of this container will match the
     * dimensions passed to terminal's builder.
     */
    public RootContainer(Context context) {
        this.context = context;
        bounds = new Region(0,
                            0,
                            context.getNumberOfLines(),
                            context.getLineSize());
        children = new ArrayList<>();
    }

    @Override
    public void setParent(Container parent) {
        throw new UnsupportedOperationException("Cannot set the parent of a " +
                                                "root container.");
    }

    @Override
    public void resize(int lines, int lineSize) {
        /*
         * The context has received a request to change the dimensions of the
         * text interface, so we need to update the bounds our children may
         * render themselves within before before they are redrawn.
         */
        bounds = new Region(0,
                            0,
                            lines,
                            lineSize);
        layout.setParentBounds(bounds);
        children.forEach(child -> child.getBoundsFromParent());
    }
}
