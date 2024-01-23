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
package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.List;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class SequentialLineFactory {
	/**
	 * Returns result of input line divisions by given points.
	 *
	 * @param points
	 *            on input line sequentially.
	 * @param lineType
	 *            of new lines.
	 *
	 * @return lines created by connecting points in {@code points} one by one.
	 */
	public List<OriLine> createSequentialLines(final List<? extends Vector2d> points,
			final OriLine.Type lineType, final double pointEps) {
		var newLines = new ArrayList<OriLine>();

		Vector2d prePoint = points.get(0);

		// add new lines sequentially
		for (int i = 1; i < points.size(); i++) {
			Vector2d p = points.get(i);
			// remove very short line
			if (GeomUtil.distance(prePoint, p) < pointEps) {
				continue;
			}

			newLines.add(new OriLine(prePoint, p, lineType));

			prePoint = p;
		}

		return newLines;
	}

}
