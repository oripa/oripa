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
package oripa.gui.presenter.creasepattern.enlarge;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.ObjectGraphicDrawer;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class EnlargeLineAction extends AbstractGraphicMouseAction {
	private Vector2d mouseStartPoint;
	private Vector2d originOfEnlargement;

	private RectangleDomain originalDomain;
	private RectangleDomain enlargedDomain;

	private Enlarger enlarger;

	public EnlargeLineAction() {
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);

		setActionState(new SelectingLine());
	}

	@Override
	public boolean isUsingCtrlKeyOnDrag() {
		return isEnlarging();
	}

	private boolean isEnlarging() {
		return originOfEnlargement != null;
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(false);

		Collection<OriLine> creasePattern = context.getCreasePattern();
		if (creasePattern == null) {
			return;
		}

		creasePattern.stream()
				.filter(line -> line.selected)
				.forEach(context::pushLine);

		originalDomain = createDomain(context.getPickedLines());
	}

	private RectangleDomain createDomain(final Collection<OriLine> lines) {
		return lines.isEmpty() ? null : new RectangleDomain(lines);
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		super.onMove(viewContext, paintContext, false);

		if (isEnlarging()) {
			// Should not come to this path because onDrag() should be called
			// instead.
			throw new RuntimeException("wrong execution path.");
		}

		if (originalDomain == null) {
			return null;
		}

		var points = List.of(
				originalDomain.getLeftTop(),
				originalDomain.getLeftBottom(),
				originalDomain.getRightTop(),
				originalDomain.getRightBottom());

		mouseStartPoint = NearestItemFinder.getNearestVertex(viewContext, points);
		return mouseStartPoint;
	}

	@Override
	public GraphicMouseAction onLeftClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var nextAction = super.onLeftClick(viewContext, paintContext, differentAction);

		originalDomain = createDomain(paintContext.getPickedLines());

		return nextAction;
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean doSpecial) {
		super.onRightClick(viewContext, paintContext, doSpecial);

		originalDomain = createDomain(paintContext.getPickedLines());
	}

	/**
	 * set old line-selected marks to current context.
	 */
	@Override
	public void undo(final PaintContext context) {
		context.creasePatternUndo().undo();

		recover(context);
	}

	@Override
	public void redo(final PaintContext context) {
		context.creasePatternUndo().redo();

		recover(context);
	}

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (mouseStartPoint == null) {
			return;
		}

		switchEnlarger(differentAction);
	}

	private void switchEnlarger(final boolean differentAction) {
		enlarger = differentAction ? new CenterOriginEnlarger() : new CornerOriginEnlarger();
		originOfEnlargement = enlarger.createOriginOfEnlargement(originalDomain, mouseStartPoint);
	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var mousePoint = getMousePoint(viewContext, paintContext);

		if (mouseStartPoint == null) {
			return;
		}

		switchEnlarger(differentAction);
		enlargedDomain = enlarger.createEnlargedDomain(mousePoint, originOfEnlargement, mouseStartPoint);
	}

	private Vector2d getMousePoint(final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		setCandidateVertexOnMove(viewContext, paintContext, false);

		var mousePoint = paintContext.getCandidateVertexToPick();
		if (mousePoint == null) {
			mousePoint = viewContext.getLogicalMousePoint();
		}

		return mousePoint;
	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (!isEnlarging()) {
			return;
		}

		paintContext.creasePatternUndo().pushUndoInfo();
		enlargeLines(viewContext, paintContext);

		originOfEnlargement = null;

		mouseStartPoint = null;
		originalDomain = null;
		enlargedDomain = null;

		enlarger = null;
	}

	private void enlargeLines(final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		var painter = paintContext.getPainter();
		painter.removeLines(paintContext.getPickedLines());

		var mousePoint = getMousePoint(viewContext, paintContext);
		painter.addLines(enlarger.createEnlargedLines(mousePoint, originOfEnlargement, mouseStartPoint,
				paintContext.getPickedLines()));

		paintContext.clear(true);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		if (!isEnlarging()) {
			this.drawPickCandidateLine(drawer, viewContext, paintContext);
		}

		if (mouseStartPoint != null) {
			drawer.selectAssistLineColor();
			drawer.selectMouseActionVertexSize(viewContext.getScale());
			drawer.drawVertex(mouseStartPoint);
		}

		if (originalDomain != null) {
			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAreaSelectionColor();
			drawer.drawRectangle(originalDomain.getLeftTop(), originalDomain.getRightBottom());
		}

		if (enlargedDomain != null) {
			this.drawPickCandidateVertex(drawer, viewContext, paintContext);

			drawer.selectCandidateLineStroke(viewContext.getScale(), viewContext.isZeroLineWidth());
			drawer.selectAssistLineColor();
			var mousePoint = getMousePoint(viewContext, paintContext);
			enlarger.createEnlargedLines(mousePoint, originOfEnlargement, mouseStartPoint,
					paintContext.getPickedLines()).forEach(drawer::drawLine);

			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAssistLineColor();
			drawer.drawRectangle(enlargedDomain.getLeftTop(), enlargedDomain.getRightBottom());
		}
	}
}
