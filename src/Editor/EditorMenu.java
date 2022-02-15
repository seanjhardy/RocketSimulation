package Editor;

import Graphics.AdvancedButton;
import Graphics.GUIManager;
import static Graphics.GUIManager.*;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class EditorMenu {
    private final EditorPanel parent;
    private double x,y,width,height;
    private final double moveSpeed = 0.5;
    private boolean visible = true;
    private final Color backgroundColor = new Color(21, 71, 60,100);
    private final Color borderColor = brightness(backgroundColor, 2);
    private AdvancedButton closeBtn;
    private ArrayList<Stage> stages = new ArrayList<>();
    private PartInstance selectedStageItem = null;
    private Stage selectedStage;
    private int totalCost = 0, totalMass = 0;
    
    //part data menu
    private PartInstance selectedPartInstance = null;
    private Part selectedPart = null;
    private JTable itemDataTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel totalCostLabel, totalMassLabel;
    private static int menuWidth = 250, menuHeight = 400;
    
    //initialisation
    public EditorMenu(EditorPanel parent, int x, int y, int width, int height){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateCoords();
        createComponents();
        stages.add(new Stage(parent));
    }
    public void createComponents(){
        Border emptyBorder = BorderFactory.createEmptyBorder();
        Border raisedBorder = BorderFactory.createMatteBorder(5,5,5,5,borderColor);
        Font font = new Font(GUIManager.getDefaultFont(), Font.BOLD, 25);
        closeBtn = new AdvancedButton("<");
        closeBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == closeBtn){
                visible = !visible;
                hidePartData();
                if(visible){
                    closeBtn.setText("<");
                }else{
                    closeBtn.setText(">");
                }
            }
        });
        closeBtn.setFont(font);
        closeBtn.setForeground(WHITE);
        closeBtn.setColour(new Color(204, 54, 43));
        closeBtn.addBorder(5);
        parent.add(closeBtn);
        
        
        totalCostLabel = new JLabel("", SwingConstants.RIGHT);
        totalCostLabel.setForeground(new Color(131, 254, 57));
        totalCostLabel.setFont(font);
        parent.add(totalCostLabel);
        
        font = new Font(GUIManager.getDefaultFont(), Font.BOLD, 25);
        totalMassLabel = new JLabel("", SwingConstants.RIGHT);
        totalMassLabel.setForeground(new Color(97, 255, 251));
        totalMassLabel.setFont(font);
        parent.add(totalMassLabel);
        
        tableModel = new DefaultTableModel();
        itemDataTable = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int row, int column) {                
                return false;    
            }
            
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component returnComp = super.prepareRenderer(renderer, row, column);
                JLabel jc = (JLabel)returnComp;
                
                ListDataObj entry = (ListDataObj) itemDataTable.getValueAt(row, column);
                
                if(entry.getType() == 1){
                    jc.setIcon(entry.getImage());
                }else{
                    jc.setIcon(null);
                }
                itemDataTable.setRowHeight(row, entry.getSize());
                jc.setText(entry.getValue());
                jc.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 2, 0, new Color(86, 255, 240, 200)));
                return jc;
            }
        };
        font = new Font(GUIManager.getDefaultFont(), Font.BOLD, 16);
        itemDataTable.setModel(tableModel);
        itemDataTable.setFont(font);
        itemDataTable.getTableHeader().setBackground(new Color(0,0,0,0));
        itemDataTable.getTableHeader().setForeground(new Color(255,255,255,255));
        itemDataTable.setBackground(new Color(0,0,0,0));
        itemDataTable.setForeground(WHITE);
        itemDataTable.setVisible(true);
        itemDataTable.setFocusable(false);
        itemDataTable.setTableHeader(null);
        
        scrollPane = new JScrollPane(itemDataTable);
        scrollPane.setBorder(emptyBorder);
        scrollPane.setViewportView(itemDataTable);
        scrollPane.getViewport().setBackground(new Color(0,0,0,0));
        scrollPane.setBackground(new Color(0,0,0,50));
        scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        scrollPane.setForeground(WHITE);
        parent.add(scrollPane);
    }
    
    //drawing methods
    public void draw(Graphics2D g){
        //draw menu
        drawMenu(g);
        setBounds();
    }
    public void drawMenu(Graphics2D g){
        if(visible){
            if(x < 0){
                x += FastMath.max((double)(FastMath.abs(x))*moveSpeed,1);
                updateCoords();
            }
        }else{
            if(x > -width){
                x -= FastMath.max((double)FastMath.abs(-width - x)*moveSpeed,1);
                updateCoords();
            }
        }
        g.setColor(backgroundColor);
        g.fillRect((int)x,(int)y,(int)width,(int)height);
        //draw border
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(4));
        g.drawRect((int)x, (int)y, (int)width-2, (int)height);
        g.setStroke(new BasicStroke(1));
        updateMenuData(g);
        //draw part menu
        parent.getPartManager().draw(g);
        if(selectedPart != null || selectedPartInstance != null){
            drawPartDataMenu(g);
        }else{
            scrollPane.setBounds(0,0,0,0);
        }
        drawStaging(g);
    }
    public void drawPartDataMenu(Graphics2D g){
        int borderWdith = 5;
        double cameraX = getEditorPanel().getCameraX();
        double cameraY = getEditorPanel().getCameraY();
        double scale = getEditorPanel().getScale();
        int startY = parent.getPartManager().getBlockHeight()*2 + 30;
        int menuX = 0, menuY = 0;
        if(selectedPart != null){
            menuX = (int) (x + width) + borderWdith + 10;
            menuY = startY + selectedPart.getY();
            if(menuY + menuHeight > getScreenSize().getHeight()){
                menuY -= ((menuY + menuHeight + borderWdith*2) - getScreenSize().getHeight());
            }
            //draw selection
            g.setColor(new Color(86, 255, 240, 200));
            g.fillRect(20 + selectedPart.getX(),
                    startY + selectedPart.getY(),
                    (int)((x+width) - (20+selectedPart.getX()) + 10),
                    10);
            //draw border
            g.setStroke(new BasicStroke(borderWdith));
            g.drawRect(menuX - borderWdith, 
                    menuY - borderWdith, 
                    menuWidth + borderWdith*2, 
                    menuHeight + borderWdith*2);
            g.setStroke(new BasicStroke(1));
            //draw menu
            g.setColor(new Color(43, 142, 120, 100));
            g.fillRect(menuX, menuY, menuWidth, menuHeight);
            drawPartData(g, menuX, menuY, menuWidth, menuHeight);
        }
        if(selectedPartInstance != null){
            double[] coords = newPositions(selectedPartInstance.getX(), selectedPartInstance.getY(), cameraX, cameraY, scale);
            menuX = (int)(coords[0] + selectedPartInstance.getPartType().getImageWidth()/2 + 40);
            menuY = (int)(coords[1] - menuHeight/2);
            g.setColor(new Color(86, 255, 240, 200));
            g.setStroke(new BasicStroke(borderWdith));
            int imageWidth = selectedPartInstance.getPartType().getImageWidth()/2;
            if(coords[0] > getScreenSize().getWidth()/2){
                menuX = (int)(coords[0] + (coords[0] - (menuX + menuWidth)));
                g.drawLine((int)(coords[0] - imageWidth),
                        (int)(coords[1]), 
                        (int)(coords[0] - imageWidth - 20),
                        (int)(coords[1]));
                g.drawLine((int)(coords[0] - imageWidth - 20),
                        (int)(coords[1]),
                        (int)(coords[0] - imageWidth - 20),
                        (int)(menuY + 20));
                g.drawLine((int)(coords[0] - imageWidth - 20),
                        (int)(menuY + 20),
                        (int)(coords[0] - imageWidth - 40),
                        (int)(menuY + 20));
            }else{
                g.drawLine((int)(coords[0] + imageWidth),
                        (int)(coords[1]),
                        (int)(coords[0] + imageWidth + 20),
                        (int)(coords[1]));
                g.drawLine((int)(coords[0] + imageWidth + 20),
                        (int)(coords[1]),
                        (int)(coords[0] + imageWidth + 20),
                        (int)(menuY + 20));
                g.drawLine((int)(coords[0] + imageWidth + 20),
                        (int)(menuY + 20), 
                        (int)(coords[0] + imageWidth + 40),
                        (int)(menuY + 20));
            }
            g.drawRect(menuX - borderWdith, 
                    menuY - borderWdith, 
                    menuWidth + borderWdith*2, 
                    menuHeight + borderWdith*2);
            g.setStroke(new BasicStroke(1));

            g.setColor(new Color(43, 142, 120, 100));
            g.fillRect(menuX, menuY, menuWidth, menuHeight);
            drawPartInstanceData(g, menuX, menuY, menuWidth, menuHeight);
        }
    }
    public void drawPartData(Graphics2D g, int menuX, int menuY, int menuWidth, int menuHeight){
        scrollPane.setBounds(menuX, menuY, menuWidth, menuHeight);
    }
    public void drawPartInstanceData(Graphics2D g, int menuX, int menuY, int menuWidth, int menuHeight){
         scrollPane.setBounds(menuX, menuY, menuWidth, menuHeight);
    }
    public void drawStaging(Graphics2D g){
        int x = (int)(getScreenSize().getWidth()*0.96);
        int y = (int)(getScreenSize().getHeight());
        for(Stage stage: stages){
            
            y -= (stage.getHeight()+20);
            stage.draw(g, x, y);
        }
    }
    public void updateCoords(){
        parent.getPartManager().setCoords((x+width*0.05),(y+height*0.02),(width*0.9),(y+height*0.8));
    }
    public void updateMenuData(Graphics2D g){
        g.drawImage(getImage("MenuDataBackground"), (int)(x),(int)(y + height - 153), null);
        totalCostLabel.setText(Integer.toString(totalCost));
        totalMassLabel.setText(Integer.toString(totalMass));
        removeCost(totalCost);
        removeMass(totalMass);
    }
    
    //setter methods
    public void setBounds(){
        closeBtn.setBounds((int)(x+width),(int)(y),50,50);
        totalCostLabel.setBounds((int)(x+30),(int)(y + height - 123),240,(int)(36));
        totalMassLabel.setBounds((int)(x+30),(int)(y + height - 66),255,(int)(43));
    }
    public void addStage(int i){
        Stage s = new Stage(parent);
        stages.add(i, s);
    }
    public void addStageItem(PartInstance p){
        if(stages.isEmpty()){
            Stage s = new Stage(parent);
            stages.add(s);
        }
        stages.get(0).addStageItem(p);
    }
    public void removeStageItem(PartInstance p){
        for(Stage stage: stages){
            stage.removeStageItem(p);
        }
    }
    public void removeStage(int i){
        stages.remove(stages.get(i));
    }
    public void setSelectedStageItem(PartInstance p){
        selectedStageItem = p;
    }
    public void setSelectedStage(Stage s){
        selectedStage = s;
    }
    public void addCost(int c){
        totalCost += c;
    }
    public void removeCost(int c){
        totalCost -= c;
    }
    public void addMass(int m){
        totalMass += m;
    }
    public void removeMass(int m){
        totalMass -= m;
    }
    
    //getter methods
    public double getX(){
        return x;
    }
    public double getWidth(){
        return width;
    }
    public ArrayList<Stage> getStages(){
        return stages;
    }
    public void showPartData(PartInstance p){
        selectedPartInstance = p;
        selectedPart = null;
        tableModel.setRowCount(0);
        int size = 80; 
        tableModel.addRow(new Object[]{new ListDataObj("Fuel: " + 1)});
    }
    public void showPartData(Part p){
        selectedPart = p;
        selectedPartInstance = null;
        tableModel.setRowCount(0);
        int size = 80; 
        String[] columnNames = {"Test"};
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.addRow(new Object[]{new ListDataObj(selectedPart.getIcon(size), "<html>" + selectedPart.getName() + "<br>" + selectedPart.getGroup() + "</html>")});
        //tableModel.addRow(new Object[]{new ListDataObj("Type: " + selectedPart.getPartType().getType())});
        tableModel.addRow(new Object[]{new ListDataObj("<html>Cost: " + Integer.toString(selectedPart.getCost()) + "<span style='display:inline;font-family: \"Times New Roman\", Times, serif'>∇</span></html>")});
        tableModel.addRow(new Object[]{new ListDataObj("Mass: " + Integer.toString(selectedPart.getMass()) + " kg")});
        if(selectedPart.getCrewSize() != 0){
            tableModel.addRow(new Object[]{new ListDataObj("Crew Size: " + Double.toString(selectedPart.getCrewSize()))});
        }
        if(selectedPart.getAutonomy() != 0){
            tableModel.addRow(new Object[]{new ListDataObj("Autonomy: " + Double.toString(selectedPart.getAutonomy()))});
        }
        if(selectedPart.getPowerStorage() != 0){
            tableModel.addRow(new Object[]{new ListDataObj("Power Storage: " + Double.toString(selectedPart.getPowerStorage()) + " kw/h")});
        }
        if(selectedPart.getPowerUsage() != 0){
            tableModel.addRow(new Object[]{new ListDataObj("Power Usage: " + Double.toString(selectedPart.getPowerUsage()) + " kw/h")});
        }
    }
    
    public void hidePartData(){
        selectedPartInstance = null;
        selectedPart = null;
    }
    public Part getSelectedPart(){
        return selectedPart;
    }
    public PartInstance getSelectedStageItem(){
        return selectedStageItem;
    }
    public Stage getSelectedStage(){
        return selectedStage;
    }
    
    public class ListDataObj{
        private String value;
        private Icon icon;
        private int type = 0;
        private int size = 25;
       
        public ListDataObj(String value) {
            this.value = value;
            this.size = 25;
        }
        
        public ListDataObj(BufferedImage image, String value) {
            this.size = 85;
            this.icon = new ImageIcon(image);
            this.value = value;
            this.type = 1;
        }
        
        public String getValue() {
            return value;
        }
        public int getType() {
            return type;
        }
        public Icon getImage(){
            return icon;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public int getSize(){
            return size;
        }
        
    } 
}
