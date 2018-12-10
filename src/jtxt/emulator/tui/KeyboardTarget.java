package jtxt.emulator.tui;

/**
 * Indicates that a component wishes to be the target for receiving information
 * about key presses when they are focused in the terminal. Implementing this
 * interface does not mean that a component will receive all key presses, but
 * rather that they will be able to receive key presses when this input is
 * directed toward them.
 * 
 * @see jtxt.emulator.Terminal#focus(Component)
 * @see jtxt.emulator.Terminal#focusAt(Location)
 */
public interface KeyboardTarget {
    /**
     * This method should handle key presses directed toward a component that
     * is the target of keyboard events when the component is focused.
     * 
     * @param event The keyboard event carrying information about the key press.
     */
    void keyPressed(Event event);
    
    /**
     * Carries information about an event, such as the cursor location when
     * that key press was generated, the key that was pressed, and other
     * contextual information related to the key press.
     */
    public static class Event { /* TODO */ }
}
