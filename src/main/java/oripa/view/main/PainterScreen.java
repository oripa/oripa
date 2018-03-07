/*
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

package oripa.view.main;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;
import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.mouse.MouseUtility;
import oripa.paint.EditMode;
import oripa.paint.core.LineSetting;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PaintContext;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;

import javax.swing.*;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;


public class PainterScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener,
		ActionListener, ComponentListener, Observer{


	private MainScreenSettingDB setting = MainScreenSettingDB.getInstance();
	private ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
	private PaintContext mouseContext = PaintContext.getInstance();

	private Image bufferImage;
	private Graphics2D bufferg;
	private Point2D preMousePoint; // Screen coordinates
	private Point2D.Double currentMousePointLogic = new Point2D.Double(); // Logic coordinates
	private double scale;
	private double transX;
	private double transY;
	// Temporary information when editing
	// Affine transformation information
	private Dimension preSize;
	private AffineTransform affineTransform = new AffineTransform();

	public PainterScreen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		screenUpdater.addObserver(this);
		setting.addObserver(this);

		scale = 1.5;

		preSize = getSize();
	}

	// update actual AffineTransform
	private void updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
		affineTransform.scale(scale, scale);
		affineTransform.translate(transX, transY);
	}

	public Image getCreasePatternImage(){
		return bufferImage;
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

		// clears the image buffer
		bufferg.setColor(PaintConfig.colors.getBackgroundColor());
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		// set the AffineTransform of buffer
		bufferg.setTransform(affineTransform);

		// set up the graphics context
		Graphics2D g2d = bufferg;
		bufferg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(LineSetting.STROKE_VALLEY);

		Doc document = ORIPA.doc;
		CreasePattern creasePattern = document.getCreasePattern();

		// draw the faces (for the paper background)
        if(PaintConfig.dispPaper) {
            drawPaper(g2d, creasePattern);
        }

		// draw the lines
		if (setting.isGridVisible()) {
			drawGridLine(g2d);
		}
		drawLines(g2d, creasePattern);

		// draw the vertices
		if (PaintConfig.getMouseAction().getEditMode() == EditMode.VERTEX || PaintConfig.dispVertex) {
			drawVertices(g2d, creasePattern);
		}

		//Draw the mouse action
		if (PaintConfig.mouseAction != null) {
			PaintConfig.mouseAction.onDraw(g2d, mouseContext);
			g.drawImage(bufferImage, 0, 0, this);
			drawCandidatePosition(g);
		}
		else {
			g.drawImage(bufferImage, 0, 0, this);
		}
	}

	private void drawPaper(Graphics2D g2d, CreasePattern cp) {
		// only consider de-duplicated boundary vertices
		HashSet<Vector2D> boundaryVerts = new HashSet<>();
		for(OriLine line : cp) {
			if(line.typeVal == OriLine.TYPE_CUT) {
				boundaryVerts.add(new Vector2D(line.p0.x, line.p0.y));
				boundaryVerts.add(new Vector2D(line.p1.x, line.p1.y));
			}
		}

		// early exit if there's no paper to draw
		if(boundaryVerts.isEmpty()) {
			return;
		}

		// convex hull to estimate paper shape
		MonotoneChain chain = new MonotoneChain(true);
		ArrayList<Vector2D> convexHullVerts = new ArrayList<>(chain.findHullVertices(boundaryVerts));

		// build and draw paper polygon
		Path2D.Double paperShape = new Path2D.Double(Path2D.WIND_EVEN_ODD);

		Vector2D startVert = convexHullVerts.remove(0);
		paperShape.moveTo(startVert.getX(), startVert.getY());
		convexHullVerts.forEach(v -> paperShape.lineTo(v.getX(), v.getY()));
		paperShape.closePath();

		g2d.setColor(PaintConfig.colors.getPaperColor());
		g2d.fill(paperShape);
	}

	private void drawLines(Graphics2D g2d, Collection<OriLine> lines){
		for (OriLine line : lines) {
			if (line.typeVal == OriLine.TYPE_NONE && !PaintConfig.dispAuxLines) {
				continue;
			}

			if ((line.typeVal == OriLine.TYPE_RIDGE || line.typeVal == OriLine.TYPE_VALLEY) && !PaintConfig.dispMVLines) {
				continue;
			}

			g2d.setColor(PaintConfig.colors.getColorForLine(line.getTypeValue()));
			g2d.setStroke(PaintConfig.colors.getStrokeForLine(line.getTypeValue(), scale));

			if(PaintConfig.mouseAction != null){
				if(!mouseContext.getLines().contains(line)){
					g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x, line.p1.y));
				}
			}
		}
	}

	private void drawVertices(Graphics2D g2d, CreasePattern creasePattern) {
		// consider only de-duplicated vertices attached to lines that will be drawn
		HashSet<Vector2d> toDraw = new HashSet<>();
		for (OriLine line : creasePattern) {
			if (!PaintConfig.dispAuxLines && line.typeVal == OriLine.TYPE_NONE) {
				continue;
			}
			if (!PaintConfig.dispMVLines && (line.typeVal == OriLine.TYPE_RIDGE || line.typeVal == OriLine.TYPE_VALLEY)) {
				continue;
			}
			toDraw.add(line.p0);
			toDraw.add(line.p1);
		}

		g2d.setColor(PaintConfig.colors.getVertexColor());
		final double vertexDrawSize = 2;
		toDraw.forEach(v -> {
			g2d.fill(new Ellipse2D.Double(v.x - vertexDrawSize / scale,
					v.y - vertexDrawSize / scale, vertexDrawSize * 2.0 / scale,
					vertexDrawSize * 2.0 / scale));
		});
	}

	private void drawCandidatePosition(Graphics g) {
		Vector2d candidate = mouseContext.pickCandidateV;
		if(candidate != null){
			g.setColor(PaintConfig.colors.getUiOverlayColor());
			g.drawString("(" + candidate.x + "," + candidate.y + ")", 0, 10);
		}
	}

	private void drawGridLine(Graphics2D g2d) {
		g2d.setColor(PaintConfig.colors.getColorForLine(OriLine.TYPE_NONE));
		g2d.setStroke(PaintConfig.colors.getStrokeForLine(OriLine.TYPE_NONE, scale));

		double paperSize = ORIPA.doc.getPaperSize();

		int lineNum = PaintConfig.gridDivNum;
		double step = paperSize / lineNum;

		for (int i = 1; i < lineNum; i++) {
			g2d.draw(new Line2D.Double(
					step * i - paperSize / 2.0, -paperSize / 2.0,
					step * i - paperSize / 2.0, paperSize / 2.0));

			g2d.draw(new Line2D.Double(
					-paperSize / 2.0, step * i - paperSize / 2.0,
					paperSize / 2.0, step * i - paperSize / 2.0));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (PaintConfig.mouseAction == null) {
			return;
		}

		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			PaintConfig.mouseAction.onRightClick(
					mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		}
		else {
			PaintConfig.mouseAction = PaintConfig.mouseAction.onLeftClick(
					mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if(PaintConfig.mouseAction == null){
			return;
		}

		PaintConfig.mouseAction.onPress(mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));

		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Rectangular Selection

		if(PaintConfig.mouseAction != null){
			PaintConfig.mouseAction.onRelease(mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		}
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
			transX += (e.getX() - preMousePoint.getX()) / scale;
			transY += (e.getY() - preMousePoint.getY()) / scale;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		} else {
			mouseContext.setLogicalMousePoint( MouseUtility.getLogicalPoint(affineTransform, e.getPoint()) );
			PaintConfig.getMouseAction().onDrag(mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));
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

		mouseContext.scale = scale;
		mouseContext.dispGrid = setting.isGridVisible();
		mouseContext.setLogicalMousePoint( MouseUtility.getLogicalPoint(affineTransform, e.getPoint()) );

		if (PaintConfig.mouseAction == null) {
			return;
		}

		PaintConfig.mouseAction.onMove(mouseContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		//this.mouseContext.pickCandidateV = Globals.mouseAction.onMove(mouseContext, affineTransform, e);
		repaint();

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


	@Override
	public void update(Observable o, Object arg) {
		String name = o.toString();
		if(name.equals(screenUpdater.getName())){
			if(arg != null){
				if(arg.equals(ViewScreenUpdater.REDRAW_REQUESTED)){
					repaint();
				}
			}
		}
	}
}
