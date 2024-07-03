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

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.p2ltp.SelectingFirstVertex;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class PointToLineThroughPointAxiomAction extends AbstractGraphicMouseAction {

	public PointToLineThroughPointAxiomAction() {
		setActionState(new SelectingFirstVertex());
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		super.recoverImpl(context);
		setActionState(new SelectingFirstVertex());
	}

	@Override
	public Optional<Vector2d> onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		if (paintContext.getVertexCount() <= 1 && paintContext.getLineCount() <= 1) {
			return super.onMove(viewContext, paintContext, differentAction);
		}

		if (paintContext.getVertexCount() == 2 && paintContext.getLineCount() == 1
				&& paintContext.getSnapPoints().isEmpty()) {
			var solutionLineOpt = NearestItemFinder.getNearestInSolutionLines(viewContext, paintContext);
			if (solutionLineOpt.isPresent()) {
				paintContext.setSolutionLineToPick(solutionLineOpt.orElseThrow());
			} else {
				paintContext.setSolutionLineToPick(null);
			}
			return Optional.empty();
		}

		var nearestOpt = super.onMove(viewContext, paintContext, differentAction);

		var snapPointOpt = NearestItemFinder.getNearestInSnapPoints(viewContext, paintContext);

		var mousePoint = viewContext.getLogicalMousePoint();
		snapPointOpt
				.filter(snapPoint -> nearestOpt.isEmpty()
						|| snapPoint.distance(mousePoint) < nearestOpt.orElseThrow().distance(mousePoint))
				.ifPresent(snapPoint -> paintContext.setCandidateVertexToPick(snapPoint));

		return snapPointOpt;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {

		if (paintContext.getVertexCount() == 0 && paintContext.getLineCount() == 0) {
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		}
		if (paintContext.getVertexCount() == 1 && paintContext.getLineCount() == 0) {
			drawPickCandidateLine(drawer, viewContext, paintContext);
		}
		if (paintContext.getVertexCount() == 1 && paintContext.getLineCount() == 1) {
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		}

		if (!paintContext.getSolutionLines().isEmpty()) {
			drawSolutionLines(drawer, viewContext, paintContext);
			drawSolutionCandidateLine(drawer, viewContext, paintContext);
		}

		if (!paintContext.getSnapPoints().isEmpty()) {
			drawSnapPoints(drawer, viewContext, paintContext);
			drawPickCandidateVertex(drawer, viewContext, paintContext);
		}

		if (paintContext.getVertexCount() == 3) {
			drawTemporaryLine(drawer, viewContext, paintContext);
		}

		super.onDraw(drawer, viewContext, paintContext);
	}
}
