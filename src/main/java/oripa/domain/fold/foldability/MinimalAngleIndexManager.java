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

import java.util.HashSet;
import java.util.Set;

import oripa.domain.fold.foldability.ring.RingArrayList;

/**
 * @author OUCHI Koji
 *
 */
class MinimalAngleIndexManager {
	private final Set<Integer> indices = new HashSet<>();

	public MinimalAngleIndexManager(final RingArrayList<LineGap> ring, final AngleMinimalityHelper helper) {
		for (int i = 0; i < ring.size(); i++) {
			if (helper.isMinimal(ring, i)) {
				indices.add(i);
			}
		}
	}

	public boolean exists(final int ringIndex) {
		return indices.contains(ringIndex);
	}

	public void add(final int ringIndex) {
		indices.add(ringIndex);
	}

	public int pop() {
		var iterator = indices.iterator();
		int index = iterator.next();
		iterator.remove();

		return index;
	}

	public void remove(final int ringIndex) {
		indices.remove(ringIndex);
	}

	public boolean isEmpty() {
		return indices.isEmpty();
	}
}
