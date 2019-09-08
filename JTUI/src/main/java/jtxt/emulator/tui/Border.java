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
package jtxt.emulator.tui;

import java.awt.Color;
import java.util.Arrays;

import jtxt.GlyphBuffer;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class Border extends Decorator {
  /**
   * The type of border to draw. Each type defines the characters that it will use
   * when being rendered.
   */
  public enum Type {
    DASHED(
      '-',
      '|',
      '+'),
    DOTTED('.'),
    CROSS('+');

    private final char span,
      edge,
      corner;

    Type(char c) {
      /*
       * If only one c is specified for this border, use it for
       * drawing the span and edges.
       */
      this(
        c,
        c,
        c);
    }

    Type(
      char span,
      char edge,
      char corner)
    {
      this.span = span;
      this.edge = edge;
      this.corner = corner;
    }
  }

  private Type type;

  /**
   * Initializes a border for the given component, using the character defined by the
   * type and the color for drawing the border.
   *
   * @param component The component to draw this border around.
   * @param type The type of character to use for drawing the border.
   * @param color The color of the border.
   */
  public Border(
    Type type,
    Color color,
    Component component)
  {
    super(component);
    this.type = type;
    this.fg = color;

    setBackground(component.bg);
  }

  @Override
  public void setBounds(Region bounds) {
    super.setBounds(bounds);

    component.setBounds(new Region(
      bounds.start.line+1,
      bounds.start.position+1,
      bounds.end.line-1,
      bounds.end.position-1));
  }

  @Override
  public void draw(GlyphBuffer buffer) {
    super.draw(buffer);

    Glyph[] glyphs = new Glyph[getWidth()];
    Arrays.fill(glyphs, new Glyph(
      type.span,
      fg,
      bg));
    glyphs[0] = glyphs[getWidth() - 1] = new Glyph(
      type.corner,
      fg,
      bg);
    GString border = new GString(glyphs);

    for (int l = bounds.start.line; l < bounds.end.line; l++) {
      /*
       * Only fill the line when this is the top or bottom edge; otherwise, add
       * the border at the left and rightmost positions.
       */
      if (l == bounds.start.line || l == bounds.end.line - 1) {
        for (int p = bounds.start.position; p < bounds.end.position; p++) {
          buffer.update(border, Location.at(
            bounds,
            l,
            bounds.start.position));
        }

        continue;
      }

      Glyph edge = new Glyph(
        type.edge,
        fg,
        bg);
      Location s, e;

      s = Location.at(
        bounds,
        l,
        bounds.start.position);
      e = Location.at(
        bounds,
        l,
        bounds.end.position-1);
      buffer.update(
        edge,
        s,
        e);
    }
  }
}
