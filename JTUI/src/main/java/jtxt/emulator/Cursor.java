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
package jtxt.emulator;

/**
 * Holds information about the current position to insert text into the {@code
 * Terminal}.
 */
public class Cursor {
  /**
   * The current location of the cursor.
   */
  private Location location;

  /**
   * Initializes a new {@code Cursor} with the line and position set to (0,&nbsp;0).
   */
  public Cursor() {
    this(
      0,
      0);
  }

  /**
   * Constructs a new {@code Cursor} at the given line and position.
   *
   * @param line The line number.
   * @param pos The position in the line.
   */
  public Cursor(
    int line,
    int pos)
  {
    this(new Location(
      line,
      pos));
  }

  /**
   * Constructs a {@code Cursor} at the given location.
   *
   * @param location The location to orient the cursor to.
   */
  public Cursor(Location location) {
    this.location = location;
  }

  /**
   * Updates the location of the cursor.
   *
   * @param newLine The line number.
   * @param newPos The position in the line.
   */
  public void setLocation(
    int newLine,
    int newPos)
  {
    location.setLocation(
      newLine,
      newPos);
  }

  /**
   * @return The current location of the cursor.
   */
  public Location getLocation() {
    return new Location(location);
  }

  /**
   * @return The current location's line.
   */
  public int getLine() {
    return location.line;
  }

  /**
   * @return The current location's position.
   */
  public int getPosition() {
    return location.position;
  }

  /**
   * Reduces the cursor's position in the current line.
   *
   * @param spaces The number of spaces to go back in the line.
   */
  public void goBack(int spaces) {
    location.position -= spaces;
  }

  /**
   * Advances the cursor forward in the current line.
   *
   * @param spaces The number of spaces to move forward.
   */
  public void goForward(int spaces) {
    location.position += spaces;
  }

  /**
   * Moves the cursor upward in the current position.
   *
   * @param spaces The number of spaces to move upward.
   */
  public void goUp(int spaces) {
    location.line -= spaces;
  }

  /**
   * Moves the cursor down in the current position.
   *
   * @param spaces The number of spaces to move downward.
   */
  public void goDown(int spaces) {
    location.line += spaces;
  }
}
