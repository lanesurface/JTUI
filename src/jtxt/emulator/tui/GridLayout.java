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
        private int width;
        
        private int height;
        
        private boolean occupied;
        
        public Cell(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public void enable() {
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
    
    protected Region getCellBounds(int row, int col) {
        Cell cell = cells[row][col];
        
        // TODO: Replace `2` and `4` with something meaningful.
        Location start = new Location(parentBounds.start.line * 2,
                                      parentBounds.start.position * 4);
        return new Region(start.line,
                          start.position,
                          start.line + cell.height,
                          start.position + cell.width);
    }
    
    /**
     * Gets the {@link Layout.Parameters} object which represents the region
     * defined by the chosen cell within the grid.
     * 
     * @param row The row within the layout that this parameter object should
     *            be created for.
     * @param col The column within the layout that this parameter object
     *            should be created for.
     * 
     * @return A new {@code Layout.Parameter} object which represents the row
     *         and column within this layout that has been selected.
     */
    public Layout.Parameters getParametersFromPosition(int row,
                                                       int col) {
        Region bounds = getCellBounds(row, col);
        
        return new Layout.Parameters(bounds.getWidth(),
                                     bounds.getHeight());
    }
    
    @Override
    public void setParentBounds(Region parentBounds) {
        this.parentBounds = parentBounds;
    }
    
    @Override
    public Region getBounds(int width, int height) {
        /*
         * Every time that a component requests bounds within this layout, make
         * sure to enable the cells which are now allocated to this component.
         */
        
        return null;
    }
}
