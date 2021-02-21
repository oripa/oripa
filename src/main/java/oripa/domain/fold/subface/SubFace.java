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

package oripa.domain.fold.subface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.geom.GeomUtil;

public class SubFace {

	public OriFace outline;
	/**
	 * faces containing this subface.
	 */
	public ArrayList<OriFace> parentFaces;
	/**
	 * working stack
	 */
	private final ArrayList<OriFace> sortedParentFaces;
//	public int tmpInt;
	public ArrayList<StackConditionOf4Faces> condition4s = new ArrayList<>();
	public ArrayList<StackConditionOf3Faces> condition3s = new ArrayList<>();
	public boolean allFaceOrderDecided = false;
	public ArrayList<ArrayList<OriFace>> answerStacks = new ArrayList<>();

	public SubFace(final OriFace f) {
		outline = f;
		parentFaces = new ArrayList<>();
		sortedParentFaces = new ArrayList<>();
	}

	/**
	 *
	 * @param modelFaces
	 *            all faces of inputted model.
	 * @param orMat
	 *            overlap relation matrix.
	 * @return the number of possible stacks.
	 */
	public int sortFaceOverlapOrder(final List<OriFace> modelFaces, final int[][] orMat) {
		sortedParentFaces.clear();
		for (int i = 0; i < parentFaces.size(); i++) {
			sortedParentFaces.add(null);
		}

		// Count the number of pending surfaces
		int cnt = 0;
		int f_num = parentFaces.size();
		for (int i = 0; i < f_num; i++) {
			for (int j = i + 1; j < f_num; j++) {
				if (orMat[parentFaces.get(i).faceID][parentFaces
						.get(j).faceID] == OverlapRelationValues.UNDEFINED) {
					cnt++;
				}
			}
		}

		// Exit if the order is already settled
		if (cnt == 0) {
			allFaceOrderDecided = true;
			return 0;
		}

		for (OriFace f : parentFaces) {
			f.condition3s.clear();
			f.condition4s.clear();
			f.condition2s.clear();

			for (StackConditionOf3Faces cond : condition3s) {
				if (f.faceID == cond.other) {
					f.condition3s.add(cond);
				}
			}
			for (StackConditionOf4Faces cond : condition4s) {
				if (f.faceID == cond.upper1 || f.faceID == cond.upper2) {
					f.condition4s.add(cond);
				}
			}

			for (OriFace ff : parentFaces) {
				if (orMat[f.faceID][ff.faceID] == OverlapRelationValues.LOWER) {
					f.condition2s.add(Integer.valueOf(ff.faceID));
				}
			}
		}

		for (OriFace f : parentFaces) {
			f.alreadyStacked = false;
			f.indexForStack = -1;
		}

		// From the bottom
		sort(modelFaces, 0);

		// Returns the number of solutions obtained
		return answerStacks.size();
	}

	/**
	 *
	 * @return geometric center of this subface
	 */
	public Vector2d getInnerPoint() {
		return GeomUtil.computeCentroid(outline.halfedges.stream()
				.map(he -> he.getPosition())
				.collect(Collectors.toList()));
	}

	private void sort(final List<OriFace> modelFaces, final int index) {

		if (index == parentFaces.size()) {
			ArrayList<OriFace> ans = new ArrayList<>(sortedParentFaces);
			answerStacks.add(ans);
			return;
		}

		for (OriFace f : parentFaces) {
			if (f.alreadyStacked) {
				continue;
			}

			if (!checkConditionOf2Faces(modelFaces, f)) {
				continue;
			}

			if (!checkForSortLocally3(modelFaces, f)) {
				continue;
			}

			sortedParentFaces.set(index, f);
			f.alreadyStacked = true;
			f.indexForStack = index;

			sort(modelFaces, index + 1);

			sortedParentFaces.get(index).alreadyStacked = false;
			sortedParentFaces.get(index).indexForStack = -1;
			sortedParentFaces.set(index, null);
		}
	}

	private boolean checkConditionOf2Faces(final List<OriFace> modelFaces, final OriFace f) {
		for (Integer ii : f.condition2s) {
			if (!modelFaces.get(ii.intValue()).alreadyStacked) {
				return false;
			}
		}
		return true;
	}

	private boolean checkForSortLocally3(final List<OriFace> modelFaces, final OriFace face) {

		for (StackConditionOf3Faces cond : face.condition3s) {
			if (modelFaces.get(cond.lower).alreadyStacked
					&& !modelFaces.get(cond.upper).alreadyStacked) {
				return false;
			}
		}

		// check condition4
		// aabb or abba or baab are good, but aba or bab are impossible

		// stack lower2 < lower1, without upper1 being stacked, dont stack
		// upper2
		// stack lower1 < lower2, without upper2 being stacked, dont stack
		// upper1

		for (StackConditionOf4Faces cond : face.condition4s) {

			if (face.faceID == cond.upper2
					&& modelFaces.get(cond.lower2).alreadyStacked
					&& modelFaces.get(cond.lower1).alreadyStacked
					&& !modelFaces.get(cond.upper1).alreadyStacked
					&& modelFaces.get(cond.lower2).indexForStack < modelFaces
							.get(cond.lower1).indexForStack) {
				return false;
			}
			if (face.faceID == cond.upper1
					&& modelFaces.get(cond.lower2).alreadyStacked
					&& modelFaces.get(cond.lower1).alreadyStacked
					&& !modelFaces.get(cond.upper2).alreadyStacked
					&& modelFaces.get(cond.lower1).indexForStack < modelFaces
							.get(cond.lower2).indexForStack) {
				return false;
			}
		}
		return true;
	}
}