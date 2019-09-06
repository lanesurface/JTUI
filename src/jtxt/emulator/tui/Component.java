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
import java.util.ArrayList;
import java.util.List;

import jtxt.GlyphBuffer;
import jtxt.emulator.Glyph;
import jtxt.emulator.Region;

/**
 * The root of all TUI components. A {@code Component} may draw itself within the
 * bounds that it has been allocated by a {@code Layout}, and may be added to a
 * special kind of Component, which is called a {@code Container}. Components should
 * be lightweight and able to draw themselves fairly quickly, as it may be asked to
 * do so many times per second. A Component does not necessarily belong to a
 * terminal, and may be rendered to any object which implements the {@code
 * GlyphBuffer} interface. This could be a document, PDF, the terminal emulator, or
 * any number of extensions.
 *
 * @see Container
 * @see jtxt.Document
 * @see Layout
 * @see jtxt.GlyphBuffer
 */
public abstract class Component {
  protected Region bounds;
  protected Object params;
  private int width, height;
  protected List<ComponentObserver> observers;
  protected Color fg, bg;

  protected Component() {
    this(
      0,
      0,
      Color.BLACK,
      Glyph.TRANSPARENT);
  }

  protected Component(
    int width,
    int height,
    Color fg,
    Color bg)
  {
    this.fg = fg;
    this.bg = bg;
    observers = new ArrayList<>();
  }

  /**
   * Gets the bounds that this component has been allocated within its parent
   * container.
   *
   * @return The bounds that this component occupies within its container.
   */
  public Region getBounds() {
    return bounds;
  }

  /**
   * Sets the bounds of this component to the given {@code Region}.
   *
   * @param bounds The new bounds that this component may use to render itself
   *   within.
   */
  public void setBounds(Region bounds) {
    this.bounds = bounds;
    setWidth(bounds.getWidth());
    setHeight(bounds.getHeight());
  }

  public void setBackground(
    int red,
    int green,
    int blue,
    int alpha)
  {
    setBackground(new Color(
      red,
      green,
      blue,
      alpha));
  }

  public void setBackground(Color background) {
    this.bg = background;
  }

  /**
   * Gets the params that define how this component should be placed within its
   * parent container.
   *
   * @return The params which define how this component should be placed within
   *   its parent.
   */
  public Object getLayoutParameters() {
    return params;
  }

  public void registerObserver(ComponentObserver observer) {
    observers.add(observer);
  }

  protected void update() {
    for (ComponentObserver co : observers)
      co.update();
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }
}
