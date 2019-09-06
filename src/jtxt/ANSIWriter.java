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

import jtxt.emulator.Region;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Draws Components to an external source, such as a system console or other device
 * which can be written to with an instance of {@code OutputStream}.
 */
public class ANSIWriter implements DrawableSurface {
  private final PrintStream output;

  public ANSIWriter(OutputStream outputStream) {
    output = new PrintStream(
      outputStream,
      true,
      StandardCharsets.UTF_8);
  }

  Region getConsoleDimensions() {
    int w, h;

    w = -1;
    h = -1;
    output.print("\u001B[s\n" +
                 "\u001B[5000;5000H\n" +
                 "\u001B[6n\n" +
                 "\u001B[u\n");

    try {
      InputStream input = System.in;
      byte[] bytes = new byte[input.available()];
      input.read(
        bytes,
        0,
        bytes.length);

      if (bytes.length == 0) {
        output.println("This output device is not capable of " +
                       "rendering the interface... aborting.");
        return null;
      }

      output.format(
        "bytes=%s,%n%s",
        Arrays.toString(bytes),
        new String(bytes));
    } catch (IOException ie) { /* I don't know when this happens. */ }

    return new Region(
      0,
      0,
      h,
      w);
  }

  @Override
  public void draw(GlyphBuffer buffer) {
    int w, h;
    Region bounds = buffer.getBounds();
    w = bounds.getWidth();
    h = bounds.getHeight();

    for (int line = 0; line < h; line++)
      output.println(buffer.getString(line).getData(
        0,
        w));
  }
}
