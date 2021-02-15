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
 * Possible stack order of 4 faces connecting at e0 or e1 where e0 and e1 are
 * overlapping.
 *
 * @author OUCHI Koji
 *
 */
class Condition4 {
	/**
	 * ID of face of upper side sharing 1st edge
	 */
	public int upper1;
	/**
	 * ID of face of lower side sharing 1st edge
	 */
	public int lower1;
	/**
	 * ID of face of upper side sharing 2nd edge
	 */
	public int upper2;
	/**
	 * ID of face of lower side sharing 2nd edge
	 */
	public int lower2;
}