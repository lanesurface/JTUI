package jtxt.emulator.tui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.GString;
import jtxt.emulator.Glyph;
import jtxt.emulator.Location;

public class ASCIImage extends Component {
    /**
     * The characters that can be used for converting the image into ASCII
     * characters. These are stored in descending order of intensity.
     */
    private static final char[] ASCII_CHARS = { '$', '@', 'B', '%', '8', '&',
                                                'W', 'M', '#', '*', 'o', 'a',
                                                'h', 'k', 'b', 'd', 'p', 'q',
                                                'w', 'm', 'Z', 'O', '0', 'Q',
                                                'L', 'C', 'J', 'U', 'Y', 'X',
                                                'z', 'c', 'v', 'u', 'n', 'x',
                                                'r', 'j', 'f', 't', '/', '\\',
                                                '|', '(', ')', '1', '{', '}',
                                                '[', ']', '?', '-', '_', '+',
                                                '~', '<', '>', 'i', '!', 'l',
                                                'I', ';', ':', ',', '\"', '^',
                                                '`', '\'', '.', ' ' };
    
    /**
     * The source image that this ASCII image is made from. We keep a reference
     * to it here so that we can resize the ASCII image later if we ever need 
     * to.
     */
    private final BufferedImage source;
    
    /**
     * The strings that make up this image; each string represents row of
     * glyphs in the image.
     */
    private GString[] cached;
    
    /**
     * Create a new ASCII image from given source image.
     * 
     * @param source The image to use for creating this ASCII image.
     */
    public ASCIImage(BufferedImage source) {
        this.source = source;
    }
    
    private BufferedImage resize(BufferedImage source, int width) {
        int height = source.getHeight() / (source.getWidth() / width);
        
        Image scaled = source.getScaledInstance(width,
                                                height,
                                                Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(width,
                                                height,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawImage(scaled, 0, 0, null);
        
        return image;
    }
    
    private GString[] mapToGlyphs(BufferedImage image) {
        double range = 255.0 / ASCII_CHARS.length;
        
        GString[] glyphs = new GString[image.getHeight()];
        for (int y = 0; y < image.getHeight(); y++) {
            GString line = GString.of("");
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y),
                    lum = ((rgb >> 16 & 0xFF) +
                           (rgb >> 8 & 0xFF) + 
                           (rgb & 0xFF)) / 3;
                
                int index = (int)Math.min(Math.round(lum / range), 
                                          ASCII_CHARS.length - 1);
                char out = ASCII_CHARS[ASCII_CHARS.length - index - 1];
                Glyph glyph = new Glyph(out, new Color(rgb));
                line = line.append(glyph);
            }
            
            glyphs[y] = line;
        }
        
        return glyphs;
    }
    
    @Override
    public void draw(BufferedFrame frame) {
        if (cached == null
            || cached.length != bounds.getHeight()
            || cached[0].length() != bounds.getWidth())
        {
            BufferedImage image = resize(source, bounds.getWidth());
            cached = mapToGlyphs(image);
        }
        
        for (int line = 0; line < bounds.getHeight(); line++)
            frame.update(cached[line], new Location(bounds.start.line + line,
                                                    bounds.start.position));
    }
}
