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
	 * @param width
	 * @param heigh
	 * @return crease pattern of non-folded case.
	 */
	public CreasePattern createCreasePattern(final double width, final double height) {
		double halfW = width / 2.0;
		double halfH = height / 2.0;

		OriLine l0 = new OriLine(-halfW, -halfH, halfW, -halfH, OriLine.Type.CUT);
		OriLine l1 = new OriLine(halfW, -halfH, halfW, halfH, OriLine.Type.CUT);
		OriLine l2 = new OriLine(halfW, halfH, -halfW, halfH, OriLine.Type.CUT);
		OriLine l3 = new OriLine(-halfW, halfH, -halfW, -halfH, OriLine.Type.CUT);

		var lines = List.of(l0, l1, l2, l3);
		var domain = RectangleDomain.createFromSegments(lines);

		CreasePattern creasePattern = new CreasePatternImpl(domain);

		creasePattern.addAll(lines);

		return creasePattern;
	}

	public CreasePattern createSquareCreasePattern(final double paperSize) {
		return createCreasePattern(paperSize, paperSize);
	}

	/**
	 * creates crease pattern which consists of given lines and no other lines.
	 *
	 * @param lines
	 * @return
	 */
	public CreasePattern createCreasePattern(final Collection<OriLine> lines) {
		return createCreasePattern(lines, 0);
	}

	public CreasePattern createCreasePattern(final Collection<OriLine> lines, final double eps) {
		// To get paper size, consider boundary only
		var domain = RectangleDomain.createFromSegments(
				lines.stream()
						.filter(OriLine::isBoundary)
						.toList());

		if (domain.isVoid()) {
			domain = RectangleDomain.createFromSegments(lines);
		}

		domain.enlarge(eps);

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

	/**
	 * Create a new crease pattern preserving the bounds (width/height) of an
	 * existing crease pattern. Only boundary will be recreated; lines are not
	 * copied by this method.
	 */
	public CreasePattern createCreasePatternFromContourOf(final CreasePattern existing) {
		var domain = existing.getPaperDomain();
		return createCreasePattern(domain.getWidth(), domain.getHeight());
	}

}
