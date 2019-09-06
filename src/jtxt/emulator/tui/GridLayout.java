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

import java.util.Arrays;

import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class GridLayout implements Layout {
  /**
   * All of the {@code Cell}s which {@code Component}s can occupy within this
   * instance of the layout. See the constructor for more details about how the
   * dimensions of this array are calculated.
   */
  private Cell[][] cells;

  /**
   * Construct a new {@code GridLayout} with the given dimensions for each of the
   * rows in the layout.
   *
   * @param dimensions The array which specifies the number of cells in each row,
   *   and where the number of rows is determined by the length of this array.
   */
  public GridLayout(int... dimensions) {
    cells = new Cell[dimensions.length][];
    for (int i = 0; i < cells.length; i++) {
      cells[i] = new Cell[dimensions[i]];

      for (int j = 0; j < dimensions[i]; j++)
        cells[i][j] = new Cell();
    }
  }

  @Override
  public void setParentBounds(Region bounds) {
    Location current = bounds.getStart();
    int height = bounds.getHeight() / cells.length;

    for (int r = 0; r < cells.length; r++) {
      int width = bounds.getWidth() / cells[r].length;

      for (int c = 0; c < cells[r].length; c++) {
        Cell cell = cells[r][c];
        cell.setBounds(Region.fromLocation(
          current,
          width,
          height));
        cell.occupied = false;

        current.advanceForward(width);
      }

      current = new Location(
        current.line+height,
        bounds.start.position);
    }
  }

  /**
   * Creates an instance of {@code GridParameters} which uses the cell at the given
   * width and height to calculate the bounds within the container this instance of
   * the layout belongs to.
   *
   * @param row The row within this layout of the cell.
   * @param col The column within this layout of the cell.
   *
   * @return An instance of {@code GridParameters} which represents the cell at
   *   {@code row} and {@code col}.
   */
  public GridParameters getParametersForCell(
    int row,
    int col)
  {
    return new GridParameters(
      row,
      col,
      row,
      col);
  }

  /**
   * Creates an instance of {@code GridParameters} for the given range of cells, so
   * that the params specify that the {@code Component} which these params
   * are applied to should occupy that entire range of cells within the layout.
   *
   * @param startRow The row of the first cell in the range.
   * @param startCol The column of the first cell in the range.
   * @param endRow The row of the last cell in the range.
   * @param endCol The column of the last cell in the range.
   *
   * @return An instance of {@code GridParameters} which indicates that the component
   *   should occupy all of the cells within the range from the start (row,&nbsp;col)
   *   to the end (row,&nbsp;col), including the last cell in this range.
   */
  public GridParameters getParametersForCellsInRange(
    int startRow,
    int startCol,
    int endRow,
    int endCol)
  {
    return new GridParameters(
      startRow,
      startCol,
      endRow,
      endCol);
  }

  /**
   * The params which describe the {@code Cell}s which a {@code Component} should
   * occupy within this layout.
   */
  public class GridParameters {
    protected final Cell first, last;
    private final int sr,
      sc,
      er,
      ec;

    public GridParameters(
      int sr,
      int sc,
      int er,
      int ec)
    {
      this.sr = sr;
      this.sc = sc;
      this.er = er;
      this.ec = ec;

      first = cells[sr][sc];
      last = cells[er][ec];
    }
  }

  private void enableCells(GridParameters params) {
    for (int r = params.sr; r < cells.length && r < params.er; r++) {
      for (int c = params.sc; c < cells[r].length && c < params.ec; c++) {
        Cell cell = cells[r][c];
        if (cell.occupied)
          throw new IllegalArgumentException("Cells are already " +
                                             "occupied.");

        cell.occupied = true;
      }
    }
  }

  @Override
  public Region getBounds(Object params) {
    if (!(params instanceof GridParameters))
      throw new IllegalArgumentException("Layout params must be " +
                                         "of an appropriate type.");

    GridParameters gp;
    Cell f, l;

    gp = (GridParameters)params;
    enableCells(gp);
    f = gp.first;
    l = gp.last;

    return new Region(
      f.bounds.start,
      l.bounds.end);
  }

  public static GridLayout initializeForDimensions(
    int rows,
    int columns)
  {
    int[] dimensions = new int[rows];
    Arrays.fill(
      dimensions,
      columns);

    return new GridLayout(dimensions);
  }

  /**
   * A region within this layout which a given component can request to occupy,
   * unless the {@code Cell} is already occupied by another component.
   */
  protected static class Cell {
    /**
     * The bounds within this layout's parent container that this cell occupies.
     */
    Region bounds;

    /**
     * Whether this {@code Cell} is already occupied by another component within this
     * layout.
     */
    boolean occupied;

    private void setBounds(Region bounds) {
      this.bounds = bounds;
    }
  }
}
