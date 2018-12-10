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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;

import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.KeyboardTarget;

/**
 * <p>
 * Allows for applications that rely on console IO facilities to control the
 * appearance of their text on the screen, as well as providing the ability
 * to dynamically update this text and control the location in which it is
 * placed.
 * </p>
 * 
 * <p>
 * It is important to note that this class does not create an instance of an 
 * operating system's underlying console interface, but rather abstracts 
 * console IO over a Java2D application. Running an application created with
 * this API in an OS terminal will not display it in the terminal, but rather
 * create a new window in which the text of the application will be displayed.
 * </p>
 * 
 * <p>
 * IO should be directed through the methods provided by this class instead 
 * of {@code System.in} and {@code System.out}, as these will display in the
 * underlying terminal rather than the application window. The methods in this
 * class have additional parameters for specifying the location of the text in 
 * the window, as well as special ways of dealing with user-input. Error
 * messages and similar output that is not relevant to the application being
 * displayed can still use the OS terminal for logging.
 * </p>
 */
public final class Terminal {
    /**
     * Holds general information regarding the settings of this instance of the
     * terminal.
     */
    private Context context;
    
    /**
     * Keeps track of the next position to insert text into the terminal when
     * a {@code Location} is not specified.
     * 
     * @see putChar(char)
     * @see putLine(String)
     */
    public Cursor cursor;
    
    /**
     * The window for displaying the console to the screen. Used for Java2D
     * abstraction over the text-based application.
     * 
     * @see BufferedTextPane
     */
    private JFrame window;
    
    /**
     * The {@code BufferedTextPane} is the area that the text is rendered to.
     */
    private BufferedTextPane pane;
    
    /**
     * The prompt handles all user-input in the terminal. Separate from the
     * text-area, this object fetches and parses input. There should only be
     * one prompt per instance of a {@code Terminal}.
     * 
     * @see requestInput(String)
     */
    private Prompt prompt;
    
    /**
     * The root of all components in the terminal. This container will occupy
     * the entire 
     */
    private Container rootContainer;
    
    /**
     * All of the components in this terminal that can be the target of a
     * keyboard event. This list is to aid in the traversal of these
     * components when a traversal key is pressed (usually tab).
     */
    private java.util.List<KeyboardTarget> targets;
    
    /**
     * The target for key events within the terminal.
     * 
     * @see #focus(Component)
     * @see #focusAt(Location)
     */
    private Component focusedComponent;
    
    /**
     * Creates a new instance of {@code Terminal} based on the given 
     * {@code Configuration}'s properties. 
     * 
     * @param context The setting information for the terminal.
     */
    public Terminal(Context context) {
        this.context = context;
        
        cursor = new Cursor();
        
        window = new JFrame(context.title);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FontMetrics fm = window.getFontMetrics(context.font);
        context.setCharDimensions(fm.charWidth('X'),
                                 fm.getHeight());
        
        pane = new BufferedTextPane(context);
        pane.setPreferredSize(context.windowSize);
        pane.setFont(context.font);
        window.add(pane);
        
        prompt = new Prompt();
        // The prompt spans a single line at the bottom of the window.
        prompt.setPreferredSize(new Dimension(context.windowSize.width,
                                              context.charSize.height));
        prompt.setFont(context.font.deriveFont(Font.BOLD));
        window.add(prompt, java.awt.BorderLayout.SOUTH);
        
        window.pack();
        window.setVisible(true);
        
        /*
         * Make sure there are no null characters in the buffer (some fonts
         * don't handle these very well).
         */
        clear();
        
        // Warning message sent to the system console when an application is
        // created from the command line.
        System.out.println("Terminal created...\nWARNING: Do not close this " +
                           "window until the application has terminated.");
    }
    
    /**
     * Get the properties of this {@code Terminal} as a {@code Configuration}.
     * 
     * @return The {@code Configuration} representing the properties of this
     *         {@code Terminal}.
     */
    public Context getContext() {
        return new Context(context);
    }
    
    /**
     * Updates the character at the given position.
     * 
     * @param c The new character.
     * @param l The {@code Location} for the character.
     *            
     * @throws LocationOutOfBoundsException if the line or position is less 
     *                                      than zero, or if the line is 
     *                                      greater than the number of lines,
     *                                      or if the position is greater than
     *                                      the line size of the terminal.
     */
    public void putChar(char c, Location l) {
        if (l.outside(context))
            throw new LocationOutOfBoundsException(l);
        
        pane.update(new Glyph(c, Color.WHITE), l.line, l.position);
        window.repaint();
    }
    
    /**
     * Writes a character to the terminal at the current cursor position.
     * 
     * @param c The character to write.
     */
    public void putChar(char c) {
        putChar(c, cursor.getLocation());
        cursor.goForward(1);
    }
    
    /**
     * Finds the length of the largest word in a string of text.
     * 
     * @param text The text to search.
     * 
     * @return The maximum size of a word in the string of text.
     */
    private int findMaxWordLength(String text) {
        int max = 0;
        
        String[] words = text.split("\\s+");
        
        for (String word : words)
            if (word.length() > max) max = word.length();
        
        return max;
    }
    
    /**
     * <p>
     * Given a string of characters, this method will wrap the characters onto
     * separate lines, based on their position in the line and the specified
     * edge.
     * </p>
     * 
     * <p>
     * <i>Implementation Note</i>: This algorithm is greedy and makes no
     * attempt to balance the distribution of characters between lines.
     * </p>
     * 
     * @param line The line of text to wrap.
     * @param position The position for the first character in the line within
     *                 the terminal's buffer.
     * @param edge The right bounding position for the line.
     * 
     * @return An array of Strings, where each String's length is guaranteed to
     *         be no greater than the amount of characters between the position
     *         of the text and the edge, and spaces between words at the bounds
     *         of a line are discarded.
     */
    private String[] wrapLine(String line, int position, int edge) {
        if (edge >= context.lineSize)
            throw new IllegalArgumentException("The character limit [edge=" +
                                               edge + "] is too large for " +
                                               "the buffer.");
        
        if (position >= edge) 
            throw new IllegalArgumentException("The position cannot be " +
                                               "greater than the edge of " +
                                               "the line.");
        
        int len = line.length();
        
        // The amount of characters that can fit onto each line.
        int room = edge - position;
        
        int maxWordSize = findMaxWordLength(line);
        if (maxWordSize > room)
            throw new IllegalArgumentException("Cannot break the line, as " +
                                               "the word size is too large.");
        
        if (len > room) {
            /* 
             * Keeps track of the index of the first character in the line that
             * still needs to be wrapped. 
             */
            int index = 0;
            
            /*
             * The difference between the number of characters that have been
             * wrapped into lines and the number of characters that still need 
             * to be processed.
             */
            int delta = len;
            
            ArrayList<String> lines = new ArrayList<>();
            
            while (delta > room) {
                for (int i = index + room; i > index; i--) {
                    if (line.charAt(i) == ' ') {
                        lines.add(line.substring(index, i));
                        delta -= ++i - index;
                        index = i;
                        
                        break;
                    }
                }
            }
            lines.add(line.substring(index));
            
            return lines.toArray(new String[lines.size()]);
        }
        
        return new String[] { line };
    }
    
    public void putLines(String[] lines, Location l) {
        AtomicInteger loc = new AtomicInteger(l.line);
        Arrays.stream(lines)
            .forEach(line -> pane.update(line,
                                         loc.getAndIncrement(),
                                         l.position));
        cursor.goDown(lines.length);
        
        window.repaint();
    }
    
    /**
     * Writes a {@code String} to the console.
     * 
     * @param text The string to write.
     * @param l The {@code Location} for the first character in the
     *          {@code String}.
     * @param edge The right-most limit for the text inserted into the buffer.
     * 
     * @throws LocationOutOfBoundsException if the line or position is less
     *                                      than zero, or if the location is
     *                                      greater than the height of the
     *                                      terminal.
     */
    public void putLine(String text, Location l, int edge) {
        if (l.line < 0 || l.position < 0 || l.line >= context.numLines)
            throw new LocationOutOfBoundsException(l);
        
        /* 
         * If the line overflows the edge, wrap the text onto the next line at
         * the specified Location's position.
         */
        String[] lines = wrapLine(text, l.position, edge);
        
        if (l.line + lines.length >= context.numLines)
            throw new LocationOutOfBoundsException("The line was too big to " +
                                                   "wrap at line " + l.line);
        
        putLines(lines, l);
    }
    
    /**
     * Inserts text into the terminal at the specified Location. Text that
     * overflows the end of the line will be wrapped onto the next line at
     * the same position as the first line. (Therefore, all text in the String
     * will be left-aligned.)
     * 
     * @param text The text to insert into the terminal.
     * @param l The location at which to place the first character of the text.
     */
    public void putLine(String text, Location l) {
        putLine(text, l, context.lineSize-1);
    }
    
    /**
     * Writes a string to the console at the current cursor position.
     * 
     * @param line The string to write.
     */
    public void putLine(String text) {
        putLine(text, cursor.getLocation());
        cursor.goForward(text.length());
    }
    
    public void putLine(Glyph[] glyphs) {
        for (int i = 0; i < glyphs.length; i++) {
            pane.update(glyphs[i], cursor.getLine(), cursor.getPosition());
            cursor.goForward(1);
        }
        cursor.setLocation(cursor.getLine()+1, 0);
    }
    
    /**
     * Writes a string to the console at the current cursor position and
     * advances the cursor position down by one line.
     * 
     * @param text The string to write.
     */
    public void putNewLine(String text) {
        putLine(text, cursor.getLocation());
        cursor.goDown(1);
    }
    
    public void clear() {
        for (int row = 0; row < context.numLines; row++)
            for (int col = 0; col < context.lineSize; col++)
                pane.update(new Glyph(' ', Color.WHITE), row, col);
        
        cursor.setLocation(0, 0);
    }
    
    /**
     * Allows for a user to input text into the prompt. This method returns
     * once the enter key is pressed.
     * 
     * @param msg The message to show to the user in the prompt. This message
     *            will be followed by a separator character, after which the
     *            editable part of the prompt will follow.
     * 
     * @return A string containing the inputed text.
     */
    public String requestInput(String msg) { 
        prompt.requestFocus();
        prompt.setMessage(msg);
        prompt.repaint();
        
        return prompt.getInput();
    }
}
