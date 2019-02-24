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
package jtxt.emulator;

import jtxt.DrawableSurface;
import jtxt.Terminal;
import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Interactable;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 */
public class EmulatedTerminal extends Terminal {
    protected JFrame window;

    protected EventDispatcher dispatcher;

    private Color background;

    private Font font;

    protected int charWidth,
                  charHeight;

    private float transparency;

    public EmulatedTerminal(String title,
                            int width,
                            int height,
                            String fontName,
                            int size,
                            Color background,
                            float transparency) {
        super(width, height);

        this.background = background;
        this.transparency = transparency;

        window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        font = new Font(fontName,
                        Font.PLAIN,
                        size);
        FontMetrics fm = window.getFontMetrics(font);
        charWidth = fm.getMaxAdvance();
        charHeight = fm.getHeight() - fm.getLeading();

        surface = createDrawableSurface(width,
                                        height);
        window.pack();
        window.setVisible(true);
    }

    public void generateClickForComponentAt(int line, int position) {
        Component component = getComponentAt(line, position);
        if (component == null) return;

        Interactable interactable = (Interactable)component;
        interactable.clicked(new Location(line,
                                          position));
    }

    @Override
    public RootContainer createRootContainer(Layout layout) {
        super.createRootContainer(layout);
        
        Thread poller = new Thread(dispatcher);
        poller.start();

        return root;
    }

    @Override
    protected DrawableSurface createDrawableSurface(int width, int height) {
//        BitmapFont font = null;
//        try {
//            Path path = Paths.get(
//                    ClassLoader.getSystemResource("dejavu-sans-mono-256.bmp")
//                               .toURI()
//            );
//            charWidth = 8;
//            charHeight = 15;
//            font = new BitmapFont(path,
//                                  charWidth,
//                                  charHeight,
//                                  32,
//                                  256);
//        }
//        catch (URISyntaxException urisex) { /* 0.0 */ }

        Renderer renderer = Renderer.getInstance(font,
                                                 charWidth,
                                                 charHeight,
                                                 background,
                                                 transparency);
        renderer.setPreferredSize(new Dimension(charWidth * width,
                                                charHeight * height));
        window.add(renderer);
        
        dispatcher = new EventDispatcher(this, renderer);
        window.addMouseListener(dispatcher);
        
        return renderer;
    }
}
