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

package oripa.gui.view.model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cutmodel.CutModelOutlinesFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.FolderTool;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.drawer.java2d.FoldedModelObjectDrawer;
import oripa.gui.presenter.model.FoldedModelGraphicDrawer;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.gui.view.util.MouseUtility;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.value.OriLine;

/**
 * Screen to show the silhouette of origami which is the result of face
 * transform according to the creases.
 *
 * @author OUCHI Koji
 *
 */
public class ModelViewScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener,
		ComponentListener {
	private static final Logger logger = LoggerFactory.getLogger(ModelViewScreen.class);

	private Image bufferImage = null;
	private Graphics2D bufferg = null;
	private Point2D preMousePoint; // Screen coordinates
	private double scale = 1;
	private double transX = 0;
	private double transY = 0;
	private final Vector2d modelCenter = new Vector2d();
	private double rotateAngle = 0;
	private final AffineTransform affineTransform = new AffineTransform();

	private OriLine scissorsLine = null;
	private boolean scissorsLineVisible = false;
	private int scissorsLineAngleDegree = 90;
	private double scissorsLinePosition = 0;
	private ModelDisplayMode modelDisplayMode = ModelDisplayMode.FILL_ALPHA;

	private OrigamiModel origamiModel = null;
	private final CutModelOutlinesHolder lineHolder;
	private final CallbackOnUpdate onUpdateScissorsLine;
	private final MainScreenSetting mainScreenSetting;

	public ModelViewScreen(final CutModelOutlinesHolder aLineHolder, final CallbackOnUpdate c,
			final MainScreenSetting mainScreenSetting) {

		lineHolder = aLineHolder;
		onUpdateScissorsLine = c;

		this.mainScreenSetting = mainScreenSetting;

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);

//		scissorsLine = new OriLine();
		scale = 1.0;
		rotateAngle = 0;
		setBackground(Color.white);

		addPropertyChangeListenersToSetting();
	}

	private void addPropertyChangeListenersToSetting() {
		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.CROSS_LINE_VISIBLE, e -> {
					scissorsLineVisible = (boolean) e.getNewValue();
					if (scissorsLineVisible) {
						recalcScissorsLine();
					} else {
						repaint();
						onUpdateScissorsLine.onUpdate();
					}
				});
	}

	public void setModelDisplayMode(final ModelDisplayMode mode) {
		modelDisplayMode = mode;
	}

	public ModelDisplayMode getModelDisplayMode() {
		return modelDisplayMode;
	}

	public void setModel(final OrigamiModel origamiModel, final int boundSize) {
		this.origamiModel = origamiModel;
		resetViewMatrix(boundSize);
	}

	// ! Asynchronous behavior of JComponent causes
	// delayed response of size changing:
	// setSize(w, h);
	// resetViewMatrix();
	// may results scale = zero if it is just after construction.

	private void resetViewMatrix(final int boundSize) {

		List<OriFace> faces = origamiModel.getFaces();

		boolean hasModel = origamiModel.hasModel();

		rotateAngle = 0;
		if (!hasModel) {
			logger.info("reset view matrix: origamiModel does not have a model data.");
			scale = 1.0;
		} else {
			// Align the center of the model, combined scale
			var folderTool = new FolderTool();
			var domain = folderTool.createDomainOfFoldedModel(faces);
			modelCenter.x = domain.getCenterX();
			modelCenter.y = domain.getCenterY();

			scale = 0.8 * Math.min(
					boundSize / domain.getWidth(), boundSize / domain.getHeight());

			updateAffineTransform();
			recalcScissorsLine();
		}
	}

	// Update the current AffineTransform
	private void updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
		affineTransform.scale(scale, scale);
		affineTransform.translate(transX, transY);
		affineTransform.rotate(rotateAngle);
		affineTransform.translate(-modelCenter.x, -modelCenter.y);
	}

	private void buildBufferImage() {
		bufferImage = createImage(getWidth(), getHeight());
		bufferg = (Graphics2D) bufferImage.getGraphics();

		updateAffineTransform();

	}

	// Scaling relative to the center of the screen
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (bufferImage == null) {
			buildBufferImage();
		}

		bufferg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		bufferg.setTransform(new AffineTransform());
		bufferg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		bufferg.setTransform(affineTransform);

		Graphics2D g2d = bufferg;

		if (origamiModel == null) {
			logger.info("null origamiModel.");
			return;
		}

		if (!origamiModel.hasModel()) {
			logger.info("origamiModel does not have a model data.");
			return;
		}
		var objDrawer = new FoldedModelObjectDrawer(g2d);

		var drawer = new FoldedModelGraphicDrawer();

		drawer.draw(objDrawer, origamiModel, scissorsLineVisible ? scissorsLine : null, modelDisplayMode, scale);

//		g2d.setStroke(selector.createDefaultStroke(scale));
//		if (modelDisplayMode == ModelDisplayMode.FILL_ALPHA) {
//			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
//		}
//		drawModel(g2d);
		g.drawImage(bufferImage, 0, 0, this);

	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		Point2D.Double clickPoint = new Point2D.Double();
		try {
			affineTransform.inverseTransform(e.getPoint(), clickPoint);
		} catch (Exception ex) {
		}
	}

	public void setScissorsLineAngle(final int angleDegree) {
		scissorsLineAngleDegree = angleDegree;
		recalcScissorsLine();
	}

	public void setScissorsLinePosition(final int positionValue) {
		scissorsLinePosition = positionValue;
		recalcScissorsLine();
	}

	public void recalcScissorsLine() {
		scissorsLine = new OriLine();

		Vector2d dir = new Vector2d(Math.cos(Math.PI * scissorsLineAngleDegree / 180.0),
				Math.sin(Math.PI * scissorsLineAngleDegree / 180.0));
		scissorsLine.p0.set(modelCenter.x - dir.x * 300, modelCenter.y - dir.y * 300);
		scissorsLine.p1.set(modelCenter.x + dir.x * 300, modelCenter.y + dir.y * 300);
		Vector2d moveVec = new Vector2d(-dir.y, dir.x);
		moveVec.normalize();
		moveVec.scale(scissorsLinePosition);
		scissorsLine.p0.add(moveVec);
		scissorsLine.p1.add(moveVec);

		var factory = new CutModelOutlinesFactory();
		lineHolder.setOutlines(factory.createOutlines(scissorsLine, origamiModel));

		repaint();

		onUpdateScissorsLine.onUpdate();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {

	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (MouseUtility.isRightButtonDown(e)) {
			transX += (e.getX() - preMousePoint.getX()) / scale;
			transY += (e.getY() - preMousePoint.getY()) / scale;

			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		} else if (MouseUtility.isLeftButtonDown(e)) {
			rotateAngle += (e.getX() - preMousePoint.getX()) / 100.0;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		double scale_ = (100.0 - e.getWheelRotation() * 5) / 100.0;
		scale *= scale_;
		updateAffineTransform();
		repaint();
	}

	@Override
	public void componentResized(final ComponentEvent arg0) {
		var preSize = getSize();

		transX = transX - preSize.width * 0.5 + getWidth() * 0.5;
		transY = transY - preSize.height * 0.5 + getHeight() * 0.5;

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
}
