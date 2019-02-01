/* 
 * Copyright 2018, 2019 Lane W. Surface 
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

import javax.swing.JFrame;

import jtxt.emulator.Renderer.RasterType;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.ComponentObserver;
import jtxt.emulator.tui.Interactable;
import jtxt.emulator.tui.KeyboardTarget;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

/**
 * <P>
 * A class for displaying TUI {@code Component}s on the screen. Components
 * which are interactive may use this class to receive input from the mouse
 * and keyboard.
 * </P>
 * 
 * <P>
 * This is not a real terminal, and does not expose the underlying system's
 * command interface. The aim of this class is to present Components as if they
 * are being displayed in a terminal. It should be noted then that an 
 * application started from the system command line will open a new window in
 * which it will be displayed.
 * </P>
 * 
 * <P>
 * Before Components can be added to an instance of this class, the terminal
 * must be initialized with a {@code Layout} for the {@code RootContainer}.
 * This RootContainer is a Container at the top of the Component hierarchy in
 * the terminal (meaning that all Components displayed in a terminal window are
 * an ancestor of this Container in the Component tree). A typical
 * instantiation of a terminal might look something like this:
 * 
 * <PRE>
 *  Terminal terminal = new Terminal.Builder()
 *                                  // ...
 *                                  .dimensions(40, 40)
 *                                  .build();
 *  terminal.createRootContainer(new ALayoutOfSomeKind());
 *  // Create some Components here...
 *  terminal.add(component1,
 *               component2,
 *               component3);
 * </PRE>
 * 
 * Note that Components (including other Containers) can be added at any time
 * to the interface, and this interface will update automatically. Nothing
 * needs to be done on the client-side, aside from initializing the terminal,
 * creating the Components, and adding them to the terminal (where they will
 * be direct children of the RootContainer) or adding them to another Container
 * which has been added to the terminal.
 * </P>
 */
public class Terminal implements ComponentObserver {
    /**
     * Holds general information regarding the settings of this instance of the
     * terminal.
     */
    protected Context context;
    
    /**
     * The window for displaying the buffer to the screen. Used for Java2D
     * abstraction over the text-based application.
     * 
     * @see BufferedFrame
     */
    protected JFrame window;
    
    /**
     * Uses the given render settings to rasterize and display frames in the
     * window.
     */
    private Renderer renderer;
    
    /**
     * The prompt handles all user-input in the terminal. Separate from the
     * text-area, this object fetches and parses input. There should only be
     * one prompt per instance of a {@code Terminal}.
     * 
     * @see #requestInput(String)
     */
    private Prompt prompt;
    
    /**
     * The root container that all components that appear in the terminal
     * belong to. Components that are not added to another container will be
     * direct ancestors of this container.
     * 
     * @see #add(Component[])
     */
    protected RootContainer root;
    
    /**
     * Handles all events that may occur at random (and unpredictable) times
     * within the terminal. These events are usually user-generated.
     */
    private EventDispatcher dispatcher;
    
    /**
     * The current component receiving key events.
     * 
     * @see #focus(KeyboardTarget)
     * @see #focusAt(Location)
     */
    protected KeyboardTarget focused;
    
    /**
     * Creates a new instance of {@code Terminal} based on the given 
     * {@code Context}'s properties. 
     * 
     * @param context The setting information for the terminal.
     * @param title The title of the terminal window.
     * @param background The color of the background which appears being the
     *                   text.
     * @param transparency A normalized value between zero and one, which will
     *                     determine how much of the screen is visible behind
     *                     the window, as well as the visibility of the text.
     * @param rasterType The type of rasterizer to use for rendering text to
     *                   the window. This should not make much noticeable
     *                   difference in appearance, though it may impact
     *                   performance of an application.
     */
    public Terminal(Context context,
                    String title,
                    Color background,
                    float transparency,
                    Renderer.RasterType rasterType) {
        this.context = context;
        
        window = new JFrame(title);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FontMetrics fm = window.getFontMetrics(context.font);
        context.setCharDimensions(fm.charWidth('X'),
                                  fm.getHeight());
        
        renderer = Renderer.getInstance(context,
                                        background,
                                        transparency,
                                        rasterType);
        renderer.setPreferredSize(context.windowSize);
        window.add(renderer);
        
        dispatcher = new EventDispatcher(this,
                                         renderer);
        window.addMouseListener(dispatcher);
        
        prompt = new Prompt();
        // The prompt spans a single line at the bottom of the window.
        prompt.setPreferredSize(new Dimension(context.windowSize.width,
                                              context.charSize.height));
        prompt.setFont(context.font.deriveFont(Font.BOLD));
        window.add(prompt, java.awt.BorderLayout.SOUTH);
        
        window.pack();
        window.setVisible(true);
    }
    
    public Context getContext() {
        return new Context(context);
    }
    
    /**
     * Constructs and returns a new {@code RootContainer} using this terminal
     * as the context, and the given layout as the super layout of all
     * {@code Component}s within the terminal.
     * 
     * @param layout The layout to use for placing Components within this
     *               terminal.
     * 
     * @return A new {@code RootContainer} which has been constructed for this
     *         terminal with the given layout.
     */
    public RootContainer createRootContainer(Layout layout) {
        RootContainer container = new RootContainer(context.getBounds(),
                                                    layout);
        context.remove(root);
        root = container;
        context.subscribe(container);
        container.registerObserver(this);
        
        /*
         * Create the thread which will listen for updates to state in the
         * terminal.
         */
        Thread poller = new Thread(dispatcher);
        poller.setDaemon(true);
        poller.start();
        
        return container;
    }
    
    /**
     * Gets the root container, which is the parent of all components within
     * the terminal.
     * 
     * @return The root container of this terminal.
     */
    public RootContainer getRoot() {
        return root;
    }
    
    int getCharWidth() { return context.charSize.width; }
    
    int getCharHeight() { return context.charSize.height; }
    
    /**
     * Adds the components to the root container.
     * 
     * @param components The components to add to the root container.
     */
    public void add(Component... components) {
        root.add(components);
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
    
    public KeyboardTarget getFocusedComponent() {
        return focused;
    }
    
    protected void clickComponent(Location location) {
        for (Component component : root) {
            if (component instanceof Interactable
                && location.inside(component.getBounds()))
            {
                Interactable icomp = (Interactable)component;
                focused = icomp.clicked(location)
                          ? (KeyboardTarget)icomp
                          : focused;
                
                break;
            }
        }
    }
    
    /**
     * Aids in the construction of new instance of a {@code Terminal}. Various
     * properties can be passed into the methods of this builder, and the
     * terminal will be constructed with a call to {@link Builder#build()}.
     */
    public static class Builder {
        private String title,
                       fontName;
        
        private int lineSize,
                    numLines,
                    textSize,
                    updatesPerSecond;
        
        private Color background;
        
        private Renderer.RasterType rasterType;
        
        private float transparency;
        
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
            updatesPerSecond = 60;
            background = Color.BLACK;
            rasterType = RasterType.SOFTWARE;
            transparency = 0.8f;
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
        
        public Builder ups(int updatesPerSecond) {
            this.updatesPerSecond = updatesPerSecond;
            
            return this;
        }
        
        public Builder background(Color background) {
            this.background = background;
            
            return this;
        }
        
        public Builder rasterType(Renderer.RasterType rasterType) {
            this.rasterType = rasterType;
            
            return this;
        }
        
        public Builder transparency(float transparency) {
            this.transparency = transparency;
            
            return this;
        }
        
        public Terminal build() {
            Context context = new Context(lineSize,
                                          numLines,
                                          fontName,
                                          textSize,
                                          updatesPerSecond);
            return new Terminal(context,
                                title,
                                background,
                                transparency,
                                rasterType);
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
    
    /**
     * Redraws all {@code Component}s within the root container to a new frame,
     * and then renders that frame to the screen.
     */
    @Override
    public void update() {
        BufferedFrame frame = new BufferedFrame(context.getBounds());
        root.draw(frame);
        renderer.renderFrame(frame,
                             context.getWidth(),
                             context.getHeight());
    }
}
