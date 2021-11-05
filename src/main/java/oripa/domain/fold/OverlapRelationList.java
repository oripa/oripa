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

import java.util.ArrayList;
import java.util.List;

import oripa.domain.fold.origeom.OverlapRelation;

public class OverlapRelationList {
	private final List<OverlapRelation> overlapRelations = new ArrayList<>();
	private int currentORmatIndex = 0;

	public void setNextIndex() {
		if (currentORmatIndex < overlapRelations.size() - 1) {
			currentORmatIndex++;
		}
	}

	public void setPrevIndex() {
		if (currentORmatIndex > 0) {
			currentORmatIndex--;
		}

	}

	public OverlapRelation getOverlapRelation() {
		return overlapRelations.get(currentORmatIndex);
	}

	public void add(final OverlapRelation o) {
		overlapRelations.add(o);
	}

	public int getCurrentORmatIndex() {
		return currentORmatIndex;
	}

	public void setCurrentORmatIndex(final int currentORmatIndex) {
		this.currentORmatIndex = currentORmatIndex;
	}

	public int getFoldablePatternCount() {
		return overlapRelations.size();
	}

	public boolean isEmpty() {
		return overlapRelations.isEmpty();
	}
}
