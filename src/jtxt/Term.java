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

import java.util.Objects;

import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.ComponentObserver;
import jtxt.emulator.tui.KeyboardTarget;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

/**
 * 
 */
public abstract class Term implements ComponentObserver {
    protected int width,
                  height;
    
    protected RootContainer root;
    
    protected DrawableSurface surface;
    
    private KeyboardTarget focusedComponent;
    
    protected Term(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void add(Component... components) {
        Objects.requireNonNull(root, "Cannot add Components to the terminal "
                                     + "before a RootContainer is created.");
        root.add(components);
    }
    
    public RootContainer createRootContainer(Layout layout) {
        root = new RootContainer(new Region(0, 0, width, height),
                                 layout);
        root.registerObserver(this);
        
        return root;
    }
    
    public void focus(int line, int position) {
        Component component = getComponentAt(line,
                                             position);
        KeyboardTarget focusedComponent = component instanceof KeyboardTarget
                                          ? (KeyboardTarget)component
                                          : this.focusedComponent;
        focus(focusedComponent);
    }
    
    public void focus(KeyboardTarget target) {
        focusedComponent = target;
    }
    
    public void update() {
        Region bounds = root.getBounds();
        surface.draw(root.drawToBuffer(bounds.getWidth(),
                                       bounds.getHeight()));
        
        /*
         * TODO: I need to separate the updates coming from the EventDispatcher
         *       and updates which Components generate (which means that a new
         *       frame needs to be rasterized).
         */
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
