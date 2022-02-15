/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Editor.Part;
import static Graphics.GUIManager.*;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author seanjhardy
 */
public class PartInstance {
    private boolean visited = false, connected = false;
    private Part partType;
    private PartInstance parent;
    private ArrayList<ConnectionPoint> connectionPoints = new ArrayList<>();
    private HashMap<ConnectionPoint, PartInstance> children;
    private int x = 0, y = 0;
    private int rotation = 0;
    
    public PartInstance(Part parent, int partID, int x, int y){
        this.children = new HashMap<>();
        this.partType = parent;
        this.x = x;
        this.y = y;
        for(ConnectionPoint p : parent.getConnectionPoints()){
            connectionPoints.add(p.createPointInstance(this));
        }
        if(parent.hasStaging()){
            getEditorPanel().getEditorMenu().addStageItem(this);
        }
    }
    
    public void update(Graphics2D g, boolean connected){
        if(connected){
            getEditorPanel().getEditorMenu().addCost(partType.getCost());
            getEditorPanel().getEditorMenu().addMass(partType.getMass());
        }
        this.connected = connected;
        double cameraX = getEditorPanel().getCameraX();
        double cameraY = getEditorPanel().getCameraY();
        double scale = getEditorPanel().getScale();
        double[] coords = newPositions(x,y,cameraX, cameraY, scale);
        if(coords[0] + partType.getImageWidth() > 0 || coords[0] < getScreenSize().getWidth() ||
           coords[1] + partType.getImageHeight() > 0 || coords[1] < getScreenSize().getHeight() ){
            partType.drawImage(g, this, coords[0], coords[1], scale, rotation, connected); 
        }
        if(connected){
            for (Map.Entry<ConnectionPoint, PartInstance> entry : children.entrySet()) {
		PartInstance child = entry.getValue();
                child.update(g, connected);
            }
        }
        visited = true;
    }
    
    public Part getPartType(){
        return partType;
    }
    public PartInstance getParent(){
        return parent;
    }
    
    public void rotate(int centreX, int centreY, int n){
        this.rotation += n;
        double[] coords = rotatePoint(this.x,this.y,
                centreX,centreY,n);
        this.x = (int) coords[0];
        this.y = (int) coords[1];
        for(ConnectionPoint p: connectionPoints){
            p.rotate(n);
        }
        for (Map.Entry<ConnectionPoint, PartInstance> entry : children.entrySet()) {
            PartInstance child = entry.getValue();
            child.rotate(centreX, centreY, n);
        }
    }
    public void setX(double x){
        for (Map.Entry<ConnectionPoint, PartInstance> entry : children.entrySet()) {
            PartInstance child = entry.getValue();
            child.setX(child.getX() + (x - this.x));
        }
        this.x = (int) x;
    }
    public void setY(double y){
        for (Map.Entry<ConnectionPoint, PartInstance> entry : children.entrySet()) {
            PartInstance child = entry.getValue();
            child.setY(child.getY() + (y - this.y));
        }
        this.y = (int) y;
    }
    public void removePartInstance(){
        
        for (Map.Entry<ConnectionPoint, PartInstance> entry : children.entrySet()) {
            PartInstance child = entry.getValue();
            child.removePartInstance();
        }
        getEditorPanel().getPartManager().removePartInstance(this);
    }
    public void setVisited(boolean v){
        visited = v;
    }
    public void addChild(ConnectionPoint connectionPoint, PartInstance part){
        this.children.put(connectionPoint, part);
    }
    public void removeChild(ConnectionPoint connection){
        children.remove(connection);
    }
    
    public void addAvaliableConnections(){
        for(ConnectionPoint point: connectionPoints){
            if(point.getConnectState() == 1){
                point.setConnectState(0);
            }
        }
        if(parent != null){
            ConnectionPoint connectionToRemove = null;
            for (Map.Entry<ConnectionPoint, PartInstance> entry : parent.getChildren().entrySet()) {
                ConnectionPoint connectionPoint = entry.getKey();
                PartInstance child = entry.getValue();
                if(child == this){
                    connectionPoint.setConnectState(0);
                    connectionToRemove = connectionPoint;
                }
            }
            if(connectionToRemove != null){
                parent.removeChild(connectionToRemove);
            }
        }
        parent = null;
    }
    public void setParent(PartInstance p){
        this.parent = p;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public boolean getVisited(){
        return visited;
    }
    public HashMap<ConnectionPoint, PartInstance> getChildren(){
        return children;
    }
    public boolean isConnected(){
        return connected;
    }
    
    public ArrayList<ConnectionPoint> getConnectionPoints(){
        return connectionPoints;
    }
    
    public boolean contains(int xPoint, int yPoint){
        double cameraX = getEditorPanel().getCameraX();
        double cameraY = getEditorPanel().getCameraY();
        double scale = getEditorPanel().getScale();
        double[] coords = newPositions(x, y, cameraX, cameraY, scale);
        return (xPoint < coords[0] + partType.getImageWidth()/2 && xPoint > coords[0] - partType.getImageWidth()/2 && 
                yPoint < coords[1] + partType.getImageHeight()/2 && yPoint > coords[1] - partType.getImageHeight()/2);
    }
    
    public boolean isOverlapping(PartInstance p) {
        double cameraX = getEditorPanel().getCameraX();
        double cameraY = getEditorPanel().getCameraY();
        double scale = getEditorPanel().getScale();
        double[] c = newPositions(x,y, cameraX, cameraY, scale);
        double[] c2 = newPositions(p.getX(),p.getY(), cameraX, cameraY, scale);
        if (c[1] - partType.getImageHeight()/2 > c2[1] + p.getPartType().getImageHeight()/2
          || c[1] + partType.getImageHeight()/2 < c2[1] - p.getPartType().getImageHeight()/2) {
            return false;
        }
        if (c[0] - partType.getImageWidth()/2 > c2[0] + p.getPartType().getImageWidth()/2
          || c[0] + partType.getImageWidth()/2 < c2[0] - p.getPartType().getImageWidth()/2) {
            return false;
        }
        return true;
    }
    
}
