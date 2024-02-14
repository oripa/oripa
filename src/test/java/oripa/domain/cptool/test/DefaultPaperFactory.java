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
package oripa.domain.cptool.test;

import java.util.ArrayList;
import java.util.List;

import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class DefaultPaperFactory {

	/**
	 * Returns boundary of square ((-200, -200), (200, 200))
	 *
	 * @return
	 */
	public List<OriLine> create() {
		var leftTop = new Vector2d(-200, -200);
		var rightTop = new Vector2d(200, -200);
		var leftBottom = new Vector2d(-200, 200);
		var rightBottom = new Vector2d(200, 200);

		// Square paper
		var creasePattern = new ArrayList<>(List.of(
				new OriLine(leftTop, rightTop, Type.CUT),
				new OriLine(leftTop, leftBottom, Type.CUT),
				new OriLine(leftBottom, rightBottom, Type.CUT),
				new OriLine(rightTop, rightBottom, Type.CUT)));

		return creasePattern;
	}
}
