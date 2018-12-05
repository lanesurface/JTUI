package jtxt.emulator.util.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import jtxt.emulator.Context;
import jtxt.emulator.Glyph;

public class ASCIImage {
    private int width;
    
    private int height;
    
    private BufferedImage image;
    
    public ASCIImage(Context context, 
                     String filename,
                     ColorSamplingStrategy samplingStrategy,
                     int outputWidth) {
        BufferedImage source = null;
        
        try { 
            URL path = ClassLoader.getSystemResource(filename);
            source = ImageIO.read(path);
        }
        catch (IOException ie) {
            System.err.println("Could not read the image " + filename);
        }
        
//        source = Images.convertToGrayscale(source);
        
        Dimension cdim = context.getCharacterDimensions();
        Glyph[][] chars = Images.mapToCharacters(source,
                                                 samplingStrategy,
                                                 outputWidth,
                                                 cdim.width,
                                                 cdim.height);
        
        width = cdim.width * chars[0].length;
        height = cdim.height * chars.length;
        System.out.printf("ASCII width=%d,height=%d%n", width, height);
        
        drawToImage(chars, cdim);
    }
    
    private void drawToImage(Glyph[][] chars, Dimension cdim) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 8));
        
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        
        int baseline = graphics.getFontMetrics(graphics.getFont()).getHeight();
        for (int row = 0; row < chars.length; row++) {
            for (int col = 0; col < chars[row].length; col++) {
                graphics.setColor(chars[row][col].color);
                graphics.drawString(chars[row][col].character+"",
                                    (col)*cdim.width,
                                    (row)*cdim.height+baseline);
            }
        }
    }
    
    public void resize(int width) {
        throw new UnsupportedOperationException("Could not resize the image.");
    }
    
    public void writeToFile(String filename) {
        String[] parts = filename.split("[.]");
        String format = parts[parts.length-1];
        
        try {
            ImageIO.write(image, format, new java.io.File(filename));
        }
        catch (IOException ie) {
            System.out.printf("Could not write image \"%s\" to file.%n",
                              filename);
        }
    }
}
