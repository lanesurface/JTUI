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

import jtxt.emulator.Context;
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
    protected Context context;
    
    protected RootContainer root;
    
    private KeyboardTarget focusedComponent;
    
    public void add(Component... components) {
        if (root == null)
            throw new IllegalStateException("Cannot add components to the "
                                            + "terminal before a RootContainer "
                                            + "is created.");
        
        root.add(components);
    }
    
    public RootContainer createRootContainer(Layout layout) {
        root = new RootContainer(context.getBounds(),
                                 layout);
        root.registerObserver(this);
        
        return root;
    }
    
    public void focus(Location location) {
        Component component = getComponentAt(location.line,
                                             location.position);
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
        int width = bounds.getWidth(),
            height = bounds.getHeight();
        
        /*
         * TODO: Have a way to only update the part of the screen that changed.
         *       (Would we have to draw straight to the screen, and pass the
         *       buffer stage somehow?) This method would also need to take the
         *       bounds of the region that needs to be updated (or the
         *       component?)
         */
        
        /*
         * `DrawableSurface`s are components which are able to render a
         * GlyphBuffer. An instance of this class will determine which kind of
         * renderer it will use when it implements this method. For the terminal
         * emulator, it will want to return a renderer that can be added to a
         * swing window (aka a JComponent), but for a terminal which outputs to
         * the native console, it should return a renderer which encodes its
         * output in ANSI escapes/whatever.
         */
        
        /*
         * I also need to separate the updates coming from the EventDispatcher
         * and updates which Components gernerate (which means that a new
         * frame needs to be rasterized).
         */
        DrawableSurface surface = createDrawableSurface(width, height);
        surface.draw(root.drawToBuffer(width, height));
    }
    
    protected Component getComponentAt(int line, int position) {
        return root.getComponentAt(Location.at(root.getBounds(),
                                               line,
                                               position));
    }
    
    protected abstract DrawableSurface createDrawableSurface(int width,
                                                             int height);
}
