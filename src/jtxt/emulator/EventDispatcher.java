/* 
 * Copyright (C) 2019 Lane W. Surface
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package jtxt.emulator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles events which are propagated by a user, and which must be polled for
 * over the lifetime of the program. This class serves as the foundation for
 * asynchronous notifications within the terminal, and the bridge between Swing
 * events and terminal user-interface Components.
 */
class EventDispatcher extends MouseAdapter implements Runnable {
    /**
     * The instance of the terminal that this dispatcher is listening for. Keep
     * a reference here, as we may need to notify it whenever some relavent
     * state changes.
     */
    private Terminal terminal;
    
    /**
     * The instance of the {@code Renderer} that the terminal constructed. 
     * May need to be notified whenever the window is resized, or the screen
     * needs to be refreshed. (The amount of times that this Renderer will be
     * notified depends on the number of updates per second that the client
     * has requested.)
     */
    private Renderer renderer;
    
    /**
     * The environment context, which we may need to update if the dimensions
     * of the window change, or if we need to determine certain properties that
     * influence how we dispatch events to the terminal.
     */
    private Context context;
    
    /**
     * A Queue containing all of the mouse events that have occurred since the
     * last update (processing event), which will be either dispatched to their
     * respective component or discarded if the target component isn't
     * {@code Interactable}.
     */
    private Queue<MouseEvent> mouseEvents;
    
    private boolean running = true;
    
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
        
        while (running) {
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
    
    public void stop() {
        running = false;
    }
    
    @Override
    public void run() {
        poll();
    }
}
