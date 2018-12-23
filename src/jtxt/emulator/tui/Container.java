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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.Region;

/**
 * A container is a {@link Component} that owns other components. All
 * components that appear in the terminal must belong to a parent container,
 * which defines their layout within the terminal and keeps track of the
 * location of each. A container may contain other containers (as it's a
 * component itself). A container owned by another does not necessarily have
 * the same layout as its parent, thus making it easy to create complex
 * layouts.
 */
public class Container extends Component 
                       implements Iterable<Component> {
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
     * Each container can define its own background color for painting onto
     * the screen.
     */
    protected Color background;
    
    /**
     * Creates a new container which occupies the given region.
     * 
     * @param bounds The region that this container occupy.
     */
    public Container(Region bounds) {
        this.bounds = bounds;
        children = new ArrayList<>();
    }
    
    /**
     * Adds the component to this container, using the inflated properties
     * of that component to determine the bounds it may occupy within this
     * container.
     * 
     * @param child The component to add to this container.
     */
    public void add(Component child) {
        children.add(child);
        child.setParent(this);
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        for (Component child : children)
            child.draw(frame);
    }
    
    /**
     * Returns the components in this container in the order defined by the
     * layout that has been set. 
     * 
     * @return The components that this container owns in the order defined by
     *         this container's layout.
     */
    public Component[] getChildren() {
        return children.toArray(new Component[0]);
    }
    
    /**
     * Sets the layout for this container. The layout is used to determine
     * how components placed within this container will be oriented within
     * the terminal. 
     * 
     * @param layout The layout to use for orienting components within this
     *               container.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
    
    /**
     * Gets an iterator for the components within this container, and returns
     * them according to the order they will appear in the terminal.
     */
    public Iterator<Component> iterator() {
        return new ContainerIterator(this);
    }
    
    private static class ContainerIterator implements Iterator<Component> {
        /**
         * All of the children that are owned by the root container, including
         * any sub-containers that root may contain.
         */
        private final Component[] children;
        
        /**
         * The index of the component in the array that is to be returned next.
         */
        private int index;
        
        /**
         * Construct an iterator for the container, where that container is
         * the parent of all components returned by this iterator.
         * 
         * @param root The container to iterate over.
         */
        public ContainerIterator(Container root) {
            this.children = root.getChildren();
        }
        
        @Override
        public boolean hasNext() {
            return index < children.length;
        }

        @Override
        public Component next() {
            Component current = children[index++];
            if (current instanceof Container) {
                Container container = (Container)current;
                
                for (Component component : container)
                    return component;
            }
            
            return current;
        }
    }
}
