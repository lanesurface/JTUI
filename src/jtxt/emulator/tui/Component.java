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
    /**
     * Renders the component and any children within the bounds that this
     * component has set forth. This method will be called by the terminal
     * each time the window needs to update. 
     * 
     * @param graphics The graphics context to use for rendering to the screen.
     */
    void draw(java.awt.Graphics2D graphics);
    
    /**
     * Checks whether a point is within the region defined by this component.
     * 
     * @param point The point that may possibly be within the bounds of the
     *              component.
     * 
     * @return Whether or not the point is this components region.
     */
    // TODO: Location class is not needed, as Java API already specifies the
    // Point class for screen locations.
    boolean intersects(jtxt.emulator.Location location);
    
    /**
     * Determines whether the region is within the region of this component.
     * 
     * @param region The region to check against this shape's.
     * 
     * @return Whether or not all bounds of the given region lie within the
     *         region of this component.
     */
    boolean inside(jtxt.emulator.Region region);
    
    /**
     * Gets the region which this component controls. This region is the bounds
     * set forth for rendering, and which sub-components (if any) are contained
     * in.
     * 
     * @return The region that this component controls.
     */
    jtxt.emulator.Region getBounds();
    
    /**
     * Gets all children that this component contains.
     * 
     * @return The children of this component.
     * 
     * @throws UnsupportedOperationException if this component does not have
     *                                       the ability to contain children.
     */
    Component[] getChildren() throws UnsupportedOperationException;
    
    /**
     * Gets the child component at the specified index. The index determines
     * the place in the layout in which the child has been designated to 
     * render.
     * 
     * @param index ...
     * 
     * @return ...
     * 
     * @throws UnsupportedOperationException ...
     */
    Component getChild(int index) throws UnsupportedOperationException;
}
