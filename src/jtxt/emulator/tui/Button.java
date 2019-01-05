package jtxt.emulator.tui;

import java.awt.Color;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.util.Glyphs;

public class Button extends Component {
    /**
     * All buttons should have a clearly identifiable region in which they
     * respond to input events.
     */
    private Border outline;
    
    /**
     * The text that this button will display on the screen.
     */
    private TextBox text;
    
    public Button(String text, Color textColor) {
        this.text = new TextBox(Glyphs.escape(textColor) + text,
                                TextBox.Position.CENTER);
        outline = new Border(this.text,
                             Border.Type.DASHED,
                             Color.WHITE);
    }
    
    @Override
    public void setParent(Container parent) {
        super.setParent(parent);
        outline.setParent(parent);
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        outline.setSize(width, height);
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        outline.draw(frame);
    }
}
