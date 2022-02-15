/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RocketSimulation;

import static Graphics.GUIManager.*;
import static RocketSimulation.RocketSimulation.generatePlanetColours;
import static RocketSimulation.RocketSimulation.random;
import static RocketSimulation.SimulationPanel.*;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class Planet {
    private double x,y, speed, size;
    private Color terrainColour, skyColour;
    
    public Planet(double x, double y, double size){
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = (50.0/size) * random.nextDouble();
        Color[] colours = generatePlanetColours();
        terrainColour = colours[0];
        skyColour = colours[1];
    }
    
    public void draw(Graphics2D g){
        double a = getSimulationPanel().getSunAngle();
        int startX = (int) (x + (double)size - (double)FastMath.cos(a)*50);
        int startY = (int) (y + (double)size - (double)FastMath.sin(a)*50);
        int endX = (int) (x + (double)size + (double)FastMath.cos(a)*10);
        int endY = (int) (y + (double)size + (double)FastMath.sin(a)*10);
        y += speed;
        if(y > 1080+size*2){
            y = -size*2;
        }
        Shape shape = new Arc2D.Double(x, y, 
                size*2, size*2, 
                0, 360, 
                Arc2D.OPEN);
        Color parentSkyColour = getTerrainRenderer().getColours()[1];
        parentSkyColour = interpolate(parentSkyColour, new Color(0,0,0), FastMath.sin(((double)getCurrentStep()/getStepsPerDay() * 2* FastMath.PI))*0.5 + 0.5);
        double atmosphericDensity = getTerrainRenderer().getAtmosphericDensity();
        //g.rotate(a, size, size);
        g.setPaint(new GradientPaint(
                   new Point(startX, startY), interpolate(terrainColour, parentSkyColour, (atmosphericDensity*0.25 + 0.75)),
                   new Point(endX,endY), parentSkyColour));
        g.fill(shape);
    }
}
