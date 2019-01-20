/* 
 * Copyright (C) 2018 Lane W. Surface
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package jtxt.emulator;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

/*
 * You're gonna need some bleach after reading this file. ;)
 */

@SuppressWarnings( "serial" )
class Prompt extends JComponent {
    private String input = "",
                   message = "";
    
    private Color backgroundColor = new Color(230, 230, 230),
                  messageColor = Color.BLACK,
                  textColor = Color.RED;
    
    private boolean ret = false;
    
    public Prompt() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent ke) {
                int kc = ke.getKeyChar();
                
                switch (kc) {
                case KeyEvent.VK_ENTER:
                    ret = true;
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    input = input.substring(0, input.length()-1);
                    repaint();
                    break;
                default:
                    input += ke.getKeyChar()+"";
                    repaint();
                }
            }
        });
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getInput() {
        while (ret == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {}
        }
        ret = false;
        
        String tmp = input;
        input = message = "";
        repaint();
        
        return tmp;
    }
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Antialias text? Redundant....
        
        g.setColor(messageColor);
        g.drawString(message, 2, getHeight()-3);
        
        int offset = getFontMetrics(getFont()).stringWidth(message);
        
        g.setColor(textColor);
        g.drawString(input, offset+2, getHeight()-3);
    }
}
