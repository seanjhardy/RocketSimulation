 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphics;

import Editor.EditorPanel;
import RocketSimulation.SimulationPanel;
import RocketSimulation.MainMenuPanel;
import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import net.jafama.FastMath;

/**
 *
 * @author s-hardy
 */
public class GUIManager extends JFrame{
    private static CardLayout layoutController;
    private static JPanel mainPanel;
    private static String currentPanel = "main";
    private static MainMenuPanel mainMenuPanel;
    private static SimulationPanel simulationPanel;
    private static EditorPanel editorPanel;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static File imageDirectory = new File("images");
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static final String font = "Russo One";
    private static TerrainRenderer terrainRenderer;
    
    private static AlphaComposite alphaComposite, normalComposite;

    //initialisation
    public GUIManager(){
        super("Rocket simulator");
        loadImages();
        createPanels();
        setFrameProperties();
        int rule = AlphaComposite.SRC_OVER;
        alphaComposite = AlphaComposite.getInstance(rule , 0.5f);
        normalComposite = AlphaComposite.getInstance(rule , 1f);
    }
    public void createPanels(){ 
        
        mainMenuPanel = new MainMenuPanel(this);
        simulationPanel = new SimulationPanel(this);
        editorPanel = new EditorPanel(this);
        editorPanel.getPartManager().updateCurrentItems();
        //editorPanel.add(ItemPanel);
        
        terrainRenderer = new TerrainRenderer();
        terrainRenderer.setup();
        layoutController = new CardLayout();
        mainPanel = new JPanel(layoutController);
        
        //This componentListener allows the panel to
        //dynamically resize every widget when the frame changes shape
        mainPanel.addComponentListener(new ComponentAdapter() {  
            public void componentResized(ComponentEvent evt) {
                switch (currentPanel) {
                    case "mainMenuPanel":
                        mainMenuPanel.revalidate();
                        mainMenuPanel.repaint();
                        break;
                    case "simulationPanel":
                        simulationPanel.revalidate();
                        simulationPanel.repaint();
                        break;
                    case "editorPanel":
                        editorPanel.revalidate();
                        editorPanel.repaint();
                        break;
                    default:
                        break;
                }
            }
        });
        layoutController.addLayoutComponent(mainMenuPanel, "mainMenuPanel");
        mainPanel.add(mainMenuPanel);
        layoutController.addLayoutComponent(simulationPanel, "simulationPanel");  
        mainPanel.add(simulationPanel);
        layoutController.addLayoutComponent(editorPanel, "editorPanel");
        mainPanel.add(editorPanel);
        
        add(mainPanel);
    }
    public void setFrameProperties(){
        setCurrentPanel("mainMenuPanel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
        setBackground(new Color(16, 29, 48));
        setVisible(true); 
    }
    
    //image/colour manipulation
    public static Color brightness(Color c, double i){
        int R = (int) FastMath.max(FastMath.min(c.getRed()*i,255),0);
        int G = (int) FastMath.max(FastMath.min(c.getGreen()*i,255),0);
        int B = (int) FastMath.max(FastMath.min(c.getBlue()*i,255),0);
        return new Color(R,G,B,c.getAlpha());
    }
    public static Color interpolate(Color x, Color y, double blending){
        double inverse_blending = 1 - blending;
        double red =   x.getRed()   * inverse_blending   +   y.getRed()   * blending;
        double green = x.getGreen() * inverse_blending   +   y.getGreen() * blending;
        double blue =  x.getBlue()  * inverse_blending   +   y.getBlue()  * blending;
        double alpha =  x.getAlpha()  * inverse_blending   +   y.getAlpha()  * blending;
        Color blended = new Color((int)red, (int)green, (int)blue, (int)alpha);
        return blended;
    }
    public static Color addAlpha(Color c, int a){
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
    public static BufferedImage resize(BufferedImage img, double newW, double newH) {
        BufferedImage after = new BufferedImage((int)newW, (int)newH, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(newW/img.getWidth(), newH/img.getHeight());
        AffineTransformOp scaleOp = 
           new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        after = scaleOp.filter(img, after);
        return after;
    }
    public static BufferedImage rotate(BufferedImage img, double angle){
        final double rads = FastMath.toRadians(angle);
        final double sin = FastMath.abs(FastMath.sin(rads));
        final double cos = FastMath.abs(FastMath.cos(rads));
        final int w = (int) FastMath.floor(img.getWidth() * cos + img.getHeight() * sin);
        final int h = (int) FastMath.floor(img.getHeight() * cos + img.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, img.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-img.getWidth() / 2, -img.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(img, rotatedImage);
        return rotatedImage;
    }
    public static BufferedImage tintImage(BufferedImage image, int red, int green, int blue, int alpha) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(),
            BufferedImage.TRANSLUCENT);
        Graphics2D g = img.createGraphics(); 
        Color newColor = new Color(red, blue, green, 255);
        g.setXORMode(newColor);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return img;
    }
    
    //coordinate manipulaton
    public static double[] newPositions(double x, double y, double cameraX, double cameraY, double scale){
        double width = getScreenSize().getWidth();
        double height = getScreenSize().getHeight();
        double newX = ((x - cameraX + (width/(scale*2.0)))*scale);
        double newY = ((y - cameraY + (height/(scale*2.0)))*scale);
        double[] newValues = {newX,newY};
        return newValues;
    }
    public static double[] inversePositions(double x, double y, double cameraX, double cameraY, double scale){
        double width = getScreenSize().getWidth();
        double height = getScreenSize().getHeight();
        double newX = (x/scale) - width/(2.0*scale) + cameraX;
        double newY = (y/scale) - height/(2.0*scale) + cameraY;
        double[] newValues = {newX,newY};
        return newValues;
    }
    public static double[] rotatePoint(double x, double y, double axisX, double axisY, double degrees){
        double alpha = FastMath.toRadians(degrees);
        double x2 = (x - axisX)*FastMath.cos(alpha) - (y - axisY)*FastMath.sin(alpha);
        double y2 = (x - axisX)*FastMath.sin(alpha) + (y - axisY)*FastMath.cos(alpha);
        double[] coords = {x2 + axisX, y2 + axisY};
        return coords;
    }
    
    //getter methods
    public static Dimension getScreenSize(){
        return screenSize;
    }
    public static SimulationPanel getSimulationPanel(){
        return (SimulationPanel) simulationPanel;
    }
    public static EditorPanel getEditorPanel(){
        return (EditorPanel) editorPanel;
    }
    public static TerrainRenderer getTerrainRenderer(){
        return terrainRenderer;
    }
    public static AlphaComposite getAlphaComposite(){
        return alphaComposite;
    }
    public static AlphaComposite getNormalComposite(){
        return normalComposite;
    }
    public static void loadImages(){
        try {
            for (File file : imageDirectory.listFiles()){
                String name = file.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                images.put(name, ImageIO.read(file));
            }
        } catch (IOException ex) {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static BufferedImage getImage(String imageName){
        return images.get(imageName);
    }
    public static String getDefaultFont(){
        return font;
    }
    
    //setter methods
    public static void setCurrentPanel(String panel){
        currentPanel = panel;
        layoutController.show(mainPanel, panel);
    }
}
