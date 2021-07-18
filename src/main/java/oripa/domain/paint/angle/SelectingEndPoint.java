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

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.AbstractActionState;
import oripa.domain.paint.core.PickedVerticesConnectionLineAdderCommand;
import oripa.util.Command;

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

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean doSpecial) {

//		var picked = NearestItemFinder.getNearestInAngleSnapCrossPoints(context);
		var picked = context.getCandidateVertexToPick();

		if (picked == null) {
			return false;
		}

		context.pushVertex(picked);

		return true;
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		if (context.getVertexCount() != 2) {
			throw new IllegalStateException("wrong state: impossible vertex selection.");
		}

		Command command = new PickedVerticesConnectionLineAdderCommand(context);
		command.execute();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.AbstractActionState#undoAction(oripa.domain.paint
	 * .PaintContextInterface)
	 */
	@Override
	protected void undoAction(final PaintContext context) {
		context.popVertex();
		context.getAngleSnapCrossPoints().clear();
	}

}
