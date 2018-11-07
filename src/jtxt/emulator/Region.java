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
 * Defines a region within the terminal which can be passed to drawing methods
 * to give the bounds where text should be drawn. This allows for us to wrap 
 * text more easily in places where we wish for wrapping to be performed before
 * the edge of the terminal itself.
 */
public class Region { 
    /**
     * The upper-left bound for the region this text is contained in.
     */
    private Location start;
    
    /**
     * The lower-left bound for the region this text is contained in.
     */
    private Location end;
    
    /**
     * Constructs a {@code Region} with the given start and end 
     * {@code Location}s.
     * 
     * @param start The upper-right {@code Location} for the region.
     * @param end The lower-left {@code Location} for the region.
     * 
     * @throws IllegalArgumentException if the start position is greater than
     *                                  the end position or if the start line
     *                                  is greater than the end line.
     */
    public Region(Location start, Location end) {
        if (start.position > end.position)
            throw new IllegalArgumentException("The start position [pos=" + 
                                               start.position + "] must be " + 
                                               "less than the end position " + 
                                               "pos=[" + end.position + "].");
        
        if (start.line > end.line)
            throw new IllegalArgumentException("The start line [line=" + 
                                               start.line + "] is greater " +
                                               "than the end line [line=" + 
                                               end.line + "].");
        
        this.start = start;
        this.end = end;
    }
    
    /**
     * 
     * @param startLine
     * @param startPosition
     * @param endLine
     * @param endPosition
     */
    public Region(int startLine, 
                  int startPosition, 
                  int endLine, 
                  int endPosition) {
        this(new Location(startLine, startPosition), 
             new Location(endLine, endPosition));
    }
    
    public Location getStartLocation() {
        return new Location(start);
    }
    
    public Location getEndLocation() {
        return new Location(end);
    }
    
    public int getWidth() {
        return end.position - start.position;
    }
    
    public int getHeight() {
        return end.line - start.line;
    }
}
