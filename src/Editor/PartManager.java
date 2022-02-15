    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import static Editor.Part.getPartIconSize;
import Graphics.AdvancedButton;
import Graphics.GUIManager;
import static Graphics.GUIManager.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class PartManager{
    private int blockHeight = 50;
    private EditorPanel parent;
    private int x, y, width, height;
    private Color backgroundColour = new Color(100,100,100,100);
    private Color borderColor = WHITE;
    private JScrollPane scrollPane;
    private static ItemPanel itemPanel;
    
    private ArrayList<PartType> partTypes = new ArrayList<>();
    private ArrayList<Part> parts = new ArrayList<>();
    private ArrayList<Part> currentItems = new ArrayList<>();
    private ArrayList<PartInstance> partInstances = new ArrayList<>();
    private ArrayList<PartInstance> selection = new ArrayList<>();
    private PartInstance root;
    
    
    private static File partDataDirectory = new File("partData");
    private int partID;
    
    private static int currentPartType = -1;//-1 displays all parts
    private AdvancedButton sortBtn;
    private JComboBox sortType;
    private String sortField;
    private int sortState = 0;// 0 no sorting, 1 sort down, -1 sort up
    
    //initialisation
    public PartManager(EditorPanel editorPanel){
        parent = editorPanel;
        createComponents();
        loadParts();
    }
    
    public void createComponents(){
        sortBtn = new AdvancedButton("Sort");
        sortBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == sortBtn){
                sortState *= -1;
                if(sortState == 0){
                    sortState = 1;
                }
                sortField = (String) sortType.getSelectedItem();
                if(sortField.equals("All")){
                    currentPartType = -1;
                }
                if(sortState == 1){
                    sortBtn.setText("Sort ˅");  
                }else if(sortState == -1){
                    sortBtn.setText("Sort ^");  
                }
                updateCurrentItems();
            }
        });
        sortBtn.setForeground(WHITE);
        sortBtn.setColour(new Color(204, 54, 43));
        sortBtn.addBorder(5);
        parent.add(sortBtn);
        
        sortType = new JComboBox<>();
        sortType.removeAllItems();
        sortType.addItem("All");
        sortType.addItem("Name");
        sortType.addItem("Cost");
        sortType.addItem("Mass");
        sortType.addItem("Level");
        sortType.addItem("Type");
        
        sortType.setBounds(500,500,200,200);
        sortType.setFont(new Font(getDefaultFont(), 1, 16));
        sortType.setOpaque(false);
        sortType.setForeground(WHITE);
        sortType.setBackground(new Color(0, 0, 0));
        sortType.setMaximumRowCount(12);
        parent.add(sortType);
        
        itemPanel = new ItemPanel();
        itemPanel.setVisible(true);
        itemPanel.setOpaque(false);
        itemPanel.setBackground(new Color(0,0,0,0));
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(itemPanel);
        
        scrollPane.setWheelScrollingEnabled(true);
        Border emptyBorder = BorderFactory.createEmptyBorder();
        scrollPane.setBorder(emptyBorder);
        scrollPane.setSize(new Dimension(500,200));
        scrollPane.setOpaque(false);
        scrollPane.setBackground(new Color(0,0,0,50));
        scrollPane.getViewport().setBackground(new Color(0,0,0,0));
        scrollPane.setForeground(WHITE);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        parent.add(scrollPane);
    }
    
    public Part getPartByName(String part) {
        return (Part) parts.stream().filter(p -> p.getName().equals(part)).findFirst().orElse(null);
    }
    
    public void loadParts(){
        BufferedReader reader;
        try {
            for (File file : partDataDirectory.listFiles()){
                if(file.getName().equals("PartData.csv")){
                    reader = new BufferedReader(new FileReader(file));
                    String row = reader.readLine();
                    while ((row = reader.readLine()) != null) {
                        String[] data = row.split(",");
                        if(data.length != 0 && !data[0].equals("")){
                            if(getPartType(data[1]) == null){
                                partTypes.add(new PartType(parent, data[1], partTypes.size()));
                            }
                            Part p = new Part(parent, data[0]);
                            p.setType(getPartType(data[1]));
                            p.setType(data[2]);
                            p.setStaging(Integer.parseInt(data[4]) == 1);
                            p.setConnectionPoints(data[5]);
                            p.setCost(Integer.parseInt(data[6]));
                            p.setMass(Integer.parseInt(data[7]));
                            p.setAutonomy(Integer.parseInt(data[10]));
                            p.setCrewSize(Integer.parseInt(data[12]));
                            p.setPowerUsage(Double.parseDouble(data[13]));
                            p.setPowerStorage(Double.parseDouble(data[14]));
                            parts.add(p);
                        }
                    }
                    reader.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GUIManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addPartInstance(PartInstance p){
        if(root == null){
            root = p;
        }
        partInstances.add(p);
    }
    public ArrayList<PartInstance> getPartInstances(){
        return partInstances;
    }
    public void setPartInstances(ArrayList<PartInstance> p){
        partInstances = p;
    }
    public void removePartInstance(PartInstance p){
        partInstances.remove(p);
        getEditorPanel().getEditorMenu().removeStageItem(p);
        if(p == root){
            root = null;
        }
    }
    
    public void draw(Graphics2D g){
        drawItems(g);
        //draw menu
        g.setColor(backgroundColour);
        g.fillRect(x,y,width,height);
        //draw border
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(4));
        g.drawRect(x-2, y-2, width+4, height+4);
        int partHeight = blockHeight;
        g.drawRect(x-2, y+partHeight+2, width+4, partHeight+6);
        g.setStroke(new BasicStroke(1));
        //draw icons
        for(PartType partType: partTypes){
            partType.draw(g);
        }
        drawItemIcons(g);
        //draw buttons
        setBounds();
    }
    public void setCoords(double x, double y, double width, double height){
        this.x = (int) x;
        this.y = (int) y;
        this.width = (int) width;
        this.height = (int) height;
        
        int partWidth = (int) ((width)/partTypes.size());
        for(int i = 0; i < partTypes.size(); i++){
            PartType partType = partTypes.get(i);
            int partX = (int) (x + (width) * ((double)i/partTypes.size()));
            partType.setCoords(partX, y, partWidth, blockHeight);
        }
    }
    
    public void drawItemIcons(Graphics2D g){
        for(Part p : parts){
            p.draw(g,-100, -100);
        }
        int numColumns = (int) FastMath.floor(((double)width)/getPartIconSize());
        int currentRow = 0;
        int currentColumn = 0;
        int padding = 10;
        int xPos = padding;
        int yPos = padding - itemPanel.getScroll();
        for(int partNum = 0; partNum < currentItems.size(); partNum++){
            currentItems.get(partNum).draw(g, xPos + currentColumn*(getPartIconSize()+padding) , yPos + currentRow*(getPartIconSize()+padding));
            if((partNum + 1) % numColumns == 0 && partNum != 0){
                currentRow += 1;
                currentColumn = 0;
            }else{
                currentColumn += 1;
            }
        }
    }
    public void drawItems(Graphics2D g){
        for(PartInstance partInstance: partInstances){
            partInstance.setVisited(false);
        }
        if(root != null){
            root.update(g, true);
        }
        for(PartInstance partInstance: partInstances){
            if(!partInstance.getVisited()){
                partInstance.update(g, false);
            }
        }
    }
    public void resizeImages(double scale){
        for(Part p : parts){
            p.resizeImage(scale);
        }
    }
    public void updateCurrentItems(){
        currentItems = new ArrayList<>();
        for(Part part: parts){
            if(currentPartType == -1 || part.getPartType().getPartID() == currentPartType){
                currentItems.add(part);
            }
        }
        Collections.sort(currentItems);
        itemPanel.setScroll(0);
    }
    public static void setCurrentPart(int newPart){
        currentPartType = newPart;
    }
    
    public int getCurrentPart(){
        return currentPartType;
    }
    public PartType getPartType(String type){
        for(int i = 0; i < partTypes.size(); i++){
            PartType p = partTypes.get(i);
            if(p.getType().equals(type)){
                return p;
            }
        }
        return null;
    }
    public String getSortType(){
            return sortState != 0 ? sortField : "";
    }
    public int getSortState(){
        return sortState;
    }
    public ArrayList<PartInstance> getSelection(){
        return selection;
    }
    public boolean isSelectionEmpty(){
        return selection.isEmpty();
    }
    public int getNextPartID(){
        partID += 1;
        return partID;
    }
    public PartInstance getRoot(){
        return root;
    }
    public static ItemPanel getItemPanel(){
        return itemPanel;
    }
    public int getBlockHeight(){
        return blockHeight;
    }
    public int getHeight(){
        return height;
    }
    
    public void setSortState(int i){
        sortState = i;
        if(sortState == 0){
            sortBtn.setText("Sort");  
        }else if(sortState == 1){
            sortBtn.setText("Sort ˅");  
        }else if(sortState == -1){
            sortBtn.setText("Sort ^");  
        }
    }
    public void setBounds(){
        sortBtn.setBounds((int) (x+(width*0.7)),y+ blockHeight +4, (int)(width*0.3), blockHeight);
        sortType.setBounds(x, y + blockHeight + 4, (int) (width*0.7), blockHeight);
        sortType.getComponents()[0].setSize(20, blockHeight);
        sortType.getComponents()[0].setLocation((int) ((width*0.7) - sortType.getComponents()[0].getWidth()),0);
        
        int panelYSize = (int) ((FastMath.ceil((double)currentItems.size()/3.0))*(Part.getPartIconSize() + 10));
        itemPanel.setScrollSize(panelYSize, (height - blockHeight*2));
        itemPanel.setPreferredSize(new Dimension((int)width,(int)FastMath.max(panelYSize, height - blockHeight*2)));
        scrollPane.setBounds((int)x,(int)y+blockHeight*2 + 10,(int)width,(int)height-blockHeight*2 - 10); 
    }
    public void resetSelection(){
        selection = new ArrayList<>();
    }
    public void addToSelection(PartInstance part){
        selection.add(part);
    }
    public void setSelection(ArrayList<PartInstance> s){
        selection = s;
    }
    
    public class ItemPanel extends JPanel implements MouseWheelListener{
        private int scroll = 0;
        private int scrollIncrement = 30;
        private int maxScrollSize = 0;
        
        public ItemPanel(){
            addMouseWheelListener(this);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //g.setColor(new Color(0,0,0,50));
            //g.fillRect(0,0,getWidth(),getHeight());
            Part selectedPart = getEditorPanel().getEditorMenu().getSelectedPart();
            if(selectedPart != null){
                g.setColor(new Color(86, 255, 240, 200));
                g.fillRect(selectedPart.getX(),
                         selectedPart.getY(),
                        (int)((x+width) - (selectedPart.getX()) + 15),
                        10);
                g.fillRect(selectedPart.getX()-5,
                        selectedPart.getY()-5,
                        selectedPart.getPartIconSize()+10,
                        selectedPart.getPartIconSize()+10);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int notches = e.getWheelRotation();
            if(notches > 0){
                if(scroll + scrollIncrement < maxScrollSize + 30){
                    scroll += scrollIncrement;
                }
            }else if(notches < 0){   
                if(scroll - scrollIncrement > - 30){
                    scroll -= scrollIncrement;
                }
            }
        }
        
        public int getScroll(){
            return scroll;
        }
        public void setScroll(int s){
            this.scroll = s;
        }
        
        public void setScrollSize(int size, int paneSize){
            if(size > paneSize){
                this.maxScrollSize = size - paneSize;
            }else{
                this.maxScrollSize = 0;
            }
        }
    }
}
