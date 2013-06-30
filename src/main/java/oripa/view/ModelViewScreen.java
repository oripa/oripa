/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.ORIPA;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriLine;
import oripa.paint.Globals;
import oripa.resource.Constants;

public class ModelViewScreen extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener, ComponentListener {

    private Image bufferImage;
    private Graphics2D bufferg;
    private Point2D preMousePoint; // Screen coordinates
    private Point2D.Double currentMousePointLogic = new Point2D.Double(); // Logical coordinates
    private double scale;
    private double transX;
    private double transY;
    private Vector2d modelCenter = new Vector2d();
    private Dimension preSize;
    private double rotateAngle;
    private AffineTransform affineTransform = new AffineTransform();
    public boolean dispSlideFace = false;
    private OriLine crossLine = null;
    private int crossLineAngleDegree = 90;
    private double crossLinePosition = 0;

    public ModelViewScreen() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(this);

        crossLine = new OriLine();
        scale = 1.0;
        rotateAngle = 0;
        setBackground(Color.white);

        preSize = getSize();
    }

    public void resetViewMatrix() {
        rotateAngle = 0;
        if (!ORIPA.doc.hasModel) {
            scale = 1.0;
        } else {
            // Align the center of the model, combined scale
            Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
            Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
            for (OriFace face : ORIPA.doc.faces) {
                for (OriHalfedge he : face.halfedges) {
                    maxV.x = Math.max(maxV.x, he.vertex.p.x);
                    maxV.y = Math.max(maxV.y, he.vertex.p.y);
                    minV.x = Math.min(minV.x, he.vertex.p.x);
                    minV.y = Math.min(minV.y, he.vertex.p.y);
                }
            }
            modelCenter.x = (maxV.x + minV.x) / 2;
            modelCenter.y = (maxV.y + minV.y) / 2;
            scale = 0.8 * Math.min(getWidth() / (maxV.x - minV.x), getHeight() / (maxV.y - minV.y));
            updateAffineTransform();
            recalcCrossLine();
        }
    }

    public void drawModel(Graphics2D g2d) {
        for (OriFace face : ORIPA.doc.sortedFaces) {
            if (Globals.modelDispMode == Constants.ModelDispMode.FILL_COLOR) {
                if (face.faceFront) {
                    g2d.setColor(new Color(255, 200, 200));
                } else {
                    g2d.setColor(new Color(200, 200, 255));
                }
                g2d.fill(face.outline);
            } else if (Globals.modelDispMode == Constants.ModelDispMode.FILL_WHITE) {
                g2d.setColor(Color.WHITE);
                g2d.fill(face.outline);
            } else if (Globals.modelDispMode == Constants.ModelDispMode.FILL_ALPHA) {
                g2d.setColor(new Color(100, 100, 100));
                g2d.fill(face.outline);
            }

            g2d.setColor(Color.BLACK);
            for (OriHalfedge he : face.halfedges) {
                if (he.pair == null) {
                    g2d.setStroke(Config.MODEL_STROKE_CUT);
                } else {
                    g2d.setStroke(Config.STROKE_CUT);
                }
                g2d.draw(new Line2D.Double(he.positionForDisplay.x, 
                        he.positionForDisplay.y, he.next.positionForDisplay.x, 
                        he.next.positionForDisplay.y));
            }
        }

        if (Globals.bDispCrossLine) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setStroke(Config.MODEL_STROKE_CUT);
            g2d.setColor(Color.RED);

            g2d.draw(new Line2D.Double(crossLine.p0.x, crossLine.p0.y, crossLine.p1.x, crossLine.p1.y));
        }
    }

    // Update the current AffineTransform
    private void updateAffineTransform() {
        affineTransform.setToIdentity(); 
        affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
        affineTransform.scale(scale, scale);   
        affineTransform.translate(transX, transY);
        affineTransform.rotate(rotateAngle);
        affineTransform.translate(-modelCenter.x, -modelCenter.y);
    }

    // Scaling relative to the center of the screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferg = (Graphics2D) bufferImage.getGraphics();

            updateAffineTransform();
            preSize = getSize();
        }
        bufferg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        bufferg.setTransform(new AffineTransform());
        bufferg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        bufferg.setColor(Color.WHITE);
        bufferg.fillRect(0, 0, getWidth(), getHeight());

        bufferg.setTransform(affineTransform);

        Graphics2D g2d = bufferg;


        if (ORIPA.doc.hasModel) {
            g2d.setStroke(Config.STROKE_CUT);
            if (Globals.modelDispMode == Constants.ModelDispMode.FILL_ALPHA) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            }
            drawModel(g2d);
            g.drawImage(bufferImage, 0, 0, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point2D.Double clickPoint = new Point2D.Double();
        try {
            affineTransform.inverseTransform(e.getPoint(), clickPoint);
        } catch (Exception ex) {
        }
    }

    public void setCrossLineAngle(int angleDegree) {
        crossLineAngleDegree = angleDegree;
        recalcCrossLine();
    }

    public void setCrossLinePosition(int positionValue) {
        crossLinePosition = positionValue;
        recalcCrossLine();
    }

    public void recalcCrossLine() {
        Vector2d dir = new Vector2d(Math.cos(Math.PI * crossLineAngleDegree / 180.0), 
                Math.sin(Math.PI * crossLineAngleDegree / 180.0));
        crossLine.p0.set(modelCenter.x - dir.x * 300, modelCenter.y - dir.y * 300);
        crossLine.p1.set(modelCenter.x + dir.x * 300, modelCenter.y + dir.y * 300);
        Vector2d moveVec = new Vector2d(-dir.y, dir.x);
        moveVec.normalize();
        moveVec.scale(crossLinePosition);
        crossLine.p0.add(moveVec);
        crossLine.p1.add(moveVec);

        ORIPA.doc.setCrossLine(crossLine);
        repaint();
        ORIPA.mainFrame.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        preMousePoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
            transX += (double) (e.getX() - preMousePoint.getX()) / scale;
            transY += (double) (e.getY() - preMousePoint.getY()) / scale;

            preMousePoint = e.getPoint();
            updateAffineTransform();
            repaint();
        } else if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            rotateAngle += ((double) e.getX() - preMousePoint.getX()) / 100.0;
            preMousePoint = e.getPoint();
            updateAffineTransform();
            repaint();
        }

        // Gets the value of the current logical coordinates of the mouse
        try {
            affineTransform.inverseTransform(e.getPoint(), currentMousePointLogic);
        } catch (Exception ex) {
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            affineTransform.inverseTransform(e.getPoint(), currentMousePointLogic);
        } catch (Exception ex) {
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale_ = (100.0 - e.getWheelRotation() * 5) / 100.0;
        scale *= scale_;
        updateAffineTransform();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        preSize = getSize();

        transX = transX - preSize.width * 0.5 + getWidth() * 0.5;
        transY = transY - preSize.height * 0.5 + getHeight() * 0.5;

        bufferImage = createImage(getWidth(), getHeight());
        bufferg = (Graphics2D) bufferImage.getGraphics();

        updateAffineTransform();
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }
}
