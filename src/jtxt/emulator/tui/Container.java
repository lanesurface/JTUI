package jtxt.emulator.tui;

import java.util.List;

/**
 * An object which holds other components and determines their layout on the
 * screen. 
 */
public abstract class Container implements Component {
    /**
     * A collection of all the components owned by this container.
     */
    protected volatile List<Component> children;
    
    @Override
    public void draw(java.awt.Graphics2D graphics) {
        for (Component child : children) 
            child.draw(graphics);
    }
    
    @Override
    public Component[] getChildren() {
        Component[] components = new Component[children.size()];
        children.toArray(components);
        return components;
    }
    
    @Override
    public Component getChild(int index) {
        return children.get(index);
    }
}
