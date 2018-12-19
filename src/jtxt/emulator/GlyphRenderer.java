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

/*
 * Maybe individual TextRenderers should define which font they use for
 * rendering their glyphs?
 */
public interface GlyphRenderer {
    /**
     * Update the character at the given location. This method may overwrite
     * the character that occupied this location beforehand.
     * 
     * @param glyph The glyph to update.
     * @param location The location in which this glyph is placed.
     */
    void update(Glyph glyph, Location location);
    
    /**
     * The renderer should update the Glyphs in the given region, such that
     * the region is defined as the bounding box for these Glyphs. Glyphs that
     * overflow the region should be handled appropriately. This method should
     * only throw an exception if the region is not within the bounds that this
     * renderer has defined.
     * 
     * @param glyphs The Glyphs to place within the given region.
     * @param region The bounding box for these Glyphs, such that all Glyphs
     *               are guaranteed to be within it. Glyphs may not take up the
     *               full region, but should never overflow it.
     */
    void update(GString glyphs, Region region);
    
    /**
     * Updates the region in the renderer bounded in the upper-right corner by
     * start, assuming that the renderer has allocated enough space to store
     * this array in it's internal buffer.
     * 
     * @param glyphs
     * @param start
     */
    void update(GString[] glyphs, Location start);
    
    /**
     * Returns the glyph which occupies the given location.
     * 
     * @param location The location in this buffer where the glyph is stored.
     * 
     * @return The glyph in the given location.
     */
    Glyph getGlyph(Location location);
    
    /**
     * Given a region, this method will return all glyphs within that region as
     * a two-dimensional array.
     * 
     * @param region The region in which the requested glyphs are contained in.
     * 
     * @return The glyphs in the given region.
     */
    GString[] getGlyphs(Region region);
}
