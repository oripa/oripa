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
package oripa.domain.fold.foldability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.rule.AbstractRule;

/**
 * @author Koji
 *
 */
public class MaekawaTheorem extends AbstractRule<OriVertex> {
	private static final Logger logger = LoggerFactory.getLogger(MaekawaTheorem.class);

	@Override
	public boolean holds(final OriVertex vertex) {
		boolean includesCut = vertex.edgeStream()
				.anyMatch(e -> e.isBoundary());
		if (includesCut) {
			return true;
		}

		// counts lines which ends on given vertex
		long mountainCount = vertex.edgeStream()
				.filter(e -> e.isMountain())
				.count();
		long valleyCount = vertex.edgeStream()
				.filter(e -> e.isValley())
				.count();

		// maekawa's claim
		if (Math.abs(mountainCount - valleyCount) != 2) {
			logger.trace("edge type count invalid: " + vertex + " "
					+ Math.abs(mountainCount - valleyCount));
			return false;
		}

		return true;
	}
}
