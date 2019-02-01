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
package test;

import jtxt.GlyphBuffer;
import jtxt.emulator.tui.Component;

/**
 * @author Lane
 *
 */
public class VertexRenderer extends Component {
    void render(int[] vertices) { /* ... */ }
    
    int[] generateCube(int x,
                       int y,
                       int width,
                       int height) { /* ... */ return null; }
    
    @Override
    public void draw(GlyphBuffer buffer) { }
}