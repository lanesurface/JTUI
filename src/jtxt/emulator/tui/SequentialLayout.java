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

import jtxt.emulator.Location;
import jtxt.emulator.Region;

/**
 * A {@code Layout} which aligns components along an axis, and which wraps {@code
 * Component}s which overflow the bounds of their parent container onto the next
 * suitable {@code Location} within the container, such that no two components will
 * ever intersect.
 */
public class SequentialLayout implements Layout {
  /**
   * The axis that components will be aligned on. If this value is set to Axis.X,
   * components will be layed out horizontally before being wrapped to the next
   * available line; otherwise, they will be aligned vertically before being
   * wrapped.
   */
  private final Axis axis;

  /**
   * The bounds of the parent container that components requesting bounding
   * information are being added to.
   */
  private Region parentBounds;

  /**
   * The upper-right corner of the bounding box that is to be returned to the next
   * component that requests bounds within the container. The lower- left corner will
   * be generated when the width and height are known, and rely on the axis that we
   * are laying components out against.
   */
  private Location next;

  /**
   * The width or height (depending on the {@code Axis} that this layout is
   * initialized with) of the largest {@code Component} on this layout's major axis.
   */
  private int extent;

  public SequentialLayout(Axis axis) {
    this.axis = axis;
  }

  @Override
  public void setParentBounds(Region parentBounds) {
    this.parentBounds = parentBounds;
    next = parentBounds.getStart();
    extent = 0;
  }

  @Override
  public Region getBounds(Object params) {
    SequentialParameters sp = (SequentialParameters)params;
    Location start = new Location(next);

    switch (axis) {
    case X: {
        int room = parentBounds.getWidth() - next.position;

        /*
         * If we overflow the width of the container, wrap this
         * component onto the next available line.
         */
        if (sp.width > room)
          start.setLocation(
            next.line+extent,
            parentBounds.start.position);

        if (sp.height > extent)
          extent = sp.height;

        next.setLocation(
          start.line,
          start.position+sp.width);

        break;
    }
    case Y: {
        int room = parentBounds.getHeight() - next.line;

        /*
         * If we overflow the height of the container, wrap this
         * component at the next available position.
         */
        if (sp.height > room)
          start.setLocation(
            parentBounds.start.line,
            next.position+extent);

        if (sp.width > extent)
          extent = sp.width;

        next.setLocation(
          start.line+sp.height,
          start.position);

        break;
      }
    }

    return Region.fromLocation(
      start,
      sp.width,
      sp.height);
  }

  /**
   * The parameter object which describes the width and height of a component that is
   * added to this {@code Layout}'s container.
   */
  public static class SequentialParameters {
    protected int width, height;

    public SequentialParameters(
      int width,
      int height)
    {
      this.width = width;
      this.height = height;
    }
  }
}
