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
package oripa.gui.presenter.creasepattern;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.geometry.NearestVertexFinder;
import oripa.value.CalculationResource;
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

	public EnlargeLineAction() {
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);

		setActionState(new SelectingLine());
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
		super.onMove(viewContext, paintContext, differentAction);

		if (originOfEnlargement == null && paintContext.getCandidateLineToPick() == null) {
			var points = List.of(
					originalDomain.getLeftTop(),
					originalDomain.getLeftBottom(),
					originalDomain.getRightTop(),
					originalDomain.getRightBottom());

			mouseStartPoint = NearestVertexFinder.findNearestVertex(
					viewContext.getLogicalMousePoint(), points).point;

			return mouseStartPoint;
		}

		return null;
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

		if (paintContext.getCandidateLineToPick() == null) {
			originOfEnlargement = getOppositePoint(originalDomain, mouseStartPoint);
		}
	}

	private Vector2d getOppositePoint(final RectangleDomain domain, final Vector2d p) {
		if (p.epsilonEquals(domain.getLeftTop(), CalculationResource.POINT_EPS)) {
			return domain.getRightBottom();
		} else if (p.epsilonEquals(domain.getLeftBottom(), CalculationResource.POINT_EPS)) {
			return domain.getRightTop();
		} else if (p.epsilonEquals(domain.getRightTop(), CalculationResource.POINT_EPS)) {
			return domain.getLeftBottom();
		} else if (p.epsilonEquals(domain.getRightBottom(), CalculationResource.POINT_EPS)) {
			return domain.getLeftTop();
		}

		return null;
	}

	@Override
	public void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		var mousePoint = getMousePoint(viewContext, paintContext);

		var scales = computeScales(mousePoint);

		var currentPoint = scalePosition(mouseStartPoint, scales.getX(), scales.getY());

		enlargedDomain = new RectangleDomain(
				originOfEnlargement.getX(), originOfEnlargement.getY(),
				currentPoint.getX(), currentPoint.getY());
	}

	private Vector2d getMousePoint(final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		setCandidateVertexOnMove(viewContext, paintContext, false);

		var mousePoint = paintContext.getCandidateVertexToPick();
		if (mousePoint == null) {
			mousePoint = viewContext.getLogicalMousePoint();
		}

		return mousePoint;
	}

	private Vector2d computeScales(final Vector2d mousePoint) {
		var diff = new Vector2d();

		diff.sub(mousePoint, originOfEnlargement);

		double scaleX = diff.getX() / originalDomain.getWidth();
		double scaleY = diff.getY() / originalDomain.getHeight();

		return new Vector2d(scaleX, scaleY);
	}

	private Vector2d scalePosition(final Vector2d p, final double scaleX, final double scaleY) {

		double absScale = Math.min(Math.abs(scaleX), Math.abs(scaleY));

		double signX = Math.signum(scaleX);
		double signY = Math.signum(scaleY);

		var scaledDiff = new Vector2d();
		scaledDiff.setX(Math.abs(p.getX() - originOfEnlargement.getX()) * absScale * signX);
		scaledDiff.setY(Math.abs(p.getY() - originOfEnlargement.getY()) * absScale * signY);

		var scaled = new Vector2d();
		scaled.add(originOfEnlargement, scaledDiff);

		return scaled;
	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (originOfEnlargement != null) {
			paintContext.creasePatternUndo().pushUndoInfo();
			enlargeLines(viewContext, paintContext, differentAction);
		}

		originOfEnlargement = null;

		mouseStartPoint = null;
		originalDomain = null;
		enlargedDomain = null;
	}

	private void enlargeLines(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var painter = paintContext.getPainter();
		painter.removeLines(paintContext.getPickedLines());

		painter.addLines(createEnlargedLines(viewContext, paintContext));

		paintContext.clear(true);
	}

	private Collection<OriLine> createEnlargedLines(final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		var scales = computeScales(getMousePoint(viewContext, paintContext));

		return paintContext.getPickedLines().stream()
				.map(line -> new OriLine(
						scalePosition(line.getP0(), scales.getX(), scales.getY()),
						scalePosition(line.getP1(), scales.getX(), scales.getY()),
						line.getType()))
				.collect(Collectors.toList());

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		if (originOfEnlargement == null) {
			this.drawPickCandidateLine(drawer, viewContext, paintContext);
		}

		if (mouseStartPoint != null && paintContext.getCandidateLineToPick() == null) {
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
			createEnlargedLines(viewContext, paintContext).forEach(drawer::drawLine);

			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAssistLineColor();

			drawer.drawRectangle(enlargedDomain.getLeftTop(), enlargedDomain.getRightBottom());
		}

	}

}
