package jtxt.emulator.util.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import jtxt.emulator.Glyph;

public class Images {
    /**
     * The characters that can be used for rasterizing the image in an ASCII
     * format. These characters are stored in descending order of intensity.
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
    
    private Images() { /* Do not allow for this class to be instantiated. */ }
    
    /**
     * Given an Image, this method converts that image into an array of
     * Strings, where each string represents a single row of pixels in the
     * output image. Color information is not preserved.
     * 
     * @param source The source image to translate.
     * @param outputWidth The number of characters per line in the output 
     *                    image.
     * 
     * @return An array of Strings representing the ASCII image. 
     */
    public static String[] convertToASCII(Image source, int outputWidth) {
        Glyph[][] output = mapToCharacters(toBufferedImage(source),
                                           outputWidth);
        
        String[] lines = new String[output.length];
        for (int i = 0; i < lines.length; i++) {
            StringBuilder builder = new StringBuilder();
            
            Glyph[] glyphs = output[i];
            for (Glyph g : glyphs)
                builder.append(g.character);
            
            lines[i] = builder.toString();
        }
        
        return lines;
    }
    
    public static Glyph[][] mapToCharacters(BufferedImage img, 
                                            int outputWidth) {
        int width = img.getWidth(),
            height = img.getHeight();
        
        // FIXME: Need to cast to double; however, the decimal on scale
        // accumulates and causes an IndexOutOfBoundsException to be thrown.
        double scale = width / outputWidth;
        
        System.out.println("scale=" + scale);
        
        int xChars = (int)(width / scale),
            yChars = (int)(height / scale);
        
        /*
         * Pixels at the edge of the image need to be discarded if they cannot
         * be averaged.
         */
        width -= width % scale;
        height -= height % scale;
        System.out.printf("width=%d,height=%d%nxChars=%d,yChars=%d%n", 
                          width,
                          height,
                          xChars, 
                          yChars);
        
        double steps = (double)255 / ASCII_CHARS.length;
        
        Glyph[][] characters = new Glyph[yChars][xChars];
        int charY = 0,
            charX;
        for (int y = 0; y < height; y += scale) {
            charX = 0;
            for (int x = 0; x < width; x += scale) {
                int avg = -1,
                    lum = -1;
                
                int[][] colors = new int[(int)scale][(int)scale];
                int row = 0, 
                    col = 0;
                /*
                 * Iterate over every pixel in this chunk and add it's RGB
                 * value to the average. 
                 */
                for (int cy = y; cy < y + scale; cy++) {
                    col = 0;
                    for (int cx = x; cx < x + scale; cx++) {
                        /*
                         * Do grayscale image conversion in here (despite the
                         * #toGrayscale(BufferedImage) method in this class
                         * already performing a similar function), as it
                         * significantly speeds up the process if we don't
                         * iterate over the pixels in this image twice.
                         */
                        int argb = img.getRGB(cx, cy),
                            grayscale = ((argb >> 16 & 0xFF) +
                                         (argb >> 8 & 0xFF) +
                                         (argb & 0xFF)) / 3;
                        
//                        avg += argb;
                        lum += grayscale;
                        
                        colors[row][col++] = argb;
                    }
                    row++;
                }
                
                int color = findMean(colors);
                
//                avg /= scale * scale;
                lum /= scale * scale;
                
                /*
                 * Find the appropriate character in the array for the relative
                 * brightness of the pixels in this chunk.
                 */
                int index = (int)(lum / steps);
                char out = ASCII_CHARS[index];
                
                int red = (color >> 16) & 0xFF,
                    green = (color >> 8) & 0xFF,
                    blue = color & 0xFF;
//                System.out.printf("red=%d,green=%d,blue=%d%n",
//                                  red,
//                                  green,
//                                  blue);
                
                Glyph g = new Glyph(out, new Color(red, green, blue));
                characters[charY][charX++] = g;
            }
            charY++;
        }
        
        return characters;
    }
    
    private static int findMean(int[][] colors) {
        HashMap<Integer, Integer> occurences = new HashMap<>();
        int mean = 0,
            maxOcc = -1;
        
        for (int row = 0; row < colors.length; row++) {
            for (int col = 0; col < colors[row].length; col++) {
                int color = colors[row][col];
                
                Integer occurence = occurences.get(color);
                
                if (occurence == null) {
                    occurence = 1;
                }
                
                if (occurence > maxOcc) {
                    mean = color;
                    maxOcc = occurence;
                }
                occurences.put(color, occurence+1);
            }
        }
        
        return mean;
    }
    
    /**
     * Converts an {@code Image} into a {@code BufferedImage}.
     * 
     * @param source The input image.
     * 
     * @return A writable image identical to the source.
     */
    public static BufferedImage toBufferedImage(Image source) {
        if (source instanceof BufferedImage)
            return (BufferedImage)source;
        
        BufferedImage image = new BufferedImage(source.getWidth(null),
                                                source.getHeight(null),
                                                BufferedImage.TYPE_INT_ARGB);
        
        java.awt.Graphics graphics = image.getGraphics();
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        
        return image;
    }
    
    public static BufferedImage convertToGrayscale(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Extract the ARGB components from the image.
                int argb = image.getRGB(x, y),
                    alpha = (argb >> 24) & 0xFF,
                    red = (argb >> 16) & 0xFF,
                    green = (argb >> 8) & 0xFF,
                    blue = argb & 0xFF;
                
                // Adaptive luminance
//                red *= 0.2126;
//                blue *= 0.7152;
//                green *= 0.0722;
                
                /*
                 * Compute the averages of the RGB channels and sum them
                 * together in grayscale.
                 */
                int avg = (red + green + blue) / 3,
                    grayscale = alpha << 24
                                | avg << 16
                                | avg << 8
                                | avg;
                
                image.setRGB(x, y, grayscale);
            }
        }

        return image;
    }
}
