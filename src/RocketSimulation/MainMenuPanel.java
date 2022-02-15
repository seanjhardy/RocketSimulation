/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RocketSimulation;

import Graphics.GUIManager;
import Graphics.AdvancedButton;
import static Graphics.GUIManager.*;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

/**
 *
 * @author s-hardy
 */
public class MainMenuPanel extends JPanel{
    public GUIManager parent;
    public AdvancedButton exitBtn, simulationPanelBtn, editorPanelBtn;
    
    public MainMenuPanel(GUIManager parent){
        this.parent = parent;
        createComponents();
        setBackground(new Color(16, 29, 48));
    }
    
    public void createComponents(){
        exitBtn = new AdvancedButton(getImage("ExitIcon"));
        exitBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == exitBtn){
                System.exit(0);
            }
        });
        exitBtn.setFont(new Font(getDefaultFont(), 1, 12));
        exitBtn.setForeground(WHITE);
        exitBtn.setColour(new Color(204, 54, 43));
        exitBtn.addBorder(5, "down");
        add(exitBtn);
        
        simulationPanelBtn = new AdvancedButton("Simulation Panel");
        simulationPanelBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == simulationPanelBtn){
                setCurrentPanel("simulationPanel");
            }
        });
        simulationPanelBtn.setFont(new Font(getDefaultFont(), 1, 16));
        simulationPanelBtn.setForeground(WHITE);
        simulationPanelBtn.setColour(new Color(86, 164, 227));
        simulationPanelBtn.addBorder(5);
        add(simulationPanelBtn);
        
        editorPanelBtn = new AdvancedButton("Editor Panel");
        editorPanelBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == editorPanelBtn){
                setCurrentPanel("editorPanel");
            }
        });
        editorPanelBtn.setFont(new Font(getDefaultFont(), 1, 16));
        editorPanelBtn.setForeground(WHITE);
        editorPanelBtn.setColour(new Color(86, 164, 227));
        editorPanelBtn.addBorder(5);
        add(editorPanelBtn);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setBounds();
    }
    
    public void setBounds(){
        double width = parent.getScreenSize().getWidth();
        double height = parent.getScreenSize().getHeight();
        exitBtn.setBounds((int) (width-60),0,60,50);
        simulationPanelBtn.setBounds((int) (width/2-150), (int) (height/2 - 200),300,75);
        editorPanelBtn.setBounds((int) (width/2-150), (int) (height/2),300,75);
    }
}
