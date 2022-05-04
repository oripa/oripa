/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.gui.presenter.main;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.vecmath.Vector2d;

import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.drawer.java2d.CreasePatternObjectDrawer;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.CreasePatternGraphicDrawer;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ObjectGraphicDrawer;
import oripa.gui.view.main.PaintComponentParameter;
import oripa.gui.view.main.PainterScreenView;
import oripa.gui.viewsetting.main.MainScreenUpdater;

/**
 * @author OUCHI Koji
 *
 */
public class PainterScreenPresenter {
	private final PainterScreenView view;

	private final MainScreenUpdater screenUpdater;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final CutModelOutlinesHolder cutOutlinesHolder;

	private final MouseActionHolder mouseActionHolder;

	private final CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();

	private RectangleDomain paperDomainOfModel;

	public PainterScreenPresenter(final PainterScreenView view,
			final MouseActionHolder mouseActionHolder,
			final CreasePatternViewContext viewContext,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutOutlineHolder) {
		this.view = view;

		screenUpdater = view.getScreenUpdater();
		this.paintContext = paintContext;
		this.viewContext = viewContext;
		this.cutOutlinesHolder = cutOutlineHolder;
		this.mouseActionHolder = mouseActionHolder;

		var actionSwitcher = new SwitcherBetweenPasteAndChangeOrigin(mouseActionHolder);
		screenUpdater.setChangeActionIfCopyAndPaste(actionSwitcher);

		setListeners();

		view.initializeCamera(paintContext.getPaperDomain());
	}

	private void setListeners() {
		view.setPaintComponentListener(this::paintComponent);

		view.setMouseLeftClickListener(this::mouseLeftClicked);
		view.setMouseRightClickListener(this::mouseRightClicked);
		view.setMouseDragListener(this::mouseDragged);
		view.setMouseMoveListener(this::mouseMoved);
		view.setMousePressListener(this::mousePressed);
		view.setMouseReleaseListener(this::mouseReleased);

		view.setZeroLineWidthUpdateListener(viewContext::setZeroLineWidth);
		view.setVertexVisibleUpdateListener(viewContext::setVertexVisible);
		view.setMVLineVisibleUpdateListener(viewContext::setMVLineVisible);
		view.setAuxLineVisibleUpdateListener(viewContext::setAuxLineVisible);
		view.setGridVisibleUpdateListener(viewContext::setGridVisible);
		view.setCrossLineVisibleUpdateListener(viewContext::setCrossLineVisible);

		view.setCameraScaleUpdateListener(this::updateCameraScale);
		view.setCameraCenterUpdateListener(this::updateCameraCenter);

		view.setUsingCtrlKeyOnDragListener(this::updateUsingCtrlKeyOnDrag);
	}

	private void paintComponent(final PaintComponentParameter p) {
		var g = p.getGraphics();
		var g2d = (Graphics2D) g;
		var bufferG2D = p.getBufferGraphics();
		var bufferImage = p.getBufferImage();

		if (!viewContext.isZeroLineWidth()) {
			bufferG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		GraphicMouseAction action = mouseActionHolder.getMouseAction();

		ObjectGraphicDrawer bufferObjDrawer = new CreasePatternObjectDrawer(bufferG2D);

		drawer.draw(bufferObjDrawer, viewContext, paintContext,
				action == null ? false : action.getEditMode() == EditMode.VERTEX);

		if (paperDomainOfModel != null) {
			drawPaperDomainOfModel(bufferObjDrawer);
		}

		if (viewContext.isCrossLineVisible()) {
			var crossLines = cutOutlinesHolder.getOutlines();
			drawer.drawAllLines(bufferObjDrawer, crossLines, viewContext.getScale(),
					viewContext.isZeroLineWidth());
		}

		if (action == null) {
			g.drawImage(bufferImage, 0, 0, view.asPanel());
			return;
		}

		action.onDraw(bufferObjDrawer, viewContext, paintContext);

		g.drawImage(bufferImage, 0, 0, view.asPanel());

		ObjectGraphicDrawer objDrawer = new CreasePatternObjectDrawer(g2d);
		drawer.drawCandidatePositionString(objDrawer,
				paintContext.getCandidateVertexToPick());
	}

	private void drawPaperDomainOfModel(final ObjectGraphicDrawer objDrawer) {
		objDrawer.selectAssistLineColor();
		objDrawer.selectAreaSelectionStroke(viewContext.getScale());
		var domain = new RectangleDomain(
				paperDomainOfModel.getLeft() - 10, paperDomainOfModel.getTop() - 10,
				paperDomainOfModel.getRight() + 10, paperDomainOfModel.getBottom() + 10);
		objDrawer.drawRectangle(domain.getLeftTop(), domain.getRightBottom());
	}

	private void mouseLeftClicked(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		final GraphicMouseAction action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		mouseActionHolder.setMouseAction(action.onLeftClick(
				viewContext, paintContext, isCtrlKeyDown));
	}

	private void mouseRightClicked(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		final GraphicMouseAction action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		action.onRightClick(viewContext, paintContext, isCtrlKeyDown);
	}

	private void mousePressed(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		GraphicMouseAction action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		action.onPress(viewContext, paintContext, isCtrlKeyDown);
	}

	private void mouseReleased(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		GraphicMouseAction action = mouseActionHolder.getMouseAction();

		if (action == null) {
			return;
		}

		action.onRelease(viewContext, paintContext, isCtrlKeyDown);
	}

	private void mouseDragged(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		GraphicMouseAction action = mouseActionHolder.getMouseAction();

		viewContext.setLogicalMousePoint(mousePoint);
		action.onDrag(viewContext, paintContext, isCtrlKeyDown);
	}

	private void updateCameraScale(final Double scale) {
		viewContext.setScale(scale);
	}

	private void updateCameraCenter() {
		view.updateCameraCenter(paintContext.getPaperDomain());
	}

	private void mouseMoved(final Vector2d mousePoint, final boolean isCtrlKeyDown) {

		viewContext.setLogicalMousePoint(mousePoint);

		final GraphicMouseAction action = mouseActionHolder.getMouseAction();
		if (action == null) {
			return;
		}

		action.onMove(viewContext, paintContext, isCtrlKeyDown);
	}

	public void setPaperDomainOfModel(final RectangleDomain domain) {
		paperDomainOfModel = domain;
		screenUpdater.updateScreen();
	}

	private void updateUsingCtrlKeyOnDrag() {
		view.setUsingCtrlKeyOnDrag(mouseActionHolder.getMouseAction().isUsingCtrlKeyOnDrag());
	}

}
