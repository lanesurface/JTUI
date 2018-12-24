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

import jtxt.emulator.BufferedFrame;
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
public abstract class Component {
    /**
     * The region within the terminal that this component has control over.
     * (This is used when determining which component should receive input
     * events, as well as the bounds in which this component can draw itself.)
     */
    protected Region bounds;
    
    /**
     * The container that this component belongs to. Although only containers
     * will make requests of their respective components, we may use this
     * reference to ask that the container perform some action with us, such
     * as modifying our positioning within it.
     */
    protected Container parent;
    
    /**
     * Renders the component and any children within the bounds of that this
     * component has been inflated to. 
     * 
     * @param frame The frame that is up to be rendered to the terminal next;
     *              adding characters within the region owned by this component
     *              guarantees that the characters will be rendered at the
     *              appropriate time.
     */
    public abstract void draw(BufferedFrame frame);
    
    /**
     * Resizes this component to the given width and height, provided that the
     * parent container has enough room within its bounds to accommodate this
     * change; if not, the component will be inflated to the maximum size
     * available within the container's layout.
     * 
     * @param width The new width for this component.
     * @param height The new height for this component.
     */
    public void inflate(int width, int height) {
        if (parent == null)
            throw new IllegalStateException("Component must be the child of " +
                                            "a container before inflating.");
        
        bounds = parent.layout.getBounds(width,
                                         height);
    }
    
    /**
     * Sets the parent container of this component.
     * 
     * @param parent The parent container of this component.
     */
    void setParent(Container parent) {
        this.parent = parent;
    }
    
    /**
     * Gets the region which this component controls. This region is the bounds
     * set forth for rendering, and which sub-components (if any) are contained
     * in.
     * 
     * @return The region that this component controls.
     */
    public Region getBounds() {
        return bounds;
    }
}
