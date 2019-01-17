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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jtxt.GlyphBuffer;

public class SWRasterizer implements GlyphRasterizer {
    protected Context context;
    
    public SWRasterizer(Context context) {
        this.context = context;
    }
    
    @Override
    public BufferedImage rasterize(GlyphBuffer buffer) {
        Region bounds = buffer.getBounds();
        
        BufferedImage image = new BufferedImage(context.windowSize.width,
                                                context.windowSize.height,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        
        g.setFont(context.font);
        int ascent = g.getFontMetrics().getAscent(),
            numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();
        
        for (int line = 0; line < numLines; line++)
            g.drawString(buffer.getString(line).getData(0, lineSize),
                         0,
                         line * context.charSize.height + ascent);
        g.dispose();
        
        return image;
    }
}
