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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import jtxt.GlyphBuffer;

class SwingRasterizer implements GlyphRasterizer {
    protected Font font;
    
    public SwingRasterizer(Font font) {
        this.font = font;
    }
    
    @Override
    public RenderedImage rasterize(GlyphBuffer buffer,
                                   int width,
                                   int height) {
        Region bounds = buffer.getBounds();
        
        BufferedImage image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        
        int ascent = fm.getAscent(),
            numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();

        int charWidth = fm.getMaxAdvance(),
            charHeight = fm.getHeight() - fm.getLeading();
        
        for (int line = 0; line < numLines; line++) {
            for (int position = 0; position < lineSize; position++) {
                Glyph glyph = buffer.getGlyph(Location.at(bounds,
                                                          line,
                                                          position));
                int x = position * charWidth,
                    y = line * charHeight + ascent;
                
                g.setColor(glyph.background);
                g.fillRect(x,
                           line * charHeight,
                           x + charWidth,
                           line * charHeight * 2);
                
                g.setColor(glyph.color);
                g.drawString(glyph.character + "", x, y);
            }
        }
        
        g.dispose();
        
        return image;
    }
}
