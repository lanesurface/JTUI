package jtxt.emulator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

class EventDispatcher extends MouseAdapter implements Runnable {
    private Terminal terminal;
    
    private Renderer renderer;
    
    private Context context;
    
    /**
     * A Queue containing all of the mouse events that have occurred since the
     * last update (processing event), which will be either dispatched to their
     * respective component or discarded if the target component isn't
     * {@code Interactable}.
     */
    protected Queue<MouseEvent> mouseEvents;
    
    public EventDispatcher(Terminal terminal,
                           Context context,
                           Renderer renderer) {
        this.terminal = terminal;
        this.renderer = renderer;
        this.context = context;
        mouseEvents = new LinkedList<>();
    }
    
    /**
     * Polls for changes that can occur frequently and at random times (such
     * as changes to window dimensions, and input events from the keyboard and
     * mouse).
     */
    private void poll() {
        long last = System.currentTimeMillis(),
             msPerUpdate = 1000 / context.updatesPerSecond,
             lag = 0;
        terminal.update();
        
        while (true) {
            long now = System.currentTimeMillis(),
                 elapsed = now - last;
            last = now;
            lag += elapsed;

            while (lag >= msPerUpdate) {
                // Make sure that the bounds of the window have not changed.
                int width = renderer.getWidth(),
                    height = renderer.getHeight(),
                    numLines = height / context.charSize.height,
                    lineSize = width / context.charSize.width;
                
                if (numLines != context.getNumberOfLines()
                    || lineSize != context.getLineSize())
                {
                    context.setDimensions(numLines,
                                          lineSize,
                                          width,
                                          height);
                    
                    terminal.update();
                }
                
                dispatchMouseEvents();
                
                lag -= msPerUpdate;
            }
            
            renderer.repaint();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent event) {
        mouseEvents.add(event);
    }
    
    /**
     * <P>
     * Removes all mouse events from the event queue, and notifies the
     * respective {@code Component}s that should receive the events if they are
     * {@code Interactable} (meaning they can receive, and respond to, mouse
     * events). The order in which these events are dispatched is the same as
     * the order in which they originally occurred.
     * </P>
     * <P>
     * Additionally, this method may change the actively focused Component
     * within the terminal if any of the Components which received a mouse
     * event and were Interactable requested to be focused by the terminal.
     * </P>
     */
    protected synchronized void dispatchMouseEvents() {
        while (!mouseEvents.isEmpty()) {
            MouseEvent event = mouseEvents.remove();
            int x = event.getX(),
                y = event.getY();
            
            // Determine the location that this event orginated from.
            Location loc = new Location(y / context.charSize.height,
                                        x / context.charSize.width);
            
            terminal.clickComponent(loc);
        }
    }
    
    @Override
    public void run() {
        poll();
    }
}
