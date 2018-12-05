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

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import jtxt.emulator.Location;

/**
 * A container is a {@link Component} that owns other components. All
 * components that appear in the terminal must belong to a parent container,
 * which defines their layout within the terminal and keeps track of the
 * location of each. A container may contain other containers (as it's a
 * component itself). A container owned by another does not necessarily have
 * the same layout as its parent, thus making it easy to create complex
 * layouts.
 */
public abstract class Container implements Component, 
                                           Iterable<Component> {
    /**
     * A collection of all the children this container owns. Components owned
     * by this container inherit certain properties of it. This container may
     * also dictate the way that components added to it appear on the screen.
     */
    protected List<Component> children;
    
    /**
     * The layout that determines how the children of this container will be
     * placed within it.
     */
    protected Layout layout;
    
    /**
     * Each container defines a location that all sub-components are
     * placed relative to in the terminal. This needs to be added to a child's
     * location to determine its position in screen-space.
     */
    protected Location localOrigin;
    
    /**
     * Each container can define its own background color for painting onto
     * the screen.
     */
    protected Color backgroundColor;
    
    /**
     * If the color of text is undefined, the component's nearest ancestor's
     * color is used for rendering the text. The font color of a container is
     * inherited from its parent (and if there is not a parent, it's white by
     * default).
     */
    protected Color fontColor;
    
    public abstract void add(Component child);
    
    /**
     * Returns the components in this container in the order defined by the
     * layout that has been set. 
     * 
     * @return The components that this container owns in the order defined by
     *         this container's layout.
     */
    public abstract Component[] getChildren();
    
    /**
     * Gets an iterator for the components within this container, and returns
     * them according to the order they will appear in the terminal.
     */
    public abstract Iterator<Component> iterator();
}
