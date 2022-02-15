/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphics;

import RocketSimulation.Planet;
import static Graphics.GUIManager.*;
import static RocketSimulation.RocketSimulation.*;
import static RocketSimulation.SimulationPanel.*;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public final class TerrainRenderer {
    
    public static ArrayList<Terrain> terrain = new ArrayList<>(); //[terrain][[x,y],[x,y]...]
    private int terrainWidthRight = (int)(getScreenSize().getWidth() * 1.5);
    private int terrainWidthLeft = (int)(getScreenSize().getWidth() * 1.5);
    private Color terrainColour, skyColour;
    private ArrayList<Planet> planets = new ArrayList<>();
    private double atmosphericDensity;
            
    public void setup(){
        atmosphericDensity = random.nextDouble()*0.5 + 0.5;
        Color[] colours = generatePlanetColours();
        terrainColour = colours[0];
        skyColour  = colours[1];
        
        initialiseTerrain();
        updateTerrain();
        Collections.sort(terrain);
        createPlanets();
    }
    
    public void initialiseTerrain(){
        int avgHeight = random.nextInt(10) + 1;
        int rockiness = random.nextInt(5) + 1;
        int numTracks = random.nextInt(20) + 40;
        int minHeight = 10;
        for(int t = 0; t < numTracks; t ++){
            double maxSteepness = FastMath.min(FastMath.max(random.nextGaussian()*2*rockiness + 5*rockiness, 5), 20*rockiness);
            int step = random.nextInt(20) + 10;
            int maxHeight = random.nextInt(50)*avgHeight + minHeight;
            int steepnessChange = (random.nextInt(40) + 1);
            int chunkSize = (int)(200/step);
            Terrain terrainObj = new Terrain(this, chunkSize, step, maxHeight, steepnessChange, maxSteepness);
            int yPos =random.nextInt(maxHeight - minHeight + 1) + minHeight;
            terrainObj.setLastY(yPos);
            terrainObj.setLastY2(yPos);
            terrain.add(terrainObj);
        }
    }
    
    public void updateTerrain(){
        for(int t = 0; t < terrain.size(); t++){
            while(terrain.get(t).getRightChunks().size()*terrain.get(t).getChunkSize()*terrain.get(t).getStepSize() < terrainWidthRight){
                generateTerrain(t, false);
            }
            while(terrain.get(t).getLeftChunks().size()*terrain.get(t).getChunkSize()*terrain.get(t).getStepSize() < terrainWidthLeft){
                generateTerrain(t, true);
            }
        }
    }
    
    public void generateTerrain(int t, boolean left){
        int minHeight = 10;
        double maxSteepness = terrain.get(t).getMaxSteepness();
        int step = terrain.get(t).getStepSize();
        int maxHeight = terrain.get(t).getMaxHeight();
        int steepnessChange = terrain.get(t).getSteepnessChange();
        int chunkSize = terrain.get(t).getChunkSize();
        
        int xPoint = 0, steepness = 0, maxActiveHeight = 0, currentPoint = 1;;
        int yPoint, terrainCurrentPoint;
        boolean largeRocks;
        
        if(left){
            yPoint = terrain.get(t).getLastY2();
            terrainCurrentPoint = terrain.get(t).getCurrentPoint2();
            largeRocks = terrain.get(t).getLargeRocks2();
        }else{
            yPoint = terrain.get(t).getLastY();
            terrainCurrentPoint = terrain.get(t).getCurrentPoint();
            largeRocks = terrain.get(t).getLargeRocks();
        }
        
        ArrayList<Integer[]> points = points = new ArrayList<>();
        Integer[] startCoords = {0, 0};
        points.add(startCoords);
        
        Integer[] nextCoords = {0, yPoint};
        points.add(nextCoords);
        xPoint += step;
        
        while(currentPoint < chunkSize){
            if(random.nextInt(2000) == 0){
               largeRocks = true;
            }if(random.nextInt(200) == 0){
                largeRocks = false;
            }
            yPoint = (int) FastMath.max(FastMath.min(yPoint + steepness, maxHeight), minHeight);
            steepness = (int)FastMath.min(FastMath.max(steepness + random.nextInt(steepnessChange*2+1) - steepnessChange, -maxSteepness + random.nextInt(20)), maxSteepness - random.nextInt(20));
            if(largeRocks){
                yPoint = (int) FastMath.max(FastMath.min(yPoint + FastMath.cos((double)terrainCurrentPoint/((random.nextInt(10)+10)))*maxSteepness, maxHeight), minHeight);
            }
            Integer[] coords = {xPoint, yPoint};
            points.add(coords);
            xPoint += step; //subtract if going left
            maxActiveHeight = FastMath.max(yPoint, maxActiveHeight);
            currentPoint += 1;
            terrainCurrentPoint += 1;
        }
        //final points in terrain completes the loop
        Integer[] coords = {xPoint, yPoint};
        points.add(coords);

        Integer[] coords2 = {xPoint, 0};
        points.add(coords2);

        Integer[] coords3 = {0, 0};
        points.add(coords3);
        
        if(left){
            terrain.get(t).setLargeRocks2(largeRocks);
            terrain.get(t).setCurrentPoint2(terrainCurrentPoint);
            terrain.get(t).addLeftChunk(points);
            terrain.get(t).setLastY2(yPoint);
            terrain.get(t).maxHeightReached(maxActiveHeight);
            renderTerrain(true, t, terrain.get(t).getLeftChunks().size()-1);
        }else{
            terrain.get(t).setLargeRocks(largeRocks);
            terrain.get(t).setCurrentPoint(terrainCurrentPoint);
            terrain.get(t).addRightChunk(points);
            terrain.get(t).setLastY(yPoint);
            terrain.get(t).maxHeightReached(maxActiveHeight);
            renderTerrain(false, t, terrain.get(t).getRightChunks().size()-1);
        }
    }
    
    public void renderTerrain(boolean left, int t, int n){
        //generate chunk
        Path2D poly = new Path2D.Double();
        poly.moveTo(0, 0);
        if(left){
            for(int p = 0; p < terrain.get(t).getLeftChunks().get(n).size(); p++){
                Integer[] point = terrain.get(t).getLeftChunks().get(n).get(p);
                point[0] = -point[0];
                poly.lineTo((int)point[0], -(int)point[1]);
                terrain.get(t).getLeftChunks().get(n).get(p)[0] = (int)(terrain.get(t).getLeftChunks().get(n).get(p)[0]);
                terrain.get(t).getLeftChunks().get(n).get(p)[1] = (int)(-terrain.get(t).getLeftChunks().get(n).get(p)[1]);
            }
            poly.closePath();
            terrain.get(t).addLeftPolygon(poly);
        }else{
            for(int p = 0; p < terrain.get(t).getRightChunks().get(n).size(); p++){
                Integer[] point = terrain.get(t).getRightChunks().get(n).get(p);
                poly.lineTo((int)point[0], -(int)point[1]);
                terrain.get(t).getRightChunks().get(n).get(p)[0] = (int)(terrain.get(t).getRightChunks().get(n).get(p)[0]);
                terrain.get(t).getRightChunks().get(n).get(p)[1] = (int)(-terrain.get(t).getRightChunks().get(n).get(p)[1]);
            }
            poly.closePath();
            terrain.get(t).addRightPolygon(poly);
        }
    }
    
    public void createPlanets(){
        double width = getScreenSize().getWidth();
        double height = getScreenSize().getHeight();
        for(int i = 0; i < 1; i++){
            Planet planet = new Planet(random.nextInt((int) width),
                    random.nextInt(500), 
                    random.nextInt(800)+200);
            planets.add(planet);
        }
        for(int i = 0; i < 2; i++){
            Planet planet = new Planet(random.nextInt((int) width),
                    random.nextInt(500), 
                    random.nextInt(200)+50);
            planets.add(planet);
        }
    }
    
    public Color[] getColours(){
        Color[] colours = new Color[2];
        colours[0] = terrainColour;
        colours[1] = skyColour;
        return colours;
    } 
    public double getAtmosphericDensity(){
        return atmosphericDensity;
    }
    public int getRightTerrainWidth(){
        return terrainWidthRight;
    }
    public int getLeftTerrainWidth(){
        return terrainWidthLeft;
    }
    public void setRightTerrainWidth(int width){
        this.terrainWidthRight = FastMath.max(width, this.terrainWidthRight);
    }
    
    public void setLeftTerrainWidth(int width){
        this.terrainWidthLeft = FastMath.max(-width, this.terrainWidthLeft);
    }
    
    public ArrayList<Terrain> getTerrain(){
        return terrain;
    }
    public int getIndexOf(Terrain t){
        return terrain.indexOf(t);
    }
    public void draw(Graphics g){
        double width = getScreenSize().getWidth();
        double height = getScreenSize().getHeight();
        Color sky1 = interpolate(skyColour, new Color(0,0,0), FastMath.sin(((double)getCurrentStep()/getStepsPerDay() * 2* FastMath.PI))*0.5 + 0.5);
        Color sky2 = interpolate(new Color(0,0,0), sky1, atmosphericDensity);
        Color terrain1 = interpolate(terrainColour, new Color(0,0,0), FastMath.sin(((double)getCurrentStep()/getStepsPerDay() * 2* FastMath.PI))*0.4 + 0.5);
        Color terrain2 = interpolate(terrain1, brightness(sky1, atmosphericDensity), 0.2);
        Graphics2D g2 = (Graphics2D)g;
        //draw sky
        g2.setPaint(new GradientPaint(
                        new Point(0, 0), sky1,
                        new Point(0, (int)(height)), sky1));
        g2.fillRect(0, 0, 1920, 1080);
        //draw planets
        for(Planet planet: planets){
            planet.draw(g2); 
        }
        //draw terrain
        double scale = getSimulationPanel().getScale();
        int minHeight =  (int) (terrain.get(terrain.size()-1).getY());
        int maxHeight = (int) (terrain.get(0).getY());
        g2.setPaint(new GradientPaint(
                        new Point(0, minHeight + 100), terrain1,
                        new Point(0, maxHeight), terrain2));
        g2.fillRect(0, maxHeight, 1920, 1080);
        for(int i = 0; i < terrain.size(); i++){
            terrain.get(i).draw(g2, terrain.size()-i);
        }
        g2.setPaint(terrain1);
        g2.fillRect(0, minHeight, 1920, 1080);
        
    }
}
