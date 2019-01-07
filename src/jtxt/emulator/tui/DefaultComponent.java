package jtxt.emulator.tui;

import jtxt.emulator.Region;

public abstract class DefaultComponent implements Component {
    protected Region bounds;
    
    protected Object parameters;
    
    protected int width,
                  height;
    
    @Override
    public Region getBounds() {
        return bounds;
    }
    
    @Override
    public void setBounds(Region bounds) {
        this.bounds = bounds;
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    @Override
    public Object getLayoutParameters() {
        return parameters;
    }
}
