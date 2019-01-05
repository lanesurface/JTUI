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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.Region;

/**
 * A {@code Container} is a component which owns other components. Containers
 * handle the placement of their children (the components which they own)
 * inside the bounds that the container that owns themselves has allocated. A
 * container that does not have a parent is called a {@code RootContainer} and
 * lies at the top of the component hierarchy. Each container can decide which
 * {@code Layout} it would like to use whenever it's constructed, and
 * subcontainers don't necessarily have the same layout as their parents.
 * 
 * @see RootContainer
 */
public class Container implements Component,
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
     * TODO
     */
    protected Layout.Parameters parameters;
    
    /**
     * The bounds that this container occupies.
     */
    protected Region bounds;
    
    /**
     * Each container can define its own background color for painting onto
     * the screen.
     */
    protected Color background;
    
    public Container(Layout.Parameters parameters,
                     Layout layout,
                     Component... children) {
        this.parameters = parameters;
        this.layout = layout;
        Arrays.asList(children)
              .stream()
              .forEach(child -> add(child,
                                    child.getLayoutParameters()));
    }
    
    /**
     * Adds the component to this container, using the inflated properties
     * of that component to determine the bounds it may occupy within this
     * container.
     * 
     * @param child The component to add to this container.
     */
    public void add(Component child, Layout.Parameters parameters) {
        children.add(child);
        child.setBounds(layout.getBounds(parameters.getWidth(),
                                         parameters.getHeight()));
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
    
    @Override
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
    
    @Override
    public void draw(BufferedFrame frame) {
        for (Component child : children)
            child.draw(frame);
    }
    
    @Override
    public void setBounds(Region bounds) {
        this.bounds = bounds;
        layout.setParentBounds(bounds);
        /*
         * Now we need to make sure each of our children has been adjusted
         * for these new bounds.
         */
        for (Component child : children) {
            Layout.Parameters params = child.getLayoutParameters();
            child.setBounds(layout.getBounds(params.getWidth(),
                                             params.getHeight()));
        }
    }
    
    @Override
    public Layout.Parameters getLayoutParameters() {
        return parameters;
    }
}
