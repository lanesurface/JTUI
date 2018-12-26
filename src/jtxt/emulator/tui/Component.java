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
import jtxt.emulator.Context;
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
     * The number of characters that this component will occupy within a
     * line in the terminal.
     */
    protected int width;
    
    /**
     * The number of lines that this component will occupy within the terminal.
     */
    protected int height;
    
    /**
     * The container that this component belongs to. Although only containers
     * will make requests of their respective components, we may use this
     * reference to ask that the container perform some action with us, such
     * as modifying our positioning within it.
     */
    protected Container parent;
    
    /**
     * A reference to the context which was constructed with this instance of
     * the terminal, and which contains important rendering properties.
     */
    protected Context context;
    
    /**
     * Renders the component and any children within the bounds of that this
     * component has been inflated to. 
     * 
     * @param frame The frame that that terminal will draw next. Updating 
     *              characters within this frame will cause them to appear
     *              after the next update to the screen.
     */
    public abstract void draw(BufferedFrame frame);
    
    /**
     * Sets the width and height of this component.
     * 
     * @param width The width of this component.
     * @param height The height of this component.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Sets the parent container of this component.
     * 
     * @param parent The parent container of this component.
     */
    void setParent(Container parent) {
        this.parent = parent;
        this.context = parent.context;
        
        /* 
         * Get the bounds that this component may occupy within parent. If the
         * parent doesn't have enough room to accommodate the requested width
         * and height, we will wrap this component in a scrollable interface.
         */
        bounds = parent.layout.getBounds(width, height);
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
