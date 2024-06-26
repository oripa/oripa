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

import oripa.domain.cptool.PointToLinePointToLineAxiom;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.ValidatablePaintCommand;

/**
 * @author OUCHI Koji
 *
 */
public class PointToLinePointToLineAxiomLineSetterCommand extends ValidatablePaintCommand {

	private final PaintContext context;

	public PointToLinePointToLineAxiomLineSetterCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {
		final int vertexCount = 2;
		final int lineCount = 2;

		validateCounts(context, vertexCount, lineCount);

		var p0 = context.getVertex(0);
		var p1 = context.getVertex(1);

		var s0 = context.getLine(0);
		var s1 = context.getLine(1);

		var paperSize = context.getCreasePattern().getPaperSize();
		var range = paperSize * 2;

		var foldLines = new PointToLinePointToLineAxiom().createFoldLines(
				p0, s0, p1, s1, range, context.getPointEps());

		context.setSolutionLines(foldLines);

	}

}
