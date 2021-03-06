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

import jtxt.GlyphBuffer;
import jtxt.emulator.GString;
import jtxt.emulator.Location;

public class TextBox extends Component {
  /**
   * The position within the bounds of this component that the text should be
   * justified in.
   */
  public enum Position
    { LEFT
    , CENTER
    , RIGHT };

  /**
   * The text to draw onto the screen.
   */
  private GString text;
  private Position just;

  /**
   * Creates a new {@code TextBox} containing the given string in the given position
   * (see {@link Position}). The string is interpreted before the component is drawn,
   * so the presence of escapes within the string will be applied to their respective
   * characters. (In other words, there is no need to convert an escaped string into
   * a {@link GString}.
   *
   * @param params The params for the layout that this component's parent
   *   container has defined. Note passing an incompatible parameter type into this
   *   argument may cause the layout to throw an exception when this component is
   *   added.
   * @param text The text which this component should contain.
   * @param just The position within this component in which the text
   *   should be placed.
   */
  public TextBox(
    Object params,
    String text,
    Position just)
  {
    this.parameters = params;
    this.text = GString.of(
      text,
      background);
    this.just = just;
  }

  @Override
  public void draw(GlyphBuffer buffer) {
    GString[] lines = text.wrap(width);
    for (int line = 0; line < lines.length; line++) {
      int spos, sline;
      spos = bounds.start.position;
      sline = bounds.start.line + (bounds.getHeight()-lines.length) / 2;

      switch (just) {
      case RIGHT:
        spos += bounds.getWidth() - lines[line].length();
        break;
      case CENTER:
        spos += (bounds.getWidth() - lines[line].length()) / 2;
        break;
      default:
        break;
      }

      buffer.update(lines[line], new Location(
        sline+line,
        spos));
    }
  }
}
