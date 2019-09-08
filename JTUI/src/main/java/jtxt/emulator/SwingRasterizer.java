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

  SwingRasterizer(Font font) {
    this.font = font;
  }

  @Override
  public RenderedImage rasterize(
    GlyphBuffer buffer,
    int width,
    int height)
  {
    Region bounds = buffer.getBounds();
    BufferedImage image = new BufferedImage(
      width,
      height,
      BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();

    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_OFF);
    g.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

    g.setFont(font);
    FontMetrics fm = g.getFontMetrics();

    int ascent = fm.getAscent(),
      numLines = bounds.getHeight(),
      lineSize = bounds.getWidth();

    int charWidth = fm.getMaxAdvance(),
      charHeight = fm.getHeight() - fm.getLeading();

    for (int l = 0; l < numLines; l++) {
      for (int p = 0; p < lineSize; p++) {
        Glyph glyph = buffer.getGlyph(Location.at(
          bounds,
          l,
          p));
        int x, y;

        x = p * charWidth;
        y = l * charHeight + ascent;

        g.setColor(glyph.background);
        g.fillRect(
          x,
          l*charHeight,
          x+charWidth,
          l*charHeight*2);

        g.setColor(glyph.color);
        g.drawString(
          glyph.character+"",
          x,
          y);
      }
    }

    g.dispose();

    return image;
  }
}
