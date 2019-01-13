package jtxt.emulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jtxt.GlyphBuffer;

public class SWRasterizer implements GlyphRasterizer {
    protected Context context;
    
    public SWRasterizer(Context context) {
        this.context = context;
    }
    
    @Override
    public Image rasterize(GlyphBuffer buffer) {
        Region bounds = buffer.getBounds();
        
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
        
        g.setFont(context.font);
        int ascent = g.getFontMetrics().getAscent(),
            numLines = bounds.getHeight(),
            lineSize = bounds.getWidth();
        
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < lineSize; j++) {
                Glyph glyph = buffer.getGlyph(new Location(i, j));
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
