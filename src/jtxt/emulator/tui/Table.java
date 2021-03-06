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
import jtxt.emulator.Region;
import jtxt.emulator.tui.GridLayout.GridParameters;

import java.util.Arrays;

/**
 * A {@code Container} which organizes its children into rows and cols. This
 * Container can be used to easily align a collection of Components on both of their
 * axes. (Components which have the same row or column number will be aligned along
 * an axis.)
 *
 * @see Container
 * @see GridLayout
 */
public class Table extends Container<Table.Column> {
  /**
   * The Layout used for this table. Since tables are very closely related to the
   * {@code GridLayout}, we use much of the functionality already written for this
   * layout manager when drawing the Components of this Container.
   */
  private GridLayout grid;
  private int rows, cols;

  /**
   * Creates a new {@code Table} which organizes it's children into a grid of rows
   * and cols. A Table is similar to the {@code GridLayout}, but allows for
   * insertion of rows and cols at runtime, and which makes it more convenient to
   * group related children in a structured fashion.
   *
   * @param parameters The layout parameters for this Container.
   * @param rows The initial number of rows in this Table.
   * @param columns The initial number of cols in this Table.
   */
  public Table(
    Object parameters,
    int rows,
    int columns)
  {
    super(parameters, GridLayout.initializeForDimensions(
      rows,
      columns));
    grid = (GridLayout)layout;
    this.rows = rows;
    this.cols = columns;

    Column[] cols = new Column[columns];
    for (int c = 0; c < columns; c++)
      cols[c] = createColumn(c);

    children = Arrays.asList(cols);
  }

  /**
   * Creates a new Column which can be inserted into the Table for the given column
   * number, using the number of rows to determine the number of components it can
   * hold.
   *
   * @param columnNumber The position within the Table that this column will
   *   occupy.
   *
   * @return A new Column for the given <code>columnNumber</code>.
   */
  private Column createColumn(int columnNumber) {
    GridParameters params = grid.getParametersForCellsInRange(
      0,
      columnNumber,
      rows-1,
      columnNumber);

    return new Column(
      params,
      rows);
  }

  /**
   * Adds the given Components to this Table, where the row and column number
   * determine where the first Component will appear, and all subsequent Components
   * will follow the first in the column. If the number of Components passed into
   * this method are greater than the number of rows allocated within that column, an
   * exception will be thrown.
   *
   * @param rn The row number of the first Component to add.
   * @param cn The column number of the first Component to add.
   * @param components The Components to add to this Table.
   *
   * @see Table#insertIntoColumn(int, int, Component...)
   */
  public void add(
    int rn,
    int cn,
    Component... components)
  {
    if (cn >= cols || rn + components.length >= rows)
      throw new IllegalArgumentException("The given indices are out " +
                                         "of bounds");

    children.get(cn).add(
      rn,
      components);
  }

  /**
   * Inserts the given Components into this table at the specified row and column,
   * shifting any elements within that column downward to accommodate these new
   * elements.
   *
   * @param rowNumber The row that the Components should be inserted before.
   * @param columnNumber The column that the Components should be inserted in.
   * @param components The Components to insert before the row and column.
   */
  public void insertIntoColumn(
    int rowNumber,
    int columnNumber,
    Component... components)
  {
    rows += components.length;
    for (int column = 0; column < cols; column++)
      children.get(column).shiftRows(
        rowNumber,
        components.length);

    children.get(rowNumber).add(
      rowNumber,
      components);
  }

  public void insertColumn(
    int columnNumber,
    Component... components)
  {
    Column col = createColumn(columnNumber);
    col.add(
      0,
      components);
    children.add(
      columnNumber,
      col);
    cols += 1;
  }

  /**
   * A column of components within the Container. As the Table has been set up to
   * only be able to hold children of this type, Components which are added to the
   * Table must be added to a Column in the correct position (determined by the row
   * number given when that Component is added). Each Component within a Column will
   * have the same column number.
   */
  static class Column extends Component {
    protected Component[] components;

    private int size,
      rheight;

    private Column(
      Object parameters,
      int size)
    {
      this.size = size;
      this.parameters = parameters;
      components = new Component[size];
    }

    void add(
      int start,
      Component... components)
    {
      for (int r = start; r < start + components.length; r++)
        this.components[r] = components[r-start];
    }

    /**
     * Shifts all of the rows in this Column, starting at <code>start</code> and
     * continuing to the end of this Column, to an index shifted by the
     * <code>amount</code> specified. (Therefore, the new index of a Component
     * will be at the position <code>oldIndex + amount</code>.)
     *
     * <p><i>
     * Do note that shifting the rows causes this Column's size to be increased
     * by the amount. Calls to this method should usually reside in a loop,
     * where each of the rows within a Table will be shifted appropriately.
     * </i></p>
     *
     * @param start The first row which should be shifted.
     * @param amount The amount of indices that each row should be shifted by.
     */
    void shiftRows(
      int start,
      int amount)
    {
      resize(size + amount);

      for (int r = size-1; r >= amount && r >= start; r--)
        components[r] = components[r - amount];
    }

    /**
     * Adjusts the number of rows within this Column, growing or shrinking as
     * necessary. If the given size is less than the current size of this Column, the
     * Components which fall outside the new range of the Column will be discarded.
     * If the size indicates that the Column needs to grow, the new rows within the
     * Column will contain
     * <code>null</code> elements.
     *
     * @param size The number of rows that this Column should contain after
     *   resizing occurs.
     */
    void resize(int size) {
      Component[] components = new Component[size];
      System.arraycopy(
        this.components,
        0,
        components,
        0,
        this.size);
      this.components = components;
      this.size = size;
    }

    @Override
    public void setBounds(Region bounds) {
      super.setBounds(bounds);
      rheight = height / size;

      for (int r = 0; r < components.length; r++) {
        Component component = components[r];
        if (component == null)
          continue;

        Location start = new Location(
          r*rheight,
          bounds.start.position);
        component.setBounds(Region.fromLocation(
          start,
          width,
          rheight));
      }
    }

    @Override
    public void draw(GlyphBuffer buffer) {
      for (Component component : components) {
        if (component == null)
          continue;
        component.draw(buffer);
      }
    }
  }
}
