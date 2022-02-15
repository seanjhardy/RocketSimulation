/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RocketSimulation;

import Graphics.GUIManager;
import Graphics.AdvancedButton;
import static Graphics.GUIManager.*;
import static RocketSimulation.RocketSimulation.*;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import net.jafama.FastMath;

/**
 *
 * @author s-hardy
 */
public class SimulationPanel extends JPanel implements MouseWheelListener{
    private GUIManager parent;
    private AdvancedButton exitBtn;
    private int cameraMove = 80;
    private boolean up, down, left, right;
    private double cameraX = 0, cameraY = 0, scale = 1;
    private double sunAngle = random.nextDouble()*FastMath.PI/2 + FastMath.PI/2;
    private static int step = 0, stepsPerDay = 2000;
    
    public SimulationPanel(GUIManager parent){
        this.parent = parent;
        createComponents();
        setBindings();
        setBackground(new Color(16, 29, 48));
        addMouseWheelListener(this);
    }
    
    public void createComponents(){
        exitBtn = new AdvancedButton(getImage("ExitIcon"));
        exitBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == exitBtn){
                setCurrentPanel("mainMenuPanel");
            }
        });
        exitBtn.setForeground(WHITE);
        exitBtn.setColour(new Color(204, 54, 43));
        exitBtn.addBorder(5, "down");
        add(exitBtn);
    }
    public void setBindings(){
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "UP1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "DOWN1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "LEFT1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "RIGHT1");
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "UP2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "DOWN2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "LEFT2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "RIGHT2");
        am.put("UP1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               up = true;
            }   
        });
        am.put("DOWN1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               down = true;
            }   
        });
        am.put("LEFT1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               left = true;
            }   
        });
        am.put("RIGHT1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               right = true;
            }   
        });
        am.put("UP2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               up = false;
            }   
        });
        am.put("DOWN2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               down = false;
            }   
        });
        am.put("LEFT2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               left = false;
            }   
        });
        am.put("RIGHT2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               right = false;
            }   
        });
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        step += 1;
        calculateCameraMovement();
        setBounds();
        getTerrainRenderer().draw(g);
        repaint();
    }
    
    public void calculateCameraMovement(){
        if(up){
            cameraY += cameraMove/scale;
        }if(down){
            if(cameraY > 0){
                cameraY -= cameraMove/scale;
            }else{
                cameraY = 0;
            }
        }if(left){
            cameraX -= cameraMove/scale;
        }if(right){
            cameraX += cameraMove/scale;
        }
        if(right || left){
            getTerrainRenderer().setRightTerrainWidth((int) ((cameraX + getScreenSize().getWidth()/2)/scale));
            getTerrainRenderer().setLeftTerrainWidth((int) ((cameraX - getScreenSize().getWidth()/2)/scale));
            if(cameraX < getTerrainRenderer().getRightTerrainWidth() || cameraX > getTerrainRenderer().getLeftTerrainWidth()){
                getTerrainRenderer().updateTerrain();
            }
        }
    }
    
    public void setBounds(){
        exitBtn.setBounds((int) (parent.getScreenSize().getWidth()-60),0,60,50);
    }
    public double getCameraY(){
        return cameraY;
    }
    public double getCameraX(){
        return cameraX;
    }
    public double getScale(){
        return scale;
    }
    
    public double getSunAngle(){
        return sunAngle;
    }
    public static int getCurrentStep(){
        return step;
    }
    public static int getStepsPerDay(){
        return stepsPerDay;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            if(scale * 1.5 < 10){
                scale *= 1.5;
            }
        }else if(notches > 0){
            if(scale / 1.5 > 0.25){
                scale /= 1.5;
                getTerrainRenderer().setRightTerrainWidth((int) ((cameraX + getScreenSize().getWidth()/2)/scale));
                getTerrainRenderer().setLeftTerrainWidth((int) ((cameraX - getScreenSize().getWidth()/2)/scale));
                if(cameraX < getTerrainRenderer().getRightTerrainWidth() || cameraX > getTerrainRenderer().getLeftTerrainWidth()){
                    getTerrainRenderer().updateTerrain();
                }
            }
        }
    }
}
