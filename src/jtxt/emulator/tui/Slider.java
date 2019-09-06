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
package jtxt.emulator.tui;

import jtxt.GlyphBuffer;
import jtxt.emulator.Location;

/**
 * TODO
 */
public class Slider extends Component implements Interactable {
  private final int res,
    min,
    max;

  protected int value;

  public Slider(
    Object params,
    int res,
    int min,
    int max)
  {
    this.params = params;
    this.res = res;
    this.min = min;
    this.max = max;
  }

  public void moveTo(int value) {
    this.value = value;
    update();
  }

  @Override
  public boolean clicked(Location clickLocation) {
    int range, pos;

    range = (max-min);
    pos = range * ((bounds.getWidth() - clickLocation.position) / 100);
    moveTo(pos);

    // Don't capture focus of the keyboard.
    return false;
  }

  @Override
  public void draw(GlyphBuffer buffer) { /* ... */ }
}
