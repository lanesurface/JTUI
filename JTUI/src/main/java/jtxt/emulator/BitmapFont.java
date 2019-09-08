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
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

/**
 * A font which has been rasterized and saved in an appropriate image format. This
 * kind of font does not need to be rasterized but, rather, only needs to serve a
 * matching glyph from the image when requested to do so.
 *
 * @see #getCharacterAsImage(Glyph)
 */
class BitmapFont {
  private int minCode,
    maxCode,
    width,
    height,
    xOff,
    yOff;

  /**
   * The raw glyph values within this font. (Each of these rasters must be processed
   * before it can be appropriately rendered.)
   */
  private WritableRaster[] glyphs;
  private ColorModel inCM, outCM;

  /**
   * The color that will be filtered out of the bitmap. All pixels of this color will
   * be turned transparent.
   */
  private static final int COLOR_MASK = 0x000000;

  BitmapFont(
    Path fontPath,
    int width,
    int height,
    int minCode,
    int np)
  {
    this.width = width;
    this.height = height;
    this.minCode = minCode;
    maxCode = minCode + np;
    glyphs = new WritableRaster[np];

    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    int[] nbits = { 8, 8, 8 };
    inCM = new ComponentColorModel(
      cs,
      nbits,
      false,
      false,
      Transparency.OPAQUE,
      DataBuffer.TYPE_BYTE);
    outCM = new ComponentColorModel(
      cs,
      new int[] { 8, 8, 8, 8 },
      true,
      false,
      Transparency.BITMASK,
      DataBuffer.TYPE_BYTE);

    try {
      BufferedImage fontImage = ImageIO.read(fontPath.toFile());
      WritableRaster fontRaster = fontImage.getRaster();

      int cells = (int)Math.sqrt(np);
      xOff = fontImage.getWidth() / cells - width;
      yOff = fontImage.getHeight() / cells - height;

      for (int r = 0; r < cells; r++) {
        for (int c = 0; c < cells; c++) {
          int x, y;

          x = (width + xOff) * c;
          y = (height + yOff) * r;
          glyphs[r * cells + c] = fontRaster.createWritableChild(
            x,
            y,
            width,
            height,
            0,
            0,
            null);
        }
      }
    } catch (IOException ie) { } /* FIXME */
  }

  Image getCharacterAsImage(Glyph glyph) {
    char character = glyph.character;

    if (character == '\0'
        || character < minCode
        || character > maxCode)
      return null;

    WritableRaster raster = changeColor(
      glyphs[character-minCode],
      glyph.color);

    return new BufferedImage(
      outCM,
      raster,
      outCM.isAlphaPremultiplied(),
      null);
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  /**
   * For the glyph defined by the given Raster, the visible areas of the letter form
   * will be transformed to the specified color, and all pixels of the same color as
   * <code>colorMask</code> (not accounting for the alpha component) will be filtered
   * out of the Raster returned from this method.
   *
   * @param r The pixel data for the glyph to modify.
   * @param c The color that the glyph returned from this method should be
   *   transformed to.
   *
   * @return A Raster containing the same pixel data as the input Raster, with the
   *   mask c filtered out and all other pixels being the c which was given.
   */
  private WritableRaster changeColor(
    WritableRaster r,
    Color c)
  {
    WritableRaster colored = outCM.createCompatibleWritableRaster(
        r.getWidth(),
        r.getHeight());

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pix = inCM.getRGB(r.getDataElements(
          x,
          y,
          null));
        boolean transparent = (pix << 8 | COLOR_MASK) == COLOR_MASK;
        pix &= c.getRGB();

        byte[] samples = new byte[outCM.getNumComponents()];
        inCM.getDataElements(
          pix,
          samples);
        samples[samples.length-1] = transparent
          ? (byte)0
          : (byte)255;

        colored.setDataElements(
          x,
          y,
          samples);
      }
    }


    return colored;
  }
}
