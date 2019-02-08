/* 
 * Copyright 2019 Lane W. Surface
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jtxt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import jtxt.emulator.BufferedFrame;
import jtxt.emulator.Region;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

/**
 * Draws Components to an external source, such as a system console or other
 * device which can be written to with an instance of {@code OutputStream}.
 */
public class PlainTextWriter {
    private final PrintStream output;
    
    public PlainTextWriter(OutputStream outputStream) {
        PrintStream ps = null;
        try {
            ps = new PrintStream(outputStream,
                                 true,
                                 "UTF-8");
        }
        catch (UnsupportedEncodingException e) { ps = System.out; }
        
        output = ps;
    }
    
    public BufferedFrame createSuitableFrame() {
        int width = -1,
            height = -1;
        
        output.print("\u001B[s\n" +
                     "\u001B[5000;5000H\n" + 
                     "\u001B[6n\n" +
                     "\u001B[u\n");
        
        try {
            InputStream input = System.in;
            byte[] bytes = new byte[input.available()];
            input.read(bytes,
                       0,
                       bytes.length);
            
            if (bytes.length == 0) {
                output.println("This output device is not capable of " +
                               "rendering the interface... aborting.");
                return null;
            }
            
            output.format("bytes=%s,%n%s",
                          Arrays.toString(bytes),
                          new String(bytes));
        }
        catch (IOException ie) { /* I don't know when this happens. */ }
        
        return new BufferedFrame(new Region(0,
                                            0,
                                            width,
                                            height));
    }
    
    /**
     * Converts and outputs the {@code GlyphBuffer} to the stream which this
     * printer was initialized with. 
     * 
     * @param buffer The buffer to write to the OutputStream.
     */
    public void print(GlyphBuffer buffer) {
        Region bounds = buffer.getBounds();
        int width = bounds.getWidth(),
            height = bounds.getHeight();
         
        for (int line = 0; line < height; line++)
            output.println(buffer.getString(line).getData(0,
                                                          width));
    }

    public void draw(int width,
                     int height,
                     Layout layout,
                     Component... components) {
        Region bounds = new Region(0,
                                   0,
                                   height,
                                   width);
        BufferedFrame buffer = new BufferedFrame(bounds);
        RootContainer root = new RootContainer(bounds,
                                               layout,
                                               components);
        
        root.draw(buffer);
        print(buffer);
    }
}
