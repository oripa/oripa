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

package oripa.view.estimation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriVertex;
import oripa.fold.OrigamiModel;
import oripa.paint.core.LineSetting;
import oripa.paint.core.PaintConfig;
import oripa.value.OriLine;
import oripa.corrugation.CorrugationChecker;
import oripa.corrugation.EdgePair;

/**
 * A screen to show the results of corrugation check.
 * @author Koji
 *
 */
public class CorrugationScreen extends JPanel
        implements ComponentListener {

    private boolean bDrawFaceID = false;
    private Image bufferImage;
    private Graphics2D bufferg;
    private double scale;
    private double transX;
    private double transY;
    // Temporary information when editing
    private ArrayList<Vector2d> tmpOutline = new ArrayList<>(); // Contour line in the edit
    private boolean dispGrid = true;
    // Affine transformation information
    private Dimension preSize;
    private AffineTransform affineTransform = new AffineTransform();
    private ArrayList<Vector2d> crossPoints = new ArrayList<>();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem popupItem_DivideFace = new JMenuItem("Face division");
    private JMenuItem popupItem_FlipFace = new JMenuItem("Face Inversion");

    private OrigamiModel origamiModel = null;
    private Collection<OriLine> creasePattern = null;
    private CorrugationChecker corrugationChecker = null;
    //    private FoldedModelInfo foldedModelInfo = null;
    
    CorrugationScreen() {
    	
        addComponentListener(this);

        scale = 1.5;
        setBackground(Color.white);

        popup.add(popupItem_DivideFace);
        popup.add(popupItem_FlipFace);
        preSize = getSize();
    }

    public void showModel(
    		OrigamiModel origamiModel, 
    		Collection<OriLine> creasePattern,  //, FoldedModelInfo foldedModelInfo
    		CorrugationChecker corrugationChecker
    		) {
    	this.origamiModel = origamiModel;
    	this.creasePattern = creasePattern;
    	this.corrugationChecker = corrugationChecker;
//    	this.foldedModelInfo = foldedModelInfo;
    	
    	this.setVisible(true);
    }

 

    public void drawModel(Graphics2D g2d) {
    	if (origamiModel == null) {
    		return;
    	}
    	
    	List<OriFace> faces = origamiModel.getFaces();
        List<OriVertex> vertices = origamiModel.getVertices();

        for (OriFace face : faces) {
            g2d.setColor(new Color(255, 210, 210));
            g2d.fill(face.preOutline);
        }

        g2d.setColor(Color.RED);

        for (OriFace face: corrugationChecker.getFaceFailures()) {
        	g2d.fill(face.preOutline);	
        }
        
        for (OriVertex v : corrugationChecker.getVertexTypeFailures()) {
        	g2d.fill(new Rectangle2D.Double(v.preP.x - 8.0 / scale, 	
        			v.preP.y - 8.0 / scale, 16.0 / scale, 16.0 / scale));
        }

        for (OriVertex v: corrugationChecker.getVertexEdgeCountFailures()) {
        	BasicStroke superwideStroke = new BasicStroke(6.0f);
        	g2d.setStroke(superwideStroke);
        	g2d.setColor(Color.BLACK);
        	g2d.draw(new Line2D.Double(v.preP.x - 8.0 / scale, v.preP.y - 8.0 / scale, v.preP.x + 8.0 / scale, v.preP.y + 8.0 / scale));
        	g2d.draw(new Line2D.Double(v.preP.x + 8.0 / scale, v.preP.y - 8.0 / scale, v.preP.x - 8.0 / scale, v.preP.y + 8.0 / scale));        	
        }
            
        for (EdgePair e: corrugationChecker.getVertexAngleFailures()) {
        	OriEdge e1 = e.first(); 
        	OriEdge e2 = e.second();
        	OriVertex v1 = e1.sv;
        	OriVertex v2 = e1.ev;
        	OriVertex v3 = e2.sv;
        	OriVertex v4 = e2.ev;
        	// make the first argument the apex of the angle
        	OriVertex apex, end1, end2;
        	if (v1 == v3) {
        		apex = v1; end1 = v2; end2 = v4;
        	} else if (v1 == v4) {
        		apex = v1; end1 = v2; end2 = v3;
        	} else if (v2 == v3) {
        		apex = v2; end1 = v1; end2 = v4;
        	} else {
        		apex = v2; end1 = v1; end2 = v3;
        	}        	
        	BasicStroke wideStroke = new BasicStroke(4.0f);
        	g2d.setColor(Color.RED);
        	g2d.setStroke(wideStroke);
        	g2d.draw(new Line2D.Double(apex.preP.x, apex.preP.y, (apex.preP.x + end1.preP.x)/2.0, (apex.preP.y + end1.preP.y)/2.0));
        	g2d.draw(new Line2D.Double(apex.preP.x, apex.preP.y, (apex.preP.x + end2.preP.x)/2.0, (apex.preP.y + end2.preP.y)/2.0));
        }
 
    }

    // To update the current AffineTransform
    private void updateAffineTransform() {
        affineTransform.setToIdentity(); 
        affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5); 
        affineTransform.scale(scale, scale);    
        affineTransform.translate(transX, transY); 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferg = (Graphics2D) bufferImage.getGraphics();
            updateAffineTransform();
            preSize = getSize();
        }

        bufferg.setTransform(new AffineTransform());

        bufferg.setColor(Color.WHITE);
        bufferg.fillRect(0, 0, getWidth(), getHeight());

        bufferg.setTransform(affineTransform);

        Graphics2D g2d = bufferg;

        		
        g2d.setStroke(LineSetting.STROKE_VALLEY);
        g2d.setColor(Color.black);
        for (OriLine line : creasePattern) {
            switch (line.typeVal) {
                case OriLine.TYPE_NONE:
                    if (!PaintConfig.dispAuxLines) {
                        continue;
                    }
                    g2d.setColor(LineSetting.LINE_COLOR_AUX);
                    g2d.setStroke(LineSetting.STROKE_CUT);
                    break;
                case OriLine.TYPE_CUT:
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(LineSetting.STROKE_CUT);
                    break;
                case OriLine.TYPE_RIDGE:
                    if (!PaintConfig.dispMVLines) {
                        continue;
                    }
                    g2d.setColor(LineSetting.LINE_COLOR_RIDGE);
                    g2d.setStroke(LineSetting.STROKE_RIDGE);
                    break;
                case OriLine.TYPE_VALLEY:
                    if (!PaintConfig.dispMVLines) {
                        continue;
                    }
                    g2d.setColor(LineSetting.LINE_COLOR_VALLEY);
                    g2d.setStroke(LineSetting.STROKE_VALLEY);
                    break;
            }


            g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x, line.p1.y));
        }


        // Drawing of the vertices
        List<OriVertex> vertices = origamiModel.getVertices();

        for (OriVertex v : vertices) {
            double vertexDrawSize = 2.0;
            g2d.setColor(Color.BLACK);
            g2d.fill(new Rectangle2D.Double(v.preP.x - vertexDrawSize / scale, v.preP.y - vertexDrawSize / scale, vertexDrawSize * 2 / scale, vertexDrawSize * 2 / scale));
        }

        for (Vector2d v : crossPoints) {
            g2d.setColor(Color.RED);
            g2d.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0 / scale, 10.0 / scale, 10.0 / scale));
        }

        drawModel(g2d);

        g.drawImage(bufferImage, 0, 0, this);
    }

    public void setDispGrid(boolean dispGrid) {
        this.dispGrid = dispGrid;
        resetPickElements();
        repaint();
    }

    public void modeChanged() {

        repaint();
    }

    public void resetPickElements() {
        crossPoints.clear();
        tmpOutline.clear();
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
