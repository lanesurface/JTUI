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

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComponent;

import jtxt.DrawableSurface;
import jtxt.GlyphBuffer;

public final class Renderer extends JComponent implements DrawableSurface {
    private static final long serialVersionUID = 1L;

    /**
     * This is a screenshot of the screen before we began rendering. When the
     * transparency of the window is less than <code>1.0f</code>, the pixels of
     * this image are combined with those of the terminal to give an illusion
     * of a transparent window.
     */
    private final BufferedImage screen;
    
    /**
     * The color that is painted behind the text in the terminal.
     */
    private Color background;
    
    /**
     * The transparency of this component. (A value less than <code>1.0f</code>
     * will cause the values of pixels behind the window to be combined with
     * those of the terminal, to give the illusion of transparency.)
     */
    private float transparency;
    
    /**
     * The {@code GlyphRasterizer} that is used to convert the frames passed to
     * this renderer into an image suitable for drawing to the screen.
     */
    private GlyphRasterizer rasterizer;
    
    /**
     * The frame that is being painted to the window, where the last call to
     * this render generated this image. This rasterized image may be painted
     * to the window as many times as necessary before the next frame is
     * requested.
     */
    private RenderedImage rasterizedFrame;
    
    private int charWidth,
                charHeight;
    
    Renderer(Color background,
             float transparency,
             GlyphRasterizer rasterizer,
             int charWidth,
             int charHeight) {
        BufferedImage scr = null;
        if (transparency != 1.0f) {
            try {
                Robot r = new Robot();
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension bounds = tk.getScreenSize();
                scr = r.createScreenCapture(new Rectangle(0,
                                                          0,
                                                          bounds.width,
                                                          bounds.height));
            }
            catch (AWTException awtex) { }
        }
        this.screen = scr;
        
        this.background = background;
        this.transparency = transparency;
        this.rasterizer = rasterizer;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
    }
    
    public static Renderer getInstance(Font font,
                                       int charWidth,
                                       int charHeight,
                                       Color background,
                                       float transparency) {
        GlyphRasterizer rasterizer = null;
        
        if (font == null) {
            try {
                Path path = Paths.get(
                    ClassLoader.getSystemResource("dejavu-sans-mono-256.bmp")
                               .toURI()
                );
                charWidth = 8;
                charHeight = 15;
                BitmapFont bfnt = new BitmapFont(path,
                                                 charWidth,
                                                 charHeight,
                                                 32,
                                                 256);
                rasterizer = new ChunkingRasterizer(bfnt,
                                                    16);
            }
            catch (URISyntaxException ex) { /* TODO */ }
        }
        else rasterizer = new SwingRasterizer(font);
        
        return new Renderer(background,
                            transparency,
                            rasterizer,
                            charWidth,
                            charHeight);
    }
    
    @Override
    public void draw(GlyphBuffer buffer) {
        Region bounds = buffer.getBounds();
        rasterizedFrame = rasterizer.rasterize(buffer,
                                               charWidth * bounds.getWidth(),
                                               charHeight * bounds.getHeight());
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Graphics2D graphics = (Graphics2D)g;
        int width = getWidth(),
            height = getHeight();
        
        Point location = getLocationOnScreen();
        int startX = location.x,
            startY = location.y,
            endX = startX + width,
            endY = startY + height;
        /*
         * Draw the screenshot within the bounds of this renderer. If this
         * component is opaque, the image won't be visible anyway.
         */
//        graphics.drawImage(screen,
//                           0,
//                           0,
//                           width,
//                           height,
//                           startX,
//                           startY,
//                           endX,
//                           endY,
//                           null);
//
//        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//                                                    transparency);
//        graphics.setComposite(comp);
        graphics.setColor(background);
        graphics.fillRect(0,
                          0,
                          width,
                          height);
        
        if (rasterizedFrame == null) return;
        graphics.drawRenderedImage(rasterizedFrame, null);
    }
    
    @Override
    public String toString() {
        return String.format("Renderer@" + hashCode() + "[rasterizer=%s, "
                             + "%n\tbg=%s, %n\ttransparency=%.1f]",
                             rasterizer,
                             background,
                             transparency);
    }
}
