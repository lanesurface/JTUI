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
package jtxt.emulator;

import java.awt.image.RenderedImage;

import jtxt.GlyphBuffer;

interface GlyphRasterizer {
  /**
   * For the given frame of {@code Glyphs}, this method will rasterize the glyphs
   * onto an image, according to the properties defined in the {@code Context} which
   * was used to construct this instance.
   *
   * @param buffer The buffer of glyphs to rasterize to the image.
   * @param width The width of the image to create (in pixels).
   * @param height The height of the image to create.
   *
   * @return An image which contains the appropriately rasterized glyphs, and which
   *   is the same width and height set in the context the frame has been constructed
   *   with, so that this image can be rendered directly to the window.
   */
  RenderedImage rasterize(
    GlyphBuffer buffer,
    int width,
    int height);
}
