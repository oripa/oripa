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

import oripa.geom.RectangleDomain;
import oripa.util.Matrices;

public class FoldedModelInfo {
	private int overlapRelation[][] = null;
	private List<int[][]> overlapRelations = new ArrayList<int[][]>();
	private int currentORmatIndex = 0;

	private RectangleDomain rectangleDomain;

	public void setNextORMat() {
		if (currentORmatIndex < overlapRelations.size() - 1) {
			currentORmatIndex++;
			Matrices.copy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}
	}

	public void setPrevORMat() {
		if (currentORmatIndex > 0) {
			currentORmatIndex--;
			Matrices.copy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}

	}

	public int[][] getOverlapRelation() {
		return overlapRelation;
	}

	public void setOverlapRelation(final int[][] overlapRelation) {
		this.overlapRelation = overlapRelation;
	}

	public List<int[][]> getFoldableOverlapRelations() {
		return overlapRelations;
	}

	public void setFoldableOverlapRelations(final List<int[][]> foldableOverlapRelations) {
		this.overlapRelations = foldableOverlapRelations;
	}

	public int getCurrentORmatIndex() {
		return currentORmatIndex;
	}

	public void setCurrentORmatIndex(final int currentORmatIndex) {
		this.currentORmatIndex = currentORmatIndex;
	}

	public RectangleDomain getRectangleDomain() {
		return rectangleDomain;
	}

	public void setRectangleDomain(final RectangleDomain rectangleDomain) {
		this.rectangleDomain = rectangleDomain;
	}

	public int getFoldablePatternCount() {
		return overlapRelations.size();
	}

}
