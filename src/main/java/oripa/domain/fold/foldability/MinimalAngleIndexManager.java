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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author OUCHI Koji
 *
 */
class MinimalAngleIndexManager {
	private final List<Boolean> existences = new ArrayList<>();
	private final AngleMinimalityHelper helper = new AngleMinimalityHelper();

	private final LinkedList<Integer> indices = new LinkedList<>();

	public MinimalAngleIndexManager(final RingArrayList<LineGap> ring) {
		for (int i = 0; i < ring.size(); i++) {
			var minimal = helper.isMinimal(ring, i);
			if (minimal) {
				indices.add(i);
			}
			existences.add(minimal);
		}
	}

	public boolean exists(final int ringIndex) {
		return existences.get(ringIndex);
	}

	public void pushIfMinimal(final RingArrayList<LineGap> ring, final int ringIndex) {
		if (helper.isMinimal(ring, ringIndex)) {
			if (exists(ringIndex)) {
				return;
			}

			indices.add(ringIndex);
			existences.set(ringIndex, true);
		}
	}

	public int pop() {
		int index = indices.pop();
		existences.set(index, false);

		return index;
	}

	public boolean isEmpty() {
		return indices.isEmpty();
	}
}
