/* 
 * Copyright 2019 Lane W. Surface
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class EventDispatcher extends MouseAdapter implements Runnable {
    /**
     * The instance of the terminal that this dispatcher is listening to. Keep
     * a reference here, as we may need to notify it whenever some relavent
     * state changes.
     */
    protected final EmulatedTerminal terminal;
    
    /**
     * The instance of the {@code Renderer} that the terminal constructed. 
     * May need to be notified whenever the window is resized, or the screen
     * needs to be refreshed. (The amount of times that this Renderer will be
     * notified depends on the number of updates per second that the client
     * has requested.)
     */
    private final Renderer renderer;
    
    /**
     * A Queue containing all of the mouse events that have occurred since the
     * last update (processing event), which will be either dispatched to their
     * respective component or discarded if the target component isn't
     * {@code Interactable}.
     */
    private Queue<MouseEvent> mouseEvents;
    
    private boolean running = true;

    // The number of times that events will be dispatched every second.
    private static final int UPDATES_PER_SECOND = 60;
    
    public EventDispatcher(EmulatedTerminal terminal,
                           Renderer renderer) {
        this.terminal = terminal;
        this.renderer = renderer;
        mouseEvents = new LinkedList<>();
    }
    
    /**
     * Polls for changes that can occur frequently and at random times (such
     * as changes to window dimensions, and input events from the keyboard and
     * mouse).
     * 
     * <p><strong>
     *  Whenever adding additional events which must be checked before
     *  updating the terminal, override this method&mdash;this class will call
     *  it every time that the terminal needs to update (determined by the
     *  ups given by the client).
     * </strong></p>
     *
     * @param delta The amount of time that has passed (in seconds) since this
     *              method was last called.
     */
    protected void poll(double delta) {
        int width = renderer.getWidth(),
            height = renderer.getHeight(),
            numLines = height / terminal.charHeight,
            lineSize = width / terminal.charWidth;

        /*
         * FIXME: This process is extremely slow; we shouldn't have to redraw
         *        the components as much as we do. There needs to be a better
         *        way to check if the window has been resized.
         */
        if ((numLines != terminal.getHeight()
            || lineSize != terminal.getWidth())
            && delta <= 0.05) terminal.resize(lineSize, numLines);

        dispatchMouseEvents();
        renderer.repaint();
    }
    
    @Override
    public synchronized void mouseClicked(MouseEvent event) {
        mouseEvents.add(event);
    }
    
    /**
     * Removes all mouse events from the event queue, and notifies the
     * respective {@code Component}s that should receive the events if they are
     * {@code Interactable} (meaning they can receive and respond to mouse
     * events). The order in which these events are dispatched is the same as
     * the order in which they originally occurred.
     *
     * <p>
     * Additionally, this method may change the actively focused Component
     * within the terminal if any of the Components which received a mouse
     * event and were Interactable requested to receive key events.
     * </p>
     */
    protected synchronized void dispatchMouseEvents() {
        while (!mouseEvents.isEmpty()) {
            MouseEvent event = mouseEvents.remove();
            int x = event.getX(),
                y = event.getY();

            terminal.generateClickForComponentAt(y / terminal.charHeight,
                                                 x / terminal.charWidth);
        }
    }

    @Override
    public void run() {
        long msPerUpdate = 1000 / UPDATES_PER_SECOND,
             current = 0,
             last;
       
       while (running) {
           last = current;
           current = System.currentTimeMillis();
           poll((current - last) / 1000.0);
           
           try {
               long sleepTime = current
                                + msPerUpdate
                                - System.currentTimeMillis();
               if (sleepTime > 0) Thread.sleep(sleepTime);
           }
           catch (InterruptedException ie) { return; }
       }
    }
}
