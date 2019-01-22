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

public class Table extends Container<Table.Column> {
    /**
     * The Layout used for this table. Since tables have much in common with the
     * GridLayout, yet we wish for them to appear as Components in the window,
     * we use an appropriate instance of a GridLayout to manage their bounds
     * instead.
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
    }
    
    public void insert(int row,
                       int column,
                       Component... components) {
        if (row >= rows
            || column + components.length >= columns)
        {
            throw new IllegalArgumentException("The given indices are out " +
                                               "of bounds");
        }
        
        addFrom(components, row, column);
    }
    
    protected void addFrom(Component[] components,
                           int startRow,
                           int column) {
        // TODO
    }
    
    protected static class Column extends DefaultComponent {
        protected Component[] components;
        
        protected Column(int size) {
            components = new Component[size];
        }

        @Override
        public void draw(GlyphBuffer buffer) {
            // TODO
        }
    }
}
