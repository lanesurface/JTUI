package jtxt.emulator.tui;

import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class TextBox implements Component {
    private Region bounds;
    
    private Glyph[][] characters;
    
    private String text;
    
    public TextBox(String text) {
        this.text = text;
    }
    
    public void setSize(int width, int height) {
        
    }

    @Override
    public boolean intersects(Location location) {
        return false;
    }

    @Override
    public boolean inside(Region region) {
        return false;
    }

    @Override
    public Region getBounds() {
        return null;
    }

    @Override
    public void setBounds(Region bounds) {
        
    }

    @Override
    public void draw() {
        
    }
}
