package jtxt.emulator.tui;

import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class GridLayout implements Layout {
    private Region parentBounds;
    
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
        for (int i = 0; i < cells.length; i++)
            cells[i] = new Cell[dimensions[i]];
    }
    
    @Override
    public void setParentBounds(Region parentBounds) {
        this.parentBounds = parentBounds;
        
        // We need to recalculate the bounds of each cell now.
        Location current = parentBounds.getStart();
        int height = parentBounds.getHeight() / cells.length;
        
        for (int row = 0; row < cells.length; row++) {
            int width = parentBounds.getWidth() / cells[row].length;
            
            for (int col = 0; col < cells[row].length; col++) {
                cells[row][col].setBounds(Region.fromLocation(current,
                                                              width,
                                                              height));
                current.advanceForward(width);
            }
            
            current = new Location(current.line + height,
                                   parentBounds.start.position);
        }
    }
    
    private void enableCells(GridParameters params) {
        for (int row = params.startRow; row < params.endRow; row++)
            for (int col = params.startCol; col < params.endCol; col++)
                cells[row][col].enable();
    }
    
    @Override
    public Region getBounds(Object params) {
        if (!(params instanceof GridParameters))
            throw new IllegalArgumentException("Layout parameters must be " +
                                               "of an appropriate type.");
        
        // TODO: Ensure requested cells are unoccupied.
        
        GridParameters gridParams = (GridParameters)params;
        enableCells(gridParams);
        
        Cell first = gridParams.first,
             last = gridParams.last;
        Region area = new Region(first.bounds.start,
                                 last.bounds.end);
        
        return area;
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
    
    private static class Cell {
        private Region bounds;
        
        private boolean occupied;
        
        private void setBounds(Region bounds) {
            this.bounds = bounds;
        }
        
        private void enable() {
            occupied = true;
        }
        
        public boolean isOccupied() {
            return occupied;
        }
    }
}
