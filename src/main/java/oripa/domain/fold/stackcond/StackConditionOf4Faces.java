/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.domain.fold.stackcond;

import java.util.Objects;

import oripa.domain.fold.origeom.OverlapRelationValues;

/**
 * Possible stack order of 4 faces connecting at e0 or e1 where e0 and e1 are
 * overlapping. Consider that upper1 and lower1 are connected at e0 and upper2
 * and lower2 are connected at e1.
 *
 * @author OUCHI Koji
 *
 */
public class StackConditionOf4Faces {
	/**
	 * ID of face of upper side sharing 1st overlapping edge. "upper" means
	 * "larger index in the face stack of subface", which is reversed order of
	 * overlap relation matrix values. Therefore, if face_i is LOWER than face_j
	 * in overlap relation matrix, then Condition4.upper1 = i.
	 */
	public int upper1;
	/**
	 * ID of face of lower side sharing 1st overlapping edge. "lower" means
	 * "smaller index in the face stack of subface", which is reversed order of
	 * overlap relation matrix values. Therefore, if face_i is UPPER than face_j
	 * in overlap relation matrix, then Condition4.lower1 = i.
	 */
	public int lower1;
	/**
	 * ID of face of upper side sharing 2nd overlapping edge. "upper" means
	 * "larger index in the face stack of subface", which is reversed order of
	 * overlap relation matrix values. Therefore, if face_i is LOWER than face_j
	 * in overlap relation matrix, then Condition4.upper2 = i.
	 */
	public int upper2;
	/**
	 * ID of face of lower side sharing 2nd overlapping edge. "lower" means
	 * "smaller index in the face stack of subface", which is reversed order of
	 * overlap relation matrix values. Therefore, if face_i is UPPER than face_j
	 * in overlap relation relation matrix, then Condition4.lower2 = i.
	 */
	public int lower2;

	public boolean isDetermined(final int[][] orMat) {
		return orMat[lower1][upper1] != OverlapRelationValues.UNDEFINED &&
				orMat[lower1][lower2] != OverlapRelationValues.UNDEFINED &&
				orMat[lower1][upper2] != OverlapRelationValues.UNDEFINED &&
				orMat[upper1][lower2] != OverlapRelationValues.UNDEFINED &&
				orMat[upper1][upper2] != OverlapRelationValues.UNDEFINED &&
				orMat[lower2][upper2] != OverlapRelationValues.UNDEFINED;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof StackConditionOf4Faces)) {
			return false;
		}
		var o = (StackConditionOf4Faces) obj;
		return upper1 == o.upper1 &&
				lower1 == o.lower1 &&
				upper2 == o.upper2 &&
				lower2 == o.lower2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(upper1, lower1, upper2, lower2);
	}

}