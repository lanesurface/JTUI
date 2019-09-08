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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package jtxt.emulator.tui;

import jtxt.emulator.GString;
import jtxt.emulator.Glyph;

import java.util.ArrayList;
import java.util.List;

/**
 * A dynamic area to which text may be added. By default, this area does not
 * support any facilities for editing this content, though it may be updated
 * programmatically at runtime. Therefore, it could be easily extended to add
 * this capability.
 *
 * <p>
 *   Exceeding the bounds of this text area is not an error in itself; that
 *   is to say that the content which is added to it is preserved and no
 *   exceptions are thrown. Do note, though, that the frame will be clipped
 *   to the bounds of the component, so if you desire to display this
 *   overflown content, you should wrap it in a scrollable element.
 * </p>
 */
public class TextArea extends AbstractElement {
  private List<GString> content;
  private int cl, cc;

  public TextArea(
    int width,
    int height)
  {
    super(
      width,
      height);

    content = new ArrayList<>(height);
  }

  public void insert(
    Glyph g,
    int r,
    int c)
  {
    content.set(r, content.get(r).set(
      c,
      g));
  }

  public void append(Glyph g) {
    content.get(cl).set(
      cc++,
      g);
  }

  public void append(GString s) {
    content.set(
      cl++,
      s);
  }

  @Override
  public void draw() {
    Frame f;
    int w, h;

    f = getFrame();
    w = getWidth();
    h = getHeight();
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        f.set(
          content.get(y).get(x),
          x,
          y);
  }
}
