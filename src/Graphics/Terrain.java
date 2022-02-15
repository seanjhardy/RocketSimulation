/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphics;

import static Graphics.GUIManager.*;
import static RocketSimulation.SimulationPanel.*;
import static Graphics.TerrainRenderer.*;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class Terrain implements Comparable<Terrain>{
    private TerrainRenderer parent;
    private ArrayList<Path2D> rightPolygons = new ArrayList<>();
    private ArrayList<ArrayList<Integer[]>> rightChunks = new ArrayList<>();
    
    private ArrayList<Path2D> leftPolygons = new ArrayList<>();
    private ArrayList<ArrayList<Integer[]>> leftChunks = new ArrayList<>();
    private double y, maxSteepness;
    private int maxHeightReached, currentPoint;
    private int chunkSize, stepSize, maxHeight, steepnessChange, lastY;
    private boolean largeRocks;
    
    private int currentPoint2, lastY2;
    private boolean largeRocks2;
    
    public Terrain(TerrainRenderer parent, int chunkSize, int step, 
            int maxHeight, int steepnessChange, double maxSteepness){
        this.parent = parent;
        
        this.chunkSize = chunkSize;
        this.stepSize = step;
        this.maxHeight = maxHeight;
        this.steepnessChange = steepnessChange;
        this.maxSteepness = maxSteepness;
    }
    
    public ArrayList<ArrayList<Integer[]>> getRightChunks(){
        return rightChunks;
    }
    public ArrayList<ArrayList<Integer[]>> getLeftChunks(){
        return leftChunks;
    }
    public void addRightChunk(ArrayList<Integer[]> points){
        this.rightChunks.add(points);
    }
    public void addLeftChunk(ArrayList<Integer[]> points){
        this.leftChunks.add(points);
    }
    public void addLeftPolygon(Path2D poly){
        this.leftPolygons.add(poly);
    }
    public void addRightPolygon(Path2D poly){
        this.rightPolygons.add(poly);
    }
    public ArrayList<Path2D> getLeftPolygons(){
        return leftPolygons;
    }
    public ArrayList<Path2D> getRightPolygons(){
        return rightPolygons;
    }

    public void setY(int y){
        this.y = y;
    }
    public void setLastY(int y){
        this.lastY = y;
    }
    public void maxHeightReached(int maxHeightReached){
        this.maxHeightReached = FastMath.max(maxHeightReached, this.maxHeightReached);
    }
    public void setCurrentPoint(int p){
        this.currentPoint = p;
    }
    public void setLargeRocks(boolean l){
        this.largeRocks = l;
    }
    
    public void setLastY2(int y){
        this.lastY2 = y;
    }
    public void setCurrentPoint2(int p){
        this.currentPoint2 = p;
    }
    public void setLargeRocks2(boolean l){
        this.largeRocks2 = l;
    }
    
    public int getY(){
        return (int)(y);
    }
    public int getLastY(){
        return (int) lastY;
    }
    
    public int getStepSize(){
        return stepSize;
    }
    public int getMaxHeight(){
        return maxHeight;
    }
    public int getSteepnessChange(){
        return steepnessChange;
    }
    public double getMaxSteepness(){
        return maxSteepness;
    }
    public int getCurrentPoint(){
        return currentPoint;
    }
    public boolean getLargeRocks(){
        return largeRocks;
    }
    public int getMaxHeightReached(){
        return maxHeightReached;
    }
    public int getChunkSize(){
        return chunkSize;
    }
    
    public int getLastY2(){
        return lastY2;
    }
    public int getCurrentPoint2(){
        return currentPoint2;
    }
    public boolean getLargeRocks2(){
        return largeRocks2;
    }
    
    public void draw(Graphics2D g , double i){
        Color[] colours = parent.getColours();
        colours[0] = interpolate(colours[0], new Color(0,0,0), FastMath.sin(((double)getCurrentStep()/getStepsPerDay() * 2* FastMath.PI))*0.4 + 0.5);
        colours[1] = interpolate(colours[1], new Color(0,0,0), FastMath.sin(((double)getCurrentStep()/getStepsPerDay() * 2* FastMath.PI))*0.2 + 0.5);
        Color terrainColour = interpolate(colours[0], brightness(colours[1], parent.getAtmosphericDensity()), (i/(terrain.size()+10)));
        g.setPaint(terrainColour);
        double scale = getSimulationPanel().getScale();
        double size = (double)chunkSize*stepSize;
        double parallaxConstant = FastMath.pow(i,1);
        AffineTransform atrans, atrans2;
        atrans2 = AffineTransform.getScaleInstance(scale,scale);
        double cameraX = (int)(getSimulationPanel().getCameraX());
        double cameraY = (int)(getSimulationPanel().getCameraY());
        
        double[] startCoords = inversePositions(0, cameraY,
                cameraX/(parallaxConstant), cameraY/(parallaxConstant), scale);
        
        double[] endCoords = inversePositions(getScreenSize().getWidth() + size, cameraY,
                cameraX/(parallaxConstant), cameraY/(parallaxConstant), scale);
        
        //end = calculate(chunkID*size)
        int leftStartID = (int)(FastMath.max(0, -endCoords[0]/(size) - 1));
        int leftEndID = (int)(FastMath.min(getLeftPolygons().size(), -startCoords[0]/(size) + 1));
        
        int rightStartID = (int)(FastMath.max(0, startCoords[0]/(size) - 1));
        int rightEndID = (int)(FastMath.min(getRightPolygons().size(), endCoords[0]/(size) + 1));
        
        double[] coords = {0,0};
        
        for(int chunk = leftStartID; chunk < leftEndID; chunk++){
            coords = newPositions(-size*chunk, 0, cameraX/(parallaxConstant), -cameraY/(parallaxConstant), scale);
            atrans = AffineTransform.getTranslateInstance(coords[0], coords[1]);
            //change its coordinates to shape
            Shape s = getLeftPolygons().get(chunk).createTransformedShape(atrans2);
            s = atrans.createTransformedShape(s);
            g.fill(s);
        }
        
        for(int chunk = rightStartID; chunk < rightEndID; chunk++){
            coords = newPositions(size*chunk, 0, cameraX/(parallaxConstant), -cameraY/(parallaxConstant), scale);
            atrans = AffineTransform.getTranslateInstance(coords[0], coords[1]);
            //change its coordinates to shape
            Shape s = getRightPolygons().get(chunk).createTransformedShape(atrans2);
            s = atrans.createTransformedShape(s);
            g.fill(s);
        }
        setY((int) coords[1]);
    }

    @Override
    public int compareTo(Terrain u) {
        if(getMaxHeightReached() < u.getMaxHeightReached()){
            return 1;
        }else if(getMaxHeightReached() > u.getMaxHeightReached()){
            return -1;
        }
        return 0;
    }
    
}
