package jtxt.emulator.tui;

import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class GridLayout implements Layout {
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
    
    public GridParameters getParametersForCell(int row, int col) {
        return new GridParameters(row,
                                  col,
                                  row,
                                  col);
    }
    
    public GridParameters getParametersForCellsInRange(int startRow,
                                                       int startCol,
                                                       int endRow,
                                                       int endCol) {
        return new GridParameters(startRow,
                                  startCol,
                                  endRow,
                                  endCol);
    }
    
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
    
    private void enableCells(GridParameters params) {
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
    
    private static class Cell {
        Region bounds;
        
        boolean occupied;
        
        private void setBounds(Region bounds) {
            this.bounds = bounds;
        }
    }
}
