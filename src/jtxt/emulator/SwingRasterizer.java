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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import jtxt.GlyphBuffer;

class SwingRasterizer implements GlyphRasterizer {
    protected Context context;
    
    public SwingRasterizer(Context context) {
        this.context = context;
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
                           RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        
        g.setFont(context.font);
        int ascent = g.getFontMetrics().getAscent(),
            numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();
        
        // Set a color here while I work out how to preserve color information.
        Paint paint = new GradientPaint(0,
                                        0,
                                        Color.CYAN,
                                        0,
                                        50,
                                        Color.PINK,
                                        true);
        g.setPaint(paint);
        
        for (int line = 0; line < numLines; line++)
            g.drawString(buffer.getString(line).getData(0, lineSize),
                         0,
                         line * context.charSize.height + ascent);
        g.dispose();
        
        return image;
    }
}
