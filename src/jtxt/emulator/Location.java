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
     * Determines whether this location is within the bounds of the context.
     * 
     * @param context The context to verify this location against.
     * 
     * @return False if this location is outside valid bounds within the
     *         context.
     */
    public boolean outside(Context context) {
        return line < 0 
               || position < 0 
               || line >= context.numLines 
               || position >= context.lineSize;
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
        return "Location: [line=" + line + "," + " position=" + position + "]";
    }
}
