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

import java.awt.Color;

import jtxt.emulator.Renderer;
import jtxt.emulator.Terminal;
import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Border;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.GridLayout.GridParameters;
import jtxt.emulator.tui.SequentialLayout.SequentialParameters;
import jtxt.emulator.tui.SequentialLayout;
import jtxt.emulator.tui.TextBox;

/**
 * @author Lane
 *
 */
public class TestColoredContainer {
    public static void main(String[] args) {
        Terminal terminal =
            new Terminal.Builder("Colored Container")
                        .rasterType(Renderer.RasterType.HARDWARE_ACCELERATED)
                        .font("Consolas")
                        .build();
        
        GridLayout layout = GridLayout.initializeForDimensions(1, 1);
        terminal.createRootContainer(layout);
        
        GridParameters parameters = layout.getParametersForCell(0, 0);
        Container<Component> container = new Container<>(parameters,
                                                         new SequentialLayout(Axis.X),
                                                         Color.CYAN);

//        terminal.add(new Border(Border.Type.DASHED,
//                                Color.LIGHT_GRAY,
//                                container));
        terminal.add(container);
        
        Component text = new TextBox(new SequentialParameters(20, 10),
                                     "Hello, O beautiful colored container!",
                                     TextBox.Position.CENTER);
        container.add(text);
    }
}
