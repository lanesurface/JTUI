package jtxt.emulator.tui;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;

public class TextBox extends Component {
    /**
     * The text to draw onto the screen.
     */
    private GString text;
    
    public TextBox(String text) {
        this.text = GString.of(text);
    }
    
    public void draw(BufferedFrame frame) {
        frame.update(text, bounds);
    }
}
