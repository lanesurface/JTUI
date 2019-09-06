/*
 * Copyright 2018 Lane W. Surface
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
package jtxt.emulator.tui;

import jtxt.emulator.Location;

/**
 * Indicates that a component wishes to be the target for receiving information about
 * key presses when they are focused in the terminal. Implementing this interface
 * does not mean that a component will receive all key presses, but rather that they
 * will be able to receive key presses when this input is directed toward them.
 *
 * @see jtxt.emulator.Terminal#focus(KeyboardTarget)
 * @see jtxt.emulator.Terminal#focusAt(Location)
 */
public interface KeyboardTarget {
  /**
   * This method should handle key presses directed toward a component that is the
   * target of keyboard events when the component is focused.
   *
   * @param event The keyboard event carrying information about the key press.
   */
  void keyPressed(char character);

  /**
   * Carries contextual information when a key is pressed. This information is
   * propagated to components listening for key presses, as well as the target
   * component itself.
   */
  class Event {
    /**
     * The location the cursor was at whenever the key was pressed. This position is
     * guaranteed to be within the target comonent's bounds.
     */
    public final Location position;

    /**
     * The key that was pressed when this event was generated.
     */
    public final Key key;

    /**
     * Whether the key that generated this event is a printable character. For
     * modifier characters (control, shift, alter, etc.) this will be false.
     */
    private boolean printable;

    /**
     * Creates a new key event with the given position and key.
     *
     * @param position The position the cursor was at when the event occured.
     * @param key The key that was pressed to generate this event.
     */
    public Event(
      Location position,
      Key key) {
      this.position = position;
      this.key = key;
    }
  }

  enum Key {
    BACKSPACE('\b'),
    ESCAPE('\u001B'),
    DELETE('\u007F');

    public final char character;

    Key(char character) {
      this.character = character;
    }

    public static Key forCharacter(char character) {
      /*
       * TODO: Somehow traverse the keys that are defined in this enum,
       * and return a key reference for the character if it exists.
       */

      return null;
    }
  }
}
