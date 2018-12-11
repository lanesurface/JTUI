package jtxt.emulator.util.image;

import java.util.HashMap;

/**
 * Defines the way in which color information should be preserved when 
 * converting from the source image to the output. Different sampling
 * strategies have implementations that are provided as a part of this 
 * interface.
 */
@FunctionalInterface
public interface ColorSamplingStrategy {
    /**
     * Given a region of pixels (which we will refer to as a chunk), this
     * method should return a color that appropriately represents the colors
     * in that chunk. An image that is constructed from these samples should
     * retain some level of resemblance to the original image.
     * 
     * @param colors The two-dimensional array of colors in the region that is
     *               being sampled, where a pixel at (x,&nbsp;y) is in the
     *               array at the indices colors[y][x].
     * 
     * @return An integer between 0-255 that represents the sampled color of
     *         this chunk of the image.
     */
    int sample(int[][] colors);
    
    /**
     * Samples an image such that each pixel of the output is equal to the most
     * frequently occurring color in the region of the source image that is
     * being sampled. This strategy gives the best results for images that have
     * many colors, but may lead to loss of detail in the output.
     */
    public static final ColorSamplingStrategy MODAL = 
            new ColorSamplingStrategy() {
        @Override
        public int sample(int[][] colors) {
            HashMap<Integer, Integer> occurences = new HashMap<>();
            int mode = 0,
                maxOcc = -1;
            
            for (int row = 0; row < colors.length; row++) {
                for (int col = 0; col < colors[row].length; col++) {
                    int color = colors[row][col];
                    
                    Integer occurence = occurences.get(color);
                    
                    if (occurence == null) {
                        occurence = 1;
                    }
                    
                    if (occurence > maxOcc) {
                        mode = color;
                        maxOcc = occurence;
                    }
                    occurences.put(color, occurence+1);
                }
            }
            
            return mode;
        }
    };
    
    /**
     * The values of the pixels in the region being sampled are averaged
     * together to determine output color. This strategy usually gives better
     * results for images that have very few colors and tends to preserve
     * details in the output.
     */
    public static final ColorSamplingStrategy AVG = 
            new ColorSamplingStrategy() {
        @Override
        public int sample(int[][] colors) {
            int avg = -1;
            
            for (int y = 0; y < colors.length; y++)
                for (int x = 0; x < colors[y].length; x++)
                    avg += colors[y][x];
            avg /= colors.length * colors[0].length;
            
            return avg;
        }
    };
}
