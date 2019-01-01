/* 
 * Copyright 2018 Lane W. Surface 
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
package jtxt.emulator;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import jtxt.emulator.util.InitializationReader;

/**
 * Encapsulates the properties of an instance of {@code Terminal}. Simple 
 * rendering hints, like the font size and family, as well as the number of
 * lines and columns in the terminal are specified using a 
 * {@code Configuration} object, and passed to the {@code Terminal} 
 * constructor.
 */
public class Context implements ResizeSubject {
    /**
     * The title of the application window.
     */
    public final String title;
    
    /**
     * The number of characters per line in the text-pane.
     */
    private int lineSize;
    
    /**
     * The number of lines in the text-pane.
     */
    private int numLines;
    
    /**
     * The font used for rendering text in the window.
     */
    public final Font font;
    
    /**
     * The size of a character. This is based on the font specified in the
     * {@code Configuration} constructor. These need to be set by a 
     * {@code Terminal} instance since they rely on java.awt.FontMetrics.
     */
    Dimension charSize;
    
    /**
     * The size of the text-pane. This is calculated from the size of a glyph
     * and the number of lines and columns.
     * 
     * @see setCharDimensions(int, int)
     */
    Dimension windowSize;

    /**
     * All of the objects which wish to be notified when the dimensions of the
     * text interface are changed.
     */
    private List<ResizeSubscriber> resizeSubscribers;

    /**
     * Constructs a new {@code Configuration} object with properties identical
     * to config.
     * 
     * @param context The {@code Configuration} to replicate.
     */
    public Context(Context context) {
        this(context.title, context.lineSize, context.numLines, 
             context.font.getName(), context.font.getSize());
        /*
         *  Make sure the dimensions are copied into the new context; this
         *  is used for returning context instance from the terminal, and
         *  rendering is based on how these may be defined.
         */
        charSize = context.charSize;
        windowSize = context.windowSize;
    }

    /**
     * @param title The tile that will appear at the top of the window.
     * @param lineSize The number of characters available on each line.
     * @param numLines The number of lines in the terminal.
     * @param fontName The name of the font to use for rendering the text in
     *                 the terminal.
     * @param fontSize The size of the terminal's font.
     */
    public Context(String title, int lineSize, int numLines,
                   String fontName, int fontSize) {
        this.title = title;
        this.lineSize = lineSize;
        this.numLines = numLines;
        font = new Font(fontName, Font.PLAIN, fontSize);
        
        resizeSubscribers = new ArrayList<>();
    }

    /**
     * Constructs a configuration for the given initialization file. See the
     * documentation for details about property values and their respective
     * format in the initialization file.
     * 
     * @param fileName An initialization file for this context, given
     *                 appropriate key=value pairs for the emulator.
     */
    public Context(String fileName) {
        InitializationReader reader = new InitializationReader(fileName);
        title = reader.getValue("title");
        lineSize = Integer.parseInt(reader.getValue("num_chars_x"));
        numLines = Integer.parseInt(reader.getValue("num_chars_y"));
        
        int height = Integer.parseInt(reader.getValue("text_size"));
        String fontName = reader.getValue("font");
        font = new Font(fontName, Font.PLAIN, height);
    }

    /**
     * Sets the dimensions of the font and calculates the size of the window
     * based on the number of lines, line size, and the font dimensions.
     * 
     * @param charWidth The width of a character based on {@link #font}
     * @param charHeight The height of a character based on {@link #font}.
     */
    void setCharDimensions(int charWidth, int charHeight) {
        charSize = new Dimension(charWidth, charHeight);
        
        int w = lineSize * charWidth,
            h = numLines * charHeight;
        windowSize = new Dimension(w, h);
    }

    public Dimension getCharacterDimensions() {
        return charSize;
    }

    public Dimension getWindowDimensions() {
        return windowSize;
    }

    @Override
    public void subscribe(ResizeSubscriber subscriber) {
        resizeSubscribers.add(subscriber);
    }

    @Override
    public void remove(ResizeSubscriber subscriber) {
        resizeSubscribers.remove(subscriber);
    }

    @Override
    public void resized() {
        for (ResizeSubscriber subscriber : resizeSubscribers)
            subscriber.resize(numLines, lineSize);
    }
    
    public void setNumberOfLines(int numLines) {
        this.numLines = numLines;
        windowSize.height = numLines * charSize.height;
        resized();
    }
    
    public int getNumberOfLines() {
        return numLines;
    }
    
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
        windowSize.width = lineSize * charSize.width;
        resized();
    }
    
    public int getLineSize() {
        return lineSize;
    }
}
