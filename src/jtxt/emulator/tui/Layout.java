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

import jtxt.emulator.Region;

/**
 * Allocates the bounds of a {@code Container}'s children, using objects passed to
 * these children to determine the location in which they will be placed.
 */
public interface Layout {
  /**
   * Calculates the size of the {@code Region} that a {@code Component} may be placed
   * within, using the given parameters object to make this calculation. The type of
   * this object, as well as the way that it affects the determination of the bounds,
   * is implementation-dependent. This method may throw an error if the type of this
   * parameter is incorrect (because a client passed an incorrect parameter when
   * constructing a Component).
   *
   * @param parameters An object which describes the positioning information that
   *   will be used to calculate these bounds. This is implementation dependent, and
   *   the type of this object is likely to change between different {@code
   *   Layout}s.
   *
   * @return The bounds that the <code>parameters</code> object represents.
   */
  Region getBounds(Object parameters);

  /**
   * Sets the bounds of the parent container that this {@code Layout} manages.
   *
   * @param parentBounds The bounds of the Container that this Layout manages.
   */
  void setParentBounds(Region parentBounds);

  /**
   * Sets the bounds of a Component, using the parameter object that the component
   * has defined.
   *
   * @param child The Component to allocate bounds to.
   */
  default void setComponentBounds(Component child) {
    child.setBounds(
      getBounds(child.getLayoutParameters())
    );
  }
}
