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

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jtxt.emulator.tui.Axis;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.Interactable;
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
public class Terminal implements ResizeSubscriber,
                                 Container.ChangeListener {
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
     * The next frame that needs to be rendered to the window. This frame could
     * possibly change before it's rasterized, and so it's not guaranteed that
     * all frames that are created will be rendered (especially in the case
     * where the dimensions of the window change quickly, such as when it's
     * resized).
     */
    protected BufferedFrame activeFrame;
    
    /**
     * Used for converting glyphs (character and color values) into a series
     * of pixels that can be individually drawn to the screen. Java provides
     * facilities to perform this process for us, but we may want to render
     * using hardware acceleration.
     */
    protected GlyphRasterizer rasterizer;
    
    /**
     * The component which is used for rendering the rasterized image onto the
     * window.
     */
    private JComponent painter;
    
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
    
    // Temporary variable to indicate if we can paint yet.
    private boolean ready;
    
    private final BufferedImage screen;
    
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
        
        activeFrame = new BufferedFrame(context.getNumberOfLines(),
                                        context.getLineSize());
        rasterizer = new SWRasterizer(context);
        
        BufferedImage scr = null;
        try {
            Robot r = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension bounds = tk.getScreenSize();
            scr = r.createScreenCapture(new Rectangle(0,
                                                         0,
                                                         bounds.width,
                                                         bounds.height));
        } catch (AWTException awtex) { }
        this.screen = scr;
        
        painter = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                
                if (!ready) return;
                
                /*
                 * Rasterize the frame and paint it onto the screen. This
                 * image may be resized slightly to avoid whitespace/gaps from
                 * appearing at the edges of the terminal whenever it's resized
                 * quickly.
                 */
                Graphics2D graphics = (Graphics2D)g;
                
                int width = getWidth(),
                    height = getHeight();

                Point location = getLocationOnScreen();
                int startX = location.x,
                    startY = location.y,
                    endX = startX + width,
                    endY = startY + height;
                graphics.drawImage(screen,
                                   0,
                                   0,
                                   width,
                                   height,
                                   startX,
                                   startY,
                                   endX,
                                   endY,
                                   null);
                
                Composite comp = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    0.6f
                );
                graphics.setComposite(comp);
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0,
                                  0,
                                  getWidth(),
                                  getHeight());
                graphics.drawRenderedImage(rasterizer.rasterize(activeFrame),
                                           null);
            }
        };
        painter.setPreferredSize(context.windowSize);
        window.add(painter);
        
        root = new RootContainer(context.getBounds(),
                                 new SequentialLayout(Axis.X));
        context.subscribe(root);
        
        prompt = new Prompt();
        // The prompt spans a single line at the bottom of the window.
        prompt.setPreferredSize(new Dimension(context.windowSize.width,
                                              context.charSize.height));
        prompt.setFont(context.font.deriveFont(Font.BOLD));
        window.add(prompt, java.awt.BorderLayout.SOUTH);
        
        window.pack();
        window.setVisible(true);
        
        Thread poller = new Thread(this::poll);
        poller.start();
        
        /////// BAD - LEAKY REFERENCE ////////
        root.registerListener(this);
    }
    
    public Context getContext() {
        return new Context(context);
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
     * This sets the root container of the terminal; however, do note that
     * calling this method means that all components which have been added to
     * the old root container will be discarded. (This is primarily because
     * some information which these components are constructed with are not
     * necessarily compatible with this object.)
     * 
     * @param root The new root container in the terminal.
     */
    public void setRootContainer(RootContainer root) {
        context.remove(this.root);
        context.subscribe(root);
        root.registerListener(this);
        this.root = root;
    }
    
    /**
     * Polls for changes that can occur frequently and at random times (such
     * as changes to window dimensions, and input events from the keyboard and
     * mouse).
     */
    private void poll() {
        context.subscribe(this);
        
        long last = System.currentTimeMillis(),
             msPerUpdate = 1000 / context.updatesPerSecond,
             lag = 0;
        
        while (true) {
            long now = System.currentTimeMillis(),
                 elapsed = now - last;
            last = now;
            lag += elapsed;

            while (lag >= msPerUpdate) {
                // Make sure that the bounds of the window have not changed.
                int width = window.getWidth(),
                    height = window.getHeight(),
                    numLines = height / context.charSize.height,
                    lineSize = width / context.charSize.width;
                
                if (numLines != context.getNumberOfLines()
                    || lineSize != context.getLineSize())
                {
                    context.setDimensions(numLines,
                                          lineSize,
                                          width,
                                          height);
                }
                
                dispatchMouseEvents();
                update();
                
                lag -= msPerUpdate;
            }
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
                    textSize,
                    updatesPerSecond;
        
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
        
        public Terminal build() {
            return new Terminal(new Context(title,
                                            lineSize,
                                            numLines,
                                            fontName,
                                            textSize,
                                            updatesPerSecond));
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

    @Override
    public void resize(int lines, int lineSize) {
        activeFrame = new BufferedFrame(lines, lineSize);
        
        /* 
         * We need to redraw all of the components now that the dimensions of
         * the interface have changed, and notify the renderer that we are
         * ready for an update.
         */
        update();
    }

    @Override
    public void update() {
        root.draw(activeFrame);
        ready = true;
        painter.repaint();
    }
}
