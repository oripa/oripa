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
	private Vector2d mouseCandidatePoint;
	private Vector2d startPoint;

	private RectangleDomain originalDomain;
	private RectangleDomain enlargedDomain;

	public EnlargeLineAction() {
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);

		setActionState(new SelectingLine()); // tentative
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

		originalDomain = new RectangleDomain(context.getPickedLines());
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		if (startPoint == null) {
			var points = List.of(
					originalDomain.getLeftTop(),
					originalDomain.getLeftBottom(),
					originalDomain.getRightTop(),
					originalDomain.getRightBottom());

			mouseCandidatePoint = NearestVertexFinder.findNearestVertex(
					viewContext.getLogicalMousePoint(), points).point;

			return mouseCandidatePoint;
		}

		return null;
//		return super.onMove(viewContext, paintContext, differentAction);
	}

	@Override
	public void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		startPoint = getOppositePoint(originalDomain, mouseCandidatePoint);
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

		var mousePoint = viewContext.getLogicalMousePoint();

		var scales = computeScales(mousePoint);

		var oppositePoint = getOppositePoint(originalDomain, startPoint);

		var currentPoint = scalePosition(oppositePoint, scales.getX(), scales.getY());

		enlargedDomain = new RectangleDomain(
				startPoint.getX(), startPoint.getY(),
				currentPoint.getX(), currentPoint.getY());
	}

	private Vector2d computeScales(final Vector2d mousePoint) {
		var diff = new Vector2d();

		diff.sub(mousePoint, startPoint);

		double scaleX = diff.x / originalDomain.getWidth();
		double scaleY = diff.y / originalDomain.getHeight();

		return new Vector2d(scaleX, scaleY);
	}

	private Vector2d scalePosition(final Vector2d p, final double scaleX, final double scaleY) {

		double absScale = Math.min(Math.abs(scaleX), Math.abs(scaleY));

		double signX = Math.signum(scaleX);
		double signY = Math.signum(scaleY);

		var scaledDiff = new Vector2d();
		scaledDiff.setX(Math.abs(p.getX() - startPoint.getX()) * absScale * signX);
		scaledDiff.setY(Math.abs(p.getY() - startPoint.getY()) * absScale * signY);

		var scaled = new Vector2d();
		scaled.add(startPoint, scaledDiff);

		return scaled;
	}

	@Override
	public void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {

		if (startPoint != null) {
			enlargeLines(viewContext, paintContext);
		}

		startPoint = null;

		mouseCandidatePoint = null;
		originalDomain = null;
		enlargedDomain = null;
	}

	private void enlargeLines(final CreasePatternViewContext viewContext, final PaintContext paintContext) {
		var painter = paintContext.getPainter();
		painter.removeLines(paintContext.getPickedLines());

		var scales = computeScales(viewContext.getLogicalMousePoint());

		var scaledLines = paintContext.getPickedLines().stream()
				.map(line -> new OriLine(
						scalePosition(line.getP0(), scales.getX(), scales.getY()),
						scalePosition(line.getP1(), scales.getX(), scales.getY()),
						line.getType()))
				.collect(Collectors.toList());

		painter.addLines(scaledLines);

		paintContext.clear(true);
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		this.drawPickCandidateLine(drawer, viewContext, paintContext);

		if (mouseCandidatePoint != null) {
			drawer.selectAssistLineColor();
			drawer.selectMouseActionVertexSize(viewContext.getScale());
			drawer.drawVertex(mouseCandidatePoint);
		}

		if (originalDomain != null) {
			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAreaSelectionColor();

			drawer.drawRectangle(originalDomain.getLeftTop(), originalDomain.getRightBottom());
		}

		if (enlargedDomain != null) {
			drawer.selectAreaSelectionStroke(viewContext.getScale());
			drawer.selectAssistLineColor();

			drawer.drawRectangle(enlargedDomain.getLeftTop(), enlargedDomain.getRightBottom());
		}

	}

}
