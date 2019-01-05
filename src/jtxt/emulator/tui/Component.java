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
public interface Component {
    /**
     * Renders the component within the bounds of that this component has been
     * inflated to.
     * 
     * @param frame The frame that that terminal will draw next. Updating 
     *              characters within this frame will cause them to appear
     *              after the next update to the screen.
     */
    void draw(BufferedFrame frame);
    
    /**
     * Sets the bounds of this component to the given {@code Region}.
     * 
     * @param region The new bounds that this component may use to render
     *               itself within.
     */
    void setBounds(Region region);
    
    /**
     * Gets the parameters that define how this component should be placed
     * within its parent container.
     * 
     * @return The parameters which define how this component should be placed
     *         within its parent.
     */
    Layout.Parameters getLayoutParameters();
}
