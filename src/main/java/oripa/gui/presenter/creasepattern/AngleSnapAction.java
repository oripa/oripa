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

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.angle.SelectingStartPoint;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;

/**
 * @author OUCHI Koji
 *
 */
public class AngleSnapAction extends GraphicMouseAction {

	/**
	 * Constructor
	 */
	public AngleSnapAction() {
		super();

		setActionState(new SelectingStartPoint());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		setActionState(new SelectingStartPoint());
	}

	@Override
	public Vector2d onMove(final PaintContextInterface context, final boolean differentAction) {
		if (context.getVertexCount() == 0) {
			return super.onMove(context, differentAction);
		}

		var crossPoint = NearestItemFinder.getNearestInAngleSnapCrossPoints(context);
		context.setCandidateVertexToPick(crossPoint);
		return crossPoint;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		if (context.getVertexCount() == 1) {
			drawSnapPoints(drawer, context);
		}

		super.onDraw(drawer, context);
		drawTemporaryLine(drawer, context);
		drawPickCandidateVertex(drawer, context);
	}

	private void drawSnapPoints(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		drawer.selectAssistLineColor();

		context.getAngleSnapCrossPoints()
				.forEach(p -> drawVertex(drawer, context, p));
	}

}
