/* 
 * Copyright 2019 Lane W. Surface
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
package jtxt;

import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.tui.*;

import java.util.Objects;

/**
 * 
 */
public abstract class Terminal implements ComponentObserver {
    protected int width,
                  height;

    /**
     * The root container that all components that appear in the terminal
     * belong to. Components that are not added to another container will be
     * direct ancestors of this container.
     *
     * @see #add(Component...)
     */
    protected RootContainer root;

    /**
     * The surface defines how {@code Glyph}s which are in the buffer are drawn
     * to the screen.
     */
    protected DrawableSurface surface;

    /**
     * The current {@code Component} receiving key events.
     *
     * @see #focus(KeyboardTarget)
     * @see #focus(int, int)
     */
    private KeyboardTarget focusedComponent;

    protected Terminal(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected Terminal() { }

    public void add(Component... components) {
        Objects.requireNonNull(root, "Cannot add Components to the terminal "
                                     + "before a RootContainer is created.");
        root.add(components);
    }

    /**
     * Constructs and returns a new {@code RootContainer} using this terminal
     * as the context, and the given layout as the super layout of all
     * {@code Component}s within the terminal.
     *
     * @param layout The layout to use for placing Components within this
     *               terminal.
     *
     * @return A new {@code RootContainer} which has been constructed for this
     *         terminal with the given layout.
     */
    public RootContainer createRootContainer(Layout layout) {
        root = new RootContainer(new Region(0,
                                            0,
                                            height,
                                            width),
                                 layout);
        root.registerObserver(this);
        
        return root;
    }
    
    public void focus(int line, int position) {
        Component component = getComponentAt(line, position);
        focusedComponent = component instanceof KeyboardTarget
                           ? (KeyboardTarget)component
                           : focusedComponent;
    }

    public void focus(KeyboardTarget target) {
        focusedComponent = target;
    }

    @Override
    public void update() {
        surface.draw(root.drawToBuffer());
        
        /*
         * TODO: I need to separate the updates coming from the EventDispatcher
         *       and updates which Components generate (which means that a new
         *       frame needs to be rasterized).
         */
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        root.resize(height, width);
        update();
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    protected void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        root.resize(width,
                    height);
    }
    
    protected Component getComponentAt(int line, int position) {
        return root.getComponentAt(Location.at(root.getBounds(),
                                               line,
                                               position));
    }
    
    /**
     * Initializes the area that text will be rendered to. This surface should
     * be capable of transforming a {@code GlyphBuffer} into a representation
     * suitable for output to the client machine.
     * 
     * @param width The number of characters wide the surface should be
     *              able to render.
     * @param height The number of character tall the surface should be
     *               able to render.
     * 
     * @return ...
     */
    protected abstract DrawableSurface createDrawableSurface(int width,
                                                             int height);
}
