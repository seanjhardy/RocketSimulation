/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import static Editor.PartManager.*;
import Graphics.AdvancedButton;
import static Graphics.GUIManager.*;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

/**
 *
 * @author seanjhardy
 */
public class PartType {
    private int x,y,width,height;
    private int partID;
    private String type;
    private AdvancedButton selectBtn;
    
    public PartType(EditorPanel editorPanel, String type, int partID){
        this.partID = partID;
        this.type = type;
        createComponents(editorPanel);
    }
    
    public void createComponents(EditorPanel editorPanel){
        selectBtn = new AdvancedButton(getImage(type + "Icon"));
        selectBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == selectBtn){
                setCurrentPart(partID);
                editorPanel.getPartManager().updateCurrentItems();
                editorPanel.getPartManager().setSortState(0);
            }
        });
        selectBtn.setForeground(WHITE);
        selectBtn.setColour(new Color(45, 115, 186));
        selectBtn.addBorder(2);
        editorPanel.add(selectBtn);
    }
    
    public int getPartID(){
        return this.partID;
    }
    public String getType(){
        return this.type;
    }
    
    public void draw(Graphics2D g){
        //draw button
        selectBtn.setBounds(x,y,width,height);
    }
    
    public void setCoords(double x, double y, double width, double height){
        this.x = (int) x;
        this.y = (int) y;
        this.width = (int) width;
        this.height = (int) height;
    }
    
}
