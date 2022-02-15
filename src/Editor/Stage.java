/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Graphics.AdvancedButton;
import Graphics.GUIManager;
import static Graphics.GUIManager.brightness;
import static Graphics.GUIManager.getEditorPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class Stage {
    private static Color backgroundColor = new Color(250, 113, 40, 150);
    private static int stageItemSize = 40;
    private int x = 0, y = 0, width = 0, height = 0;
    private ArrayList<PartInstance> stageItems = new ArrayList<>();
    private AdvancedButton addStageBtn, removeStageBtn;
    
    public Stage(EditorPanel editorPanel){
        createComponents(editorPanel);
    }
    
    public void createComponents(EditorPanel editorPanel){
        Font font = new Font(GUIManager.getDefaultFont(), Font.BOLD, 18);
        
        addStageBtn = new AdvancedButton("+");
        addStageBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == addStageBtn){
                int stageIndex = getEditorPanel().getEditorMenu().getStages().indexOf(this);
                editorPanel.getEditorMenu().addStage(stageIndex+1);
            }
        });
        addStageBtn.setFont(font);
        addStageBtn.setForeground(WHITE);
        addStageBtn.setColour(new Color(204, 54, 43));
        addStageBtn.addBorder(2);
        editorPanel.add(addStageBtn);
        
        removeStageBtn = new AdvancedButton("-");
        removeStageBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == removeStageBtn){
                removeStage(false);
            }
        });
        removeStageBtn.setFont(font);
        removeStageBtn.setForeground(WHITE);
        removeStageBtn.setColour(new Color(204, 54, 43));
        removeStageBtn.addBorder(2);
        editorPanel.add(removeStageBtn);
    }
    
    public void draw(Graphics2D g, int x, int y){
        this.x = x;
        this.y = y;
        int validParts = 0;
        for(PartInstance stageItem: stageItems){
            if(stageItem.isConnected()){
                validParts += 1;
            }
        }
        
        this.height = 35 + (stageItemSize+5)*FastMath.max(validParts,1);
        this.width = 60;
        g.setStroke(new BasicStroke(4));
        g.setColor(new Color(200, 200, 200, 150));
        g.drawRoundRect(x,y,60,height,10,10);
        g.setStroke(new BasicStroke(1));
        
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x,y,60,height,10,10);
        
        g.setColor(backgroundColor);
        g.fillRoundRect(x,y,60,30,10,10);
        
        g.setColor(brightness(backgroundColor,1.5));
        g.fillRoundRect(x,y,60,15,10,10);
        
        int partY = y + 35;
        for(PartInstance stageItem: stageItems){
            if(stageItem.isConnected()){
                g.setColor(new Color(100,100,100,100));
                g.fillRect(x+10, partY, stageItemSize, stageItemSize);
                
                g.setStroke(new BasicStroke(4));
                g.setColor(new Color(200,200,200,100));
                g.drawRect(x+10, partY, stageItemSize, stageItemSize);
                g.setStroke(new BasicStroke(1));
                
                boolean highlighted = (getEditorPanel().getEditorMenu().getSelectedStageItem() == stageItem);
                stageItem.getPartType().drawStageIcon(g, x + 10, partY, stageItemSize, highlighted);
                partY += (stageItemSize+5);
            }
        }
        if(stageItems.isEmpty()){
            partY += (stageItemSize+5);
        }
        g.setColor(new Color(255, 255,255));
        String index = Integer.toString(getEditorPanel().getEditorMenu().getStages().indexOf(this));
        g.drawString(index, x+10, y+20);
        addStageBtn.setBounds(x-25,y+5, 20,20);
        removeStageBtn.setBounds(x-25,partY-20,20,20);
    }
    
    public void addStageItem(PartInstance p){
        stageItems.add(p);
    }
    
    public void addStageItem(int i, PartInstance p){
        stageItems.add(i, p);
    }
    
    public ArrayList<PartInstance> getStageItems(){
        return stageItems;
    }
    
    public static int getStageItemSize(){
        return stageItemSize;
    }
    public void removeStageItem(PartInstance p){
        stageItems.remove(p);
    }
    public void removeStage(boolean forceRemove){
        if((stageItems.isEmpty() && getEditorPanel().getEditorMenu().getStages().size() > 1) || forceRemove){
            int stageIndex = getEditorPanel().getEditorMenu().getStages().indexOf(this);
            getEditorPanel().getEditorMenu().removeStage(stageIndex);
            getEditorPanel().remove(addStageBtn);
            getEditorPanel().remove(removeStageBtn);
        }
    }
    public PartInstance getClickedItem(int mouseX, int mouseY){
        int partY = y + 30 + (stageItemSize+5)/2;
        int minDistance = 100;
        PartInstance closestPart = null;
        for(PartInstance p : stageItems){
            int distance = (int) FastMath.hypot(partY - mouseY, 0);
            partY += (stageItemSize+5);
            if(distance < minDistance){
                minDistance = distance;
                closestPart = p;
            }
        }
        return closestPart;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
}
