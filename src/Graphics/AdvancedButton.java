/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphics;

import static Graphics.GUIManager.*;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 *
 * @author s-hardy
 */
public class AdvancedButton extends JButton {
    private Color baseColour, shadowColour;
    private int state = 0;
    private BufferedImage icon;
    private boolean iconButton = false;
    private int direction;
    
    public AdvancedButton(String text) {
        super(text);
        Border raisedBorder = BorderFactory.createRaisedBevelBorder();
        setBorder(raisedBorder);
    }
    
    public AdvancedButton(BufferedImage icon) {
        super("");
        this.icon = icon;
        iconButton = true;
        Border raisedBorder = BorderFactory.createRaisedBevelBorder();
        setBorder(raisedBorder);
    }
    
    public void setColour(Color c1){
        setContentAreaFilled(false);
        setFocusPainted(false);
        this.baseColour = c1;
        this.shadowColour = brightness(c1, 0.5);
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                state = 1;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                state = 2;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                state = 0;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                state = 1;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                state = 0;
            }
        });
    }
    
    public void addBorder(int width){
        AdvancedBevelBorder raisedBorder = new AdvancedBevelBorder(this, width);
        setBorder(raisedBorder);
    }
    
    public void addBorder(int width, String direction){
        if(direction.equals("up")){
            this.direction = 0;
        }if(direction.equals("down")){
            this.direction = 1;
        }if(direction.equals("left")){
            this.direction = 2;
        }if(direction.equals("right")){
            this.direction = 3;
        }
        AdvancedBevelBorder raisedBorder = new AdvancedBevelBorder(this, width, this.direction);
        setBorder(raisedBorder);
    }
    
    public Color getColour(){
        if(state == 0){
            return baseColour;
        }else if(state == 1){
            return brightness(baseColour,1.5);
        }else{
            return brightness(baseColour,5);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if(baseColour != null){
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(this.getColour());
            g2.fillRect(0, 0, getWidth(), getHeight());
            if(iconButton && icon != null){
                g2.drawImage(icon, 
                        getWidth()/2 -icon.getWidth()/2,
                        getHeight()/2 -icon.getHeight()/2, null);
            }
            g2.dispose();
        }
        super.paintComponent(g);
    }
}