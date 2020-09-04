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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.CreasePatternGraphicDrawer;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.persistent.doc.doc.SheetCutOutlinesHolder;
import oripa.util.gui.MouseUtility;
import oripa.value.OriLine;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainScreenSetting;
import oripa.viewsetting.main.ScreenUpdater;

public class PainterScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener,
		ComponentListener {

	private static Logger logger = LoggerFactory.getLogger(PainterScreen.class);

	private final MainScreenSetting setting = new MainScreenSetting();
	private final ScreenUpdater screenUpdater = new ScreenUpdater();
	private final PaintContextInterface paintContext;
	private final SheetCutOutlinesHolder cutOutlinesHolder;
	private final SelectionOriginHolder originHolder = setting.getOriginHolder();

	private final boolean bDrawFaceID = false;
	private Image bufferImage;
	private Graphics2D bufferg;
	private Point2D preMousePoint; // Screen coordinates
	private final Point2D.Double currentMousePointLogic = new Point2D.Double(); // Logic
																				// coordinates
	private double scale;
	private double transX;
	private double transY;
	// Temporary information when editing
	// Affine transformation information
	private Dimension preSize;
	private final AffineTransform affineTransform = new AffineTransform();
	private final ArrayList<Vector2d> crossPoints = new ArrayList<>();

	private final CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();

	private final MouseActionHolder mouseActionHolder;

	public PainterScreen(
			final MouseActionHolder mouseActionHolder,
			final PaintContextInterface aContext,
			final SheetCutOutlinesHolder aCutOutlineHolder) {
		this.mouseActionHolder = mouseActionHolder;
		screenUpdater.setMouseActionHolder(mouseActionHolder);
		paintContext = aContext;
		cutOutlinesHolder = aCutOutlineHolder;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		addPropertyChangeListenersToSetting();

		scale = 1.5;
		paintContext.setScale(scale);

		setBackground(Color.white);

		preSize = getSize();
	}

	public ViewScreenUpdater getScreenUpdater() {
		return screenUpdater;
	}

	public MainScreenSetting getMainScreenSetting() {
		return setting;
	}

	/**
	 * for verifying algorithm
	 *
	 * @param g2d
	 */
	// public void drawModel(Graphics2D g2d) {
	//
	// if (! Config.FOR_STUDY) {
	// return;
	// }
	//
	// Doc document = ORIPA.doc;
	// OrigamiModel origamiModel = document.getOrigamiModel();
	//
	// List<OriFace> faces = origamiModel.getFaces();
	// List<OriVertex> vertices = origamiModel.getVertices();
	//
	//
	// if (bDrawFaceID) {
	// g2d.setColor(Color.BLACK);
	// for (OriFace face : faces) {
	// g2d.drawString("" + face.tmpInt, (int) face.getCenter().x,
	// (int) face.getCenter().y);
	// }
	// }
	//
	// g2d.setColor(new Color(255, 210, 220));
	// for (OriFace face : faces) {
	// if (face.tmpInt2 == 0) {
	// g2d.setColor(Color.RED);
	// g2d.fill(face.preOutline);
	// } else {
	// g2d.setColor(face.color);
	// }
	//
	// if (face.hasProblem) {
	// g2d.setColor(Color.RED);
	// } else {
	// if (face.faceFront) {
	// g2d.setColor(new Color(255, 200, 200));
	// } else {
	// g2d.setColor(new Color(200, 200, 255));
	// }
	// }
	//
	// // g2d.fill(face.preOutline);
	// }
	//
	// g2d.setColor(Color.BLACK);
	//
	//
	// for (OriFace face : faces) {
	// g2d.drawString("" + face.z_order, (int) face.getCenter().x,
	// (int) face.getCenter().y);
	// }
	//
	// g2d.setColor(Color.RED);
	// for (OriVertex v : vertices) {
	// if (v.hasProblem) {
	// g2d.fill(new Rectangle2D.Double(v.p.x - 8.0 / scale,
	// v.p.y - 8.0 / scale, 16.0 / scale, 16.0 / scale));
	// }
	// }
	// }

	// update actual AffineTransform
	private void updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
		affineTransform.scale(scale, scale);
		affineTransform.translate(transX, transY);

	}

	public Image getCreasePatternImage() {

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
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		Graphics2D bufferG2D = updateBufferImage();

		drawer.draw(bufferG2D, paintContext,
				mouseActionHolder.getMouseAction().getEditMode() == EditMode.VERTEX);

		for (Vector2d v : crossPoints) {
			bufferG2D.setColor(Color.RED);
			bufferG2D.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0
					/ scale,
					10.0 / scale, 10.0 / scale));
		}

		if (paintContext.isCrossLineVisible()) {
			List<OriLine> crossLines = cutOutlinesHolder.getSheetCutOutlines();
			drawer.drawAllLines(bufferG2D, crossLines);
		}

		// Line that links the pair of unsetled faces
		// if (Config.FOR_STUDY) {
		// List<OriFace> faces = origamiModel.getFaces();
		//
		// int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
		//
		// if (overlapRelation != null) {
		// g2d.setStroke(LineSetting.STROKE_RIDGE);
		// g2d.setColor(Color.MAGENTA);
		// int size = faces.size();
		// for (int i = 0; i < size; i++) {
		// for (int j = i + 1; j < size; j++) {
		// if (overlapRelation[i][j] == Doc.UNDEFINED) {
		// Vector2d v0 = faces.get(i).getCenter();
		// Vector2d v1 = faces.get(j).getCenter();
		// g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));
		//
		// }
		// }
		// }
		// }
		// }

		GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();

		if (action != null) {
			action.onDraw(bufferG2D, paintContext);

			g.drawImage(bufferImage, 0, 0, this);

			drawer.drawCandidatePositionString((Graphics2D) g,
					paintContext.getCandidateVertexToPick());
		} else {
			g.drawImage(bufferImage, 0, 0, this);

		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		final GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (SwingUtilities.isRightMouseButton(e)) {
					action.onRightClick(
							paintContext, affineTransform,
							MouseUtility.isControlKeyDown(e));

					return null;
				}

				mouseActionHolder.setMouseAction(action.onLeftClick(
						paintContext,
						MouseUtility.isControlKeyDown(e)));
				return null;
			}

			@Override
			protected void done() {
				repaint();
				// screenUpdater.updateScreen();
			}
		}.execute();

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		action.onPress(paintContext, affineTransform,
				MouseUtility.isControlKeyDown(e));

		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();
		// Rectangular Selection

		if (action != null) {
			action.onRelease(paintContext, affineTransform,
					MouseUtility.isControlKeyDown(e));
		}
		repaint();
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {

		// zoom
		if (MouseUtility.isLeftButtonDown(e) &&
				MouseUtility.isControlKeyDown(e)) {

			double moved = e.getX() - preMousePoint.getX() + e.getY()
					- preMousePoint.getY();
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

		// move camera
		if (MouseUtility.isRightButtonDown(e) ||
				(MouseUtility.isLeftButtonDown(e) && MouseUtility.isShiftKeyDown(e))) {
			transX += (e.getX() - preMousePoint.getX()) / scale;
			transY += (e.getY() - preMousePoint.getY()) / scale;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();

			return;
		}

		GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();

		// Drag by left button
		paintContext.setLogicalMousePoint(MouseUtility.getLogicalPoint(
				affineTransform, e.getPoint()));
		action.onDrag(paintContext, affineTransform,
				MouseUtility.isControlKeyDown(e));
		repaint();
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		// Gets the value of the current logical coordinates of the mouse

		try {
			affineTransform.inverseTransform(e.getPoint(),
					currentMousePointLogic);
		} catch (Exception ex) {
			return;
		}

		paintContext.setScale(scale);
		paintContext.setLogicalMousePoint(MouseUtility.getLogicalPoint(
				affineTransform, e.getPoint()));

		final GraphicMouseActionInterface action = mouseActionHolder.getMouseAction();
		if (action == null) {
			return;
		}

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				action.onMove(paintContext, affineTransform,
						MouseUtility.isControlKeyDown(e));
				return null;
			}

			@Override
			protected void done() {
				repaint();
			}
		}.execute();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		double scale_ = (100.0 - e.getWheelRotation() * 5) / 100.0;
		scale *= scale_;
		paintContext.setScale(scale);
		updateAffineTransform();
		repaint();
	}

	@Override
	public void componentResized(final ComponentEvent arg0) {
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
	public void componentMoved(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentShown(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	private void addPropertyChangeListenersToSetting() {
		screenUpdater.addPropertyChangeListener(
				ViewScreenUpdater.REDRAW_REQUESTED, e -> repaint());

		setting.addPropertyChangeListener(
				MainScreenSetting.GRID_VISIBLE, e -> {
					paintContext.setGridVisible((boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.CROSS_LINE_VISIBLE, e -> {
					var visible = (boolean) e.getNewValue();
					logger.info("receive crossLineVisible has become " + visible);
					paintContext.setCrossLineVisible(visible);
					repaint();
				});

	}
}
