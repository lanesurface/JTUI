package jtxt.emulator.tui;

import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class GridLayout implements Layout {
//    /**
//     * Each cell within this layout has a region which it represents. A row is
//     * made up of one or more cells in which components can be placed when they
//     * request bounds within their parent container. Enabled cells (true)
//     * are those which are occupied; requesting components can only be placed
//     * in those cells which are disabled, otherwise components might overlap.
//     */
//    private boolean[][] cells;
    
    private Region parentBounds;
    
    private static class Cell {
        private Region bounds;
        
        private int width,
                    height;
        
        private boolean occupied;
        
        public Cell(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        private void setBounds(Region bounds) {
            this.bounds = bounds;
        }
        
        public Region getBounds() {
            return bounds;
        }
        
        private void enable() {
            occupied = true;
        }
        
        public boolean isOccupied() {
            return occupied;
        }
    }
    
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
    
    @Override
    public Region getBounds(Object params) {
        if (!(params instanceof GridParameters))
            throw new IllegalArgumentException("Layout parameters must be " +
                                               "of an appropriate type.");
        
        GridParameters gridParams = (GridParameters)params;
        
        return new Region(gridParams.first.bounds.start,
                          gridParams.last.bounds.end);
    }
    
    public class GridParameters {
        protected Cell first,
                       last;
        
        public GridParameters(int startRow,
                              int startCol,
                              int endRow,
                              int endCol) {
            first = cells[startRow][startCol];
            last = cells[endRow][endCol];
        }
    }
}
