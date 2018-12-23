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
package jtxt.emulator;

public interface GlyphRasterizer {
    /**
     * Converts the given glyph into a series of pixels, where each pixel is
     * represented by a single array index in the output. This method should
     * aim to comply with the sRGB color space, using all 32 bits available
     * to store the color data. Each color of a given pixel should therefore
     * occupy a single byte in the integer, where the highest byte is the alpha
     * component and subsequent bytes are red, green, and blue.
     *  
     * @param glyph The glyph to rasterize.
     * @param size The point-size of the glyph.
     */
    int[][] rasterize(Glyph glyph, int size);
    
    /**
     * Sets the font that should be used to rasterize glyphs.
     * 
     * @param font The font to use to rasterize glyphs.
     */
    void setFont(java.awt.Font font);
}
