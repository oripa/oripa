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
package oripa.domain.fold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.util.rule.AbstractRule;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class MaekawaTheorem extends AbstractRule<OriVertex> {
	private static final Logger logger = LoggerFactory.getLogger(MaekawaTheorem.class);

	@Override
	public boolean holds(final OriVertex vertex) {

		int ridgeCount = 0;
		int valleyCount = 0;

		// counts lines which ends on given vertex
		for (OriEdge e : vertex.edges) {
			if (e.type == OriLine.Type.RIDGE.toInt()) {
				ridgeCount++;
			} else if (e.type == OriLine.Type.VALLEY.toInt()) {
				valleyCount++;
			} else if (e.type == OriLine.Type.CUT.toInt()) {
				return true;
			}
		}

		// maekawa's claim
		if (Math.abs(ridgeCount - valleyCount) != 2) {
			logger.info("edge type count invalid: " + vertex + " "
					+ Math.abs(ridgeCount - valleyCount));
			return false;
		}

		return true;
	}
}
