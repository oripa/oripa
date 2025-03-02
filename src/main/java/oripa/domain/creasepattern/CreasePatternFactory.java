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
package oripa.domain.creasepattern;

import java.util.Collection;
import java.util.List;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class CreasePatternFactory {

	/**
	 * creates non-folded crease pattern. Returned collection has only 4 lines
	 * which describe the edges of a square paper. The center of the paper is
	 * set to (0, 0).
	 *
	 * @param paperSize
	 * @return crease pattern of non-folded case.
	 */
	public CreasePattern createCreasePattern(final double paperSize) {

		OriLine l0 = new OriLine(-paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0,
				-paperSize / 2.0, OriLine.Type.CUT);
		OriLine l1 = new OriLine(paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0,
				paperSize / 2.0, OriLine.Type.CUT);
		OriLine l2 = new OriLine(paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0,
				paperSize / 2.0, OriLine.Type.CUT);
		OriLine l3 = new OriLine(-paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0,
				-paperSize / 2.0, OriLine.Type.CUT);

		var lines = List.of(l0, l1, l2, l3);
		var domain = RectangleDomain.createFromSegments(lines);

		CreasePattern creasePattern = new CreasePatternImpl(domain);

		creasePattern.addAll(lines);

		return creasePattern;
	}

	/**
	 * creates crease pattern which consists of given lines and no other lines.
	 *
	 * @param lines
	 * @return
	 */
	public CreasePattern createCreasePattern(final Collection<OriLine> lines) {
		// To get paper size, consider boundary only
		var domain = RectangleDomain.createFromSegments(
				lines.stream()
						.filter(OriLine::isBoundary)
						.toList());

		if (domain.isVoid()) {
			domain = RectangleDomain.createFromSegments(lines);
		}

		// Construct CP
		CreasePattern creasePattern = new CreasePatternImpl(domain);
		creasePattern.addAll(lines);

		return creasePattern;
	}

	public CreasePattern createCreasePattern(final RectangleDomain domain) {
		// Construct CP
		CreasePattern creasePattern = new CreasePatternImpl(domain);

		return creasePattern;
	}

}
