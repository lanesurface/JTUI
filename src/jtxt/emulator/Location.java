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
    
    public Location(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    public Location(Location loc) {
        this(loc.line, loc.position);
    }
    
    public void setLocation(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    /**
     * Adds the line and position of this location to the specified location.
     * 
     * @param other The location to add to this one.
     * 
     * @return A new location that is the sum of these two locations.
     */
    public Location add(Location other) {
        return new Location(line + other.line, position + other.position);
    }
    
    /**
     * Determines whether the specified location is within valid bounds in the
     * terminal.
     * 
     * @param context The context for the terminal.
     * @param location The location to check.
     * 
     * @return Whether this location is within valid bounds in the terminal.
     */
    public boolean outside(Context context) {
        return line < 0 || line >= context.numLines ||
               position < 0 || position >= context.lineSize;
    }
    
    @Override
    public String toString() {
        return "Location: [line=" + line + "," + " position=" + position + "]";
    }
}
