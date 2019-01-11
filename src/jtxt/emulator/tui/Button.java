package jtxt.emulator.tui;

import java.awt.Color;

import jtxt.GlyphBuffer;
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
    }
    
    @Override
    public void draw(GlyphBuffer buffer) {
        outline.draw(buffer);
    }
}
