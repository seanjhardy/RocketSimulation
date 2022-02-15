/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Graphics.AdvancedButton;
import static Graphics.GUIManager.*;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import javax.swing.JPanel;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class Part implements Comparable<Part>{
    private static int partIconSize = 100;
    private ArrayList<ConnectionPoint> connectionPoints = new ArrayList<>();
    private BufferedImage image, resizedImage;
    private PartType partType;
    private int x = 0, y = 0;
    private int cost, size, level, mass, autonomy, crewSize;
    private double powerUsage, powerStorage;
    private boolean staging;
    private String name, group;
    private AdvancedButton selectBtn;
    
    //initialisation methods
    public Part(EditorPanel editorPanel, String name){
        this.name = name;
        createComponents(editorPanel);
    }
    
    public void createComponents(EditorPanel editorPanel){
        this.image = getImage(name);
        if(image != null){
            resizeImage(1);
        }
        
        BufferedImage icon = this.image;
        if(icon != null){
            double scale = FastMath.max((double)icon.getWidth()/(partIconSize-20), (double)icon.getHeight()/(partIconSize-20));
            int newWidth = (int)((double)icon.getWidth()/scale);
            int newHeight = (int)((double)icon.getHeight()/scale);
            icon = resize(icon, newWidth, newHeight);
        }
        
        selectBtn = new AdvancedButton(icon);
        selectBtn.addMouseListener(new MouseAdapterImpl(editorPanel, this));

        selectBtn.setForeground(WHITE);
        selectBtn.setColour(new Color(45, 115, 186));
        selectBtn.addBorder(5);
        PartManager.getItemPanel().add(selectBtn);
    }
    
    //drawing methods
    public void draw(Graphics2D g, int x, int y){
        this.x = x;
        this.y = y;
        selectBtn.setBounds(x, y, partIconSize, partIconSize);
    }
    public void resizeImage(double scale){
        if(image != null){
            this.resizedImage = resize(this.image, (double)this.image.getWidth()*scale, (double)this.image.getHeight()*scale);
        }
    }
    public void drawImage(Graphics2D g, PartInstance p, double x, double y, double scale, double rotation, boolean connected){
        BufferedImage rotatedImage = resizedImage;
        if(rotation != 0){
            rotatedImage = rotate(resizedImage, rotation);
        }
        if(!connected){
            g.setComposite(getAlphaComposite());
        }
        g.drawImage(rotatedImage, (int)x - rotatedImage.getWidth()/2,
                                  (int)y - rotatedImage.getHeight()/2, null);
        g.setComposite(getNormalComposite());
        if(!getEditorPanel().getPartManager().isSelectionEmpty()){
            int cameraX = (int) getEditorPanel().getCameraX();
            int cameraY = (int) getEditorPanel().getCameraX();
            double[] coords = inversePositions(x, y, cameraX, cameraY, scale);
            g.setColor(new Color(43, 142, 120, 100));
            if(p != null){
                for(ConnectionPoint connection: p.getConnectionPoints()){
                    if(connection.getConnectState() == 0){
                        double[] newCoords = newPositions(coords[0] + connection.getX(), 
                                coords[1] + connection.getY(), 
                                cameraX, cameraY, scale);
                        int radius = (int) (scale*5);
                        g.fillOval((int)(newCoords[0]) - radius, 
                                   (int)(newCoords[1]) - radius, 
                                   radius*2, radius*2);
                    }
                }
            }
        }
    }
    public void drawIcon(Graphics2D g, double x, double y, double xSize, double ySize){
        BufferedImage newImage = resize(image, xSize, ySize);
        g.drawImage(newImage, (int)x - newImage.getWidth()/2, (int)y - newImage.getHeight()/2, null);
    }
    public void drawStageIcon(Graphics2D g, int x, int y, int size, boolean highlighted){
        BufferedImage image = getImage(group + "StageIcon");
        if(image != null){
            g.drawImage(image, x, y, null);
            if(highlighted){
                g.setColor(new Color(255,255,255,200));
                g.setStroke(new BasicStroke(3));
                g.drawRect(x,y,image.getWidth(),image.getHeight());
                g.setStroke(new BasicStroke(1));
            }
        }
    }
    
    //setter methods
    public void setType(String t){
        this.group = t;
    }
    public void setPartType(PartType t){
        this.partType = t;
    }
    public void setCost(int c){
        this.cost = c;
    }
    public void setMass(int m){
        this.mass = m;
    }
    public void setType(PartType type){
        this.partType = type;
    }
    public void setStaging(boolean s){
        staging = s;
    }
    public void setConnectionPoints(String connections){
        String[] data = connections.split(":");
        if(data.length == 4){
            if(Integer.parseInt(data[0]) == 1){
                connectionPoints.add(new ConnectionPoint(0, -getImageHeight()/2));
            }if(Integer.parseInt(data[1]) == 1){
                connectionPoints.add(new ConnectionPoint(getImageWidth()/2, 0));
            }if(Integer.parseInt(data[2]) == 1){
                connectionPoints.add(new ConnectionPoint(0, getImageHeight()/2));
            }if(Integer.parseInt(data[3]) == 1){
                connectionPoints.add(new ConnectionPoint(-getImageWidth()/2, 0));
            }
        }
    }
    public void setPowerUsage(double p){
        powerUsage = p;
    }
    public void setPowerStorage(double s){
        powerStorage = s;
    }
    public void setAutonomy(int a){
        this.autonomy = a;
    }
    public void setCrewSize(int c){
        this.crewSize = c;
    }
    
    //getter methods
    public BufferedImage getIcon(int newSize){
        double scale = FastMath.max((double)getImageWidth()/(newSize), (double)getImageHeight()/(newSize));
        int newWidth = (int)((double)getImageWidth()/scale);
        int newHeight = (int)((double)getImageHeight()/scale);
        
        BufferedImage icon = new BufferedImage(newSize + 10, newSize + 10, BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = (Graphics2D) icon.getGraphics();
        BufferedImage newImage = resize(image, newWidth, newHeight);
        
        g.setColor(new Color(86, 255, 240, 200));
        g.setStroke(new BasicStroke(3));
        //g.drawRect(0, 0, newSize+10, newSize+10);
        g.setStroke(new BasicStroke(1));
        
        g.drawImage(newImage, newSize/2 - newImage.getWidth()/2 + 5, newSize/2 - newImage.getHeight()/2 + 5, null);
        g.dispose();
        return icon;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getImageWidth(){
        return resizedImage.getWidth();
    }
    public int getImageHeight(){
        return resizedImage.getHeight();
    }
    public PartType getPartType(){
        return partType;
    }
    public static int getPartIconSize(){
        return partIconSize;
    }
    public ArrayList<ConnectionPoint> getConnectionPoints(){
        return this.connectionPoints;
    }
    public String getName(){
        return name;
    }
    public String getGroup(){
        return group;
    }
    public int getCost(){
        return cost;
    }
    public int getLevel(){
        return level;
    }
    public int getMass(){
        return mass;
    }
    public boolean hasStaging(){
        return staging;
    }
    public double getPowerStorage(){
        return powerStorage;
    }
    public double getPowerUsage(){
        return powerUsage;
    }
    public int getAutonomy(){
        return autonomy;
    }
    public int getCrewSize(){
        return crewSize;
    }
    
    @Override
    public int compareTo(Part o) {
        String sortType = getEditorPanel().getPartManager().getSortType();
        int sortState = getEditorPanel().getPartManager().getSortState();
        if(sortType.equals("Cost")){
            return ((Integer)cost).compareTo(o.cost) * sortState;
        }else if(sortType.equals("Mass")){
            return ((Integer)mass).compareTo(o.mass) * sortState;
        }else if(sortType.equals("Level")){
            return ((Integer)level).compareTo(o.level) * sortState;
        }else if(sortType.equals("Type")){
            return ((Integer)partType.getPartID()).compareTo(o.partType.getPartID()) * sortState;
        }else{
            return name.compareTo(o.name) * sortState;
        }
    }

    private class MouseAdapterImpl extends MouseAdapter {

        private final EditorPanel editorPanel;
        private final Part partType;

        public MouseAdapterImpl(EditorPanel editorPanel, Part part) {
            this.editorPanel = editorPanel;
            this.partType = part;
        }

        public void mousePressed(MouseEvent mouseEvent) {
            int modifiers = mouseEvent.getModifiers();
            if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                int mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
                int mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
                int nextPartID = editorPanel.getPartManager().getNextPartID();
                PartManager partManager = editorPanel.getPartManager();
                for(int i = 0; i < editorPanel.getSelection().size(); i++){
                    editorPanel.getSelection().get(i).removePartInstance();
                }
                partManager.resetSelection();
                
                PartInstance part = new PartInstance(partType, nextPartID,
                        mouseX, mouseY);
                partManager.addToSelection(part);
                partManager.addPartInstance(part);
            }else if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                editorPanel.getEditorMenu().showPartData(partType);
            }
        }
    }
}
