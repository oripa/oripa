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
package oripa.paint;

import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class CreasePatternFactory {

	public CreasePatternInterface createCreasePattern(double paperSize) {
		CreasePatternInterface creasePattern = new CreasePattern(paperSize);

		OriLine l0 = new OriLine(-paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0, OriLine.TYPE_CUT);
		OriLine l1 = new OriLine(paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0, paperSize / 2.0, OriLine.TYPE_CUT);
		OriLine l2 = new OriLine(paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0, paperSize / 2.0, OriLine.TYPE_CUT);
		OriLine l3 = new OriLine(-paperSize / 2.0, paperSize / 2.0, -paperSize / 2.0, -paperSize / 2.0, OriLine.TYPE_CUT);
		creasePattern.add(l0);
		creasePattern.add(l1);
		creasePattern.add(l2);
		creasePattern.add(l3);

		
		return creasePattern;
	}
}
