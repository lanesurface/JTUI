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
 * A place where text can be inserted within the buffer.
 */
public class Location {
    /**
     * The line that this location represents in the terminal.
     */
    public int line;
    
    /**
     * The position within the line that this location represents within the
     * terminal.
     */
    public int position;
    
    /**
     * Creates a new location with the given line and position.
     * 
     * @param line The line of this location.
     * @param position The position within the line of this location.
     */
    public Location(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    /**
     * Creates a new location with the same line and position as the given
     * location.
     * 
     * @param loc The location to use for constructing this location.
     */
    public Location(Location loc) {
        this(loc.line, loc.position);
    }
    
    /**
     * Sets a new line and position for this location.
     * 
     * @param line The new line of this location.
     * @param position The new position of this location.
     */
    public void setLocation(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    public void add(int lines, int characters) {
        line += lines;
        position += characters;
    }
    
    /**
     * Adds the line and position of this location to the specified location.
     * 
     * @param other The location to add to this one.
     */
    public void add(Location other) {
        add(other.line, other.position);
    }
    
    /**
     * Increases the line of this {@code Location} by the given amount.
     * 
     * @param amount The number of lines to advance downward.
     */
    public void advanceDown(int amount) {
        add(amount, 0);
    }
    
    /**
     * Increases the position of this {@code Location} by the given amount.
     * 
     * @param amount The number of spaces to advance forward.
     */
    public void advanceForward(int amount) {
        add(0, amount);
    }
    
    /**
     * Determines whether this location is within the given region.
     * 
     * @param bounds The region that this location should be within.
     * 
     * @return True if this location is within the bounds, and false if it
     *         lies outside of it.
     */
    public boolean inside(Region bounds) {
        return line >= bounds.start.line
               && position >= bounds.start.position
               && line < bounds.end.line
               && position < bounds.end.position;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Location) {
            Location location = (Location)other;
            return line == location.line && position == location.position;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Location: [line=%d, position=%d]",
                             line,
                             position);
    }
}
