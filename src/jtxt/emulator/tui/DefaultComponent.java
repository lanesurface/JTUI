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

import jtxt.emulator.Region;

/**
 * A basic implementation of the {@code Component} interface, which provides
 * functionality that is common to most components that will be used in the
 * text-user interface.
 * 
 * @see Decorator
 */
public abstract class DefaultComponent implements Component {
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
    
    @Override
    public Region getBounds() {
        return bounds;
    }
    
    @Override
    public void setBounds(Region bounds) {
        this.bounds = bounds;
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    @Override
    public Object getLayoutParameters() {
        return parameters;
    }
}
