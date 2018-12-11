package jtxt.emulator.tui;

import jtxt.emulator.Region;
import jtxt.emulator.TextRenderer;
import jtxt.emulator.util.Glyphs;

public class TextBox extends Component {
    /**
     * The text to draw onto the screen.
     */
    private String text;
    
    public TextBox(String text) {
        this.text = text;
    }
    
    @Override
    public void draw(TextRenderer renderer) {
        renderer.update(Glyphs.of(text), bounds);
    }

    @Override
    public void inflate(Region bounds) {
        this.bounds = bounds;
        // TODO: Tell terminal to update us.
    }
}
