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
package jtxt.emulator;

import java.io.Serializable;
import java.util.ArrayList;

import jtxt.GlyphBuffer;

/**
 * Similar to a video frame, this is a frame of {@code Glyph}s that represents
 * the entirety of characters that are available to be fetched and painted to
 * the screen by the renderer at a specific point in time. TUI components are
 * passed an instance of this class, and may paint themselves within the bounds
 * that their parent container's layout has allocated to them. After all
 * components in the terminal have finished painting, the Glyphs belonging to
 * this frame are rasterized and rendered on the screen.
 * 
 * TODO: Make this class serializable so that it may be passed over a network
 *       connection, making way for an RFB implementation.
 * 
 * @see Terminal#update()
 * @see Renderer#renderFrame(BufferedFrame,
 *                           int,
 *                           int)
 */
@SuppressWarnings("serial")
public class BufferedFrame extends GlyphBuffer implements Serializable {
    /**
     * Creates a new frame.
     * 
     * @param bounds The region that should be allocated within this frame for
     *               {@code Glyph}s to be rendered in. (That is to say, Glyphs 
     *               may be rendered anywhere within the given bounds.)
     */
    public BufferedFrame(Region bounds) {
        super(bounds);
    }
}
