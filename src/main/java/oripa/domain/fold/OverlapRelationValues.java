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

/**
 * @author OUCHI Koji
 *
 */
public final class OverlapRelationValues {
	/**
	 * If overlapRelation[i][j] == NO_OVERLAP, then face_i does not have overlap
	 * with face_j.
	 */
	public final static int NO_OVERLAP = 0;
	/**
	 * If overlapRelation[i][j] == UPPER, then face_i is above face_j.
	 */
	public final static int UPPER = 1;
	/**
	 * If overlapRelation[i][j] == LOWER, then face_i is under face_j.
	 */
	public final static int LOWER = 2;
	/**
	 * If overlapRelation[i][j] == UNDEFINED, then face_i is above or under
	 * face_j but not determined yet.
	 */
	public final static int UNDEFINED = 9;

}
