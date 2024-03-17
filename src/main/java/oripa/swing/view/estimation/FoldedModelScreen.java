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

package oripa.swing.view.estimation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.swing.JPanel;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.geom.RectangleDomain;
import oripa.gui.view.estimation.DefaultColors;
import oripa.renderer.estimation.DistortionFacade;
import oripa.renderer.estimation.DistortionMethod;
import oripa.renderer.estimation.FoldedModelPixelRenderer;
import oripa.renderer.estimation.VertexDepthMapFactory;
import oripa.swing.drawer.java2d.GraphicItemConverter;
import oripa.swing.drawer.java2d.PixelDrawer;
import oripa.swing.view.util.AffineCamera;
import oripa.vecmath.Vector2d;

/**
 * A screen to show the folded state of origami.
 *
 * @author Koji
 *
 */
public class FoldedModelScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final BufferedImage bufferImage;
	private static final int BUFFERW = 600; // width
	private static final int BUFFERH = 600; // height
	private boolean useColor = true;
	private boolean fillFaces = true;
	private boolean ambientOcclusion = false;
	private boolean faceOrderFlip = false;
	private final double scaleRate = 0.8;
	private boolean drawEdges = true;

	private Point2D preMousePoint;
	private AffineTransform affineTransform;

	private final AffineCamera camera = new AffineCamera();

	private Color frontColor = DefaultColors.FRONT;
	private Color backColor = DefaultColors.BACK;
	private final Color singleColor = DefaultColors.WHITE;

	private OrigamiModel origamiModel = null;
	private OverlapRelation overlapRelation;
	private OriFace selectedSubface = null;

	private final FoldedModelPixelRenderer pixelRenderer;

	private RectangleDomain domain;

	private Vector2d distortionParameter = new Vector2d(0, 0);
	private double eps = 1e-5;

	private DistortionMethod distortionMethod = DistortionMethod.NONE;
	private DistortionFacade distortion;

	private Map<OriVertex, Integer> vertexDepths;

	public FoldedModelScreen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		pixelRenderer = new FoldedModelPixelRenderer(BUFFERW, BUFFERH);

		affineTransform = new AffineTransform();

		bufferImage = new BufferedImage(BUFFERW, BUFFERH, BufferedImage.TYPE_INT_RGB);
	}

	public void initializeCamera() {
		camera.updateRotateAngle(0);
		camera.updateCameraPosition(BUFFERW / 2, BUFFERH / 2);
		camera.updateScale(1);
		camera.updateCenterOfPaper(BUFFERW / 2, BUFFERH / 2);
	}

	private void resetViewMatrix() {
		if (origamiModel == null) {
			return;
		}

		domain = origamiModel.createDomainOfFoldedModel();
		distortion = new DistortionFacade(domain, BUFFERW, BUFFERH);

		initializeCamera();

		redrawOrigami();
	}

	public void redrawOrigami() {
		drawOrigami();
		repaint();
	}

	public void setUseColor(final boolean b) {
		useColor = b;
		redrawOrigami();
	}

	public void setFillFace(final boolean bFillFace) {
		fillFaces = bFillFace;
		redrawOrigami();
	}

	/**
	 *
	 * @param d
	 *            d.getX() and d.getY() should be in [-1.0, 1.0] respectively.
	 */
	public void setDistortionParameter(final Vector2d d) {
		distortionParameter = d;

		redrawOrigami();
	}

	public void setDistortionMethod(final DistortionMethod method) {
		distortionMethod = method;
		redrawOrigami();
	}

	public void drawEdge(final boolean bEdge) {
		drawEdges = bEdge;
		redrawOrigami();
	}

	public void flipFaces(final boolean bFlip) {
		this.faceOrderFlip = bFlip;
		redrawOrigami();
	}

	public void shadeFaces(final boolean bShade) {
		ambientOcclusion = bShade;
		redrawOrigami();
	}

	public void setModel(final FoldedModel foldedModel, final int overlapRelationIndex, final double eps) {
		this.eps = eps;

		if (foldedModel == null) {
			this.origamiModel = null;
		} else {
			this.origamiModel = foldedModel.getOrigamiModel();

			var overlapRelations = foldedModel.getOverlapRelations();
			if (overlapRelations == null || overlapRelations.isEmpty()) {
				overlapRelation = null;
			} else {
				updateOverlapRelation(overlapRelations.get(overlapRelationIndex));
			}
		}

		resetViewMatrix();
		redrawOrigami();
	}

	void setSelectedSubface(final OriFace face) {
		selectedSubface = face;

		redrawOrigami();
	}

	public void setOverlapRelation(final OverlapRelation overlapRelation) {
		updateOverlapRelation(overlapRelation);
		redrawOrigami();
	}

	private void updateOverlapRelation(final OverlapRelation overlapRelation) {
		if (overlapRelation == null) {
			this.overlapRelation = null;
			return;
		}

		this.overlapRelation = overlapRelation;

		vertexDepths = new VertexDepthMapFactory().create(origamiModel, overlapRelation, eps);
	}

	void setColors(final Color front, final Color back) {
		frontColor = front;
		backColor = back;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (pixelRenderer == null) {
			return;
		}

		var bufferg = bufferImage.createGraphics();

		bufferg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		bufferg.setTransform(new AffineTransform());
		// Clear image
		bufferg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		bufferg.setTransform(affineTransform);

		var drawer = new PixelDrawer();

		drawer.draw(bufferg, pixelRenderer.getPixels(), BUFFERW, BUFFERH);

		if (selectedSubface != null) {
			drawSubface(bufferg);
		}

		g.drawImage(bufferImage, 0, 0, this);

	}

	public void drawOrigami() {
		if (origamiModel == null || overlapRelation == null) {
			return;
		}

		if (!origamiModel.isFolded()) {
			return;
		}

		var converter = distortion.createCoordinateConverter(distortionMethod, distortionParameter, getFinalScale());

		var distortionResult = distortion.apply(origamiModel, overlapRelation, converter, vertexDepths, eps);

		var faces = distortionResult.getFaces();
		var interpolatedOverlapRelation = distortionResult.getInterpolatedOverlapRelation();

		pixelRenderer.render(faces, interpolatedOverlapRelation, origamiModel.createPaperDomain(),
				new FoldedModelPixelRenderer.Option()
						.setAmbientOcclusion(ambientOcclusion)
						.setDrawEdges(drawEdges)
						.setFaceOrderFlipped(faceOrderFlip)
						.setFillFace(fillFaces)
						.setColors(useColor ? frontColor : singleColor, useColor ? backColor : singleColor));
	}

	private void drawSubface(final Graphics2D g2d) {
		var converter = distortion.createCoordinateConverter(distortionMethod, distortionParameter, getFinalScale());
		var convertedSubface = selectedSubface.halfedgeStream()
				.map(v -> converter.convert(v.getPosition(), 0, v.getPositionBeforeFolding()))
				.toList();

		var itemConverter = new GraphicItemConverter();
		var path2d = itemConverter.toPath2D(convertedSubface);
		g2d.setColor(new Color(255, 255, 255, 128));
		g2d.draw(path2d);
	}

	private double getFinalScale() {
		return scaleRate * Math.min(
				BUFFERW / (domain.getWidth()),
				BUFFERH / (domain.getHeight())) * 0.95;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	@Override
	public void mouseExited(final MouseEvent e) {

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (doCameraDragAction(e, camera::updateTranslateByMouseDragged)) {
			return;
		}

		if (doCameraDragAction(e, camera::updateRotateByMouseDragged)) {
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

	@Override
	public void mouseMoved(final MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		affineTransform = camera.updateScaleByMouseWheel(e);
		repaint();
	}

	public boolean isFaceOrderFlipped() {
		return faceOrderFlip;
	}

	public double getRotateAngle() {
		return camera.getRotateAngle();
	}

	public Map<OriVertex, Integer> getVertexDepths() {
		return vertexDepths;
	}

	public double getEps() {
		return eps;
	}
}
