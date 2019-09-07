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

import jtxt.Message;
import jtxt.emulator.Glyph;

import java.awt.Color;
import java.util.List;

import static jtxt.emulator.tui.Primitives.*;

/**
 * A decorator to which an {@code Element} may be added. If the element
 * overflows the bounds of this composite, the scroll bar will allow a client
 * to adjust the visible portion thereof.
 */
public class ScrollableContainer extends AbstractElement {
  private int pos,
    cw,
    ch;
  private Element child;

  public ScrollableContainer(
    int width,
    int height,
    Element child)
  {
    super(
      width,
      height);

    this.child = child;
    cw = child.getWidth();
    ch = child.getHeight();
  }

  @Override
  public void update(
    Message message,
    double delta)
  {
    switch (message) {
    case CLICKED:
    case FOCUSED:
    case REDRAW:
      draw();
      break;
    default:
      throw new IllegalStateException("This element cannot respond to the "
                                      + "given message: " + message);
    }
  }

  @Override
  public void draw() {
    Frame f, cf;
    Glyph g;
    int w, h;

    g = new Glyph(
      '#',
      Color.WHITE);
    w = getWidth();
    h = getHeight();
    f = getFrame();
    line(
      f,
      w,
      0,
      w,
      h);
    f.set(
      g,
      w,
      pos);

    cf = child.getFrame().clip(
      0,
      0,
      w-1,
      h);
    f.blit(
      cf,
      0,
      0);
  }
}
