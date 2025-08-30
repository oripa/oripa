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

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.subface.SubFace;

/**
 * @author OUCHI Koji
 *
 */
public class TransitivityChecker {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 *
	 * @param subface
	 * @param overlapRelation
	 * @return invalid order of indices. null if valid or undetermined
	 */
	public List<Integer> checkSubfaceTransitivity(final SubFace subface, final OverlapRelation overlapRelation) {

		var parentFaceIndices = subface.getParentFaceIndices();

		var undefined = false;
		for (var i : parentFaceIndices) {
			for (var j : parentFaceIndices) {
				if (i == j) {
					continue;
				}
				if (overlapRelation.isUndefined(i, j)) {
					undefined = true;
					break;
				}
			}
			if (undefined) {
				break;
			}
		}
		if (undefined) {
			logger.trace("skip: {}", parentFaceIndices);
			return null;
		}

		var sortedParentFaceIDs = parentFaceIndices.stream()
				.sorted((a, b) -> {
					if (a == b) {
						return 0;
					}
					return overlapRelation.isLower(a, b) ? 1 : -1;
				}).toList();

		var isOk = checkTransitivity(overlapRelation, sortedParentFaceIDs);

		if (!isOk) {
			logger.debug("invalid parent face order {}", sortedParentFaceIDs);
			return sortedParentFaceIDs;
		}

		return null;
	}

	/**
	 * Check the transitivity of faces.
	 *
	 * @param overlapRelation
	 * @param order
	 *            indices of faces top to bottom
	 * @return true if valid
	 */
	private boolean checkTransitivity(final OverlapRelation overlapRelation, final List<Integer> order) {
		var length = order.size();
		for (int a = 0; a < length - 1; a++) {
			int f1 = order.get(a);
			for (int b = a + 1; b < length; b++) {
				int f2 = order.get(b);

				if (overlapRelation.isLower(f1, f2)) {
					logger.trace("wrong: ({},{})", f1, f2);
					return false;
				}
			}
		}

		return true;
	}

}
