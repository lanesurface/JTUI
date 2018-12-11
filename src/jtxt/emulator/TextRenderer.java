package jtxt.emulator;

/*
 * Maybe individual TextRenderers should define which font they use for
 * rendering their glyphs?
 */
public interface TextRenderer {
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
    void update(Glyph[] glyphs, Region region);
    
    /**
     * Returns the glyph which occupies the given location.
     * 
     * @param location The location in this buffer where the glyph is stored.
     * 
     * @return The glyph in the given location.
     */
    Glyph getGlyph(Location location);
    
    /*
     *  Odd because this method returns the glyphs within a region as a 2-D
     *  array; however, updating the glyphs in a region requires a 1-D array.
     *  
     *  Returns an array of glyphs with the same bounds as the given region.
     */
    Glyph[][] getGlyphs(Region region);
}
