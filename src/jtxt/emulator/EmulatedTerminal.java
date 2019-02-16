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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JFrame;

import jtxt.DrawableSurface;
import jtxt.Term;
import jtxt.emulator.tui.Layout;
import jtxt.emulator.tui.RootContainer;

/**
 * 
 */
public class EmulatedTerminal extends Term {
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
        font = new Font(fontName,
                        Font.PLAIN,
                        size);
        FontMetrics fm = window.getFontMetrics(font);
        charWidth = fm.getMaxAdvance();
        charHeight = fm.getHeight() - fm.getLeading();
        
        surface = createDrawableSurface(width,
                                        height);
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
        Renderer renderer = Renderer.getInstance(font,
                                                 charWidth,
                                                 charHeight,
                                                 background,
                                                 transparency);
        renderer.setPreferredSize(new Dimension(charWidth * width,
                                                charHeight * height));
        window.add(renderer);
        
        dispatcher = new EventDispatcher(null, // FIXME: Compatible interfaces
                                         renderer);
        window.addMouseListener(dispatcher);
        
        return renderer;
    }
}
