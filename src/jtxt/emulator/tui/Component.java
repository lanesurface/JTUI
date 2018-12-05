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

/**
 * <p>
 * The root of all elements that appear or are used for rendering information
 * within the terminal. Each component should be able to draw itself (including
 * any child components that make up a parent component) and each should handle 
 * all user-specified input directed toward themselves, whether that comes from
 * a keyboard event or mouse input. 
 * </p>
 * 
 * <p>
 * Parent components (or those which are capable of containing sub-components)
 * should delegate drawing to their children after appropriately rendering
 * themselves. Input which is received by a parent component should determine
 * which child the input was received by (if any) and let the child handle the
 * action. A parent should only concern itself with the arrangement of its 
 * children, and should not perform any task which could be reused in another
 * context.
 * </p>
 */
public interface Component {
    void draw();
    
    /**
     * Checks whether a point is within the region defined by this component.
     * 
     * @param point The point that may possibly be within the bounds of the
     *              component.
     * 
     * @return Whether or not the point is this components region.
     */
    boolean intersects(Location location);
    
    /**
     * Determines whether the region is within the region of this component.
     * 
     * @param region The region to check against this shape's.
     * 
     * @return Whether or not all bounds of the given region lie within the
     *         region of this component.
     */
    boolean inside(Region region);
    
    /**
     * Sets the dimensions of this component.
     * 
     * @param bounds
     */
    void setBounds(Region bounds);
    
    /**
     * Gets the region which this component controls. This region is the bounds
     * set forth for rendering, and which sub-components (if any) are contained
     * in.
     * 
     * @return The region that this component controls.
     */
    Region getBounds();
}
