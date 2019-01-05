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

import jtxt.emulator.Context;
import jtxt.emulator.Region;
import jtxt.emulator.ResizeSubscriber;

public class RootContainer extends Container implements ResizeSubscriber {
    /**
     * Creates a new container which occupies the entire area which was given
     * when the context was created. The size of this container will match the
     * dimensions passed to terminal's builder.
     */
    public RootContainer(Context context,
                         Layout layout,
                         Component... children) {
        super(null, layout, children);
        bounds = new Region(0,
                            0,
                            context.getNumberOfLines(),
                            context.getLineSize());
    }

    @Override
    public void resize(int lines, int lineSize) {
        /*
         * The context notified us that the window has been resized, so the
         * bounds of this container need to be adjusted.
         */
        setBounds(new Region(0,
                             0,
                             lines,
                             lineSize));
    }
}
