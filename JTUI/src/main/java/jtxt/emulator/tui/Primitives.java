/*
 * Copyright 2019 Lane W. Surface
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package jtxt.emulator.tui;

import jtxt.emulator.Glyph;

import static java.lang.Math.abs;

/**
 * Implementations for various utility methods which draw shapes (using
 * characters for strokes) to the given {@code Frame}.
 */
public final class Primitives {
  private static final Glyph dg = new Glyph(
    '.',
    255,
    255,
    255);

  public static void rect(
    Frame frame,
    int x,
    int y,
    int w,
    int h)
  {

  }

  public static void circle(
    Frame frame,
    int xc,
    int yc,
    int r)
  {
    int x,
      y,
      d;

    x = 0;
    y = r;
    d = 3 - 2 * r;
    while (y >= x) {
      plot(
        frame,
        xc,
        yc,
        x,
        y);
      x++;

      if (d > 0) {
        y--;
        d = d + 4 * (x-y) + 10;
      }
      else {
        d = d + 4*x + 6;
      }
    }
  }

  private static void plot(
    Frame frame,
    int xc,
    int yc,
    int x,
    int y)
  {
    frame.set(dg, xc+x, yc+y);
    frame.set(dg, xc-x, yc+y);
    frame.set(dg, xc+x, yc-y);
    frame.set(dg, xc-x, yc-y);
    frame.set(dg, xc+y, yc+x);
    frame.set(dg, xc-y, yc+x);
    frame.set(dg, xc+y, yc-x);
    frame.set(dg, xc-y, yc-x);
  }

  public static void line(
    Frame frame,
    int sx,
    int sy,
    int ex,
    int ey)
  {
    int steps,
      dx,
      dy;
    double xi,
      yi,
      x,
      y;
    char ch;

    dx = ex - sx;
    dy = ey - sy;
    steps = Math.max(
      abs(dx),
      abs(dy));
    xi = dx / (steps+0.d);
    yi = dy / (steps+0.d);
    x = y = 0;
    for (int i = 0; i < steps; i++) {
      x += xi;
      y += yi;
      frame.set(
        dg,
        (int)x,
        (int)y);
    }
  }
}
