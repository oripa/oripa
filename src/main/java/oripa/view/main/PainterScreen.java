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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.mouse.MouseUtility;
import oripa.paint.CreasePatternGraphicDrawer;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PaintContext;
import oripa.value.OriLine;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;


public class PainterScreen extends JPanel
implements MouseListener, MouseMotionListener, MouseWheelListener, 
ActionListener, ComponentListener, Observer{


	private MainScreenSettingDB setting = MainScreenSettingDB.getInstance();
	private ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
	private PaintContextInterface paintContext = PaintContext.getInstance();

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

	
	private CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();

	public PainterScreen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		screenUpdater.addObserver(this);
		setting.addObserver(this);

		scale = 1.5;
		paintContext.setScale(scale);

		setBackground(Color.white);

		popupItem_DivideFace.addActionListener(this);
		popup.add(popupItem_DivideFace);
		popupItem_FlipFace.addActionListener(this);
		popup.add(popupItem_FlipFace);
		preSize = getSize();
	}

	/**
	 * for verifying algorithm
	 * @param g2d
	 */
//	public void drawModel(Graphics2D g2d) {
//
//		if (! Config.FOR_STUDY) {
//			return;
//		}
//
//		Doc document = ORIPA.doc;
//		OrigamiModel origamiModel = document.getOrigamiModel();
//
//		List<OriFace> faces = origamiModel.getFaces();
//		List<OriVertex> vertices = origamiModel.getVertices();
//
//
//		if (bDrawFaceID) {
//			g2d.setColor(Color.BLACK);
//			for (OriFace face : faces) {
//				g2d.drawString("" + face.tmpInt, (int) face.getCenter().x,
//						(int) face.getCenter().y);
//			}
//		}
//
//		g2d.setColor(new Color(255, 210, 220));
//		for (OriFace face : faces) {
//			if (face.tmpInt2 == 0) {
//				g2d.setColor(Color.RED);
//				g2d.fill(face.preOutline);
//			} else {
//				g2d.setColor(face.color);
//			}
//
//			if (face.hasProblem) {
//				g2d.setColor(Color.RED);
//			} else {
//				if (face.faceFront) {
//					g2d.setColor(new Color(255, 200, 200));
//				} else {
//					g2d.setColor(new Color(200, 200, 255));
//				}
//			}
//
////			g2d.fill(face.preOutline);
//		}
//
//		g2d.setColor(Color.BLACK);
//
//
//		for (OriFace face : faces) {
//			g2d.drawString("" + face.z_order, (int) face.getCenter().x,
//					(int) face.getCenter().y);
//		}
//
//		g2d.setColor(Color.RED);
//		for (OriVertex v : vertices) {
//			if (v.hasProblem) {
//				g2d.fill(new Rectangle2D.Double(v.p.x - 8.0 / scale,
//						v.p.y - 8.0 / scale, 16.0 / scale, 16.0 / scale));
//			}
//		}
//	}

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


	private void buildBufferImage() {
		bufferImage = createImage(getWidth(), getHeight());
		bufferg = (Graphics2D) bufferImage.getGraphics();
		updateAffineTransform();
		preSize = getSize();
		
	}

	private Graphics2D updateBufferImage() {
		if (bufferImage == null) {
			buildBufferImage();
		}

		// initialize the AffineTransform of bufferg
		bufferg.setTransform(new AffineTransform());

		// Clears the image buffer
		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		// set the AffineTransform of buffer
		bufferg.setTransform(affineTransform);

		return bufferg;
	}

	// Scaling relative to the center of the screen
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);


		Graphics2D bufferG2D = updateBufferImage();

		Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();

		drawer.draw(bufferG2D, paintContext, creasePattern);

		for (Vector2d v : crossPoints) {
			bufferG2D.setColor(Color.RED);
			bufferG2D.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0 / scale,
					10.0 / scale, 10.0 / scale));
		}


		if (PaintConfig.bDispCrossLine) {
			List<OriLine> crossLines = document.getSheetCutOutlines();
			drawer.drawLines(bufferG2D, crossLines, null);
		}


		// Line that links the pair of unsetled faces
//		if (Config.FOR_STUDY) {
//			List<OriFace> faces = origamiModel.getFaces();
//
//			int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
//
//			if (overlapRelation != null) {
//				g2d.setStroke(LineSetting.STROKE_RIDGE);
//				g2d.setColor(Color.MAGENTA);
//				int size = faces.size();
//				for (int i = 0; i < size; i++) {
//					for (int j = i + 1; j < size; j++) {
//						if (overlapRelation[i][j] == Doc.UNDEFINED) {
//							Vector2d v0 = faces.get(i).getCenter();
//							Vector2d v1 = faces.get(j).getCenter();
//							g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));
//
//						}
//					}
//				}
//			}
//		}

		
		if (PaintConfig.mouseAction != null) {
			PaintConfig.mouseAction.onDraw(bufferG2D, paintContext);

			g.drawImage(bufferImage, 0, 0, this);

			drawer.drawCandidatePositionString((Graphics2D)g, paintContext.getCandidateVertexToPick());
		}
		else {
			g.drawImage(bufferImage, 0, 0, this);

		}
	}




	@Override
	public void mouseClicked(MouseEvent e) {

		if (PaintConfig.mouseAction == null) {
			return;
		}

		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			PaintConfig.mouseAction.onRightClick(
					paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));
			
			return;
		}
		
		
		PaintConfig.mouseAction = PaintConfig.mouseAction.onLeftClick(
				paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if(PaintConfig.mouseAction == null){
			return;
		}

		PaintConfig.mouseAction.onPress(paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));

		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Rectangular Selection

		if(PaintConfig.mouseAction != null){
			PaintConfig.mouseAction.onRelease(paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));
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
			paintContext.setScale(scale);
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();

			return;
		}
		
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			transX += (double) (e.getX() - preMousePoint.getX()) / scale;
			transY += (double) (e.getY() - preMousePoint.getY()) / scale;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();

			return;
		}
		
		// Drag by left button
		paintContext.setLogicalMousePoint( MouseUtility.getLogicalPoint(affineTransform, e.getPoint()) );
		PaintConfig.getMouseAction().onDrag(paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// Gets the value of the current logical coordinates of the mouse

		try {
			affineTransform.inverseTransform(e.getPoint(), currentMousePointLogic);
		} catch (Exception ex) {
			return;
		}

		paintContext.setScale(scale);
		paintContext.setLogicalMousePoint( MouseUtility.getLogicalPoint(affineTransform, e.getPoint()) );

		if (PaintConfig.mouseAction == null) {
			return;
		}

		PaintConfig.mouseAction.onMove(paintContext, affineTransform, MouseUtility.isControlKeyPressed(e));
		//this.mouseContext.pickCandidateV = Globals.mouseAction.onMove(mouseContext, affineTransform, e);
		repaint();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double scale_ = (100.0 - e.getWheelRotation() * 5) / 100.0;
		scale *= scale_;
		paintContext.setScale(scale);
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
		buildBufferImage();
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
		paintContext.setGridVisible(setting.isGridVisible());
		if (name.equals(screenUpdater.getName())) {
			if (arg != null) {
				if(arg.equals(ViewScreenUpdater.REDRAW_REQUESTED)){
					repaint();
				}
			}

		}

	}


}
