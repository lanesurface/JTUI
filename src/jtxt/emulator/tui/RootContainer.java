package jtxt.emulator.tui;

import java.util.ArrayList;

import jtxt.emulator.Context;
import jtxt.emulator.Region;
import jtxt.emulator.ResizeSubscriber;

public class RootContainer extends Container
                           implements ResizeSubscriber {
    /**
     * Creates a new container which occupies the entire area which was
     * given when the context was created. The size of this container will
     * match the dimensions passed to terminal's builder.
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
         * The context has received a request to change the dimensions of
         * the text interface, so we need to update the bounds our children
         * may render themselves within before they attempt to draw themselves
         * again.
         */
        bounds = new Region(0,
                            0,
                            lines,
                            lineSize);
        layout.setParentBounds(bounds);
        
        /*
         * Now each of this container's children need to update their positions
         * within their parent's new bounds.
         * 
         * FIXME: Iterating over the children of this container will return the
         *        children of subcontainers as well. We need to be sure that we
         *        don't set this container as the parent of those components
         *        which don't directly belong to it.
         */
        for (Component child : children)
            child.setParent(this);
    }
}
