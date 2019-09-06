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

/**
 * A {@code Frame} is an area which an {@code Element} may render itself upon,
 * without regard to the actual bounds which have been allocated for the
 * element in the composite which owns it. It also allows for easy clip
 * operations so that a composite may selectively choose the portion of the
 * frame which should be rendered to the screen.
 */
public interface Frame {
  void set(
    Glyph g,
    int x,
    int y);
  Frame clip(
    int x,
    int y,
    int w,
    int h);
  Frame blit(
    Frame other,
    int x,
    int y);
  int getWidth();
  int getHeight();
}
