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
package oripa.domain.paint.kawasaki;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.domain.suggestion.KawasakiTheoremSuggester;

/**
 * @author OUCHI Koji
 *
 */
public class SelectingStartPoint extends PickingVertex {
	@Override
	protected void initialize() {
		setNextClass(SelectingEndPoint.class);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		var vertex = new TargetOriVertexFactory().create(context.getCreasePattern(), context.getVertex(0));

		var suggester = new KawasakiTheoremSuggester();
		var suggestedAngles = suggester.suggest(vertex);

		var snapPoints = new KawasakiSnapPointFactory().createSnapPoints(context, vertex.getPositionBeforeFolding(),
				suggestedAngles);

		context.setSnapPoints(snapPoints);
	}
}
