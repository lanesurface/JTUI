package jtxt.emulator.tui;

import jtxt.emulator.GString;
import jtxt.emulator.GlyphRenderer;

public class TextBox extends Component {
    /**
     * The text to draw onto the screen.
     */
    private GString text;
    
    public TextBox(String text) {
        this.text = GString.of(text);
    }
    
    public void draw(GlyphRenderer renderer) {
        renderer.update(text, bounds);
    }

    public void inflate(int width, int height) {
        // TODO
    }
}
