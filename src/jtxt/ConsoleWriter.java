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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import jtxt.emulator.Region;

/**
 * 
 */
public class ConsoleWriter {
    private final PrintStream output;
    
    public ConsoleWriter(OutputStream outputStream) {
        PrintStream ps = null;
        try {
            ps = new PrintStream(outputStream,
                                 true,
                                 "UTF-8");
            ps.println("\u2588");
        }
        catch (UnsupportedEncodingException e) { ps = System.out; }
        
        output = ps;
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
}
