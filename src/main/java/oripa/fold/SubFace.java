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

package oripa.fold;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;

public class SubFace {


	public final OriFace outline;
	public final ArrayList<OriFace> faces;
	public final ArrayList<OriFace> sortedFaces;
	public int tmpInt;
	public final ArrayList<Condition4> condition4s = new ArrayList<>();
	public final ArrayList<Condition3> condition3s = new ArrayList<>();
	public boolean allFaceOrderDecided = false;
	public final ArrayList<ArrayList<OriFace>> answerStacks = new ArrayList<>();

	public SubFace(OriFace f) {
		outline = f;
		faces = new ArrayList<>();
		sortedFaces = new ArrayList<>();
	}

	public int sortFaceOverlapOrder(List<OriFace> modelFaces, int[][] mat) {
		sortedFaces.clear();
		for (int i = 0; i < faces.size(); i++) {
			sortedFaces.add(null);
		}

		// Count the number of pending surfaces
		int cnt = 0;
		int f_num = faces.size();
		for (int i = 0; i < f_num; i++) {
			for (int j = i + 1; j < f_num; j++) {
				if (mat[faces.get(i).tmpInt][faces.get(j).tmpInt] == OrigamiModelFactory.UNDEFINED) {
					cnt++;
				}
			}
		}

		// Exit if the order is already settled
		if (cnt == 0) {
			allFaceOrderDecided = true;
			return 0;
		}

		for (OriFace f : faces) {
			f.condition3s.clear();
			f.condition4s.clear();
			f.condition2s.clear();

			for (Condition3 cond : condition3s) {
				if (f.tmpInt == cond.other) {
					f.condition3s.add(cond);
				}
			}
			for (Condition4 cond : condition4s) {
				if (f.tmpInt == cond.upper1 || f.tmpInt == cond.upper2) {
					f.condition4s.add(cond);
				}
			}

			for (OriFace ff : faces) {
				if (mat[f.tmpInt][ff.tmpInt] == OrigamiModelFactory.LOWER) {
					f.condition2s.add(new Integer(ff.tmpInt));
				}
			}
		}

		for (OriFace f : faces) {
			f.alreadyStacked = false;
			f.tmpInt2 = -1;
		}

		// From the bottom
		sort(modelFaces, 0);

		// Returns the number of solutions obtained
		return answerStacks.size();
	}

	public Vector2d getInnerPoint() {
		Vector2d c = new Vector2d();
		for (OriHalfedge he : outline.halfedges) {
			c.add(he.tmpVec);
		}
		c.scale(1.0 / outline.halfedges.size());
		return c;
	}

	private void sort(List<OriFace> modelFaces, int index) {

		for (OriFace f : faces) {
			if (f.alreadyStacked) {
				continue;
			}

			boolean isOK = true;

			for (Integer ii : f.condition2s) {
				if (!modelFaces.get(ii.intValue()).alreadyStacked) {
					isOK = false;
					break;
				}
			}

			if (!isOK) {
				continue;
			}

			isOK = checkForSortLocally3(modelFaces, f);
			if (!isOK) {
				continue;
			}

			sortedFaces.set(index, f);
			f.alreadyStacked = true;
			f.tmpInt2 = index;

			if (index == faces.size() - 1) {

				ArrayList<OriFace> ans = new ArrayList<>(sortedFaces);
				answerStacks.add(ans);

				// Further continue the search for solutions
				sortedFaces.set(index, null);
				f.alreadyStacked = false;
				f.tmpInt2 = -1;
			} else {
				sort(modelFaces, index + 1);
			}
		}

		if (index == 0) {
			// Examined until the end
			return;
		}

		ORIPA.tmpInt++;
		sortedFaces.get(index - 1).alreadyStacked = false;
		sortedFaces.get(index - 1).tmpInt2 = -1;
		sortedFaces.set(index - 1, null);

	}

	private boolean checkForSortLocally3(List<OriFace> modelFaces, OriFace face) {

		for (Condition3 cond : face.condition3s) {
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

		for (Condition4 cond : face.condition4s) {

			if (face.tmpInt == cond.upper2
					&& modelFaces.get(cond.lower2).alreadyStacked
					&& modelFaces.get(cond.lower1).alreadyStacked
					&& !modelFaces.get(cond.upper1).alreadyStacked
					&& modelFaces.get(cond.lower2).tmpInt2 < modelFaces
							.get(cond.lower1).tmpInt2) {
				return false;
			}
			if (face.tmpInt == cond.upper1
					&& modelFaces.get(cond.lower2).alreadyStacked
					&& modelFaces.get(cond.lower1).alreadyStacked
					&& !modelFaces.get(cond.upper2).alreadyStacked
					&& modelFaces.get(cond.lower1).tmpInt2 < modelFaces
							.get(cond.lower2).tmpInt2) {
				return false;
			}
		}
		return true;
	}
}