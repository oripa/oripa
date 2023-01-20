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
package oripa.domain.paint.outline;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.cptool.Painter;
import oripa.domain.paint.util.PairLoop;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OutlineAdder {
	private final OverlappingLineExtractor overlappingExtractor;

	/**
	 * Constructor
	 */
	public OutlineAdder(final OverlappingLineExtractor overlappingExtractor) {
		this.overlappingExtractor = overlappingExtractor;
	}

	public void addOutlines(final Painter painter,
			final Collection<Vector2d> outlineVertices) {
		var creasePattern = painter.getCreasePattern();
		var overlappings = new ArrayList<OriLine>();
		var lines = new ArrayList<OriLine>();

		PairLoop.iterateAll(outlineVertices, (element, nextElement) -> {
			var line = new OriLine(element, nextElement, OriLine.Type.CUT);
			lines.add(line);
			overlappings.addAll(overlappingExtractor.extract(creasePattern, line, painter.pointEps()));

			return true;
		});

		painter.removeLines(overlappings);
		painter.addLines(lines);
	}

}
