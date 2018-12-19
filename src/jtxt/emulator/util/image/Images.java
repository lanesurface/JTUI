package jtxt.emulator.util.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    public static String[] convertToASCII(jtxt.emulator.Context context,
                                          Image source,
                                          int outputWidth) {
        Dimension cdim = context.getCharacterDimensions();

        Glyph[][] output = mapToCharacters(toBufferedImage(source),
                                           ColorSamplingStrategy.MODAL,
                                           outputWidth,
                                           cdim.width,
                                           cdim.height);

        String[] lines = new String[output.length];
        for (int i = 0; i < lines.length; i++) {
            StringBuilder builder = new StringBuilder();

            Glyph[] glyphs = output[i];
            for (Glyph g : glyphs)
                builder.append(g.getCharacter());

            lines[i] = builder.toString();
        }

        return lines;
    }

    public static Glyph[][] mapToCharacters(BufferedImage img,
                                            ColorSamplingStrategy cs,
                                            int outputWidth,
                                            int charWidth,
                                            int charHeight) {
        int imageWidth = img.getWidth(),
            imageHeight = img.getHeight();

        /*
         * FIXME: This should be floating-point, but that would accumulate in
         * the loop and cause us to access invalid indices. Converted images
         * therefore have slightly different aspect ratios (to avoid fixing the
         * real problem).
         */
        int xScale = imageWidth / outputWidth,
            yScale = xScale * (charHeight / charWidth);

        int xChars = imageWidth / xScale,
            yChars = imageHeight / yScale;

        /*
         * There is a certain amount of error in the scale factor if the scales
         * are not perfect divisors of the source width and height. A few
         * pixels at the left and bottom may be discarded in the conversion.
         */
        imageWidth -= imageWidth % xScale;
        imageHeight -= imageHeight % yScale;
        System.out.printf("width=%d,height=%d%nxChars=%d,yChars=%d%n",
                          imageWidth,
                          imageHeight,
                          xChars,
                          yChars);

        double steps = 255.0 / ASCII_CHARS.length;

        Glyph[][] characters = new Glyph[yChars][xChars];
        int yIndex = 0,
            xIndex;
        for (int y = 0; y < imageHeight; y += yScale) {
            xIndex = 0;
            for (int x = 0; x < imageWidth; x += xScale) {
                int lum = -1;

                int[][] colors = new int[yScale][xScale];
                int row = 0,
                    col = 0;
                /*
                 * Iterate over every pixel in this chunk and add it's RGB
                 * value to the average.
                 */
                for (int cy = y; cy < y + yScale; cy++) {
                    col = 0;
                    for (int cx = x; cx < x + xScale; cx++) {
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

                        colors[row][col++] = argb;
                        lum += grayscale;
                    }
                    row++;
                }

                int color = cs.sample(colors);
                lum /= xScale * yScale;

                /*
                 * Find the appropriate character in the array for the relative
                 * brightness of the pixels in this chunk.
                 */
                int index = (int)Math.min(Math.round(lum / steps),
                                          ASCII_CHARS.length - 1);
                char out = ASCII_CHARS[ASCII_CHARS.length - index - 1];
                characters[yIndex][xIndex++] = new Glyph(out,
                                                         new Color(color));
            }
            yIndex++;
        }

        return characters;
    }

    /**
     * For a line of text, this method will constrain that text to the bounds
     * of the source image, assuming that the bounds of the text are calculated
     * from the alpha components of each pixel. The image will be resized to
     * accommodate the maxWidth of the line, so that largest bound is the same.
     *
     * @param source
     * @param text
     * @param maxWidth
     * @return
     */
    public String[] constrainText(BufferedImage source,
                                  String text,
                                  int maxWidth) {
        int width = source.getWidth(),
            height = source.getHeight(),
            scaledHeight = height;

        BufferedImage scaled = resize(source, maxWidth, scaledHeight);

        /*
         * The rightmost bounds for the characters in the output, calculated
         * from the visible characters in the source image. Text will be
         * constrained to these values unless the text overflows the image,
         * in which case, it will be wrapped to maxWidth before breaking.
         */
        int[] bounds = new int[height];
        ArrayList<String> output = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            int line = 0;
            for (int x = 0; x < width; x++) {
                int alpha = scaled.getRGB(x, y) >> 24 & 0xFF;
                if (alpha > 0) line++;
            }
            bounds[y] = line;
        }

        /*
         * TODO: Constrain text to the number of characters per line (given in
         * bounds). Text that overflows should be wrapped to maxWidth.
         */

        return output.toArray(new String[0]);
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

    public static BufferedImage resize(BufferedImage original,
                                       int width,
                                       int height) {
        Image img = original.getScaledInstance(width,
                                               height,
                                               Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(width,
                                                 height,
                                                 BufferedImage.TYPE_INT_ARGB);
        Graphics g = scaled.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return scaled;
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
