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

import jtxt.emulator.Region;

/**
 * An area on the screen which is able to display a {@code GlyphBuffer}. This
 * area is independent of any specific buffer which is present, but may have
 * a fixed width and height that the buffer may be drawn within.
 *
 * @see GlyphBuffer
 */
public interface DrawableSurface {
    /**
     * Draws the given {@code GlyphBuffer} onto this surface, using the bounds
     * defined by this buffer to determine how much of the buffer should be
     * drawn.
     *
     * @param buffer The buffer to draw onto this surface.
     */
    void draw(GlyphBuffer buffer);

    /**
     * Draws the {@code Region} defined by a start location of (y,&nbsp;x) and
     * end location (height,&nbsp;width) of the given {@code GlyphBuffer}.
     *
     * @param buffer The buffer to draw to this surface.
     * @param x The position in the buffer for the first Glyph which should be
     *          drawn.
     * @param y The line in the buffer for the first Glyph which should be
     *          drawn.
     * @param width The width of the area to draw to this surface.
     * @param height The height of the area to draw to this surface.
     */
    default void draw(GlyphBuffer buffer,
                      int x,
                      int y,
                      int width,
                      int height) {
        draw(buffer.createClippedBuffer(new Region(y,
                                                   x,
                                                   height,
                                                   width)));
    }
}
