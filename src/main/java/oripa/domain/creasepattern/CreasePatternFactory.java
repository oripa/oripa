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
	public CreasePatternInterface createCreasePattern(final double paperSize) {
		CreasePatternInterface creasePattern = new CreasePattern(paperSize);

		OriLine l0 = new OriLine(-paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0,
				-paperSize / 2.0, OriLine.Type.CUT);
		OriLine l1 = new OriLine(paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0,
				paperSize / 2.0, OriLine.Type.CUT);
		OriLine l2 = new OriLine(paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0,
				paperSize / 2.0, OriLine.Type.CUT);
		OriLine l3 = new OriLine(-paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0,
				-paperSize / 2.0, OriLine.Type.CUT);
		creasePattern.add(l0);
		creasePattern.add(l1);
		creasePattern.add(l2);
		creasePattern.add(l3);

		return creasePattern;
	}

	/**
	 *
	 * @param paperSize
	 * @return crease pattern entity with no lines.
	 */
	public CreasePatternInterface createEmptyCreasePattern(final double paperSize) {
		return new CreasePattern(paperSize);
	}

	/**
	 * creates crease pattern which consists of given lines and no other lines.
	 * also moves the lines such that the center of the paper becomes (0, 0).
	 *
	 * @param lines
	 * @return
	 */
	public CreasePatternInterface createCreasePattern(final Collection<OriLine> lines) {
		RectangleDomain domain = new RectangleDomain(lines);
		double paperSize = Math.max(domain.getWidth(), domain.getHeight());

		CreasePatternInterface creasePattern = new CreasePattern(paperSize);

		creasePattern.addAll(lines);

		// FIXME: changing given coordinates is not preferable.
		creasePattern.move(-domain.getCenterX(), -domain.getCenterY());

		return creasePattern;
	}
}
