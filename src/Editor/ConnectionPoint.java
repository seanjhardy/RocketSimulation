/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import static Graphics.GUIManager.rotatePoint;

public class ConnectionPoint {
    private int x, y;
    private PartInstance parent;
    private int connectState = 0; //0 unconnected, 1 connected to parent, 2 connected to child
    
    public ConnectionPoint(int x, int y){
       this.x = x;
       this.y = y;
    }
    
    public ConnectionPoint(PartInstance parent, int x, int y){
       this.parent = parent;
       this.x = x;
       this.y = y;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getConnectState(){
        return connectState;
    }
    public void rotate(double n){
        double[] coords = rotatePoint(x,y,0,0,n);
        this.x = (int) coords[0];
        this.y = (int) coords[1];
    }
    public PartInstance getParent(){
        return parent;
    }
     
    public void setConnectState(int a){
        connectState = a;
        
    }
    
    public ConnectionPoint createPointInstance(PartInstance parent){
        return new ConnectionPoint(parent, x, y);
    }
    
}
