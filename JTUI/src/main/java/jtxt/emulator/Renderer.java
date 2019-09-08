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

import jtxt.DrawableSurface;
import jtxt.GlyphBuffer;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

@SuppressWarnings("Serial")
public final class Renderer extends JComponent implements DrawableSurface {
  private final BufferedImage screen;
  private Color bg;
  private float trans;
  private GlyphRasterizer rasterizer;
  private RenderedImage rasterizedFrame;
  private int cw, ch;

  private Renderer(
    Color bg,
    float trans,
    GlyphRasterizer rasterizer,
    int cw,
    int ch)
  {
    BufferedImage scr = null;
    if (trans <= 1.0f) {
      try {
        Robot r = new Robot();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension bounds = tk.getScreenSize();
        scr = r.createScreenCapture(new Rectangle(
          0,
          0,
          bounds.width,
          bounds.height));
      } catch (AWTException awtex) { }
    }

    screen = scr;
    this.bg = bg;
    this.trans = trans;
    this.rasterizer = rasterizer;
    this.cw = cw;
    this.ch = ch;
  }

  static Renderer getInstance(
    Font font,
    int charWidth,
    int charHeight,
    Color background,
    float transparency)
  {
    GlyphRasterizer rasterizer = new SwingRasterizer(font);

    return new Renderer(
      background,
      transparency,
      rasterizer,
      charWidth,
      charHeight);
  }

  public static Renderer getInstance(
    BitmapFont font,
    int charWidth,
    int charHeight,
    Color background,
    float transparency)
  {
    GlyphRasterizer rasterizer = new ChunkingRasterizer(
      font,
      16);

    return new Renderer(
      background,
      transparency,
      rasterizer,
      charWidth,
      charHeight);
  }

  @Override
  public void draw(GlyphBuffer buffer) {
    Region bounds = buffer.getBounds();
    rasterizedFrame = rasterizer.rasterize(
      buffer,
      cw*bounds.getWidth(),
      ch*bounds.getHeight());
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    Graphics2D graphics = (Graphics2D)g;
    int width = getWidth(),
      height = getHeight();

    int sx,
      sy,
      ex,
      ey;
    Point location = getLocationOnScreen();

    sx = location.x;
    sy = location.y;
    ex = sx + width;
    ey = sy + height;
    /*
     * Draw the screenshot within the bounds of this renderer. If this
     * component is opaque, the image won't be visible anyway.
     */
    graphics.drawImage(
      screen,
      0,
      0,
      width,
      height,
      sx,
      sy,
      ex,
      ey,
      null);

    Composite comp = AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER,
      trans);
    graphics.setComposite(comp);
    graphics.setColor(bg);
    graphics.fillRect(
      0,
      0,
      width,
      height);

    if (rasterizedFrame == null) return;
    graphics.drawRenderedImage(
      rasterizedFrame,
      null);
  }

  @Override
  public String toString() {
    return String.format(
      "Renderer[rasterizer=%s, %n\tbg=%s, %n\ttrans=%.1f]",
      rasterizer,
      bg,
      trans);
  }
}
