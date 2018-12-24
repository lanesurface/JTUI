package jtxt.emulator.tui;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class TextBox extends Component {
    /**
     * The text to draw onto the screen.
     */
    private GString text;
    
    public static enum Justification { LEFT, CENTER, RIGHT }
    
    private Justification justification;
    
    public TextBox(String text, Justification justification) {
        this.text = GString.of(text);
        this.justification = justification;
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        Location start = bounds.getStart(),
                 end = bounds.getEnd();
        
        GString[] lines = text.wrap(bounds.getWidth());
        for (int line = 0; line < lines.length; line++) {
            int spos = start.position;
            
            switch (justification) {
            case RIGHT:
                spos = bounds.getWidth() - lines[line].length();
                break;
            case CENTER: break;
            default: break;
            }
            
            frame.update(lines[line], new Region(start.line + line,
                                                 spos,
                                                 start.line + line + 1,
                                                 end.position));
        }
    }
}
