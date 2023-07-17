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

import oripa.domain.fold.foldability.ring.RingArrayList;

/**
 * @author OUCHI Koji
 *
 */
class MinimalAngleFolder {

	/**
	 * fold the minimal angle and remove the related indices.
	 *
	 * @param ring
	 * @param index
	 *            of minimal angle
	 * @param indices
	 * @return index of the angle after fold
	 */
	public int foldPartially(final RingArrayList<LineGap> ring, final int index,
			final MinimalAngleIndexManager indices) {
		var previousElement = ring.getPrevious(index);
		var targetElement = ring.getElement(index);
		var nextElement = ring.getNext(index);

		var previous = previousElement.getValue();
		var target = targetElement.getValue();
		var next = nextElement.getValue();

		previous.setAngleGap(previous.getAngleGap() + next.getAngleGap() - target.getAngleGap());

		indices.remove(previousElement.getRingIndex());
		indices.remove(targetElement.getRingIndex());
		indices.remove(nextElement.getRingIndex());

		ring.dropConnection(nextElement.getRingIndex());
		ring.dropConnection(targetElement.getRingIndex());

		return previousElement.getRingIndex();
	}

}
