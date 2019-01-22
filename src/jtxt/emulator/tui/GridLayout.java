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
     * instance of the layout. See the constructor for more details about how
     * the dimensions of this array are calculated.
     */
    protected Cell[][] cells;
    
    /**
     * Construct a new {@code GridLayout} with the given dimensions for each
     * of the rows in the layout.
     * 
     * @param dimensions The array which specifies the number of cells in each
     *                   row, and where the number of rows is determined by the
     *                   length of this array.
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
        
        for (int row = 0; row < cells.length; row++) {
            int width = bounds.getWidth() / cells[row].length;
            
            for (int col = 0; col < cells[row].length; col++) {
                Cell cell = cells[row][col];
                cell.setBounds(Region.fromLocation(current,
                                                   width,
                                                   height));
                cell.occupied = false;
                
                current.advanceForward(width);
            }
            
            current = new Location(current.line + height,
                                   bounds.start.position);
        }
    }
    
    /**
     * Creates an instance of {@code GridParameters} which uses the cell at the
     * given width and height to calculate the bounds within the container this
     * instance of the layout belongs to.
     * 
     * @param row The row within this layout of the cell.
     * @param col The column within this layout of the cell.
     * 
     * @return An instance of {@code GridParameters} which represents the cell
     *         at {@code row} and {@code col}.
     */
    public GridParameters getParametersForCell(int row, int col) {
        return new GridParameters(row,
                                  col,
                                  row,
                                  col);
    }
    
    /**
     * Creates an instance of {@code GridParameters} for the given range of
     * cells, so that the parameters specify that the {@code Component} which
     * these parameters are applied to should occupy that entire range of cells
     * within the layout.
     * 
     * @param startRow The row of the first cell in the range.
     * @param startCol The column of the first cell in the range.
     * @param endRow The row of the last cell in the range.
     * @param endCol The column of the last cell in the range.
     * 
     * @return An instance of {@code GridParameters} which indicates that the
     *         component should occupy all of the cells within the range from
     *         the start (row,&nbsp;col) to the end (row,&nbsp;col), including
     *         the last cell in this range.
     */
    public GridParameters getParametersForCellsInRange(int startRow,
                                                       int startCol,
                                                       int endRow,
                                                       int endCol) {
        return new GridParameters(startRow,
                                  startCol,
                                  endRow,
                                  endCol);
    }
    
    /**
     * The parameters which describe the {@code Cell}s which a
     * {@code Component} should occupy within this layout.
     */
    public class GridParameters {
        protected final Cell first,
                             last;
        
        protected final int startRow,
                            startCol,
                            endRow,
                            endCol;
        
        public GridParameters(int startRow,
                              int startCol,
                              int endRow,
                              int endCol) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            
            first = cells[startRow][startCol];
            last = cells[endRow][endCol];
        }
    }
    
    protected void enableCells(GridParameters params) {
        for (int row = params.startRow;
             row < cells.length && row < params.endRow;
             row++)
        {
            for (int col = params.startCol;
                 col < cells[row].length && col < params.endCol;
                 col++)
            {
                Cell cell = cells[row][col];
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
            throw new IllegalArgumentException("Layout parameters must be " +
                                               "of an appropriate type.");
        
        GridParameters gridParams = (GridParameters)params;
        enableCells(gridParams);
        
        Cell first = gridParams.first,
             last = gridParams.last;
        
        return new Region(first.bounds.start,
                          last.bounds.end);
    }
    
    public static GridLayout initializeForDimensions(int rows,
                                                     int columns) {
        int[] dimensions = new int[rows];
        Arrays.fill(dimensions, columns);
        
        return new GridLayout(dimensions);
    }
    
    /**
     * A region within this layout which a given component can request to
     * occupy, unless the {@code Cell} is already occupied by another
     * component.
     */
    protected static class Cell {
        /**
         * The bounds within this layout's parent container that this cell
         * occupies.
         */
        Region bounds;
        
        /**
         * Whether this {@code Cell} is already occupied by another component
         * within this layout.
         */
        boolean occupied;
        
        private void setBounds(Region bounds) {
            this.bounds = bounds;
        }
    }
}
