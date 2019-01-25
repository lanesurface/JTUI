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

import java.util.Arrays;

import jtxt.GlyphBuffer;
import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.tui.GridLayout.GridParameters;

/**
 * A {@code Container} which organizes its children into rows and columns. This
 * Container can be used to easily align a collection of Components on both of
 * their axes. (Components which have the same row or column number will be
 * aligned along an axis.)
 * 
 * @see Container
 * @see GridLayout
 */
public class Table extends Container<Table.Column> {
    /**
     * The Layout used for this table. Since tables are very closely related to
     * the {@code GridLayout}, we use much of the functionality already written
     * for this layout manager when drawing the Components of this Container.
     */
    protected GridLayout grid;
    
    protected int rows,
                  columns;
    
    public Table(Object parameters,
                 int rows,
                 int columns) {
        super(parameters, GridLayout.initializeForDimensions(rows,
                                                             columns));
        grid = (GridLayout)layout;
        this.rows = rows;
        this.columns = columns;
        
        Column[] cols = new Column[columns];
        for (int column = 0; column < columns; column++) {
            GridParameters params = grid.getParametersForCellsInRange(0,
                                                                      column,
                                                                      rows - 1,
                                                                      column);
            cols[column] = new Column(params, rows);
        }
        
        children = Arrays.asList(cols);
    }
    
    public void add(int rowNumber,
                    int columnNumber,
                    Component... components) {
        if (columnNumber >= columns || rowNumber + components.length >= rows)
            throw new IllegalArgumentException("The given indices are out " +
                                               "of bounds");
        
        addFrom(components,
                rowNumber,
                columnNumber);
    }
    
    /**
     * Inserts the given Components into this table at the specified row and
     * column, shifting any elements within that column downward to accommodate
     * these new elements.
     * 
     * @param rowNumber The row that the Components should be inserted before.
     * @param columnNumber The column that the Components should be inserted in.
     * @param after Whether to insert the Components into the column before or
     *              after the given indices. (If <code>false</code>, then the
     *              Components will occupy the position specified; otherwise,
     *              the Component which was at that position before will be
     *              retained, while all Components that followed it will have
     *              been shifted.)
     * @param components The Components to insert before the row and column.
     */
    public void insertIntoColumn(int rowNumber,
                                 int columnNumber,
                                 boolean after,
                                 Component... components) {
        rows += components.length;
        for (int column = 0; column < columns; column++)
            children.get(column).shiftRows(rowNumber,
                                           components.length);
        
        addFrom(components,
                rowNumber,
                columnNumber);
    }
    
    public void insertColumn(int row,
                             int column,
                             Component... components) {
        // TODO
    }
    
    protected void addFrom(Component[] components,
                           int startRow,
                           int columnNumber) {
        Column column = children.get(columnNumber);
        for (int row = startRow;
             row < startRow + components.length;
             row++) column.components[row] = components[row - startRow];
    }
    
    /**
     * A column of components within the Container. As the Table has been set
     * up to only be able to hold children of this type, Components which are
     * added to the Table must be added to a Column in the correct position
     * (determined by the row number given when that Component is added). Each
     * Component within a Column will have the same column number.
     */
    protected static class Column extends DefaultComponent {
        protected Component[] components;
        
        protected int size,
                      rowHeight;
        
        protected Column(Object parameters, int size) {
            components = new Component[size];
            this.size = size;
            this.parameters = parameters;
        }
        
        /**
         * Shifts all of the rows in this Column, starting at
         * <code>startRow</code> and continuing to the end of this Column, to
         * an index shifted by the <code>amount</code> specified. (Therefore,
         * the new index of a Component will be at the position <code>oldIndex
         * + amount</code>.)
         * 
         * <P><I>
         * Do note that shifting the rows causes this Column's size to be
         * increased by the amount. Calls to this method should usually reside
         * in a loop, where each of the rows within a Table will be shifted
         * appropriately.
         * </I></P>
         * 
         * @param startRow The first row which should be shifted.
         * @param amount The amount of indices that each row should be shifted
         *               by.
         */
        public void shiftRows(int startRow, int amount) {
            resize(size + amount);
            
            for (int row = size - 1;
                 row >= amount && row >= startRow;
                 row--) components[row] = components[row - amount];
        }
        
        /**
         * Adjusts the number of rows within this Column, growing or shrinking
         * as necessary. If the given size is less than the current size of
         * this Column, the Components which fall outside the new range of the
         * Column will be discarded. If the size indicates that the Column
         * needs to grow, the new rows within the Column will contain
         * <code>null</code> elements.
         * 
         * @param size The number of rows that this Column should contain 
         *             after resizing occurs.
         */
        protected void resize(int size) {
            Component[] components = new Component[size];
            System.arraycopy(this.components,
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
            rowHeight = height / size;
            
            for (int row = 0; row < components.length; row++) {
                Component component = components[row];
                if (component == null) continue;
                
                Location start = new Location(row * rowHeight,
                                              bounds.start.position);
                component.setBounds(Region.fromLocation(start,
                                                        width,
                                                        rowHeight));
            }
        }
        
        @Override
        public void draw(GlyphBuffer buffer) {
            for (Component component : components) {
                if (component == null) continue;
                component.draw(buffer);
            }
        }
    }
}
