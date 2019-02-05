/* 
 * Copyright 2018, 2019 Lane W. Surface 
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

import java.util.ArrayList;
import java.util.List;

import jtxt.GlyphBuffer;
import jtxt.emulator.Region;

/**
 * The root of all TUI components. A {@code Component} may draw itself within
 * the bounds that it has been allocated by a {@code Layout}, and may be added
 * to a special kind of Component, which is called a {@code Container}.
 * Components should be lightweight and able to draw themselves fairly quickly,
 * as it may be asked to do so many times per second. A Component does not
 * necessarily belong to a terminal, and may be rendered to any object which
 * implements the {@code GlyphBuffer} interface. This could be a document, PDF,
 * the terminal emulator, or any number of extensions.
 * 
 * @see jtxt.emulator.Terminal
 * @see Container
 * @see jtxt.Document
 * @see Layout
 * @see jtxt.GlyphBuffer
 */
public abstract class Component {
    /**
     * The bounds that this {@code Component} is allowed to draw itself within.
     */
    protected Region bounds;
    
    /**
     * The parameters that are passed to the layout that this
     * {@code Component}'s parent container has been initialized with. Do note
     * that an incorrect parameter type (or a type that isn't a parameter) will
     * cause an exception to be thrown at runtime.
     */
    protected Object parameters;
    
    protected int width,
                  height;
    
    protected List<ComponentObserver> observers;
    
    protected Component() {
        observers = new ArrayList<>();
    }
    
    /**
     * Renders the component within the bounds of that this component has been
     * inflated to. A component which updates Glyphs within the bounds that
     * have been allocated by the Layout can be sure that the characters will
     * be updated appropriately; the behavior for a component which does not is
     * undefined.
     * 
     * @param buffer An object on which a component can draw itself, and which
     *               makes certain guarantees about conforming to the bounds
     *               which are allocated by a {@code Container}'s layout.
     */
    public abstract void draw(GlyphBuffer buffer);
    
    /**
     * Gets the bounds that this component has been allocated within its parent
     * container.
     * 
     * @return The bounds that this component occupies within its container.
     */
    public Region getBounds() {
        return bounds;
    }
    
    /**
     * Sets the bounds of this component to the given {@code Region}.
     * 
     * @param bounds The new bounds that this component may use to render
     *               itself within.
     */
    public void setBounds(Region bounds) {
        this.bounds = bounds;
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    /**
     * Gets the parameters that define how this component should be placed
     * within its parent container.
     * 
     * @return The parameters which define how this component should be placed
     *         within its parent.
     */
    public Object getLayoutParameters() {
        return parameters;
    }
    
    public void registerObserver(ComponentObserver observer) {
        observers.add(observer);
    }
    
    protected void update() {
        for (ComponentObserver co : observers)
            co.update();
    }
}
