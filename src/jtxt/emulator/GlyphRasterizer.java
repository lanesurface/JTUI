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
