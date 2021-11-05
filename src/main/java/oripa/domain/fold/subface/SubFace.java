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
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;

public class SubFace {

	private final OriFace outline;
	/**
	 * faces containing this subface.
	 */
	private final List<OriFace> parentFaces = new ArrayList<>();

	private final List<StackConditionOf4Faces> condition4s = new ArrayList<>();
	private final List<StackConditionOf3Faces> condition3s = new ArrayList<>();
	private boolean localLayerOrderDeterminedByGlobal = false;

	/**
	 * A list of orders of faces where the faces include this subface. Each
	 * order is correct on this subface but it may not so on other subfaces.
	 */
	public List<List<OriFace>> localLayerOrders = new ArrayList<>();

	public SubFace(final OriFace f) {
		outline = f;
	}

	/**
	 *
	 * @param modelFaces
	 *            all faces of inputted model.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return the number of possible local layer orders.
	 */
	public int buildLocalLayerOrders(final List<OriFace> modelFaces, final OverlapRelation overlapRelation) {
		List<OriFace> sortedParentFaces = new ArrayList<>();

		for (int i = 0; i < parentFaces.size(); i++) {
			sortedParentFaces.add(null);
		}

		// Exit if the order is already settled
		if (isLocalLayerOrderDeterminedByGlobal(overlapRelation)) {
			localLayerOrderDeterminedByGlobal = true;
			return 0;
		}

		for (OriFace f : parentFaces) {
			f.clearStackConditionsOf3Faces();
			f.clearStackConditionsOf4Faces();
			f.clearStackConditionsOf2Faces();

			var faceID = f.getFaceID();
			for (StackConditionOf3Faces cond : condition3s) {
				if (faceID == cond.other) {
					f.addStackConditionOf3Faces(cond);
				}
			}
			for (StackConditionOf4Faces cond : condition4s) {
				if (faceID == cond.upper1 || faceID == cond.upper2) {
					f.addStackConditionOf4Faces(cond);
				}
			}

			for (OriFace ff : parentFaces) {
				var anotherFaceID = ff.getFaceID();
				if (overlapRelation.isLower(faceID, anotherFaceID)) {
					f.addStackConditionOf2Faces(Integer.valueOf(anotherFaceID));
				}
			}
		}

		for (OriFace f : parentFaces) {
			f.setAlreadyInLocalLayerOrder(false);
			f.clearIndexForLocalLayerOrder();
		}

		// From the bottom
		sort(modelFaces, sortedParentFaces, 0);

		// Returns the number of obtained solutions
		return localLayerOrders.size();
	}

	private boolean isLocalLayerOrderDeterminedByGlobal(final OverlapRelation overlapRelation) {
		int f_num = parentFaces.size();
		for (int i = 0; i < f_num; i++) {
			for (int j = i + 1; j < f_num; j++) {
				if (overlapRelation.isUndefined(parentFaces.get(i).getFaceID(),
						parentFaces.get(j).getFaceID())) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 *
	 * @return geometric center of this subface
	 */
	public Vector2d getInnerPoint() {
		return outline.getCentroid();
	}

	private void sort(final List<OriFace> modelFaces, final List<OriFace> sortedParentFaces, final int index) {

		if (index == parentFaces.size()) {
			var ans = new ArrayList<>(sortedParentFaces);
			localLayerOrders.add(ans);
			return;
		}

		for (OriFace f : parentFaces) {
			if (f.isAlreadyInLocalLayerOrder()) {
				continue;
			}

			if (!checkConditionOf2Faces(modelFaces, f)) {
				continue;
			}

			if (!checkForSortLocally3(modelFaces, f)) {
				continue;
			}

			sortedParentFaces.set(index, f);
			f.setAlreadyInLocalLayerOrder(true);
			f.setIndexForLocalLayerOrder(index);

			sort(modelFaces, sortedParentFaces, index + 1);

			sortedParentFaces.get(index).setAlreadyInLocalLayerOrder(false);
			sortedParentFaces.get(index).clearIndexForLocalLayerOrder();
			sortedParentFaces.set(index, null);
		}
	}

	private boolean checkConditionOf2Faces(final List<OriFace> modelFaces, final OriFace f) {
		return f.stackConditionsOf2FacesStream()
				.allMatch(ii -> modelFaces.get(ii.intValue()).isAlreadyInLocalLayerOrder());
	}

	private boolean checkForSortLocally3(final List<OriFace> modelFaces, final OriFace face) {
		if (face.stackConditionOf3FacesStream().anyMatch(cond -> modelFaces.get(cond.lower).isAlreadyInLocalLayerOrder()
				&& !modelFaces.get(cond.upper).isAlreadyInLocalLayerOrder())) {
			return false;
		}

		// check condition4
		// aabb or abba or baab are good, but aba or bab are impossible

		// stack lower2 < lower1, without upper1 being stacked, dont stack
		// upper2
		// stack lower1 < lower2, without upper2 being stacked, dont stack
		// upper1

		if (face.stackConditionOf4FacesStream().anyMatch(cond -> face.getFaceID() == cond.upper2
				&& modelFaces.get(cond.lower2).isAlreadyInLocalLayerOrder()
				&& modelFaces.get(cond.lower1).isAlreadyInLocalLayerOrder()
				&& !modelFaces.get(cond.upper1).isAlreadyInLocalLayerOrder()
				&& modelFaces.get(cond.lower2).getIndexForLocalLayerOrder() < modelFaces
						.get(cond.lower1).getIndexForLocalLayerOrder())) {
			return false;
		}

		if (face.stackConditionOf4FacesStream().anyMatch(cond -> face.getFaceID() == cond.upper1
				&& modelFaces.get(cond.lower2).isAlreadyInLocalLayerOrder()
				&& modelFaces.get(cond.lower1).isAlreadyInLocalLayerOrder()
				&& !modelFaces.get(cond.upper2).isAlreadyInLocalLayerOrder()
				&& modelFaces.get(cond.lower1).getIndexForLocalLayerOrder() < modelFaces
						.get(cond.lower2).getIndexForLocalLayerOrder())) {
			return false;
		}

		return true;
	}

	OriFace getOutline() {
		return outline;
	}

	public void addStackConditionOf4Faces(final StackConditionOf4Faces condition) {
		condition4s.add(condition);
	}

	public void addStackConditionOf3Faces(final StackConditionOf3Faces condition) {
		condition3s.add(condition);
	}

	public boolean isLocalLayerOrderDeterminedByGlobal() {
		return localLayerOrderDeterminedByGlobal;
	}

	public int localLayerOrderCount() {
		return localLayerOrders.size();
	}

	public boolean addParentFaces(final Collection<OriFace> faces) {
		return parentFaces.addAll(faces);
	}

	public OriFace getParentFace(final int index) {
		return parentFaces.get(index);
	}

	public boolean isParentFace(final OriFace face) {
		return parentFaces.contains(face);
	}

	public int parentFaceCount() {
		return parentFaces.size();
	}

	public boolean isSame(final SubFace sub) {
		if (parentFaces.size() != sub.parentFaces.size()) {
			return false;
		}

		return parentFaces.stream()
				.allMatch(face -> sub.parentFaces.contains(face));
	}

}