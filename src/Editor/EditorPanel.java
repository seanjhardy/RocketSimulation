/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Graphics.GUIManager;
import Graphics.AdvancedButton;
import static Graphics.GUIManager.*;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.jafama.FastMath;

/**
 *
 * @author s-hardy
 */
public class EditorPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener{
    private GUIManager parent;
    private AdvancedButton exitBtn, saveBtn, loadBtn, newBtn, okBtn, cancelBtn;
    private JLabel menuLabel;
    private final int gridSize = 16;
    private int cameraMove = 20;
    private boolean up, down, left, right;
    private double cameraX = getScreenSize().getWidth()/2, cameraY = getScreenSize().getWidth()/2, scale = 1;
    private PartManager partManager;
    private EditorMenu editorMenu;
    private BufferedImage deleteIcon;
    private int menuState = 0;
    
    //initialisation
    public EditorPanel(GUIManager parent){
        this.parent = parent;
        partManager = new PartManager(this);
        editorMenu = new EditorMenu(this, 0, 0, (int)(getScreenSize().getWidth()*0.2), (int)getScreenSize().getHeight());
        deleteIcon = GUIManager.resize(getImage("DeleteIcon"), 100.0,100.0);
        createComponents();
        setBindings();
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void createComponents(){
        exitBtn = new AdvancedButton(getImage("ExitIcon"));
        exitBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == exitBtn){
                setCurrentPanel("mainMenuPanel");
            }
        });
        exitBtn.setForeground(WHITE);
        exitBtn.setColour(new Color(204, 54, 43));
        exitBtn.addBorder(5, "down");
        add(exitBtn);
        
        saveBtn = new AdvancedButton(getImage("SaveIcon"));
        saveBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == saveBtn){
            }
        });
        saveBtn.setForeground(WHITE);
        saveBtn.setColour(new Color(15, 125, 44));
        saveBtn.addBorder(5, "down");
        add(saveBtn);
        
        loadBtn = new AdvancedButton(getImage("LoadIcon"));
        loadBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == loadBtn){
            }
        });
        loadBtn.setForeground(WHITE);
        loadBtn.setColour(new Color(227, 194, 27));
        loadBtn.addBorder(5, "down");
        add(loadBtn);
        
        newBtn = new AdvancedButton(getImage("NewIcon"));
        newBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == newBtn){
                if(!getPartManager().getPartInstances().isEmpty()){
                    menuState = 1;
                }
            }
        });
        newBtn.setForeground(WHITE);
        newBtn.setColour(new Color(70, 155, 235));
        newBtn.addBorder(5, "down");
        add(newBtn);
        
        okBtn = new AdvancedButton("OK");
        okBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == okBtn){
                if(menuState == 1){
                    while(!partManager.getPartInstances().isEmpty()){
                        partManager.removePartInstance(partManager.getPartInstances().get(0));
                        while(editorMenu.getStages().size() > 1){
                            editorMenu.getStages().get(0).removeStage(true);
                        }
                    }
                }
                menuState = 0;
            }
        });
        okBtn.setForeground(WHITE);
        okBtn.setColour(new Color(23, 105, 27));
        okBtn.addBorder(5);
        add(okBtn);
        
        cancelBtn = new AdvancedButton("CANCEL");
        cancelBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == cancelBtn){
                menuState = 0;
            }
        });
        cancelBtn.setForeground(WHITE);
        cancelBtn.setColour(new Color(204, 54, 43));
        cancelBtn.addBorder(5);
        add(cancelBtn);
        
        menuLabel = new JLabel("", SwingConstants.CENTER);
        menuLabel.setForeground(WHITE);
        //saveLabel.setBorder(raisedBorder);
        menuLabel.setBackground(new Color(0, 0, 0,100));
        menuLabel.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        add(menuLabel);
    }
    public void setBindings(){
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "UP1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "DOWN1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "LEFT1");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "RIGHT1");
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "UP2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "DOWN2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "LEFT2");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "RIGHT2");
        
        am.put("UP1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               up = true;
            }   
        });
        am.put("DOWN1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               down = true;
            }   
        });
        am.put("LEFT1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               left = true;
            }   
        });
        am.put("RIGHT1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               right = true;
            }   
        });
        am.put("UP2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               up = false;
            }   
        });
        am.put("DOWN2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               down = false;
            }   
        });
        am.put("LEFT2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               left = false;
            }   
        });
        am.put("RIGHT2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               right = false;
            }   
        });
    }
    
    //update camera
    public void calculateCameraMovement(){
        int width = (int) getScreenSize().getWidth();
        if(up){
             if(cameraY  > 0){
                cameraY -= cameraMove/scale;
            }else{
                cameraY = 0;
            };
        }if(down){
            if(cameraY < width){
                cameraY += cameraMove/scale;
            }else{
                cameraY = width;
            }
        }if(left){
            if(cameraX  > 0){
                cameraX -= cameraMove/scale;
            }else{
                cameraX = 0;
            }
        }if(right){
             if(cameraX  < width){
                cameraX += cameraMove/scale;
            }else{
                cameraX = width;
            }
        }
    }
    
    //drawing methods
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(new Font(getDefaultFont(), Font.PLAIN, 16)); 
        calculateCameraMovement();
        setBounds();
        drawUI(g);
        revalidate();
        repaint();
    }
    public void drawUI(Graphics g){
        double width = getScreenSize().getWidth();
        double height = getScreenSize().getHeight();
        Graphics2D g2 = (Graphics2D)g;
        //draw background
        g2.setColor(new Color(16, 29, 48));
        g2.fillRect(0,0,(int)width,(int)height);
        drawGrid(g2, width, height);
        //====================
        editorMenu.draw(g2);
        drawSelectedItems(g2, width, height);
        drawMenu(g2, (int)width, (int)height);
        
    }
    public void drawGrid(Graphics2D g2, double width, double height){
        double div = 10;
        g2.setStroke(new BasicStroke((float) (4.0f*scale)));
        for(int i = 0; i <= (width)/(gridSize); i++){
            double[] startCoords = newPositions(i*gridSize,0, cameraX, cameraY, scale);
            double[] endCoords = newPositions(i*gridSize,1920, cameraX, cameraY, scale);
            g2.setColor(new Color(255,255,255,10));
            if(i % div == 0){
                g2.setColor(new Color(255,255,255,20));
            }
            g2.drawLine((int)(startCoords[0]), (int)(startCoords[1]), (int)(endCoords[0]), (int)(endCoords[1]));
        }
        for(int i = 0; i <= (width)/(gridSize); i++){
            double[] startCoords = newPositions(0,i*gridSize, cameraX, cameraY, scale);
            double[] endCoords = newPositions(1920,i*gridSize, cameraX, cameraY, scale);
            g2.setColor(new Color(255,255,255,10));
            if(i % div == 0){
                g2.setColor(new Color(255,255,255,20));
            }
            g2.drawLine((int)(startCoords[0]), (int)(startCoords[1]), (int)(endCoords[0]), (int)(endCoords[1]));
        }
        g2.setStroke(new BasicStroke(1));
    }
    public void drawSelectedItems(Graphics2D g, double width, double height){
        if(!getSelection().isEmpty()){
            int x = (int) (editorMenu.getX() + editorMenu.getWidth() + 60);
            int y = (int) (height*0.94);
            double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
            double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
            if(FastMath.abs(mouseX - x) + FastMath.abs(mouseY - y) < 100){
                BufferedImage largeIcon = GUIManager.resize(deleteIcon, 150.0, 150.0);
                g.drawImage(largeIcon,x-75, y-75, null);
            }else{
                g.drawImage(deleteIcon,x-50, y-50, null);
            }
        }
        if(editorMenu.getSelectedStageItem() != null){
            int mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
            int mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
            placeStageItemInStage(mouseX, mouseY);
            g.setColor(new Color(100,100,100,100)); 
            int stageItemSize = Stage.getStageItemSize();
            g.fillRect(mouseX - stageItemSize/2, mouseY - stageItemSize/2, stageItemSize, stageItemSize);
            PartInstance selectedItem = editorMenu.getSelectedStageItem();
            selectedItem.getPartType().drawStageIcon(g, mouseX - stageItemSize/2, mouseY - stageItemSize/2, stageItemSize, true);
        
        }
    }
    public void drawMenu(Graphics2D g, int width, int height){
        if(menuState != 0){
            int[] menuCoords = {(int)(width*0.5 - 200), (int)(height*0.5-100), 400, 200};
            g.setColor(new Color(14, 79, 102,200));
            g.fillRect(menuCoords[0],menuCoords[1],menuCoords[2],menuCoords[3]);
            if(menuState == 1){
                setMenuText("Do you want to create a new project?<br>All existing work will be lost.");
            }else if(menuState == 2){
                setMenuText("A file with this name already exists.<br>Do you want to overwrite it?");
            }
            menuLabel.setBounds((int)(menuCoords[0] + menuCoords[2]*0.05),
                             (int)(menuCoords[1] + menuCoords[3]*0.1),
                             (int)(menuCoords[2]*0.9), (int)(menuCoords[3]*0.4));
            okBtn.setBounds((int)(menuCoords[0] + menuCoords[2]*0.1),
                             (int)(menuCoords[1] + menuCoords[3]*0.6),
                             (int)(menuCoords[2]*0.2), 50);
            cancelBtn.setBounds((int)(menuCoords[0] + menuCoords[2]*0.7),
                             (int)(menuCoords[1] + menuCoords[3]*0.6),
                             (int)(menuCoords[2]*0.2), 50);
        }else{
            okBtn.setBounds(0,0,0,0);
            cancelBtn.setBounds(0,0,0,0);
            menuLabel.setBounds(0,0,0,0);
        }
    }
    
    //connecting parts
    public double[] snapToPoint(PartInstance part, double x, double y){
        double[] mouseCoords = inversePositions(x, y, cameraX, cameraY, scale);
        int minDist = 10;
        double x2 = mouseCoords[0], y2 = mouseCoords[1];
        for(PartInstance p : getPartManager().getPartInstances()){
            if(p != part){
                for(ConnectionPoint point : part.getConnectionPoints()){
                    if(point.getConnectState() == 0){
                        for(ConnectionPoint point2 : p.getConnectionPoints()){
                            if(point2.getConnectState() == 0){
                                if(!p.contains((int)x,(int)y)){
                                    double dist = FastMath.hypot((point2.getY() + p.getY()) - (mouseCoords[1] + point.getY()), (point2.getX() + p.getX()) - (mouseCoords[0] + point.getX()));
                                    if(dist < minDist){
                                        x2 = -point.getX() + point2.getX() + p.getX();
                                        y2 = -point.getY() + point2.getY() + p.getY();
                                        minDist = (int) dist;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        double[] coords = {x2, y2};
        return coords;
    }
    public void connectToPoint(PartInstance part, double x, double y){
        int minDist = 10;
        ConnectionPoint tempPoint1 = null, tempPoint2 = null;
        PartInstance tempPart = null;
        ConnectionPoint connectionPoint = null;
        for(PartInstance p : getPartManager().getPartInstances()){
            if(p != part){
                for(ConnectionPoint point : part.getConnectionPoints()){
                    if(point.getConnectState() == 0){
                        for(ConnectionPoint point2 : p.getConnectionPoints()){
                            if(point2.getConnectState()  == 0){
                                if(!p.contains((int)x,(int)y)){
                                    double dist = FastMath.hypot((point2.getY() + p.getY()) - (y + point.getY()), (point2.getX() + p.getX()) - (x + point.getX()));
                                    if(dist < minDist){
                                        tempPoint1 = point;
                                        tempPoint2 = point2;
                                        tempPart = p;
                                        connectionPoint = point2;
                                        minDist = (int) dist;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(tempPoint1 != null && tempPoint2 != null){
            tempPoint1.setConnectState(1);
            tempPoint2.setConnectState(2);
            part.setParent(tempPart);
            tempPart.addChild(connectionPoint, part);
        }
    }
    //manage staging
    public int isClickOnStage(int mouseX, int mouseY){
        int click = -1;
        for(int i = 0; i < editorMenu.getStages().size();i++){
           Stage stage = editorMenu.getStages().get(i);
           if(mouseX < stage.getX() + stage.getWidth() && mouseX > stage.getX() &&
              mouseY < stage.getY() + stage.getHeight() && mouseY > stage.getY()){
               click = i;
           }
        }
        return click;
    }
    public void placeStageItemInStage(int mouseX, int mouseY){
        int minDistance = 20000;
        Stage tempStage = null;
        int stageIndex = 0;
        int distance = 0;
        Stage prevStage = editorMenu.getSelectedStage();
        prevStage.removeStageItem(editorMenu.getSelectedStageItem());
        for(Stage stage: editorMenu.getStages()){
            int partY = stage.getY() + 30 + (Stage.getStageItemSize()+5)/2;
            for(int i = 0; i <= stage.getStageItems().size(); i++){
                distance = (int) FastMath.hypot((partY) - mouseY, 0);
                if(distance < minDistance && distance < 40){
                    minDistance = distance;
                    stageIndex = i;
                    tempStage = stage;
                }
                partY += (Stage.getStageItemSize()+5);
            }
        }
        if(tempStage != null){
            tempStage.addStageItem(stageIndex, editorMenu.getSelectedStageItem());
            editorMenu.setSelectedStage(tempStage);
        }else{
            prevStage.addStageItem(editorMenu.getSelectedStageItem());
        }
        
    }
    
    //setter methods
    public void setBounds(){
        exitBtn.setBounds((int) (parent.getScreenSize().getWidth()-60),0,60,50);
        saveBtn.setBounds((int) (parent.getScreenSize().getWidth()-120),0,60,50);
        loadBtn.setBounds((int) (parent.getScreenSize().getWidth()-180),0,60,50);
        newBtn.setBounds((int) (parent.getScreenSize().getWidth()-240),0,60,50);
    }
    public void setMenuText(String s){
        menuLabel.setText("<html><div style='text-align: center;'>" + s+"</div></html>");
    }
    
    //getter methods
    public PartManager getPartManager(){
        return partManager;
    }
    public EditorMenu getEditorMenu(){
        return editorMenu;
    }
    public ArrayList<PartInstance> getSelection(){
        return partManager.getSelection();
    }
    public double getCameraY(){
        return cameraY;
    }
    public double getCameraX(){
        return cameraX;
    }
    public double getScale(){
        return this.scale;
    }
    
    //mouse listeners
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            if(scale * 1.5 < 20){
                scale *= 1.5;
            }
        }else if(notches > 0){
            if(scale / 1.5 > 0.5){
                scale /= 1.5;
            }
        }
        partManager.resizeImages(scale);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
        ArrayList<PartInstance> partInstances = this.getPartManager().getPartInstances();
        int deleteIconX = (int) (editorMenu.getX() + editorMenu.getWidth() + 60);
        int deleteIconY = (int)(getScreenSize().getHeight()*0.94);
        ArrayList<PartInstance> selection = getSelection();
        if (SwingUtilities.isLeftMouseButton(e)){
            if(editorMenu.getSelectedStageItem() == null){
                int stageClick = isClickOnStage(mouseX, mouseY);
                if(stageClick != -1){
                    PartInstance part = editorMenu.getStages().get(stageClick).getClickedItem(mouseX, mouseY);
                    editorMenu.setSelectedStageItem(part);
                    editorMenu.setSelectedStage(editorMenu.getStages().get(stageClick));
                }
                
                editorMenu.hidePartData();
                if(FastMath.abs(mouseX - deleteIconX) + FastMath.abs(mouseY - deleteIconY) < 100){
                    for(int i = 0; i < selection.size(); i++){
                        selection.get(i).removePartInstance();
                    }
                    partManager.setSelection(new ArrayList<>());
                }else{
                    ArrayList<PartInstance> newSelection = new ArrayList<>();
                    boolean itemSelected = false;
                    for(PartInstance p : partInstances){
                        if(!selection.contains(p) && !itemSelected){
                            if(selection.isEmpty()){
                                if(p.contains(mouseX, mouseY)){
                                    newSelection.add(p);
                                    p.addAvaliableConnections();
                                    itemSelected = true;
                                }
                            }
                        }else{
                            if(!selection.isEmpty()){
                                connectToPoint(p, p.getX(), p.getY());
                            }
                        }
                    }
                    getPartManager().setSelection(newSelection);
                }
            }
        }else if (SwingUtilities.isRightMouseButton(e)){
            for(PartInstance p : partInstances){
                if(selection.contains(p)){
                    p.rotate(p.getX(), p.getY(), 90);
                }else if(p.contains(mouseX, mouseY)){
                    editorMenu.showPartData(p);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(editorMenu.getSelectedStageItem() != null){
            editorMenu.setSelectedStageItem(null);
            editorMenu.setSelectedStage(null);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!getSelection().isEmpty()){
            double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
            double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
            for(PartInstance part: getSelection()){
                double[] newCoords = getEditorPanel().snapToPoint(part, mouseX, mouseY);
                part.setX(newCoords[0]);
                part.setY(newCoords[1]);
            }
        }
    }
}
