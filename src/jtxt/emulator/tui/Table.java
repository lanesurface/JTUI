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
    
    public void insert(int row,
                       int column,
                       Component... components) {
        if (column >= columns || row + components.length >= rows)
            throw new IllegalArgumentException("The given indices are out " +
                                               "of bounds");
        
        addFrom(components, row, column);
    }
    
    protected void addFrom(Component[] components,
                           int startRow,
                           int columnNumber) {
        Column column = children.get(columnNumber);
        for (int row = startRow;
             row < startRow + components.length;
             row++) column.components[row] = components[row - startRow];
    }
    
    protected static class Column extends DefaultComponent {
        protected Component[] components;
        
        protected int rowHeight;
        
        protected Column(Object parameters, int size) {
            components = new Component[size];
            this.parameters = parameters;
        }
        
        @Override
        public void setBounds(Region bounds) {
            super.setBounds(bounds);
            rowHeight = height / components.length;
            
            for (int row = 0; row < components.length; row++) {
                Component component = components[row];
                if (component == null) continue;
                
                Location start = new Location(row * rowHeight,
                                              bounds.start.position);
                component.setBounds(Region.fromLocation(start,
                                                        width,
                                                        rowHeight));
                System.out.format("component=%s%n",
                                  component.getBounds());
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
