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
    
    public SequentialLayout(Container container, Axis axis) {
        this.axis = axis;
        parentBounds = container.getBounds();
        next = parentBounds.getStart();
    }
    
    @Override
    public Region getBounds(int width, int height) {
        int roomX = parentBounds.getWidth() - next.position,
            roomY = parentBounds.getHeight() - next.line;
        width = Math.min(roomX, width);
        height = Math.min(roomY, height);
        
        Location start = new Location(next);
        next.setLocation(axis == Axis.X
                         ? next.line
                         : next.line + height + 1,
                         axis == Axis.Y
                         ? next.position
                         : next.position + width + 1);
        
        return new Region(start.line,
                          start.position,
                          start.line + height,
                          start.position + width);
    }
}
