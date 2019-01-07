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
import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.KeyboardTarget;
import jtxt.emulator.tui.RootContainer;
import jtxt.emulator.tui.SequentialLayout;

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
    public Context context;
    
    /**
     * The window for displaying the buffer to the screen. Used for Java2D
     * abstraction over the text-based application.
     * 
     * @see BufferedFrame
     */
    private JFrame window;
    
    /**
     * The current frame buffer, where all glyphs that are currently available
     * to be rendered are stored.
     */
    private BufferedFrame frame;
    
    /**
     * Used for converting glyphs (character and color values) into a series
     * of pixels that can be individually drawn to the screen. Java provides
     * facilities to perform this process for us, but we may want to render
     * using hardware acceleration.
     */
    private GlyphRasterizer rasterizer;
    
    /**
     * The prompt handles all user-input in the terminal. Separate from the
     * text-area, this object fetches and parses input. There should only be
     * one prompt per instance of a {@code Terminal}.
     * 
     * @see requestInput(String)
     */
    private Prompt prompt;
    
    /**
     * The root container that all components that appear in the terminal
     * belong to. Components that are not added to another container will be
     * direct ancestors of this container.
     * 
     * @see #add(Component)
     */
    private Container root;
    
    /**
     * The current component receiving key events.
     * 
     * @see #focus(Component)
     * @see #focusAt(Location)
     */
    private KeyboardTarget focused;
    
    /**
     * Creates a new instance of {@code Terminal} based on the given 
     * {@code Configuration}'s properties. 
     * 
     * @param context The setting information for the terminal.
     */
    public Terminal(Context context) {
        this.context = context;
        
        window = new JFrame(context.title);
        window.setResizable(true);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = window.getWidth(),
                    height = window.getHeight(),
                    numLines = height / context.charSize.height,
                    lineSize = width / context.charSize.width;
                
                /*
                 * FIXME: Slight rounding errors from using `int` causes the
                 *        layout to wrap slightly slower than the window's
                 *        resized.
                 */
                
                context.setDimensions(numLines,
                                      lineSize,
                                      width,
                                      height);
                if (root != null) update();
            }
        });
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FontMetrics fm = window.getFontMetrics(context.font);
        context.setCharDimensions(fm.charWidth('X'),
                                  fm.getHeight());
        
        frame = new BufferedFrame(context);
        frame.setPreferredSize(context.windowSize);
        frame.setFont(context.font);
        context.subscribe((ResizeSubscriber)frame);
        window.add(frame);
        
        prompt = new Prompt();
        // The prompt spans a single line at the bottom of the window.
        prompt.setPreferredSize(new Dimension(context.windowSize.width,
                                              context.charSize.height));
        prompt.setFont(context.font.deriveFont(Font.BOLD));
        window.add(prompt, java.awt.BorderLayout.SOUTH);
        
        window.pack();
        window.setVisible(true);

        // Warning message sent to the system console when an application is
        // created from the command line.
        System.out.println("Terminal created...\nWARNING: Do not close this " +
                           "window until the application has terminated.");
        
        root = new RootContainer(context,
                                 new SequentialLayout(Axis.X));
        context.subscribe((ResizeSubscriber)root);
    }
    
    /**
     * Gets the root container, which is the parent of all components within
     * the terminal.
     * 
     * @return The root container of this terminal.
     */
    public Container getRootContainer() {
        return root;
    }
    
    /**
     * Redraws all components within the terminal.
     */
    public void update() {
        frame.clear();
        root.draw(frame);
        frame.repaint();
    }
    
    /**
     * Adds the component to the root container.
     * 
     * @param component The component to add to the root container.
     */
    public void add(Component component) {
        root.add(component);
    }
    
    /**
     * Sets the given component as the target for key events.
     * 
     * @param component The component to focus at.
     */
    public void focus(KeyboardTarget component) {
        focused = component;
    }
    
    /**
     * Focuses the component at the given location, and makes it the target for
     * key events.
     * 
     * @param location The location to focus at.
     */
    public void focusAt(Location location) {
        for (Component component : root) {
            Region bounds = component.getBounds();
            if (location.inside(bounds) 
                && component instanceof KeyboardTarget)
            {
                focused = (KeyboardTarget)component;
                break;
            }
        }
        
    }
    
    /**
     * Aids in the construction of terminal instances. The terminal constructor
     * takes a context object as an argument to configure the properties that
     * are subject to be changed; the constructor for the context is quite 
     * complex, with many different signatures that are available. This builder
     * is to assist in creating instances of a terminal without worrying about
     * the underlying context's construction.
     */
    public static class Builder {
        private String title,
                       fontName;
        private int lineSize,
                    numLines,
                    textSize;
        
        public Builder(String title) {
            this.title = title;
            
            /* 
             * Some default settings for the terminal, if they happen to not be
             * provided before it's built.
             */
            fontName = "monospace";
            textSize = 12;
            lineSize = 80;
            numLines = 20;
        }
        
        public Builder font(String fontName) {
            this.fontName = fontName;
            
            return this;
        }
        
        public Builder textSize(int textSize) {
            this.textSize = textSize;
            
            return this;
        }
        
        public Builder dimensions(int lineSize, int numLines) {
            this.lineSize = lineSize;
            this.numLines = numLines;
            
            return this;
        }
        
        public Terminal build() {
            return new Terminal(new Context(title,
                                            lineSize,
                                            numLines,
                                            fontName,
                                            textSize));
        }
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
