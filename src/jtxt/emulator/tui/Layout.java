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

import jtxt.emulator.Region;

/**
 * Allocates the bounds that a component will occupy within its parent 
 * container, using the width and height of a component to determine the
 * respective space that it may occupy. If these space requirements cannot be
 * met (because there is not enough space within the parent), the Layout will
 * return the largest amount of space that can be allocated within the parent.
 */
public interface Layout {
    /**
     * Calculates the size of the region that the given component can fit in
     * within the parent container. As this method will often be called by
     * a component trying to resize itself, it may not know the location in
     * which it was requested to be placed within the container; therefore,
     * this method serves two purposes:
     * <OL>
     * <LI>
     *     Determine the region within the parent container that the requesting
     *     component will be placed, based on the positioning argument and the
     *     location of other components within that container.
     * </LI>
     * <LI>
     *     Calculate the maximum size less than or equal to the given width and
     *     height that the component will fit into.
     * </LI>
     * Overflowing this bounding information can lead to parts of the component
     * being discarded when added to the frame. A component should always make
     * sure that it passes in only the pre-calculated bounding information when
     * it updates and only passes in more characters than can fit within these
     * bounds if it intends for them to be discarded.
     * 
     * @param width The requested width of the component.
     * @param height The requested height for the component.
     * @param parameter An optional argument to use when determining where the
     *                  bounds should be allocated within the parent.
     * 
     * @return The bounds within the container that the given component can 
     *         occupy.
     */
    Region getBounds(int width, int height);
}
