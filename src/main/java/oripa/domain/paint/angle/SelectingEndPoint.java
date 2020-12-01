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
package oripa.domain.paint.angle;

import java.awt.geom.Point2D.Double;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.AbstractActionState;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class SelectingEndPoint extends AbstractActionState {

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.core.AbstractActionState#initialize()
	 */
	@Override
	protected void initialize() {
		setPreviousClass(SelectingStartPoint.class);
		setNextClass(SelectingStartPoint.class);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.AbstractActionState#onAct(oripa.domain.paint.
	 * PaintContextInterface, java.awt.geom.Point2D.Double, boolean)
	 */
	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		var picked = NearestItemFinder.getNearestInAngleSnapCrossPoints(context);

		if (picked == null) {
			return false;
		}

		context.pushVertex(picked);

		return true;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.AbstractActionState#onResult(oripa.domain.paint.
	 * PaintContextInterface, boolean)
	 */
	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		if (context.getVertexCount() != 2) {
			throw new IllegalStateException("wrong state: impossible vertex selection.");
		}

		var p0 = context.popVertex();
		var p1 = context.popVertex();

		context.creasePatternUndo().pushUndoInfo();

		context.getPainter().addLine(
				new OriLine(p0, p1, context.getLineTypeOfNewLines()));

		context.clear(false);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.AbstractActionState#undoAction(oripa.domain.paint
	 * .PaintContextInterface)
	 */
	@Override
	protected void undoAction(final PaintContextInterface context) {
		context.popVertex();
		context.getAngleSnapCrossPoints().clear();
	}

}
