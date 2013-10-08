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


package oripa;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;
import oripa.geom.*;


public class MainScreen extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener, ComponentListener {

    private boolean bDrawFaceID = false;
    private Image bufferImage;
    private Graphics2D bufferg;
    private Point2D preMousePoint; // Screen coordinates
    private Point2D currentMouseDraggingPoint = null;
    private Point2D.Double currentMousePointLogic = new Point2D.Double(); // Logic coordinates
    private double scale;
    private double transX;
    private double transY;
    // Temporary information when editing
    private OriLine prePickLine = null;
    private Vector2d prePickV = null;
    private Vector2d preprePickV = null;
    private Vector2d prepreprePickV = null;
    private Vector2d pickCandidateV = null;
    private OriLine pickCandidateL = null;
    private ArrayList<Vector2d> tmpOutline = new ArrayList<>(); // Contour line when editting
    private boolean dispGrid = true;
    // Affine transformation information
    private Dimension preSize;
    private AffineTransform affineTransform = new AffineTransform();
    private ArrayList<Vector2d> crossPoints = new ArrayList<>();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem popupItem_DivideFace = new JMenuItem("Dividing face");
    private JMenuItem popupItem_FlipFace = new JMenuItem("Flipping face");

    public MainScreen() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(this);

        scale = 1.5;
        setBackground(Color.white);

        popupItem_DivideFace.addActionListener(this);
        popup.add(popupItem_DivideFace);
        popupItem_FlipFace.addActionListener(this);
        popup.add(popupItem_FlipFace);
        preSize = getSize();
    }

    public void drawModel(Graphics2D g2d) {
        if (Config.FOR_STUDY) {
            if (bDrawFaceID) {
                g2d.setColor(Color.BLACK);
                for (OriFace face : ORIPA.doc.faces) {
                    g2d.drawString("" + face.tmpInt, (int) face.getCenter().x,
                            (int) face.getCenter().y);
                }
            }

            g2d.setColor(new Color(255, 210, 220));
            for (OriFace face : ORIPA.doc.faces) {
                if (face.tmpInt2 == 0) {
                    g2d.setColor(Color.RED);
                    g2d.fill(face.preOutline);
                } else {
                    g2d.setColor(face.color);
                }

                if (face.hasProblem) {
                    g2d.setColor(Color.RED);
                } else {
                    if (face.faceFront) {
                        g2d.setColor(new Color(255, 200, 200));
                    } else {
                        g2d.setColor(new Color(200, 200, 255));
                    }
                }

                g2d.fill(face.preOutline);
            }

            g2d.setColor(Color.BLACK);
            for (OriFace face : ORIPA.doc.faces) {
                g2d.drawString("" + face.z_order, (int) face.getCenter().x,
                        (int) face.getCenter().y);
            }

            g2d.setColor(Color.RED);
            for (OriVertex v : ORIPA.doc.vertices) {
                if (v.hasProblem) {
                    g2d.fill(new Rectangle2D.Double(v.p.x - 8.0 / scale,
                            v.p.y - 8.0 / scale, 16.0 / scale, 16.0 / scale));
                }
            }
        }
    }

    // update actual AffineTransform
    private void updateAffineTransform() {
        affineTransform.setToIdentity();
        affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
        affineTransform.scale(scale, scale);
        affineTransform.translate(transX, transY);
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

        // initialize the AffineTransform of bufferg
        bufferg.setTransform(new AffineTransform());

        // Clears the image buffer
        bufferg.setColor(Color.WHITE);
        bufferg.fillRect(0, 0, getWidth(), getHeight());

        // set the AffineTransform of buffer
        bufferg.setTransform(affineTransform);

        Graphics2D g2d = bufferg;

        if (ORIPA.doc.hasModel) {
            drawModel(g2d);
        }
        if (dispGrid) {
            drawGridLine(g2d);
        }

        g2d.setStroke(Config.STROKE_VALLEY);
        g2d.setColor(Color.black);
        for (OriLine line : ORIPA.doc.lines) {
            switch (line.type) {
                case OriLine.TYPE_NONE:
                    if (!Globals.dispAuxLines) {
                        continue;
                    }
                    g2d.setColor(Config.LINE_COLOR_AUX);
                    g2d.setStroke(Config.STROKE_CUT);
                    break;
                case OriLine.TYPE_CUT:
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(Config.STROKE_CUT);
                    break;
                case OriLine.TYPE_RIDGE:
                    if (!Globals.dispMVLines) {
                        continue;
                    }
                    g2d.setColor(Config.LINE_COLOR_RIDGE);
                    g2d.setStroke(Config.STROKE_RIDGE);
                    break;
                case OriLine.TYPE_VALLEY:
                    if (!Globals.dispMVLines) {
                        continue;
                    }
                    g2d.setColor(Config.LINE_COLOR_VALLEY);
                    g2d.setStroke(Config.STROKE_VALLEY);
                    break;
            }

            if ((Globals.editMode == Constants.EditMode.INPUT_LINE
                    && Globals.lineInputMode == Constants.LineInputMode.MIRROR
                    && line.selected)
                    || (Globals.editMode == Constants.EditMode.PICK_LINE
                    && line.selected)) {
                g2d.setColor(Config.LINE_COLOR_PICKED);
                g2d.setStroke(Config.STROKE_PICKED);
            }

            if (line == prePickLine) {
                g2d.setColor(Color.RED);
                g2d.setStroke(Config.STROKE_PICKED);
            } else if (line == pickCandidateL) {
                g2d.setColor(Config.LINE_COLOR_CANDIDATE);
                g2d.setStroke(Config.STROKE_PICKED);
            }

            g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x, line.p1.y));
        }

        // Shows of the outline of the editing
        int outlineVnum = tmpOutline.size();
        if (outlineVnum != 0) {
            g2d.setColor(Color.GREEN);
            g2d.setStroke(Config.STROKE_TMP_OUTLINE);
            for (int i = 0; i < outlineVnum - 1; i++) {
                Vector2d p0 = tmpOutline.get(i);
                Vector2d p1 = tmpOutline.get((i + 1) % outlineVnum);
                g2d.draw(new Line2D.Double(p0.x, p0.y, p1.x, p1.y));
            }

            Vector2d cv = pickCandidateV == null
                    ? new Vector2d(currentMousePointLogic.getX(), currentMousePointLogic.getY())
                    : pickCandidateV;
            g2d.draw(new Line2D.Double(tmpOutline.get(0).x, tmpOutline.get(0).y,
                    cv.x, cv.y));
            g2d.draw(new Line2D.Double(tmpOutline.get(outlineVnum - 1).x,
                    tmpOutline.get(outlineVnum - 1).y, cv.x, cv.y));
        }

        // Drawing of the vertices
        if (Globals.editMode == Constants.EditMode.ADD_VERTEX
                || Globals.editMode == Constants.EditMode.DELETE_VERTEX
                || Globals.dispVertex) {
            g2d.setColor(Color.BLACK);
            double vertexDrawSize = 2.0;
            for (OriLine line : ORIPA.doc.lines) {
                if (!Globals.dispAuxLines && line.type == OriLine.TYPE_NONE) {
                    continue;
                }
                if (!Globals.dispMVLines && (line.type == OriLine.TYPE_RIDGE
                        || line.type == OriLine.TYPE_VALLEY)) {
                    continue;
                }
                Vector2d v0 = line.p0;
                Vector2d v1 = line.p1;

                g2d.fill(new Rectangle2D.Double(v0.x - vertexDrawSize / scale,
                        v0.y - vertexDrawSize / scale, vertexDrawSize * 2 / scale,
                        vertexDrawSize * 2 / scale));
                g2d.fill(new Rectangle2D.Double(v1.x - vertexDrawSize / scale,
                        v1.y - vertexDrawSize / scale, vertexDrawSize * 2 / scale,
                        vertexDrawSize * 2 / scale));
            }
        }



        if (prepreprePickV != null) {
            g2d.setColor(Color.RED);
            g2d.fill(new Rectangle2D.Double(prepreprePickV.x - 5.0 / scale,
                    prepreprePickV.y - 5.0 / scale, 10.0 / scale, 10.0 / scale));
        }

        if (preprePickV != null) {
            g2d.setColor(Color.RED);
            g2d.fill(new Rectangle2D.Double(preprePickV.x - 5.0 / scale,
                    preprePickV.y - 5.0 / scale, 10.0 / scale, 10.0 / scale));
        }

        if (prePickV != null) {
            switch (Globals.inputLineType) {
                case OriLine.TYPE_NONE:
                    g2d.setColor(Config.LINE_COLOR_AUX);
                    g2d.setStroke(Config.STROKE_CUT);
                    break;
                case OriLine.TYPE_CUT:
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(Config.STROKE_CUT);
                    break;
                case OriLine.TYPE_RIDGE:
                    g2d.setColor(Config.LINE_COLOR_RIDGE);
                    g2d.setStroke(Config.STROKE_RIDGE);
                    break;
                case OriLine.TYPE_VALLEY:
                    g2d.setColor(Config.LINE_COLOR_VALLEY);
                    g2d.setStroke(Config.STROKE_VALLEY);
                    break;
            }
            g2d.fill(new Rectangle2D.Double(prePickV.x - 5.0 / scale,
                    prePickV.y - 5.0 / scale, 10.0 / scale, 10.0 / scale));

            if (Globals.lineInputMode == Constants.LineInputMode.ON_V
                    || Globals.lineInputMode == Constants.LineInputMode.DIRECT_V) {
                Vector2d cv = pickCandidateV == null
                        ? new Vector2d(currentMousePointLogic.getX(), currentMousePointLogic.getY())
                        : pickCandidateV;
                g2d.draw(new Line2D.Double(prePickV.x, prePickV.y,
                        cv.x, cv.y));
            } else if (Globals.lineInputMode == Constants.LineInputMode.OVERLAP_V) {
                if (pickCandidateV != null) {
                    Vector2d v = pickCandidateV;
                    Vector2d cv = new Vector2d((prePickV.x + v.x) / 2.0, (prePickV.y + v.y) / 2.0);
                    Vector2d dir = new Vector2d(prePickV.y - v.y, v.x - prePickV.x);
                    dir.normalize();
                    dir.scale(Constants.DEFAULT_PAPER_SIZE * 4);
                    g2d.draw(new Line2D.Double(cv.x - dir.x, cv.y - dir.y, cv.x + dir.x, cv.y + dir.y));
                }
            }
        }

        for (Vector2d v : crossPoints) {
            g2d.setColor(Color.RED);
            g2d.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0 / scale,
                    10.0 / scale, 10.0 / scale));
        }

        if (pickCandidateV != null) {
            g2d.setColor(Color.GREEN);
            g2d.fill(new Rectangle2D.Double(pickCandidateV.x - 5.0 / scale,
                    pickCandidateV.y - 5.0 / scale, 10.0 / scale, 10.0 / scale));
        }

        // display copy and paste target
        if (Globals.lineInputMode == Constants.LineInputMode.COPY_AND_PASTE) {
            double ox = ORIPA.doc.tmpSelectedLines.get(0).p0.x;
            double oy = ORIPA.doc.tmpSelectedLines.get(0).p0.y;
            g2d.setColor(Color.GREEN);
            g2d.fill(new Rectangle2D.Double(ox - 5.0 / scale, oy - 5.0 / scale,
                    10.0 / scale, 10.0 / scale));
            g2d.setColor(Color.MAGENTA);
            for (OriLine l : ORIPA.doc.tmpSelectedLines) {
                double mx, my;
                if (pickCandidateV != null) {
                    mx = pickCandidateV.x;
                    my = pickCandidateV.y;
                } else {
                    mx = currentMousePointLogic.x;
                    my = currentMousePointLogic.y;
                }
                double sx = mx + l.p0.x - ox;
                double sy = my + l.p0.y - oy;
                double ex = mx + l.p1.x - ox;
                double ey = my + l.p1.y - oy;

                g2d.draw(new Line2D.Double(sx, sy, ex, ey));
            }
        }

        if (Globals.editMode == Constants.EditMode.INPUT_LINE
                && Globals.lineInputMode == Constants.LineInputMode.BY_VALUE
                && Globals.subLineInputMode == Constants.SubLineInputMode.NONE
                && pickCandidateV != null) {
            try {
                double length = Double.valueOf(ORIPA.mainFrame.uiPanel.textFieldLength.getText());
                double angle = Math.toRadians(Double.valueOf(ORIPA.mainFrame.uiPanel.textFieldAngle.getText()));

                switch (Globals.inputLineType) {
                    case OriLine.TYPE_NONE:
                        g2d.setColor(Config.LINE_COLOR_AUX);
                        g2d.setStroke(Config.STROKE_CUT);
                        break;
                    case OriLine.TYPE_CUT:
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(Config.STROKE_CUT);
                        break;
                    case OriLine.TYPE_RIDGE:
                        g2d.setColor(Config.LINE_COLOR_RIDGE);
                        g2d.setStroke(Config.STROKE_RIDGE);
                        break;
                    case OriLine.TYPE_VALLEY:
                        g2d.setColor(Config.LINE_COLOR_VALLEY);
                        g2d.setStroke(Config.STROKE_VALLEY);
                        break;
                }
                Vector2d v = pickCandidateV;
                Vector2d dir = new Vector2d(Math.cos(angle), -Math.sin(angle));
                dir.scale(length);
                g2d.draw(new Line2D.Double(v.x, v.y, v.x + dir.x, v.y + dir.y));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Globals.bDispCrossLine) {
            if (!ORIPA.doc.crossLines.isEmpty()) {
                g2d.setStroke(Config.STROKE_TMP_OUTLINE);
                g2d.setColor(Color.MAGENTA);

                for (OriLine line : ORIPA.doc.crossLines) {
                    Vector2d v0 = line.p0;
                    Vector2d v1 = line.p1;

                    g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));

                }
            }
        }

        if (currentMouseDraggingPoint != null
                && (Globals.editMode == Constants.EditMode.PICK_LINE
                || Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE)) {
            Point2D.Double sp = new Point2D.Double();
            Point2D.Double ep = new Point2D.Double();
            try {
                affineTransform.inverseTransform(preMousePoint, sp);
                affineTransform.inverseTransform(currentMouseDraggingPoint, ep);
                g2d.setStroke(Config.STROKE_SELECT_BY_AREA);
                g2d.setColor(Color.BLACK);
                double sx = Math.min(sp.x, ep.x);
                double sy = Math.min(sp.y, ep.y);
                double w = Math.abs(sp.x - ep.x);
                double h = Math.abs(sp.y - ep.y);
                g2d.draw(new Rectangle2D.Double(sx, sy, w, h));
            } catch (Exception ex) {
            }

        }

        // Line that links the pair of unsetled faces
        if (Config.FOR_STUDY) {
            if (ORIPA.doc.overlapRelation != null) {
                g2d.setStroke(Config.STROKE_RIDGE);
                g2d.setColor(Color.MAGENTA);
                int size = ORIPA.doc.faces.size();
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {
                        if (ORIPA.doc.overlapRelation[i][j] == Doc.UNDEFINED) {
                            Vector2d v0 = ORIPA.doc.faces.get(i).getCenter();
                            Vector2d v1 = ORIPA.doc.faces.get(j).getCenter();
                            g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));

                        }
                    }
                }
            }
        }

        g.drawImage(bufferImage, 0, 0, this);

        if (pickCandidateV != null) {
            g.setColor(Color.BLACK);
            g.drawString("(" + pickCandidateV.x + "," + pickCandidateV.y + ")", 0, 10);
        }

    }

    public void setDispGrid(boolean dispGrid) {
        this.dispGrid = dispGrid;
        resetPickElements();
        repaint();
    }

    private void drawGridLine(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(Config.STROKE_GRID);

        int lineNum = Globals.gridDivNum;
        double step = ORIPA.doc.size / lineNum;
        for (int i = 1; i < lineNum; i++) {
            g2d.draw(new Line2D.Double(step * i - ORIPA.doc.size / 2.0, -ORIPA.doc.size / 2.0, step * i - ORIPA.doc.size / 2.0, ORIPA.doc.size / 2.0));
            g2d.draw(new Line2D.Double(-ORIPA.doc.size / 2.0, step * i - ORIPA.doc.size / 2.0, ORIPA.doc.size / 2.0, step * i - ORIPA.doc.size / 2.0));
        }
    }

    private boolean pickPointOnLine(Point2D.Double p, Object[] line_vertex) {
        double minDistance = Double.MAX_VALUE;
        OriLine bestLine = null;
        Vector2d nearestPoint = new Vector2d();
        Vector2d tmpNearestPoint = new Vector2d();

        for (OriLine line : ORIPA.doc.lines) {
            double dist = GeomUtil.DistancePointToSegment(new Vector2d(p.x, p.y), line.p0, line.p1, tmpNearestPoint);
            if (dist < minDistance) {
                minDistance = dist;
                bestLine = line;
                nearestPoint.set(tmpNearestPoint);
            }
        }

        if (minDistance / scale < 5) {
            line_vertex[0] = bestLine;
            line_vertex[1] = nearestPoint;
            return true;
        } else {
            return false;
        }
    }

    private Vector2d pickVertex(Point2D.Double p) {
        double minDistance = Double.MAX_VALUE;
        Vector2d minPosition = new Vector2d();

        for (OriLine line : ORIPA.doc.lines) {
            double dist0 = p.distance(line.p0.x, line.p0.y);
            if (dist0 < minDistance) {
                minDistance = dist0;
                minPosition.set(line.p0);
            }
            double dist1 = p.distance(line.p1.x, line.p1.y);
            if (dist1 < minDistance) {
                minDistance = dist1;
                minPosition.set(line.p1);
            }
        }

        if (dispGrid) {
            double step = ORIPA.doc.size / Globals.gridDivNum;
            for (int ix = 0; ix < Globals.gridDivNum + 1; ix++) {
                for (int iy = 0; iy < Globals.gridDivNum + 1; iy++) {
                    double x = -ORIPA.doc.size / 2 + step * ix;
                    double y = -ORIPA.doc.size / 2 + step * iy;
                    double dist = p.distance(x, y);
                    if (dist < minDistance) {
                        minDistance = dist;
                        minPosition.set(x, y);
                    }
                }
            }
        }

        if (minDistance < 10.0 / scale) {
            return minPosition;
        } else {
            return null;
        }
    }

    public void modeChanged() {
        resetPickElements();
        repaint();
    }

    public void resetPickElements() {
        prePickV = null;
        prePickLine = null;
        pickCandidateV = null;
        preprePickV = null;
        prepreprePickV = null;
        crossPoints.clear();
        tmpOutline.clear();
    }

    // returns the OriLine sufficiently closer to point p
    private OriLine pickLine(Point2D.Double p) {
        double minDistance = Double.MAX_VALUE;
        OriLine bestLine = null;

        for (OriLine line : ORIPA.doc.lines) {
            if (Globals.editMode == Constants.EditMode.DELETE_LINE) {
            }
            double dist = GeomUtil.DistancePointToSegment(new Vector2d(p.x, p.y), line.p0, line.p1);
            if (dist < minDistance) {
                minDistance = dist;
                bestLine = line;
            }
        }

        if (minDistance / scale < 10) {
            return bestLine;
        } else {
            return null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
            if (prepreprePickV != null) {
                prepreprePickV = null;
                repaint();
            } else if (preprePickV != null) {
                preprePickV = null;
                repaint();
            } else if (prePickV != null) {
                prePickV = null;
                repaint();
            } else if (!tmpOutline.isEmpty()) {
                tmpOutline.remove(tmpOutline.size() - 1);
                repaint();
            }

            if (Globals.editMode == Constants.EditMode.INPUT_LINE
                    && Globals.lineInputMode == Constants.LineInputMode.COPY_AND_PASTE) {
                Globals.lineInputMode = Constants.LineInputMode.DIRECT_V;
                ORIPA.mainFrame.uiPanel.modeChanged();
                ORIPA.doc.tmpSelectedLines.clear();
                repaint();
            }
            return;
        }

        if (Globals.editMode == Constants.EditMode.NONE) {
            return;
        }

        // Gets the logical coordinates of the click
        Point2D.Double clickPoint = new Point2D.Double();
        try {
            affineTransform.inverseTransform(e.getPoint(), clickPoint);
        } catch (Exception ex) {
            return;
        }

        if (Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE) {
            OriLine l = pickLine(clickPoint);
            if (l == null) {
                return;
            }
            ORIPA.doc.alterLineType(l);
            repaint();
            return;
        } else if (Globals.editMode == Constants.EditMode.DELETE_LINE) {
            OriLine l = pickLine(clickPoint);
            if (l != null) {
                ORIPA.doc.pushUndoInfo();
                ORIPA.doc.removeLine(l);
            }
            repaint();
            return;

        } else if (Globals.editMode == Constants.EditMode.PICK_LINE) {
            OriLine l = pickLine(clickPoint);
            if (l != null) {
                // If it is out of the redered model frame, do nothing
                if (!Globals.dispMVLines && (l.type == OriLine.TYPE_RIDGE || l.type == OriLine.TYPE_VALLEY)) {
                    return;
                }
                if (!Globals.dispAuxLines && l.type == OriLine.TYPE_NONE) {
                    return;
                }


                l.selected = !l.selected;
            }
            repaint();
            return;

        } else if (Globals.editMode == Constants.EditMode.ADD_VERTEX) {
            Object[] line_vertex = new Object[2];
            if (pickPointOnLine(currentMousePointLogic, line_vertex)) {
                ORIPA.doc.pushUndoInfo();
                if (!ORIPA.doc.addVertexOnLine((OriLine) line_vertex[0], (Vector2d) line_vertex[1])) {
                    ORIPA.doc.popUndoInfo();
                }
                this.pickCandidateV = null;
                repaint();
            }
            return;
        } else if (Globals.editMode == Constants.EditMode.DELETE_VERTEX) {
            Vector2d v = pickVertex(clickPoint);
            if (v != null) {
                ORIPA.doc.pushUndoInfo();
                ORIPA.doc.removeVertex(v);
                repaint();
            }
            return;
        } else if (Globals.editMode == Constants.EditMode.EDIT_OUTLINE) {
            Vector2d v = pickVertex(currentMousePointLogic);
            // Add the outline being edited
            if (v != null) {
                // Closes if it matches an existing point
                boolean bClose = false;
                for (Vector2d tv : tmpOutline) {
                    if (GeomUtil.Distance(v, tv) < 1) {
                        bClose = true;
                        break;
                    }
                }

                if (bClose) {
                    if (tmpOutline.size() > 2) {
                        closeTmpOutline();
                    }
                } else {
                    tmpOutline.add(v);
                }
                repaint();
            }
            return;
        } else if (Globals.editMode == Constants.EditMode.INPUT_LINE) {
            if (Globals.lineInputMode == Constants.LineInputMode.DIRECT_V) {
                Vector2d v = pickVertex(clickPoint);

                if (v == null) {
                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        // If Ctrl is pressed, an arbitrary point can be choosed
                        OriLine l = pickLine(clickPoint);
                        if (l != null) {
                            v = new Vector2d();
                            Vector2d cp = new Vector2d(clickPoint.x, clickPoint.y);
                            GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, v);
                        }
                    }
                }

                if (v != null) {
                    if (prePickV == null) {
                        prePickV = v;
                    } else {
                        OriLine line = new OriLine(prePickV, v, Globals.inputLineType);
                        ORIPA.doc.pushUndoInfo();
                        ORIPA.doc.addLine(line);
                        prePickV = null;
                    }
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.PBISECTOR) {
                Vector2d v = pickVertex(clickPoint);

                if (v != null) {
                    if (prePickV == null) {
                        prePickV = v;
                    } else {
                        ORIPA.doc.pushUndoInfo();
                        ORIPA.doc.addPBisector(prePickV, v);
                        prePickV = null;
                    }
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.COPY_AND_PASTE) {
                Vector2d v = pickVertex(clickPoint);
                if (v != null) {
                    if (!ORIPA.doc.tmpSelectedLines.isEmpty()) {
                        ORIPA.doc.pushUndoInfo();
                        double ox = ORIPA.doc.tmpSelectedLines.get(0).p0.x;
                        double oy = ORIPA.doc.tmpSelectedLines.get(0).p0.y;

                        for (OriLine l : ORIPA.doc.tmpSelectedLines) {
                            double mx = v.x;
                            double my = v.y;

                            double sx = mx + l.p0.x - ox;
                            double sy = my + l.p0.y - oy;
                            double ex = mx + l.p1.x - ox;
                            double ey = my + l.p1.y - oy;

                            OriLine line = new OriLine(sx, sy, ex, ey, l.type);
                            ORIPA.doc.addLine(line);
                        }
                    }
                }


            } else if (Globals.lineInputMode == Constants.LineInputMode.ON_V) {
                Vector2d v = pickVertex(clickPoint);

                if (v == null) {
                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        // If Ctrl is pressed, an arbitrary point can be choosed
                        OriLine l = pickLine(clickPoint);
                        if (l != null) {
                            v = new Vector2d();
                            Vector2d cp = new Vector2d(clickPoint.x, clickPoint.y);
                            GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, v);
                        }
                    }
                }

                if (v != null) {
                    if (prePickV == null) {
                        prePickV = v;
                    } else {
                        Vector2d dir = new Vector2d(v.x - prePickV.x, v.y - prePickV.y);
                        dir.normalize();
                        dir.scale(Constants.DEFAULT_PAPER_SIZE * 8);
                        OriLine line = new OriLine(prePickV.x - dir.x, prePickV.y - dir.y,
                                prePickV.x + dir.x, prePickV.y + dir.y, Globals.inputLineType);
                        if (GeomUtil.clipLine(line, ORIPA.doc.size / 2)) {
                            ORIPA.doc.pushUndoInfo();
                            ORIPA.doc.addLine(line);
                        }
                        prePickV = null;
                    }
                }

            } else if (Globals.lineInputMode == Constants.LineInputMode.SYMMETRIC_LINE) {
                Vector2d v = pickVertex(clickPoint);
                if (v != null) {
                    if (preprePickV == null) {
                        preprePickV = v;
                    } else if (prePickV == null) {
                        prePickV = v;
                    } else {
                        ORIPA.doc.pushUndoInfo();
                        if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                            ORIPA.doc.addSymmetricLineAutoWalk(preprePickV, prePickV, v, 0, preprePickV);
                        } else {
                            ORIPA.doc.addSymmetricLine(preprePickV, prePickV, v);
                        }
                        prePickV = null;
                        preprePickV = null;
                    }

                }

            } else if (Globals.lineInputMode == Constants.LineInputMode.TRIANGLE_SPLIT) {
                Vector2d v = pickVertex(clickPoint);
                if (v != null) {
                    if (preprePickV == null) {
                        preprePickV = v;
                    } else if (prePickV == null) {
                        prePickV = v;
                    } else {
                        ORIPA.doc.pushUndoInfo();
                        ORIPA.doc.addTriangleDivideLines(v, prePickV, preprePickV);
                        prePickV = null;
                        preprePickV = null;
                    }

                }

            } else if (Globals.lineInputMode == Constants.LineInputMode.BISECTOR) {
                if (prepreprePickV == null) {
                    prepreprePickV = pickVertex(clickPoint);
                    if (prepreprePickV != null) {
                        pickCandidateV = null;
                    }
                } else if (preprePickV == null) {
                    preprePickV = pickVertex(clickPoint);
                } else if (prePickV == null) {
                    prePickV = pickVertex(clickPoint);
                } else {
                    OriLine l = pickLine(clickPoint);
                    if (l != null) {
                        ORIPA.doc.pushUndoInfo();
                        ORIPA.doc.addBisectorLine(prepreprePickV, preprePickV, prePickV, l);
                        prePickV = null;
                        preprePickV = null;
                        prepreprePickV = null;
                    }
                }

            } else if (Globals.lineInputMode == Constants.LineInputMode.VERTICAL_LINE) {
                if (prePickV == null) {
                    prePickV = pickVertex(clickPoint);
                    if (prePickV != null) {
                        pickCandidateV = null;
                    }
                    pickCandidateV = null;
                } else {
                    OriLine l = pickLine(clickPoint);
                    if (l != null) {
                        OriLine vl = GeomUtil.getVerticalLine(prePickV, l, Globals.inputLineType);
                        ORIPA.doc.pushUndoInfo();
                        ORIPA.doc.addLine(vl);
                        prePickV = null;
                    }
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.MIRROR) {
                OriLine l = pickLine(clickPoint);
                if (l != null) {
                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        ORIPA.doc.mirrorCopyBy(l);
                        ORIPA.doc.resetSelectedOriLines();
                        ORIPA.doc.pushUndoInfo();
                    } else {
                        l.selected = !l.selected;
                    }
                }
                repaint();
                return;
            } else if (Globals.lineInputMode == Constants.LineInputMode.BY_VALUE) {
                if (Globals.subLineInputMode == Constants.SubLineInputMode.NONE) {
                    Vector2d v = pickVertex(clickPoint);
                    if (v != null) {
                        double length;
                        double angle;
                        try {
                            length = Double.valueOf(ORIPA.mainFrame.uiPanel.textFieldLength.getText());
                            angle = Double.valueOf(ORIPA.mainFrame.uiPanel.textFieldAngle.getText());

                            if (length > 0) {
                                OriLine vl = GeomUtil.getLineByValue(v, length, -angle, Globals.inputLineType);

                                ORIPA.doc.pushUndoInfo();
                                ORIPA.doc.addLine(vl);
                            }
                        } catch (Exception ex) {
                        }
                    }
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_LENGTH) {
                    Vector2d v = pickVertex(clickPoint);
                    if (v != null) {
                        if (prePickV == null) {
                            prePickV = v;
                        } else {
                            double length = GeomUtil.Distance(prePickV, v);
                            ORIPA.mainFrame.uiPanel.textFieldLength.setValue(new Double(length));
                            Globals.subLineInputMode = Constants.SubLineInputMode.NONE;
                            ORIPA.mainFrame.uiPanel.modeChanged();
                        }
                    }
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_ANGLE) {
                    Vector2d v = pickVertex(clickPoint);
                    if (v != null) {
                        if (preprePickV == null) {
                            preprePickV = v;
                        } else if (prePickV == null) {
                            prePickV = v;
                        } else {
                            Vector2d dir1 = new Vector2d(v);
                            Vector2d dir2 = new Vector2d(preprePickV);
                            dir1.sub(prePickV);
                            dir2.sub(prePickV);

                            double deg_angle = Math.toDegrees(dir1.angle(dir2));
                            ORIPA.mainFrame.uiPanel.textFieldAngle.setValue(new Double(deg_angle));
                            Globals.subLineInputMode = Constants.SubLineInputMode.NONE;
                            ORIPA.mainFrame.uiPanel.modeChanged();
                        }
                    }
                }
            }
        }

        repaint();
    }

    private Vector2d isOnTmpOutlineLoop(Vector2d v) {
        for (int i = 0; i < tmpOutline.size(); i++) {
            Vector2d p0 = tmpOutline.get(i);
            Vector2d p1 = tmpOutline.get((i + 1) % tmpOutline.size());
            if (GeomUtil.DistancePointToLine(v, new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y))) < ORIPA.doc.size * 0.001) {
                return p0;
            }
        }
        return null;
    }

    private boolean isOutsideOfTmpOutlineLoop(Vector2d v) {
        Vector2d p0 = tmpOutline.get(0);
        Vector2d p1 = tmpOutline.get(1);

        boolean CCWFlg = GeomUtil.CCWcheck(p0, p1, v);
        for (int i = 1; i < tmpOutline.size(); i++) {
            p0 = tmpOutline.get(i);
            p1 = tmpOutline.get((i + 1) % tmpOutline.size());
            if (CCWFlg != GeomUtil.CCWcheck(p0, p1, v)) {
                return true;
            }
        }
        return false;
    }

    private void closeTmpOutline() {
        ORIPA.doc.pushUndoInfo();
        // Delete the current outline
        ArrayList<OriLine> outlines = new ArrayList<>();
        for (OriLine line : ORIPA.doc.lines) {
            if (line.type == OriLine.TYPE_CUT) {
                outlines.add(line);
            }
        }
        for (OriLine line : outlines) {
            ORIPA.doc.lines.remove(line);
        }

        // Update the contour line
        int outlineVnum = tmpOutline.size();
        for (int i = 0; i < outlineVnum; i++) {
            OriLine line = new OriLine(tmpOutline.get(i),
                    tmpOutline.get((i + 1) % outlineVnum), OriLine.TYPE_CUT);
            ORIPA.doc.addLine(line);
        }

        // To delete a segment out of the contour
        while (true) {
            boolean bDeleteLine = false;
            for (OriLine line : ORIPA.doc.lines) {
                if (line.type == OriLine.TYPE_CUT) {
                    continue;
                }
                Vector2d OnPoint0 = isOnTmpOutlineLoop(line.p0);
                Vector2d OnPoint1 = isOnTmpOutlineLoop(line.p1);

                if (OnPoint0 != null && OnPoint0 == OnPoint1) {
                    ORIPA.doc.removeLine(line);
                    bDeleteLine = true;
                    break;
                }

                if ((OnPoint0 == null && isOutsideOfTmpOutlineLoop(line.p0))
                        || (OnPoint1 == null && isOutsideOfTmpOutlineLoop(line.p1))) {
                    ORIPA.doc.removeLine(line);
                    bDeleteLine = true;
                    break;
                }
            }
            if (!bDeleteLine) {
                break;
            }
        }

        tmpOutline.clear();
        Globals.editMode = Globals.preEditMode;
        ORIPA.mainFrame.uiPanel.modeChanged();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        preMousePoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Retangular Selection
        if (Globals.editMode == Constants.EditMode.PICK_LINE
                || Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE) {
            ArrayList<OriLine> selectedLines = new ArrayList<>();
            Point2D.Double sp = new Point2D.Double();
            Point2D.Double ep = new Point2D.Double();
            try {
                affineTransform.inverseTransform(preMousePoint, sp);
                affineTransform.inverseTransform(currentMouseDraggingPoint, ep);

                RectangleClipper clipper = new RectangleClipper(Math.min(sp.x, ep.x),
                        Math.min(sp.y, ep.y),
                        Math.max(sp.x, ep.x),
                        Math.max(sp.y, ep.y));
                for (OriLine l : ORIPA.doc.lines) {

                    // Selection process
                    if (Globals.editMode == Constants.EditMode.PICK_LINE) {

                        if (l.type == OriLine.TYPE_CUT) {
                            continue;
                        }
                        // Don't select if the line is hidden
                        if (!Globals.dispMVLines && (l.type == OriLine.TYPE_RIDGE
                                || l.type == OriLine.TYPE_VALLEY)) {
                            continue;
                        }
                        if (!Globals.dispAuxLines && l.type == OriLine.TYPE_NONE) {
                            continue;
                        }

                        if (clipper.clipTest(l)) {
                            l.selected = true;
                        }
                    } else if (Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE) {
                        if (clipper.clipTest(l)) {
                            selectedLines.add(l);
                        }
                    }
                }
            } catch (Exception ex) {
            }

            if (!selectedLines.isEmpty()) {
                ORIPA.doc.pushUndoInfo();
                for (OriLine l : selectedLines) {
                    // Change line type
                    ORIPA.doc.alterLineType(l);
                }

            }

        }

        currentMouseDraggingPoint = null;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0 && // zoom
                (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {

            double moved = e.getX() - preMousePoint.getX() + e.getY() - preMousePoint.getY();
            scale += moved / 150.0;
            if (scale < 0.01) {
                scale = 0.01;
            }

            preMousePoint = e.getPoint();
            updateAffineTransform();
            repaint();
        } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
            transX += (double) (e.getX() - preMousePoint.getX()) / scale;
            transY += (double) (e.getY() - preMousePoint.getY()) / scale;
            preMousePoint = e.getPoint();
            updateAffineTransform();
            repaint();
        } else if (Globals.editMode == Constants.EditMode.PICK_LINE
                || Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE) {
            currentMouseDraggingPoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Gets the value of the current logical coordinates of the mouse
        try {
            affineTransform.inverseTransform(e.getPoint(), currentMousePointLogic);
        } catch (Exception ex) {
            return;
        }

        if (Globals.editMode == Constants.EditMode.INPUT_LINE) {
            if (Globals.lineInputMode == Constants.LineInputMode.DIRECT_V
                    || Globals.lineInputMode == Constants.LineInputMode.ON_V
                    || Globals.lineInputMode == Constants.LineInputMode.OVERLAP_V
                    || Globals.lineInputMode == Constants.LineInputMode.PBISECTOR) {

                Vector2d preV = pickCandidateV;
                pickCandidateV = this.pickVertex(currentMousePointLogic);


                if (pickCandidateV == null) {
                    if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                        // If Ctrl is pressed, an arbitrary point can be selected
                        OriLine l = pickLine(currentMousePointLogic);
                        if (l != null) {
                            pickCandidateV = new Vector2d();
                            Vector2d cp = new Vector2d(currentMousePointLogic.x, currentMousePointLogic.y);
                            GeomUtil.DistancePointToSegment(cp, l.p0, l.p1, pickCandidateV);
                        }
                    }
                }
                if (pickCandidateV != preV || prePickV != null) {
                    repaint();
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.COPY_AND_PASTE) {
                pickCandidateV = this.pickVertex(currentMousePointLogic);
                repaint();
            } else if (Globals.lineInputMode == Constants.LineInputMode.SYMMETRIC_LINE) {
                Vector2d preV = pickCandidateV;
                pickCandidateV = this.pickVertex(currentMousePointLogic);
                if (pickCandidateV != preV) {
                    repaint();
                }

            } else if (Globals.lineInputMode == Constants.LineInputMode.TRIANGLE_SPLIT) {
                Vector2d preV = pickCandidateV;
                pickCandidateV = this.pickVertex(currentMousePointLogic);
                if (pickCandidateV != preV) {
                    repaint();
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.BISECTOR) {
                if (prePickV == null) {
                    Vector2d preV = pickCandidateV;
                    pickCandidateV = this.pickVertex(currentMousePointLogic);
                    if (pickCandidateV != preV || prePickV != null) {
                        repaint();
                    }
                } else {
                    OriLine preLine = pickCandidateL;
                    pickCandidateL = pickLine(currentMousePointLogic);
                    if (preLine != pickCandidateL) {
                        repaint();
                    }
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.VERTICAL_LINE) {
                if (prePickV == null) {
                    Vector2d preV = pickCandidateV;
                    pickCandidateV = this.pickVertex(currentMousePointLogic);
                    if (pickCandidateV != preV || prePickV != null) {
                        repaint();
                    }
                } else {
                    OriLine preLine = pickCandidateL;
                    pickCandidateL = pickLine(currentMousePointLogic);
                    if (preLine != pickCandidateL) {
                        repaint();
                    }
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.MIRROR) {
                OriLine preLine = pickCandidateL;
                pickCandidateL = pickLine(currentMousePointLogic);
                if (preLine != pickCandidateL) {
                    repaint();
                }
            } else if (Globals.lineInputMode == Constants.LineInputMode.BY_VALUE) {
                if (Globals.subLineInputMode == Constants.SubLineInputMode.NONE) {
                    Vector2d preV = pickCandidateV;
                    pickCandidateV = this.pickVertex(currentMousePointLogic);
                    if (pickCandidateV != preV) {
                        repaint();
                    }
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_LENGTH) {
                    Vector2d preV = pickCandidateV;
                    pickCandidateV = this.pickVertex(currentMousePointLogic);
                    if (pickCandidateV != preV || prePickV != null) {
                        repaint();
                    }
                } else if (Globals.subLineInputMode == Constants.SubLineInputMode.PICK_ANGLE) {
                    Vector2d preV = pickCandidateV;
                    pickCandidateV = this.pickVertex(currentMousePointLogic);
                    if (pickCandidateV != preV) {
                        repaint();
                    }

                }
            }
        } else if (Globals.editMode == Constants.EditMode.DELETE_LINE) {
            OriLine preLine = pickCandidateL;
            pickCandidateL = pickLine(currentMousePointLogic);
            if (preLine != pickCandidateL) {
                repaint();
            }
        } else if (Globals.editMode == Constants.EditMode.PICK_LINE) {
            OriLine preLine = pickCandidateL;
            pickCandidateL = pickLine(currentMousePointLogic);
            if (preLine != pickCandidateL) {
                repaint();
            }
        } else if (Globals.editMode == Constants.EditMode.ADD_VERTEX) {
            Object[] line_vertex = new Object[2];
            if (pickPointOnLine(currentMousePointLogic, line_vertex)) {
                pickCandidateV = (Vector2d) line_vertex[1];
                repaint();
            } else {
                if (pickCandidateV != null) {
                    pickCandidateV = null;
                    repaint();
                }
            }
        } else if (Globals.editMode == Constants.EditMode.DELETE_VERTEX) {
            Vector2d preV = pickCandidateV;
            pickCandidateV = this.pickVertex(currentMousePointLogic);
            if (pickCandidateV != preV || prePickV != null) {
                repaint();
            }
        } else if (Globals.editMode == Constants.EditMode.EDIT_OUTLINE) {
            pickCandidateV = this.pickVertex(currentMousePointLogic);
            repaint();
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
    public void actionPerformed(ActionEvent ae) {
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        preSize = getSize();

        // Update of the logical coordinates of the center of the screen
        transX = transX - preSize.width * 0.5 + getWidth() * 0.5;
        transY = transY - preSize.height * 0.5 + getHeight() * 0.5;

        // Updating the image buffer
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
