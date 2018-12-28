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
import jtxt.emulator.Region;

public class SequentialLayout implements Layout {
    public static enum Axis { X, Y }
    
    /**
     * The axis that components will be aligned on. If this value is set to
     * Axis.X, components will be layed out horizontally before being wrapped
     * to the next available line; otherwise, they will be aligned vertically
     * before being wrapped.
     */
    private final Axis axis;
    
    /**
     * The bounds of the parent container that components requesting bounding
     * information are being added to.
     */
    private final Region parentBounds;
    
    /**
     * The upper-right corner of the bounding box that is to be returned to
     * the next component that requests bounds within the container. The lower-
     * left corner will be generated when the width and height are known, and
     * rely on the axis that we are laying components out against.
     */
    private Location next;
    
    /**
     * The number of lines that the largest component on the current line is
     * occupying. As components should never intersect one another, there may
     * be some empty space when components of differing dimensions are added to
     * the same line.
     */
    private int extent;
    
    public SequentialLayout(Container container, Axis axis) {
        this.axis = axis;
        parentBounds = container.bounds;
        next = parentBounds.getStart();
    }
    
    @Override
    public Region getBounds(int width, int height) {
        Location start = new Location(next);
        
        switch (axis) {
        case X:
            {
                int room = parentBounds.getWidth() - next.position;
                
                if (height > extent) extent = height;
                
                /*
                 * If we overflow the width of the container, wrap this
                 * component onto the next available line.
                 */
                if (width > room)
                    start.setLocation(next.line + extent,
                                      parentBounds.start.position);
                
                next.setLocation(next.line,
                                 next.position + width);
                
                break;
            }
        case Y:
            {
                int room = parentBounds.getHeight() - next.line;
                
                if (width > extent) extent = width;
                
                /*
                 * If we overflow the height of the container, wrap this
                 * component at the next available position.
                 */
                if (height > room)
                    start.setLocation(parentBounds.start.line,
                                      next.position + extent);
                
                next.setLocation(next.line + height,
                                 next.position);
                
                break;
            }
        }
        
        return new Region(start.line,
                          start.position,
                          start.line + height,
                          start.position + width);
    }
}
