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

package oripa.gui.view.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.main.SwitcherBetweenPasteAndChangeOrigin;
import oripa.gui.view.util.AffineCamera;
import oripa.gui.view.util.MouseUtility;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.MainScreenUpdater;

public class PainterScreen extends JPanel
		implements PainterScreenView, MouseListener, MouseMotionListener, MouseWheelListener,
		ComponentListener {

	private static Logger logger = LoggerFactory.getLogger(PainterScreen.class);

	private final MainScreenSetting setting = new MainScreenSetting();
	private final MainScreenUpdater screenUpdater = new MainScreenUpdater();
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final CutModelOutlinesHolder cutOutlinesHolder;

	private final boolean bDrawFaceID = false;
	private Image bufferImage;
	private Point2D preMousePoint; // Screen coordinates

	private final AffineCamera camera = new AffineCamera();
	private AffineTransform affineTransform = new AffineTransform();

	private final MouseActionHolder mouseActionHolder;

	private Consumer<PaintComponentParameter> paintComponentListener;
	private BiConsumer<Vector2d, Boolean> mouseLeftClickListener;
	private BiConsumer<Vector2d, Boolean> mouseRightClickListener;
	private BiConsumer<Vector2d, Boolean> mousePressListener;
	private BiConsumer<Vector2d, Boolean> mouseReleaseListener;
	private BiConsumer<Vector2d, Boolean> mouseDragListener;
	private BiConsumer<Vector2d, Boolean> mouseMoveListener;

	private Consumer<Double> cameraScaleUpdateListener;
	private Runnable cameraCenterUpdateListener;
	private Runnable redrawRequestListener;
	private Consumer<Boolean> zeroLineWidthUpdateListener;
	private Consumer<Boolean> vertexVisibleUpdateListener;
	private Consumer<Boolean> mvLineVisibleUpdateListener;
	private Consumer<Boolean> auxLineVisibleUpdateListener;
	private Consumer<Boolean> gridVisibleUpdateListener;
	private Consumer<Boolean> crossLineVisibleUpdateListener;

	public PainterScreen(
			final MouseActionHolder mouseActionHolder,
			final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final CutModelOutlinesHolder aCutOutlineHolder) {
		this.mouseActionHolder = mouseActionHolder;

		var actionSwitcher = new SwitcherBetweenPasteAndChangeOrigin(mouseActionHolder);
		screenUpdater.setChangeActionIfCopyAndPaste(actionSwitcher);

		this.paintContext = paintContext;
		this.viewContext = viewContext;
		cutOutlinesHolder = aCutOutlineHolder;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		addPropertyChangeListenersToSetting();

//		camera.updateScale(INITIAL_CAMERA_SCALE);
//		var domain = paintContext.getPaperDomain();
//		camera.updateCenterOfPaper(domain.getCenterX(), domain.getCenterY());
//
//		viewContext.setScale(camera.getScale());

		setBackground(Color.WHITE);
	}

	@Override
	public void initializeCamera(final RectangleDomain domain) {
		camera.updateScale(INITIAL_CAMERA_SCALE);
		camera.updateCenterOfPaper(domain.getCenterX(), domain.getCenterY());

		cameraScaleUpdateListener.accept(INITIAL_CAMERA_SCALE);

		repaint();
	}

	@Override
	public MainScreenUpdater getScreenUpdater() {
		return screenUpdater;
	}

	@Override
	public MainScreenSetting getMainScreenSetting() {
		return setting;
	}

	/*
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

	private void buildBufferImage() {
		bufferImage = createImage(getWidth(), getHeight());
		affineTransform = camera.updateCameraPosition(getWidth() * 0.5, getHeight() * 0.5);
	}

	private Graphics2D updateBufferImage() {
		if (bufferImage == null) {
			buildBufferImage();
		}

		var bufferg = (Graphics2D) bufferImage.getGraphics();

		// initialize the AffineTransform of bufferg
		bufferg.setTransform(new AffineTransform());

		// Clears the image buffer
		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

//		var domain = paintContext.getPaperDomain();

		cameraCenterUpdateListener.run();

		// set the AffineTransform of buffer
		bufferg.setTransform(affineTransform);

		return bufferg;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		paintComponentListener.accept(new PaintComponentParameter(g, updateBufferImage(), bufferImage));

//
//		if (!viewContext.isZeroLineWidth()) {
//			bufferG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);
//		}
//
//		GraphicMouseAction action = mouseActionHolder.getMouseAction();
//
//		ObjectGraphicDrawer bufferObjDrawer = new CreasePatternObjectDrawer(bufferG2D);
//
//		drawer.draw(bufferObjDrawer, viewContext, paintContext,
//				action == null ? false : action.getEditMode() == EditMode.VERTEX);
//
//		if (paperDomainOfModel != null) {
//			drawPaperDomainOfModel(bufferObjDrawer);
//		}
//
//		if (viewContext.isCrossLineVisible()) {
//			var crossLines = cutOutlinesHolder.getOutlines();
//			drawer.drawAllLines(bufferObjDrawer, crossLines, camera.getScale(),
//					viewContext.isZeroLineWidth());
//		}
//
//		// Line that links the pair of unsetled faces
//		// if (Config.FOR_STUDY) {
//		// List<OriFace> faces = origamiModel.getFaces();
//		//
//		// int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
//		//
//		// if (overlapRelation != null) {
//		// g2d.setStroke(LineSetting.STROKE_RIDGE);
//		// g2d.setColor(Color.MAGENTA);
//		// int size = faces.size();
//		// for (int i = 0; i < size; i++) {
//		// for (int j = i + 1; j < size; j++) {
//		// if (overlapRelation[i][j] == Doc.UNDEFINED) {
//		// Vector2d v0 = faces.get(i).getCenter();
//		// Vector2d v1 = faces.get(j).getCenter();
//		// g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));
//		//
//		// }
//		// }
//		// }
//		// }
//		// }
//
//		if (action == null) {
//			g.drawImage(bufferImage, 0, 0, this);
//			return;
//		}
//
//		action.onDraw(bufferObjDrawer, viewContext, paintContext);
//
//		g.drawImage(bufferImage, 0, 0, this);
//
//		ObjectGraphicDrawer objDrawer = new CreasePatternObjectDrawer((Graphics2D) g);
//		drawer.drawCandidatePositionString(objDrawer,
//				paintContext.getCandidateVertexToPick());
	}

	@Override
	public void mouseClicked(final MouseEvent e) {

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					var mousePoint = createMousePoint(affineTransform, e.getPoint());
					if (MouseUtility.isRightButtonEvent(e)) {
						mouseRightClickListener.accept(
								mousePoint,
								MouseUtility.isControlKeyDown(e));
						return null;
					}

					mouseLeftClickListener.accept(
							mousePoint,
							MouseUtility.isControlKeyDown(e));
					return null;
				} catch (Exception e) {
					logger.error("error on mouse click", e);
				}
				return null;
			}

			@Override
			protected void done() {
				repaint();
			}
		}.execute();

//		final GraphicMouseAction action = mouseActionHolder.getMouseAction();
//
//		if (action == null) {
//			return;
//		}
//
//		new SwingWorker<Void, Void>() {
//			@Override
//			protected Void doInBackground() throws Exception {
//				try {
//					if (MouseUtility.isRightButtonEvent(e)) {
//						action.onRightClick(
//								viewContext, paintContext,
//								MouseUtility.isControlKeyDown(e));
//
//						return null;
//					}
//
//					mouseActionHolder.setMouseAction(action.onLeftClick(
//							viewContext, paintContext,
//							MouseUtility.isControlKeyDown(e)));
//					return null;
//				} catch (Exception e) {
//					logger.error("error on mouse click", e);
//				}
//				return null;
//			}
//
//			@Override
//			protected void done() {
//				repaint();
//			}
//		}.execute();
//
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		try {
			if (MouseUtility.isLeftButtonEvent(e)) {
				var mousePoint = createMousePoint(affineTransform, e.getPoint());
				mousePressListener.accept(mousePoint, MouseUtility.isControlKeyDown(e));
			}
		} catch (Exception ex) {
			logger.debug("error on mouse button press", ex);
		}
		preMousePoint = e.getPoint();

//		GraphicMouseAction action = mouseActionHolder.getMouseAction();
//
//		if (action == null) {
//			return;
//		}
//
//		try {
//			if (MouseUtility.isLeftButtonEvent(e)) {
//				action.onPress(viewContext, paintContext, MouseUtility.isControlKeyDown(e));
//			}
//		} catch (Exception ex) {
//			logger.debug("error on mouse button press", ex);
//		}
//		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		try {
			if (MouseUtility.isLeftButtonEvent(e)) {
				var mousePoint = createMousePoint(affineTransform, e.getPoint());
				mouseReleaseListener.accept(mousePoint, MouseUtility.isControlKeyDown(e));
			}
		} catch (Exception ex) {
			logger.debug("error on mouse button release", ex);
		}

		repaint();

//		GraphicMouseAction action = mouseActionHolder.getMouseAction();
//
//		if (action == null) {
//			return;
//		}
//
//		try {
//			if (MouseUtility.isLeftButtonEvent(e)) {
//				action.onRelease(viewContext, paintContext, MouseUtility.isControlKeyDown(e));
//			}
//		} catch (Exception ex) {
//			logger.debug("error on mouse button release", ex);
//		}
//
//		repaint();
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		GraphicMouseAction action = mouseActionHolder.getMouseAction();

		if (!action.isUsingCtrlKeyOnDrag()) {
			if (doCameraDragAction(e, camera::updateScaleByMouseDragged)) {
				cameraScaleUpdateListener.accept(camera.getScale());
				return;
			}
		}

		if (doCameraDragAction(e, camera::updateTranslateByMouseDragged)) {
			return;
		}

		try {
			if (MouseUtility.isLeftButtonEvent(e)) {
				var mousePoint = createMousePoint(affineTransform, e.getPoint());
				mouseDragListener.accept(mousePoint,
						MouseUtility.isControlKeyDown(e));
			}
		} catch (Exception ex) {
			logger.debug("error on mouse dragging", ex);
		}

		repaint();

//		if (!action.isUsingCtrlKeyOnDrag()) {
//			if (doCameraDragAction(e, camera::updateScaleByMouseDragged)) {
//				viewContext.setScale(camera.getScale());
//				return;
//			}
//		}
//
//		if (doCameraDragAction(e, camera::updateTranslateByMouseDragged)) {
//			return;
//		}
//
//		try {
//			if (MouseUtility.isLeftButtonEvent(e)) {
//				viewContext.setLogicalMousePoint(createMousePoint(affineTransform, e.getPoint()));
//				action.onDrag(viewContext, paintContext, MouseUtility.isControlKeyDown(e));
//			}
//		} catch (Exception ex) {
//			logger.debug("error on mouse dragging", ex);
//		}
//
//		repaint();
	}

	private Vector2d createMousePoint(final AffineTransform affineTransform, final Point point) {
		var logicalPoint = MouseUtility.getLogicalPoint(
				affineTransform, point);
		return new Vector2d(logicalPoint.x, logicalPoint.y);
	}

	private boolean doCameraDragAction(final MouseEvent e,
			final BiFunction<MouseEvent, Point2D, AffineTransform> onDrag) {
		var affine = onDrag.apply(e, preMousePoint);
		if (affine == null) {
			return false;
		}
		preMousePoint = e.getPoint();
		affineTransform = affine;
		repaint();
		return true;
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		cameraScaleUpdateListener.accept(camera.getScale());

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					var mousePoint = createMousePoint(affineTransform, e.getPoint());
					mouseMoveListener.accept(mousePoint, MouseUtility.isControlKeyDown(e));
				} catch (Exception ex) {
					logger.debug("error on mouse move", ex);
				}
				return null;
			}

			@Override
			protected void done() {
				repaint();
			}
		}.execute();

//		viewContext.setScale(camera.getScale());
//		viewContext.setLogicalMousePoint(createMousePoint(affineTransform, e.getPoint()));
//
//		final GraphicMouseAction action = mouseActionHolder.getMouseAction();
//		if (action == null) {
//			return;
//		}
//
//		new SwingWorker<Void, Void>() {
//			@Override
//			protected Void doInBackground() throws Exception {
//				try {
//					action.onMove(viewContext, paintContext, MouseUtility.isControlKeyDown(e));
//				} catch (Exception ex) {
//					logger.debug("error on mouse move", ex);
//				}
//				return null;
//			}
//
//			@Override
//			protected void done() {
//				repaint();
//			}
//		}.execute();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		affineTransform = camera.updateScaleByMouseWheel(e);

		cameraScaleUpdateListener.accept(camera.getScale());
		// viewContext.setScale(camera.getScale());
		repaint();
	}

	@Override
	public void componentResized(final ComponentEvent arg0) {
		if (getWidth() <= 0 || getHeight() <= 0) {
			return;
		}

		// Updating the image buffer
		buildBufferImage();
		repaint();
	}

	@Override
	public void componentMoved(final ComponentEvent arg0) {

	}

	@Override
	public void componentShown(final ComponentEvent arg0) {

	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {

	}

//	@Override
//	public void setPaperDomainOfModel(final RectangleDomain domain) {
//		paperDomainOfModel = domain;
//		repaint();
//	}

	private void addPropertyChangeListenersToSetting() {
		screenUpdater.addPropertyChangeListener(
				ViewScreenUpdater.REDRAW_REQUESTED, e -> repaint());

		setting.addPropertyChangeListener(
				MainScreenSetting.ZERO_LINE_WIDTH, e -> {
					zeroLineWidthUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.VERTEX_VISIBLE, e -> {
					vertexVisibleUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.MV_LINE_VISIBLE, e -> {
					mvLineVisibleUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.AUX_LINE_VISIBLE, e -> {
					auxLineVisibleUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.GRID_VISIBLE, e -> {
					gridVisibleUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

		setting.addPropertyChangeListener(
				MainScreenSetting.CROSS_LINE_VISIBLE, e -> {
					crossLineVisibleUpdateListener.accept((Boolean) e.getNewValue());
					repaint();
				});

//		setting.addPropertyChangeListener(
//				MainScreenSetting.ZERO_LINE_WIDTH, e -> {
//					viewContext.setZeroLineWidth((boolean) e.getNewValue());
//					repaint();
//				});
//
//		setting.addPropertyChangeListener(
//				MainScreenSetting.VERTEX_VISIBLE, e -> {
//					viewContext.setVertexVisible((boolean) e.getNewValue());
//					repaint();
//				});
//
//		setting.addPropertyChangeListener(
//				MainScreenSetting.MV_LINE_VISIBLE, e -> {
//					viewContext.setMVLineVisible((boolean) e.getNewValue());
//					repaint();
//				});
//
//		setting.addPropertyChangeListener(
//				MainScreenSetting.AUX_LINE_VISIBLE, e -> {
//					viewContext.setAuxLineVisible((boolean) e.getNewValue());
//					repaint();
//				});
//
//		setting.addPropertyChangeListener(
//				MainScreenSetting.GRID_VISIBLE, e -> {
//					viewContext.setGridVisible((boolean) e.getNewValue());
//					repaint();
//				});
//
//		setting.addPropertyChangeListener(
//				MainScreenSetting.CROSS_LINE_VISIBLE, e -> {
//					var visible = (boolean) e.getNewValue();
//					logger.info("receive crossLineVisible has become " + visible);
//					viewContext.setCrossLineVisible(visible);
//					repaint();
//				});

	}

	@Override
	public void setViewVisible(final boolean visible) {
		setVisible(visible);
	}

//	@Override
//	public void updateView() {
//		repaint();
//	}

	@Override
	public PaintContext getPaintContext() {
		return paintContext;
	}

	@Override
	public CreasePatternViewContext getViewContext() {
		return viewContext;
	}

	@Override
	public CutModelOutlinesHolder getCutModelOutlinesHolder() {
		return cutOutlinesHolder;
	}

	@Override
	public MouseActionHolder getMouseActionHolder() {
		return mouseActionHolder;
	}

	@Override
	public void setPaintComponentListener(final Consumer<PaintComponentParameter> listener) {
		paintComponentListener = listener;
	}

	@Override
	public void setMouseLeftClickListener(final BiConsumer<Vector2d, Boolean> listener) {
		mouseLeftClickListener = listener;
	}

	@Override
	public void setMouseRightClickListener(final BiConsumer<Vector2d, Boolean> listener) {
		mouseRightClickListener = listener;
	}

	@Override
	public void setMousePressListener(final BiConsumer<Vector2d, Boolean> listener) {
		mousePressListener = listener;
	}

	@Override
	public void setMouseReleaseListener(final BiConsumer<Vector2d, Boolean> listener) {
		mouseReleaseListener = listener;
	}

	@Override
	public void setMouseDragListener(final BiConsumer<Vector2d, Boolean> listener) {
		mouseDragListener = listener;
	}

	@Override
	public void setMouseMoveListener(final BiConsumer<Vector2d, Boolean> listener) {
		mouseMoveListener = listener;
	}

	@Override
	public void setCameraScaleUpdateListener(final Consumer<Double> listener) {
		cameraScaleUpdateListener = listener;
	}

	@Override
	public void setCameraCenterUpdateListener(final Runnable listener) {
		cameraCenterUpdateListener = listener;
	}

	@Override
	public void updateCameraCenter(final RectangleDomain paperDomain) {
		affineTransform = camera.updateCenterOfPaper(paperDomain.getCenterX(), paperDomain.getCenterY());
	}

//	@Override
//	public void setRedrawRequestListener(final Runnable listener) {
//		redrawRequestListener = listener;
//	}

	@Override
	public void setZeroLineWidthUpdateListener(final Consumer<Boolean> listener) {
		zeroLineWidthUpdateListener = listener;
	}

	@Override
	public void setVertexVisibleUpdateListener(final Consumer<Boolean> listener) {
		vertexVisibleUpdateListener = listener;
	}

	@Override
	public void setMVLineVisibleUpdateListener(final Consumer<Boolean> listener) {
		mvLineVisibleUpdateListener = listener;
	}

	@Override
	public void setAuxLineVisibleUpdateListener(final Consumer<Boolean> listener) {
		auxLineVisibleUpdateListener = listener;
	}

	@Override
	public void setGridVisibleUpdateListener(final Consumer<Boolean> listener) {
		gridVisibleUpdateListener = listener;
	}

	@Override
	public void setCrossLineVisibleUpdateListener(final Consumer<Boolean> listener) {
		crossLineVisibleUpdateListener = listener;
	}
}
