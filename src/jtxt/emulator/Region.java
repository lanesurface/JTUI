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
    public Location start;
    
    /**
     * The lower-right bound for the region this text is contained in.
     */
    public Location end;
    
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
        /*
         * Verify that the lines and positions haven't been flipped. This is
         * important, as some other classes use these lines and positions as
         * array indices.
         */
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
     * Constructs a new region for the given lines and positions.
     * 
     * @param startLine The upper-left line of the region.
     * @param startPosition The upper-left position of the region.
     * @param endLine The lower-right line of the region.
     * @param endPosition The lower-right position of the region.
     */
    public Region(int startLine, 
                  int startPosition, 
                  int endLine, 
                  int endPosition) {
        this(new Location(startLine, startPosition), 
             new Location(endLine, endPosition));
    }
    
    /**
     * Determines whether this region is within the given region.
     * 
     * @param other The region that possibly encompasses this one.
     * 
     * @return Whether or not this region is within the other.
     */
    public boolean inside(Region other) {
        return start.line >= other.start.line
               && start.position >= other.start.position
               && end.line <= other.end.line
               && end.position <= other.end.position;
    }
    
    /**
     * Gets the location that represents the upper-left corner of this region.
     * 
     * @return The start location of this region.
     */
    public Location getStart() {
        return new Location(start);
    }
    
    /**
     * Gets the location that represents the lower-right corner of this region.
     * 
     * @return The end location of this region.
     */
    public Location getEnd() {
        return new Location(end);
    }
    
    /**
     * Calculates the width of this region, where the width is the difference
     * between the start position and end position.
     * 
     * @return The width of this region.
     */
    public int getWidth() {
        return end.position - start.position;
    }
    
    /**
     * Calculates the height of this region, where the height is the difference
     * between the start line and end line.
     * 
     * @return The height of this region.
     */
    public int getHeight() {
        return end.line - start.line;
    }
    
    @Override
    public String toString() {
        return String.format("Region: [%s,%s]", start, end);
    }
    
    /**
     * Creates a new {@code Region} from the given start position, determining
     * the end {@code Location} from the width and height.
     * 
     * @param start The upper-left location of this region.
     * @param width The width of this region.
     * @param height The height of this region.
     * 
     * @return A new region which starts at the given location, and which will
     *         have the width and height that are specified.
     */
    public static Region fromLocation(Location start, int width, int height) {
        return new Region(start.line,
                          start.position,
                          start.line + height,
                          start.line + width);
    }
}
