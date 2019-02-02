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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import jtxt.GlyphBuffer;

/**
 * 
 */
public class ChunkingRasterizer implements GlyphRasterizer {
    private Context context;
    
    private BitmapFont font;
    
    public ChunkingRasterizer(Context context,
                              BitmapFont font,
                              int numChunks) {
        this.context = context;
        this.font = font;
    }

    @Override
    public RenderedImage rasterize(GlyphBuffer buffer,
                                   int width,
                                   int height) {
        // TODO: Do the chunking. ;)
        BufferedImage image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)image.getGraphics();
        
        Region bounds = buffer.getBounds();
        int numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();
        
        for (int line = 0; line < numLines; line++) {
            for (int position = 0; position < lineSize; position++) {
                Glyph glyph = buffer.getGlyph(Location.at(line,
                                                          position));
                
                int x = position * context.charSize.width,
                    y = line * context.charSize.height;
                g.drawImage(font.getCharacterAsImage(glyph.character),
                            x,
                            y,
                            null);
            }
        }
        
        return image;
    }
}
