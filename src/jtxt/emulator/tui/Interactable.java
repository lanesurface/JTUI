package jtxt.emulator.tui;

import jtxt.emulator.Location;

/**
 * A {@code Component} which implements this interface receives notifications
 * whenever a mouse event is generated within its bounds. This interface is 
 * specific to the terminal; only components which are meant to be part of a
 * terminal-based textual interface should be {@code Interactable}, as
 * components which are added to a document are static after that document is
 * generated.
 * 
 * @see jtxt.emulator.Terminal
 */
public interface Interactable {
    /**
     * This method is called whenever a mouse click is generated within the
     * bounds of a {@code Component} which is interactive. 
     * 
     * @param clickLocation The location within the terminal in which the mouse
     *                      was clicked.
     * 
     * @return Whether or not this component should now become a target for key
     *         events. (i.e. If this component should become focused in the
     *         terminal.)
     */
    boolean clicked(Location clickLocation);
    
    /**
     * Add a {@code Callback} that this interactive component will dispatch
     * new events to whenever they occur.
     * 
     * @param callback The callback to add to this component.
     */
    void addCallback(Callback callback);
    
    @FunctionalInterface
    public static interface Callback {
        void dispatch();
    }
}
