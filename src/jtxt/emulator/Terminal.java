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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;

import jtxt.emulator.Renderer.RasterType;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.Interactable;
import jtxt.emulator.tui.KeyboardTarget;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

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
public class Terminal implements Container.ChangeListener {
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
     * The component which is used for rendering the rasterized image onto the
     * window.
     */
    private Renderer renderer;
    
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
    protected RootContainer root;
    
    /**
     * The current component receiving key events.
     * 
     * @see #focus(Component)
     * @see #focusAt(Location)
     */
    protected KeyboardTarget focused;
    
    /**
     * A Queue containing all of the mouse events that have occurred since the
     * last update (processing event), which will be either dispatched to their
     * respective component or discarded if the target component isn't
     * {@code Interactable}.
     */
    protected Queue<MouseEvent> mouseEvents;
    
    /**
     * Creates a new instance of {@code Terminal} based on the given 
     * {@code Configuration}'s properties. 
     * 
     * @param context The setting information for the terminal.
     */
    public Terminal(Context context,
                    String title,
                    Color background,
                    Renderer.RasterType rasterType,
                    float transparency) {
        this.context = context;
        
        window = new JFrame(title);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mouseEvents = new LinkedList<>();
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                mouseEvents.add(me);
            }
        });
        
        FontMetrics fm = window.getFontMetrics(context.font);
        context.setCharDimensions(fm.charWidth('X'),
                                  fm.getHeight());
        
        renderer = Renderer.getInstance(context,
                                        background,
                                        transparency,
                                        rasterType);
        renderer.setPreferredSize(context.windowSize);
        window.add(renderer);
        
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
    public RootContainer createRootForLayout(Layout layout) {
        RootContainer container = new RootContainer(context.getBounds(),
                                                    layout);
        context.remove(root);
        root = container;
        context.subscribe(container);
        container.registerListener(this);
        
        /*
         * Create the thread which will listen for updates to state in the
         * terminal.
         */
        Thread poller = new Thread(this::poll);
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
    
    protected void createFrame() {
        BufferedFrame frame = new BufferedFrame(context.getNumberOfLines(),
                                                context.getLineSize());
        root.draw(frame);
        renderer.renderFrame(frame);
    }
    
    /**
     * Polls for changes that can occur frequently and at random times (such
     * as changes to window dimensions, and input events from the keyboard and
     * mouse).
     */
    private void poll() {
        long last = System.currentTimeMillis(),
             msPerUpdate = 1000 / context.updatesPerSecond,
             lag = 0;
        createFrame();
        
        while (true) {
            long now = System.currentTimeMillis(),
                 elapsed = now - last;
            last = now;
            lag += elapsed;

            while (lag >= msPerUpdate) {
                // Make sure that the bounds of the window have not changed.
                int width = renderer.getWidth(),
                    height = renderer.getHeight(),
                    numLines = height / context.charSize.height,
                    lineSize = width / context.charSize.width;
                
                if (numLines != context.getNumberOfLines()
                    || lineSize != context.getLineSize())
                {
                    context.setDimensions(numLines,
                                          lineSize,
                                          width,
                                          height);
                    
                    createFrame();
                }
                
                dispatchMouseEvents();
                
                lag -= msPerUpdate;
            }
            
            renderer.repaint();
        }
    }
    
    /**
     * <P>
     * Removes all mouse events from the event queue, and notifies the
     * respective {@code Component}s that should receive the events if they are
     * {@code Interactable} (meaning they can receive, and respond to, mouse
     * events). The order in which these events are dispatched is the same as
     * the order in which they originally occurred.
     * </P>
     * <P>
     * Additionally, this method may change the actively focused Component
     * within the terminal if any of the Components which received a mouse
     * event and were Interactable requested to be focused by the terminal.
     * </P>
     */
    protected synchronized void dispatchMouseEvents() {
        while (!mouseEvents.isEmpty()) {
            MouseEvent event = mouseEvents.remove();
            int x = event.getX(),
                y = event.getY();
            
            // Determine the location that this event orginated from.
            Location loc = new Location(y / context.charSize.height,
                                        x / context.charSize.width);
            
            for (Component component : root) {
                if (component instanceof Interactable
                    && loc.inside(component.getBounds()))
                {
                    Interactable inter = (Interactable)component;
                    focused = inter.clicked(loc)
                              ? (KeyboardTarget)inter
                              : focused;
                    
                    break;
                }
            }
        }
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
            return new Terminal(new Context(lineSize,
                                            numLines,
                                            fontName,
                                            textSize,
                                            updatesPerSecond),
                                title,
                                background,
                                rasterType,
                                transparency);
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
    
    public void update() {
        createFrame();
    }
}
