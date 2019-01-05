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
package jtxt.emulator.tui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;

public class ASCIImage extends Component {
    /**
     * The characters that can be used for converting the image into ASCII
     * characters. These are stored in descending order of intensity.
     */
    private static final char[] ASCII_CHARS = { '$', '@', 'B', '%', '8', '&',
                                                'W', 'M', '#', '*', 'o', 'a',
                                                'h', 'k', 'b', 'd', 'p', 'q',
                                                'w', 'm', 'Z', 'O', '0', 'Q',
                                                'L', 'C', 'J', 'U', 'Y', 'X',
                                                'z', 'c', 'v', 'u', 'n', 'x',
                                                'r', 'j', 'f', 't', '/', '\\',
                                                '|', '(', ')', '1', '{', '}',
                                                '[', ']', '?', '-', '_', '+',
                                                '~', '<', '>', 'i', '!', 'l',
                                                'I', ';', ':', ',', '\"', '^',
                                                '`', '\'', '.', ' ' };
    
    /**
     * The source image that this ASCII image is made from. We keep a reference
     * to it here so that we can resize the ASCII image later if we ever need 
     * to. (This is also incredibly expensive to load.)
     */
    private final BufferedImage source;
    
    /**
     * The array of strings which make up this image. Each string in the array
     * corresponds to a row of characters in the ASCII image.
     */
    private GString[] cached;
    
    /**
     * Create a new ASCII image from given source image.
     * 
     * @param source The image to use for creating this ASCII image.
     */
    public ASCIImage(BufferedImage source) {
        Objects.requireNonNull(source, "The source image must be loaded " + 
                                       "before conversion.");
        this.source = source;
    }
    
    /**
     * Resize the source image to the width and height given by the
     * {@link #setSize(int, int)} method. Each of the pixels in the returned
     * image represent a single glyph.
     * 
     * @return An appropriately scaled image for the size of this component.
     */
    private BufferedImage resize() {
        Image scaled = source.getScaledInstance(width,
                                                height,
                                                Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawImage(scaled, 0, 0, null);
        
        return image;
    }
    
    private void mapToGlyphs() {
        BufferedImage image = resize();

        double range = 255.0 / ASCII_CHARS.length;
        cached = new GString[height];

        for (int y = 0; y < height; y++) {
            GString line = GString.of("");
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y),
                    lum = ((rgb >> 16 & 0xFF) +
                           (rgb >> 8 & 0xFF) + 
                           (rgb & 0xFF)) / 3;
                
                int index = (int)Math.min(Math.round(lum / range), 
                                          ASCII_CHARS.length - 1);
                char out = ASCII_CHARS[ASCII_CHARS.length - index - 1];
                line = line.append(new Glyph(out, new Color(rgb)));
            }
            
            cached[y] = line;
        }
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        if (cached == null
            || cached.length != height
            || cached[0].length() != width) mapToGlyphs();
        
        for (int line = 0; line < height; line++)
            frame.update(cached[line], new Location(bounds.start.line + line,
                                                    bounds.start.position));
    }
}
