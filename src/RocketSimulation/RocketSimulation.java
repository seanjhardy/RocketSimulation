 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RocketSimulation;

import Graphics.GUIManager;
import static Graphics.GUIManager.*;
import java.awt.Color;
import java.util.Random;
import net.jafama.FastMath;

/**
 *
 * @author s-hardy
 */
public class RocketSimulation {
    private static GUIManager frame;
    public static Random random = new Random();
    
    public static void main(String[] args){
        RocketSimulation main = new RocketSimulation();
    }
    
    public RocketSimulation(){
        frame = new GUIManager();
    }
    
    public static Color[] generatePlanetColours(){
        Color[] colours = new Color[2];
        int R = random.nextInt(50);
        int G = random.nextInt(50);
        int B = random.nextInt(50);
        colours[0] = new Color(R,G,B);
        double difference = 0;
        int R2=0,G2=0,B2=0;
        while(difference < 200){
            R2 = random.nextInt(255);
            G2 = random.nextInt(255);
            B2 = random.nextInt(255);
            difference = FastMath.sqrt(FastMath.pow(R2-R,2) + FastMath.pow(G2-G,2) + FastMath.pow(B2-B,2));
        }
        colours[1] = new Color(R2,G2,B2);
        colours[1] = interpolate(colours[1], new Color(90, 170, 219), getTerrainRenderer().getAtmosphericDensity()*0.3);
        colours[1] = brightness(colours[1], 2);
        return colours;
    } 
    
}
