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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
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
    
    private int cellOffsetX,
                cellOffsetY;
    
    private WritableRaster[] glyphs;
    
    private ColorModel cm,
                       out;
    
    private int colorMask = 0x000000;
    
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
        out = new ComponentColorModel(cs,
                                      new int[] { 8, 8, 8, 8 },
                                      true,
                                      false,
                                      Transparency.BITMASK,
                                      DataBuffer.TYPE_BYTE);
        
        try {
            BufferedImage fontImage = ImageIO.read(fontPath.toFile());
            WritableRaster fontRaster = fontImage.getRaster();
            
            int cells = (int)Math.sqrt(numPoints);
            cellOffsetX = fontImage.getWidth() / cells - charWidth;
            cellOffsetY = fontImage.getHeight() / cells - charHeight;
            
            System.out.format("cellOffsetX=%d,%ncellOffsetY=%d,%n",
                              cellOffsetX,
                              cellOffsetY);
            
            for (int r = 0; r < cells; r++)
                for (int c = 0; c < cells; c++)
                    glyphs[r * cells + c] =
                        fontRaster.createWritableChild((charWidth + cellOffsetX) * c,
                                                       (charHeight + cellOffsetY) * r,
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
        
        return new BufferedImage(out,
                                 raster,
                                 out.isAlphaPremultiplied(),
                                 null);
    }
    
    protected WritableRaster transformGlyphToColor(WritableRaster raster,
                                                   Color color) {
        WritableRaster modified = 
            out.createCompatibleWritableRaster(raster.getWidth(),
                                               raster.getHeight());
        
        for (int y = 0; y < charHeight; y++) {
            for (int x = 0; x < charWidth; x++) {
                int pix = cm.getRGB(raster.getDataElements(x,
                                                            y,
                                                            null));
                boolean transparent = (pix << 8 | colorMask) == colorMask;
                pix &= color.getRGB();
                
                byte[] samples = new byte[out.getNumComponents()];
                cm.getDataElements(pix, samples);
                samples[samples.length - 1] = transparent
                                              ? (byte)0
                                              : (byte)255;
                
                modified.setDataElements(x,
                                         y,
                                         samples);
            }
        }
        
        
        return modified;
    }
}
