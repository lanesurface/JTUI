package jtxt.emulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class SRasterizer implements GlyphRasterizer {
    protected Context context;
    
    public SRasterizer(Context context) {
        this.context = context;
    }
    
    @Override
    public Image rasterize(BufferedFrame frame) {
        Region bounds = frame.getBounds();
        
        BufferedImage image = new BufferedImage(context.windowSize.width,
                                                context.windowSize.height,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
      
        g.setColor(Color.BLACK);
        g.fillRect(0, 
                   0, 
                   context.windowSize.width, 
                   context.windowSize.height);
      
        int ascent = g.getFontMetrics().getAscent();
        for (int i = 0; i < bounds.getHeight(); i++) {
            for (int j = 0; j < bounds.getWidth(); j++) {
                Glyph glyph = frame.getGlyph(new Location(i, j));
                g.setColor(glyph.getColor());
                g.drawString(glyph.getCharacter() + "",
                               j * context.charSize.width,
                               i * context.charSize.height + ascent);
            }
        }
        g.dispose();
        
        return image;
    }
}
