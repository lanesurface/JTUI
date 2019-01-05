package jtxt.emulator.tui;

import jtxt.emulator.Region;
import jtxt.emulator.tui.Layout.Parameters;

public abstract class DefaultComponent implements Component {
    protected Region bounds;
    
    protected Layout.Parameters parameters;
    
    @Override
    public void setBounds(Region bounds) {
        this.bounds = bounds;
    }

    @Override
    public Parameters getLayoutParameters() {
        return parameters;
    }
}
