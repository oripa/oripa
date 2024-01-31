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

package oripa.swing.view.foldability;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.gui.presenter.creasepattern.geometry.NearestVertexFinder;
import oripa.gui.view.View;
import oripa.gui.view.creasepattern.PaintComponentGraphics;
import oripa.gui.view.foldability.FoldabilityScreenView;
import oripa.swing.drawer.java2d.CreasePatternGraphics;
import oripa.swing.view.util.AffineCamera;
import oripa.swing.view.util.MouseUtility;
import oripa.value.CalculationResource;
import oripa.vecmath.Vector2d;

/**
 * A screen to show whether Maekawa theorem and Kawasaki theorem (and others)
 * hold.
 *
 * @author Koji
 *
 */
public class FoldabilityScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener,
		ComponentListener, FoldabilityScreenView {

	private Image bufferImage;

	private final AffineCamera camera = new AffineCamera();

	// Affine transformation information
	private AffineTransform affineTransform = new AffineTransform();

	private Point2D preMousePoint; // Screen coordinates

	private Consumer<PaintComponentGraphics> paintComponentListener;

	FoldabilityScreen() {

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

		camera.updateScale(1.5);
		setBackground(Color.WHITE);
	}

	private Collection<OriVertex> violatingVertices = new ArrayList<>();
	private OriVertex pickedViolatingVertex;

	private void buildBufferImage() {
		bufferImage = createImage(getWidth(), getHeight());
		affineTransform = camera.updateCameraPosition(getWidth() * 0.5, getHeight() * 0.5);
	}

	private Graphics2D updateBufferImage() {
		if (bufferImage == null) {
			buildBufferImage();
		}
		var bufferg = (Graphics2D) bufferImage.getGraphics();

		bufferg.setTransform(new AffineTransform());

		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		bufferg.setTransform(affineTransform);

		return bufferg;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		paintComponentListener.accept(new CreasePatternGraphics(g, updateBufferImage(), bufferImage, this));
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

	/*
	 * (non Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(final MouseEvent e) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(final MouseEvent e) {
		preMousePoint = e.getPoint();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
	 * MouseEvent)
	 */
	@Override
	public void mouseDragged(final MouseEvent e) {

		if (doCameraDragAction(e, camera::updateScaleByMouseDragged)) {
			return;
		}

		if (doCameraDragAction(e, camera::updateTranslateByMouseDragged)) {
			return;
		}
	}

	private boolean doCameraDragAction(final MouseEvent e,
			final BiFunction<MouseEvent, Point2D, Optional<AffineTransform>> onDrag) {
		var affineOpt = onDrag.apply(e, preMousePoint);
		if (affineOpt.isEmpty()) {
			return false;
		}

		var affine = affineOpt.get();
		preMousePoint = e.getPoint();
		affineTransform = affine;
		repaint();
		return true;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(final MouseEvent e) {
		var logicalPoint = MouseUtility.getLogicalPoint(affineTransform, e.getPoint());
		var mousePoint = new Vector2d(logicalPoint.x, logicalPoint.y);

		var nearestOpt = NearestVertexFinder.findNearestVertex(
				mousePoint,
				violatingVertices.stream()
						.map(v -> v.getPositionBeforeFolding())
						.toList());

		if (nearestOpt.isEmpty()) {
			return;
		}

		var nearest = nearestOpt.get();

		if (nearest.distance >= scaleDistanceThreshold()) {
			pickedViolatingVertex = null;
			repaint();
			return;
		}

		pickedViolatingVertex = violatingVertices.stream()
				.filter(vertex -> vertex.getPositionBeforeFolding().equals(nearest.point))
				.findFirst().get();

		repaint();
	}

	private double scaleDistanceThreshold() {
		return CalculationResource.CLOSE_THRESHOLD / camera.getScale();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		affineTransform = camera.updateScaleByMouseWheel(e);
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

	@Override
	public View getTopLevelView() {
		return (View) SwingUtilities.getWindowAncestor(this);
	}

	@Override
	public void setViolatingVertices(final Collection<OriVertex> vertices) {
		this.violatingVertices = vertices;
	}

	@Override
	public void setPaintComponentListener(final Consumer<PaintComponentGraphics> listener) {
		paintComponentListener = listener;
	}

	@Override
	public void updateCenterOfPaper(final double x, final double y) {
		camera.updateCenterOfPaper(x, y);
	}

	@Override
	public double getScale() {
		return camera.getScale();
	}

	@Override
	public Optional<OriVertex> getPickedViolatingVertex() {
		return Optional.ofNullable(pickedViolatingVertex);
	}

}
