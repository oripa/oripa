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

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.angle.SelectingStartPoint;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;

/**
 * @author OUCHI Koji
 *
 */
public class AngleSnapAction extends AbstractGraphicMouseAction {

	/**
	 * Constructor
	 */
	public AngleSnapAction() {
		super();

		setActionState(new SelectingStartPoint());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		setActionState(new SelectingStartPoint());
	}

	@Override
	public Vector2d onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		if (paintContext.getVertexCount() == 0) {
			return super.onMove(viewContext, paintContext, differentAction);
		}

		var crossPoint = NearestItemFinder.getNearestInSnapPoints(viewContext, paintContext);
		paintContext.setCandidateVertexToPick(crossPoint);
		return crossPoint;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		if (paintContext.getVertexCount() == 1) {
			drawSnapPoints(drawer, viewContext, paintContext);
		}

		super.onDraw(drawer, viewContext, paintContext);
		drawTemporaryLine(drawer, viewContext, paintContext);
		drawPickCandidateVertex(drawer, viewContext, paintContext);
	}

	private void drawSnapPoints(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		drawer.selectAssistLineColor();

		paintContext.getSnapPoints()
				.forEach(p -> drawVertex(drawer, viewContext, paintContext, p));
	}

}
