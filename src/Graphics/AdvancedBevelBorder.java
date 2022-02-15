
package Graphics;

import static Graphics.GUIManager.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public class AdvancedBevelBorder extends AbstractBorder{
    
    private AdvancedButton parent;
    private Color topColor, rightColor, bottomColor, leftColor;
    
    private int direction = -1;
    private int borderWidth;

    public AdvancedBevelBorder(AdvancedButton parent, int borderWidth) {
        this.parent = parent;
        this.borderWidth = borderWidth;
    }
    
    public AdvancedBevelBorder(AdvancedButton parent, int borderWidth, int direction) {
        this.parent = parent;
        this.borderWidth = borderWidth;
        this.direction = direction;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);

        int h = height;
        int w = width;
        int bw = getBorderWidth();
        int bwTop = direction == 1 ? 0 : getBorderWidth();
        int bwBottom = direction == 0 ? 0 : getBorderWidth();
        int bwLeft = direction == 3 ? 0 : getBorderWidth();
        int bwRight = direction == 2 ? 0 : getBorderWidth();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        if(direction != 1){
            Polygon topPolygon = createPolygon(new Point(0, 0), new Point(w, 0), new Point(w - bwRight, bw), new Point(bwLeft, bw),
                    new Point(0, 0));
            g2.setColor(getTopColor());
            g2.fill(topPolygon);
            g2.draw(topPolygon);
        }
        if(direction != 2){
            Polygon rightPolygon = createPolygon(new Point(w - 1, 0), new Point(w - 1, h), new Point(w - bw - 1, h - bwBottom),
                    new Point(w - bw - 1, bwTop), new Point(w - 1, 0));
            g2.setColor(getRightColor());
            g2.fill(rightPolygon);
            g2.draw(rightPolygon);
        }
        if(direction != 0){
            Polygon bottomPolygon = createPolygon(new Point(0, h - 1), new Point(w, h - 1), new Point(w - bwRight, h - bw - 1),
                    new Point(bwLeft, h - bw - 1), new Point(0, h - 1));
            g2.setColor(getBottomColor());
            g2.fill(bottomPolygon);
            g2.draw(bottomPolygon);
        }
        if(direction != 3){
            Polygon leftPolygon = createPolygon(new Point(0, 0), new Point(0, h), new Point(bw, h - bwBottom), new Point(bw, bwTop),
                    new Point(0, 0));
            g2.setColor(getLeftColor());
            g2.fill(leftPolygon);
            g2.draw(leftPolygon);
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(getBorderWidth(), getBorderWidth(), getBorderWidth() + 1, getBorderWidth() + 1);
    }

    private Polygon createPolygon(Point... points) {
        Polygon polygon = new Polygon();
        for (Point point : points) {
            polygon.addPoint(point.x, point.y);
        }
        return polygon;
    }

    public Color getTopColor() {
        return brightness(parent.getColour(), 1.5);
    }
    
    public Color getRightColor() {
        return brightness(parent.getColour(), 0.6);
    }

    public Color getBottomColor() {
        return brightness(parent.getColour(), 0.4);
    }


    public Color getLeftColor() {
        return brightness(parent.getColour(), 0.8);
    }

    public int getBorderWidth() {
        return borderWidth;
    }
}