package jtxt.emulator.tui;

import java.awt.Color;
import java.util.Arrays;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;
import jtxt.emulator.Region;

public class Border extends Component {
    private Component component;
    
    public static enum Type { 
        SOLID('\u2501'),
        DASHED('-'),
        DOTTED('.');
        
        private final char character;
        
        Type(char character) {
            this.character = character;
        }
    }
    
    private Glyph border;
    
    public Border(Component component, Type type, Color color) {
        this.component = component;
        border = new Glyph(type.character, color);
        
        Region region = component.getBounds();
        Location start = region.getStart(),
                 end = region.getEnd();
        
        bounds = new Region(start.line - 1,
                            start.position - 1,
                            end.line + 1,
                            end.position + 1);
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        Location start = bounds.getStart(),
                 end = bounds.getEnd();
        
        for (int line = start.line; line < end.line; line++) {
            if (line == start.line || line == end.line - 1) {
                for (int position = start.position;
                     position < end.position;
                     position++)
                {
                    Glyph[] glyphs = new Glyph[bounds.getWidth()];
                    Arrays.fill(glyphs, border);
                    GString border = new GString(glyphs);
                    
                    frame.update(border, new Region(line,
                                                    start.position,
                                                    line + 1,
                                                    end.position));
                }
            }
            else {
                frame.update(border, new Location(line, start.position));
                frame.update(border, new Location(line, end.position - 1));
            }
        }
    }
}
