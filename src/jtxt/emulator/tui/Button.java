package jtxt.emulator.tui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.util.Glyphs;

public class Button extends Decorator implements Interactable {
    /**
     * The actions to perform whenever this button receives a notification that
     * it has been pressed by a client.
     */
    private List<Callback> clickCallbacks;
    
    public Button(String text,
                  Color textColor,
                  Object parameters) {
        component = new Border(Border.Type.DASHED,
                               Color.WHITE,
                               new TextBox(parameters,
                                           Glyphs.escape(textColor) + text,
                                           TextBox.Position.CENTER));
        clickCallbacks = new ArrayList<>();
    }
    
    @Override
    public void setBounds(Region bounds) {
        component.setBounds(bounds);
    }
    
    @Override
    public Region getBounds() {
        return component.getBounds();
    }
    
    @Override
    public void addCallback(Callback callback) {
        clickCallbacks.add(callback);
    }
    
    @Override
    public boolean clicked(Location clickLocation) {
        clickCallbacks.stream()
                      .forEach(Callback::performAction);
        
        /*
         * We can't do anything with keyboard input, so yield control of the
         * input.
         */
        return false;
    }
}
