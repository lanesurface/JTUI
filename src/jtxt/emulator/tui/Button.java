package jtxt.emulator.tui;

import java.awt.Color;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.util.Glyphs;

public class Button extends DefaultComponent {
    /**
     * All buttons should have a clearly identifiable region in which they
     * respond to input events.
     */
    private Border outline;
    
    /**
     * The text that this button will display on the screen.
     */
    private TextBox text;
    
    public Button(String text,
                  Color textColor,
                  Object parameters) {
        this.text = new TextBox(parameters,
                                Glyphs.escape(textColor) + text,
                                TextBox.Position.CENTER);
        outline = new Border(Border.Type.DASHED,
                             Color.WHITE,
                             this.text);
        
        /*
         * Some things to think about. How are we going to make components
         * which should respond whenever they are pressed? Should there be an
         * interface that needs to be implemented if a component wants to be
         * able to respond to clicks within itself?
         * 
         * What are we going to do about the parameters object? There needs to
         * be some sort of common interface for parameters so that we don't
         * need to type cast every time we call Layout#getBounds(Object).
         */
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        outline.draw(frame);
    }
}
