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

import jakarta.inject.Inject;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.CreasePatternGraphicDrawer;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.gui.view.creasepattern.PaintComponentGraphics;
import oripa.gui.view.main.PainterScreenView;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class PainterScreenPresenter {
	private final PainterScreenView view;

	private final ViewScreenUpdater screenUpdater;
	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;

	private final CutModelOutlinesHolder cutOutlinesHolder;

	private final MouseActionHolder mouseActionHolder;

	private final CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();

	private RectangleDomain paperDomainOfModel;

	@Inject
	public PainterScreenPresenter(final PainterScreenView view,
			final ViewScreenUpdater screenUpdater,
			final CreasePatternViewContext viewContext,
			final MouseActionHolder mouseActionHolder,
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutOutlineHolder) {
		this.view = view;

		this.screenUpdater = screenUpdater;
		this.paintContext = paintContext;
		this.viewContext = viewContext;
		this.mouseActionHolder = mouseActionHolder;
		this.cutOutlinesHolder = cutOutlineHolder;

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
		view.setGridVisibleUpdateListener(gridVisible -> {
			viewContext.setGridVisible(gridVisible);
			if (gridVisible) {
				paintContext.updateGrids();
			} else {
				paintContext.clearGrids();
			}
		});
		view.setCrossLineVisibleUpdateListener(viewContext::setCrossLineVisible);

		view.setCameraScaleUpdateListener(this::updateCameraScale);
		// view.setCameraCenterUpdateListener(this::updateCameraCenter);

		view.setUsingCtrlKeyOnDragListener(this::updateUsingCtrlKeyOnDrag);
	}

	private void paintComponent(final PaintComponentGraphics p) {
		var bufferObjDrawer = p.getBufferObjectDrawer();

		bufferObjDrawer.setAntiAlias(!viewContext.isZeroLineWidth());

		var actionOpt = mouseActionHolder.getMouseAction();

		var forceShowingVertex = actionOpt
				.map(action -> action.getEditMode() == EditMode.VERTEX)
				.orElse(false);

		drawer.draw(bufferObjDrawer, viewContext, paintContext, forceShowingVertex);

		if (paperDomainOfModel != null) {
			drawPaperDomainOfModel(bufferObjDrawer);
		}

		if (viewContext.isCrossLineVisible()) {
			var crossLines = cutOutlinesHolder.getOutlines();
			drawer.drawAllLines(bufferObjDrawer, crossLines, viewContext.getScale(),
					viewContext.isZeroLineWidth());
		}

		actionOpt.ifPresentOrElse(
				action -> drawByAction(action, p),
				() -> p.drawBufferImage());
	}

	private void drawByAction(final GraphicMouseAction action,
			final PaintComponentGraphics p) {
		var bufferObjDrawer = p.getBufferObjectDrawer();
		var objDrawer = p.getObjectDrawer();

		action.onDraw(bufferObjDrawer, viewContext, paintContext);

		p.drawBufferImage();

		paintContext.getCandidateVertexToPick()
				.ifPresent(candidate -> drawer.drawCandidatePositionString(objDrawer, candidate));

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
		var actionOpt = mouseActionHolder.getMouseAction();

		actionOpt.ifPresent(action -> mouseActionHolder.setMouseAction(action.onLeftClick(
				viewContext, paintContext, isCtrlKeyDown)));
	}

	private void mouseRightClicked(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		var actionOpt = mouseActionHolder.getMouseAction();

		actionOpt.ifPresent(action -> action.onRightClick(viewContext, paintContext, isCtrlKeyDown));
	}

	private void mousePressed(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		var actionOpt = mouseActionHolder.getMouseAction();

		actionOpt.ifPresent(action -> action.onPress(viewContext, paintContext, isCtrlKeyDown));
	}

	private void mouseReleased(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		var actionOpt = mouseActionHolder.getMouseAction();

		actionOpt.ifPresent(action -> action.onRelease(viewContext, paintContext, isCtrlKeyDown));
	}

	private void mouseDragged(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		viewContext.setLogicalMousePoint(mousePoint);

		var actionOpt = mouseActionHolder.getMouseAction();
		actionOpt.ifPresent(action -> action.onDrag(viewContext, paintContext, isCtrlKeyDown));
	}

	private void updateCameraScale(final Double scale) {
		viewContext.setScale(scale);
	}

	public void updateCameraCenter() {
		view.updateCameraCenter(paintContext.getPaperDomain());
	}

	private void mouseMoved(final Vector2d mousePoint, final boolean isCtrlKeyDown) {
		viewContext.setLogicalMousePoint(mousePoint);

		var actionOpt = mouseActionHolder.getMouseAction();
		actionOpt.ifPresent(action -> action.onMove(viewContext, paintContext, isCtrlKeyDown));
	}

	public void setPaperDomainOfModel(final RectangleDomain domain) {
		paperDomainOfModel = domain;
		screenUpdater.updateScreen();
	}

	private void updateUsingCtrlKeyOnDrag() {
		var actionOpt = mouseActionHolder.getMouseAction();

		actionOpt.ifPresent(action -> view.setUsingCtrlKeyOnDrag(action.isUsingCtrlKeyOnDrag()));
	}

	public void updateScreen() {
		screenUpdater.updateScreen();
	}

}
