package jtxt.emulator.tui;

import java.util.Objects;

import jtxt.emulator.Region;

public class GridLayout implements Layout {
    /**
     * Each cell within this layout has a region which it represents. A row is
     * made up of one or more cells in which components can be placed when they
     * request bounds within their parent container. Enabled cells (true)
     * are those which are occupied; requesting components can only be placed
     * in those cells which are disabled, otherwise components might overlap.
     */
    private boolean[][] cells;
    
    private Region parentBounds;
    
    /**
     * The number of lines each row is made up of.
     */
    private int rowLength;
    
    /**
     * Construct a new {@code GridLayout} with the given dimensions for each
     * of the rows in the layout.
     * 
     * @param dimensions The array which specifies the number of cells in each
     *                   row, and where the number of rows is determined by the
     *                   length of this array.
     */
    public GridLayout(int... dimensions) {
        cells = new boolean[dimensions.length][];
        for (int i = 0; i < cells.length; i++)
            cells[i] = new boolean[dimensions[i]];
    }
    
    @Override
    public void setParentBounds(Region parentBounds) {
        this.parentBounds = parentBounds;
        rowLength = parentBounds.getHeight() / cells.length;
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
