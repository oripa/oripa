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


package oripa.view.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.OriFace;
import oripa.geom.OriLine;
import oripa.geom.OriVertex;
import oripa.mouse.MouseUtility;
import oripa.paint.EditMode;
import oripa.paint.ElementSelector;
import oripa.paint.Globals;
import oripa.paint.PaintContext;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;


public class MainScreen extends JPanel
	implements MouseListener, MouseMotionListener, MouseWheelListener, 
	ActionListener, ComponentListener, Observer{


	private MainScreenSettingDB setting = MainScreenSettingDB.getInstance();
	private ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
	private PaintContext mouseContext = PaintContext.getInstance();
	
	private boolean bDrawFaceID = false;
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
	private ArrayList<Vector2d> crossPoints = new ArrayList<>();
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem popupItem_DivideFace = new JMenuItem("Dividing face");
	private JMenuItem popupItem_FlipFace = new JMenuItem("Flipping face");

	public MainScreen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		screenUpdater.addObserver(this);
		setting.addObserver(this);

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


	public Image getCreasePatternImage(){

		return bufferImage;
	}


	private void drawLines(Graphics2D g2d, Collection<OriLine> lines){

		ElementSelector selector = new ElementSelector();
		for (OriLine line : lines) {
			if (line.typeVal == OriLine.TYPE_NONE &&!Globals.dispAuxLines) {
				continue;
			}

			if ((line.typeVal == OriLine.TYPE_RIDGE || line.typeVal == OriLine.TYPE_VALLEY)
					&& !Globals.dispMVLines) {
				continue;
			}

			g2d.setColor(selector.selectColorByLineType(line.typeVal));
			g2d.setStroke(selector.selectStroke(line.typeVal));

			if(Globals.mouseAction != null){
				if(mouseContext.getLines().contains(line) == false){
					g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x, line.p1.y));
				}
			}

		}

	}

	void drawVertexRectangles(Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		final double vertexDrawSize = 2.0;
		for (OriLine line : ORIPA.doc.creasePattern) {
			if (!Globals.dispAuxLines && line.typeVal == OriLine.TYPE_NONE) {
				continue;
			}
			if (!Globals.dispMVLines && (line.typeVal == OriLine.TYPE_RIDGE
					|| line.typeVal == OriLine.TYPE_VALLEY)) {
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
		if (setting.isGridVisible()) {

			drawGridLine(g2d);
		}

		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setStroke(Config.STROKE_VALLEY);
		g2d.setColor(Color.black);

		drawLines(g2d, ORIPA.doc.creasePattern);



		// Drawing of the vertices
		if (Globals.getMouseAction().getEditMode() == EditMode.VERTEX 
				|| Globals.dispVertex) {
			drawVertexRectangles(g2d);
		}


		for (Vector2d v : crossPoints) {
			g2d.setColor(Color.RED);
			g2d.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0 / scale,
					10.0 / scale, 10.0 / scale));
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

		if (Globals.mouseAction != null) {
			Globals.mouseAction.onDraw(g2d, mouseContext);

			g.drawImage(bufferImage, 0, 0, this);

			drawCandidatePosition(g);
		}
		else {
			g.drawImage(bufferImage, 0, 0, this);

		}
	}
	
	private void drawCandidatePosition(Graphics g){
		Vector2d candidate = mouseContext.pickCandidateV;
		if(candidate != null){
			g.setColor(Color.BLACK);
			g.drawString("(" + candidate.x + 
					"," + candidate.y + ")", 0, 10);
		}	
		
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




	@Override
	public void mouseClicked(MouseEvent e) {

		if (Globals.mouseAction == null) {
			return;
		}

		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			Globals.mouseAction.onRightClick(
					mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));
		}
		else {
			Globals.mouseAction = Globals.mouseAction.onLeftClick(
					mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if(Globals.mouseAction == null){
			return;
		}
		
		Globals.mouseAction.onPress(mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));

		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Rectangular Selection

		if(Globals.mouseAction != null){
			Globals.mouseAction.onRelease(mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));
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
			transX += (double) (e.getX() - preMousePoint.getX()) / scale;
			transY += (double) (e.getY() - preMousePoint.getY()) / scale;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		} else {
			mouseContext.setLogicalMousePoint( MouseUtility.getLogicalPoint(affineTransform, e.getPoint()) );
			Globals.getMouseAction().onDrag(mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));
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

		if (Globals.mouseAction == null) {
			return;
		}

		Globals.mouseAction.onMove(mouseContext, affineTransform, MouseUtility.isControlButtonPressed(e));
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
				if(arg.equals(ScreenUpdater.REDRAW_REQUESTED)){
					repaint();    		
				}
			}

		}

	}


}
