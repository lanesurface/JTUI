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
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Handles events which are propagated by a user, and which must be polled for over
 * the lifetime of the program. This class serves as the foundation for asynchronous
 * notifications within the terminal, and the bridge between Swing events and
 * terminal user-interface Components.
 */
public class EventDispatcher extends MouseAdapter implements Runnable {
  /**
   * The instance of the terminal that this dispatcher is listening to. Keep a
   * reference here, as we may need to notify it whenever some relavent state
   * changes.
   */
  private final EmulatedTerminal terminal;

  /**
   * The instance of the {@code Renderer} that the terminal constructed. May need to
   * be notified whenever the window is resized, or the screen needs to be refreshed.
   * (The amount of times that this Renderer will be notified depends on the number
   * of updates per second that the client has requested.)
   */
  private final Renderer renderer;

  /**
   * A Queue containing all of the mouse events that have occurred since the last
   * update (processing event), which will be either dispatched to their respective
   * component or discarded if the target component isn't {@code Interactable}.
   */
  private Deque<MouseEvent> events;

  private boolean running = true;

  private static final int UPS = 60;

  public EventDispatcher(
    EmulatedTerminal terminal,
    Renderer renderer)
  {
    this.terminal = terminal;
    this.renderer = renderer;
    events = new ArrayDeque<>();
  }

  private void poll(double delta) {
    int width = renderer.getWidth(),
      height = renderer.getHeight(),
      numLines = height / terminal.getCharHeight(),
      lineSize = width / terminal.getCharWidth();

    /*
     * FIXME: This process is extremely slow; we shouldn't have to redraw
     *        the components as much as we do. There needs to be a better
     *        way to check if the window has been resized.
     */
    if ((numLines != terminal.getHeight()
        || lineSize != terminal.getWidth())
        && delta <= 0.05)
    {
      terminal.resize(
        lineSize,
        numLines);
    }

    dispatchMouseEvents();
    renderer.repaint();
  }

  /**
   * Removes all mouse events from the event queue, and notifies the respective
   * {@code Component}s that should receive the events if they are {@code
   * Interactable} (meaning they can receive and respond to mouse events). The order
   * in which these events are dispatched is the same as the order in which they
   * originally occurred.
   *
   * <p>
   * Additionally, this method may change the actively focused Component within the
   * terminal if any of the Components which received a mouse event and were
   * Interactable requested to receive key events.
   * </p>
   */
  private synchronized void dispatchMouseEvents() {
    while (!events.isEmpty()) {
      MouseEvent event;
      int x, y;

      event = events.poll();
      x = event.getX();
      y = event.getY();

      terminal.generateClickForComponentAt(
        y / terminal.getCharHeight(),
        x / terminal.getCharWidth());
    }
  }

  @Override
  public synchronized void mouseClicked(MouseEvent event) {
    events.offer(event);
  }

  @Override
  public void run() {
    long now,
      last,
      start;
    int msPerUpdate;

    msPerUpdate = 1000 / UPS;
    now = 0;
    while (running) {
      last = now;
      now = System.currentTimeMillis();
      poll((now - last) / 1000.0);

      try {
        start = now + msPerUpdate - System.currentTimeMillis();
        if (start > 0)
          Thread.sleep(start);
      } catch (InterruptedException ie) { return; }
    }
  }
}
