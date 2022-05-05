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

import oripa.geom.RectangleDomain;
import oripa.gui.view.creasepattern.PaintComponentGraphicsJava2D;
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

	private Image bufferImage;
	private Point2D preMousePoint; // Screen coordinates

	private final AffineCamera camera = new AffineCamera();
	private AffineTransform affineTransform = new AffineTransform();

	private Consumer<PaintComponentGraphicsJava2D> paintComponentListener;
	private BiConsumer<Vector2d, Boolean> mouseLeftClickListener;
	private BiConsumer<Vector2d, Boolean> mouseRightClickListener;
	private BiConsumer<Vector2d, Boolean> mousePressListener;
	private BiConsumer<Vector2d, Boolean> mouseReleaseListener;
	private BiConsumer<Vector2d, Boolean> mouseDragListener;
	private BiConsumer<Vector2d, Boolean> mouseMoveListener;

	private Consumer<Double> cameraScaleUpdateListener;
	private Runnable cameraCenterUpdateListener;

	private Runnable usingCtrlKeyOnDragListener;

	private Consumer<Boolean> zeroLineWidthUpdateListener;
	private Consumer<Boolean> vertexVisibleUpdateListener;
	private Consumer<Boolean> mvLineVisibleUpdateListener;
	private Consumer<Boolean> auxLineVisibleUpdateListener;
	private Consumer<Boolean> gridVisibleUpdateListener;
	private Consumer<Boolean> crossLineVisibleUpdateListener;

	private boolean actionUsingCtrlKeyOnDrag;

	public PainterScreen() {

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		addPropertyChangeListenersToSetting();

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

		cameraCenterUpdateListener.run();

		// set the AffineTransform of buffer
		bufferg.setTransform(affineTransform);

		return bufferg;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		paintComponentListener.accept(new PaintComponentGraphicsJava2D(g, updateBufferImage(), bufferImage));

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
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {

		usingCtrlKeyOnDragListener.run();

		if (!actionUsingCtrlKeyOnDrag) {
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
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		affineTransform = camera.updateScaleByMouseWheel(e);

		cameraScaleUpdateListener.accept(camera.getScale());

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
	}

	@Override
	public void setViewVisible(final boolean visible) {
		setVisible(visible);
	}

	@Override
	public void setPaintComponentListener(final Consumer<PaintComponentGraphicsJava2D> listener) {
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

	@Override
	public void setUsingCtrlKeyOnDragListener(final Runnable listener) {
		usingCtrlKeyOnDragListener = listener;
	}

	@Override
	public void setUsingCtrlKeyOnDrag(final boolean using) {
		actionUsingCtrlKeyOnDrag = using;
	}

}
