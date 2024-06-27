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
package oripa.domain.paint.p2lp2l;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.AbstractActionState;
import oripa.domain.paint.core.SnapPointFactory;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class SelectingSolutionLine extends AbstractActionState {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingSecondLine.class);
		setNextClass(SelectingFirstVertexForFoldLine.class);

	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint, final boolean doSpecial) {
		var solutionOpt = context.getSolutionLineToPick();
		if (solutionOpt.isPresent()) {
			return true;
		}

		return false;
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		var snapPoints = new SnapPointFactory().createSnapPoints(context.getCreasePattern(),
				context.getSolutionLineToPick().orElseThrow(), context.getPointEps());

		context.setSnapPoints(snapPoints);
	}

	@Override
	protected void undoAction(final PaintContext context) {
		context.clearSolutionLines();
		context.setSolutionLineToPick(null);
		context.popLine();
	}

}
