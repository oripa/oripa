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

package oripa.domain.fold;

/**
 * If face[i] and face[j] touching edge is covered by face[k] then OR[i][k] =
 * OR[j][k]
 */
class StackConditionOf3Faces {
	/**
	 * ID of lower face. "lower" means "smaller index in the face stack of
	 * subface", which is reversed order of overlap relation matrix values.
	 * Therefore, if face_i is UPPER than face_j in overlap relation matrix,
	 * then Condition3.lower = i.
	 */
	public int lower;
	/**
	 * ID of upper face. "upper" means "larger index in the face stack of
	 * subface", which is reversed order of overlap relation matrix values.
	 * Therefore, if face_i is LOWER than face_j in overlap relation matrix,
	 * then Condition3.upper = i.
	 */
	public int upper;
	/**
	 * ID of a face covering both upper and lower.
	 */
	public int other;
}
