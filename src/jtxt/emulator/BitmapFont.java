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
package jtxt.emulator;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

/**
 * 
 */
public class BitmapFont {
    public final int minCodePoint,
                     maxCodePoint;
    
    private Path fontPath;
    
    protected int charWidth,
                  charHeight;
    
    private WritableRaster[] glyphs;
    
    private ColorModel cm;
    
    private Color maskColor = Color.WHITE;
    
    protected BitmapFont(Path fontPath,
                         int charWidth,
                         int charHeight,
                         int minCodePoint,
                         int numPoints) {
        this.fontPath = fontPath;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.minCodePoint = minCodePoint;
        maxCodePoint = minCodePoint + numPoints;
        glyphs = new WritableRaster[numPoints];
        
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] nbits = { 8, 8, 8 };
        cm = new ComponentColorModel(cs,
                                     nbits,
                                     false, /* hasAlpha */
                                     false, /* isAlphaPremultiplied */
                                     Transparency.OPAQUE,
                                     DataBuffer.TYPE_BYTE);
        
        try {
            BufferedImage fontImage = ImageIO.read(fontPath.toFile());
            WritableRaster fontRaster = fontImage.getRaster();
            
            int cols = fontRaster.getWidth() / charWidth,
                rows = fontRaster.getHeight() / charHeight;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    glyphs[r * rows + c] =
                        fontRaster.createWritableChild(c * charWidth,
                                                       r * charHeight,
                                                       charWidth,
                                                       charHeight,
                                                       0,
                                                       0,
                                                       null);
        }
        catch (IOException ie) { /* TODO */ }
    }
    
    public Image getCharacterAsImage(Glyph glyph) {
        char character = Character.toTitleCase(glyph.character);
        
        if (character == '\0') return null;
        if (character < minCodePoint || character > maxCodePoint) {
            String msg = String.format("The given character %c is outside the "
                                       + "valid range of characters, starting "
                                       + "at the codepoint %d and ending at "
                                       + "the codepoint %d.%n",
                                       character,
                                       minCodePoint,
                                       maxCodePoint);
            throw new IllegalArgumentException(msg);
        }
        
        WritableRaster raster =
            transformGlyphToColor(glyphs[character - minCodePoint],
                                  glyph.color);
        
        return new BufferedImage(cm,
                                 raster,
                                 cm.isAlphaPremultiplied(),
                                 null);
    }
    
    protected WritableRaster transformGlyphToColor(WritableRaster raster,
                                                   Color color) {
        Rectangle dims = new Rectangle(raster.getMinX(),
                                       raster.getMinY(),
                                       32,
                                       32);
        WritableRaster modified = raster.createCompatibleWritableRaster(dims);
        
        for (int y = dims.y; y < dims.height; y++) {
            for (int x = dims.x; x < dims.width; x++) {
                int pix = cm.getRGB(raster.getDataElements(x,
                                                           y,
                                                           null));
                pix &= color.getRGB();
                
                byte[] samples = new byte[cm.getNumComponents()];
                cm.getDataElements(pix, samples);
                modified.setDataElements(x,
                                         y,
                                         samples);
            }
        }
        
        
        return modified;
    }
}
