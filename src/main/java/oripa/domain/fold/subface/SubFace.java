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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//	private boolean localLayerOrderDeterminedByGlobal = false;

	/**
	 *
	 * @param f
	 *            A face object describing the shape of this subface.
	 */
	public SubFace(final OriFace f) {
		outline = f;
	}

	/**
	 * Builds all possible local layer orders. All parent faces should be added
	 * to this subface before this method is called.
	 *
	 * @param modelFaces
	 *            all faces of inputted model.
	 * @param overlapRelation
	 *            overlap relation matrix.
	 * @return a list of possible local layer orders. {@code null} if order is
	 *         uniquely determined by overlap relation.
	 */
	public List<List<OriFace>> createLocalLayerOrders(final List<OriFace> modelFaces,
			final OverlapRelation overlapRelation) {

		// Exit if the order is already settled
		if (isLocalLayerOrderDeterminedByGlobal(overlapRelation)) {
			return null;
		}

		// A list of orders of faces where the faces include this subface. Each
		// order is correct on this subface but it can be wrong on other
		// subfaces.
		var localLayerOrders = new ArrayList<List<OriFace>>();

		var localLayerOrder = new ArrayList<OriFace>();
		var isAlreadyInLayerOrder = new boolean[modelFaces.size()];
		var indexOnOrdering = new HashMap<OriFace, Integer>();
		var stackConditionsOf2Faces = new HashMap<OriFace, List<Integer>>();
		var stackConditionsOf3Faces = new HashMap<OriFace, List<StackConditionOf3Faces>>();
		var stackConditionsOf4Faces = new HashMap<OriFace, List<StackConditionOf4Faces>>();

		for (int i = 0; i < parentFaces.size(); i++) {
			localLayerOrder.add(null);
		}

		for (OriFace f : parentFaces) {
			stackConditionsOf2Faces.put(f, new ArrayList<Integer>());
			stackConditionsOf3Faces.put(f, new ArrayList<StackConditionOf3Faces>());
			stackConditionsOf4Faces.put(f, new ArrayList<StackConditionOf4Faces>());

			var faceID = f.getFaceID();
			for (StackConditionOf3Faces cond : condition3s) {
				if (faceID == cond.other) {
					stackConditionsOf3Faces.get(f).add(cond);
				}
			}
			for (StackConditionOf4Faces cond : condition4s) {
				if (faceID == cond.upper1 || faceID == cond.upper2) {
					stackConditionsOf4Faces.get(f).add(cond);
				}
			}

			for (OriFace ff : parentFaces) {
				var anotherFaceID = ff.getFaceID();
				if (overlapRelation.isLower(faceID, anotherFaceID)) {
					stackConditionsOf2Faces.get(f).add(anotherFaceID);
				}
			}
		}

		for (OriFace f : parentFaces) {
			isAlreadyInLayerOrder[f.getFaceID()] = false;
			indexOnOrdering.put(f, -1);
		}

		// From the bottom
		sort(modelFaces,
				localLayerOrders,
				localLayerOrder,
				isAlreadyInLayerOrder,
				indexOnOrdering,
				stackConditionsOf2Faces,
				stackConditionsOf3Faces,
				stackConditionsOf4Faces,
				0);

		// Returns the number of obtained solutions
		return localLayerOrders;
	}

	private boolean isLocalLayerOrderDeterminedByGlobal(final OverlapRelation overlapRelation) {
		int parentFaceCount = parentFaces.size();
		for (int i = 0; i < parentFaceCount; i++) {
			for (int j = i + 1; j < parentFaceCount; j++) {
				if (overlapRelation.isUndefined(parentFaces.get(i).getFaceID(),
						parentFaces.get(j).getFaceID())) {
					return false;
				}
			}
		}

		return true;
	}

	public int countUndefined(final OverlapRelation overlapRelation) {
		int count = 0;
		int parentFaceCount = parentFaces.size();
		for (int i = 0; i < parentFaceCount; i++) {
			for (int j = i + 1; j < parentFaceCount; j++) {
				if (overlapRelation.isUndefined(parentFaces.get(i).getFaceID(),
						parentFaces.get(j).getFaceID())) {
					count++;
				}
			}
		}

		return count;
	}

	private void sort(final List<OriFace> modelFaces,
			final List<List<OriFace>> localLayerOrders,
			final List<OriFace> localLayerOrder,
			final boolean[] isAlreadyInLocalLayerOrder,
			final Map<OriFace, Integer> indexOnOrdering,
			final Map<OriFace, List<Integer>> stackConditionsOf2Faces,
			final Map<OriFace, List<StackConditionOf3Faces>> stackConditionsOf3Faces,
			final Map<OriFace, List<StackConditionOf4Faces>> stackConditionsOf4Faces,
			final int index) {

		if (index == parentFaces.size()) {
			var ans = new ArrayList<>(localLayerOrder);
			localLayerOrders.add(ans);
			return;
		}

		for (OriFace f : parentFaces) {
			if (isAlreadyInLocalLayerOrder[f.getFaceID()]) {
				continue;
			}

			if (!satisfiesConditionOf2Faces(modelFaces, isAlreadyInLocalLayerOrder, stackConditionsOf2Faces, f)) {
				continue;
			}

			if (!satisfiesConditionOf3Faces(modelFaces, isAlreadyInLocalLayerOrder, stackConditionsOf3Faces, f)) {
				continue;
			}

			if (!satisfiesConditionOf4Faces(modelFaces, isAlreadyInLocalLayerOrder, indexOnOrdering,
					stackConditionsOf4Faces, f)) {
				continue;
			}

			localLayerOrder.set(index, f);
			isAlreadyInLocalLayerOrder[f.getFaceID()] = true;
			indexOnOrdering.put(f, index);

			sort(modelFaces,
					localLayerOrders,
					localLayerOrder,
					isAlreadyInLocalLayerOrder,
					indexOnOrdering,
					stackConditionsOf2Faces,
					stackConditionsOf3Faces,
					stackConditionsOf4Faces,
					index + 1);

			isAlreadyInLocalLayerOrder[localLayerOrder.get(index).getFaceID()] = false;
			indexOnOrdering.put(localLayerOrder.get(index), -1);
			localLayerOrder.set(index, null);
		}
	}

	private boolean satisfiesConditionOf2Faces(final List<OriFace> modelFaces,
			final boolean[] isAlreadyInLocalLayerOrder,
			final Map<OriFace, List<Integer>> stackConditionsOf2Faces,
			final OriFace f) {
		return stackConditionsOf2Faces.get(f).stream()
				.allMatch(i -> isAlreadyInLocalLayerOrder[i]);
	}

	private boolean satisfiesConditionOf3Faces(final List<OriFace> modelFaces,
			final boolean[] isAlreadyInLocalLayerOrder,
			final Map<OriFace, List<StackConditionOf3Faces>> stackConditionsOf3Faces,
			final OriFace face) {
		if (stackConditionsOf3Faces.get(face).stream().anyMatch(cond -> isAlreadyInLocalLayerOrder[cond.lower]
				&& !isAlreadyInLocalLayerOrder[cond.upper])) {
			return false;
		}

		return true;
	}

	private boolean satisfiesConditionOf4Faces(final List<OriFace> modelFaces,
			final boolean[] isAlreadyInLocalLayerOrder,
			final Map<OriFace, Integer> indexOnOrdering,
			final Map<OriFace, List<StackConditionOf4Faces>> stackConditionsOf4Faces,
			final OriFace face) {
		// check condition4
		// aabb or abba or baab are good, but aba or bab are impossible

		// stack lower2 < lower1, without upper1 being stacked, dont stack
		// upper2
		// stack lower1 < lower2, without upper2 being stacked, dont stack
		// upper1

		if (stackConditionsOf4Faces.get(face).stream().anyMatch(cond -> face.getFaceID() == cond.upper2
				&& isAlreadyInLocalLayerOrder[cond.lower2]
				&& isAlreadyInLocalLayerOrder[cond.lower1]
				&& !isAlreadyInLocalLayerOrder[cond.upper1]
				&& indexOnOrdering.get(modelFaces.get(cond.lower2)) < indexOnOrdering
						.get(modelFaces.get(cond.lower1)))) {
			return false;
		}

		if (stackConditionsOf4Faces.get(face).stream().anyMatch(cond -> face.getFaceID() == cond.upper1
				&& isAlreadyInLocalLayerOrder[cond.lower2]
				&& isAlreadyInLocalLayerOrder[cond.lower1]
				&& !isAlreadyInLocalLayerOrder[cond.upper2]
				&& indexOnOrdering.get(modelFaces.get(cond.lower1)) < indexOnOrdering.get(modelFaces
						.get(cond.lower2)))) {
			return false;
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

	OriFace getOutline() {
		return outline;
	}

	public void addStackConditionOf4Faces(final StackConditionOf4Faces condition) {
		condition4s.add(condition);
	}

	public void addStackConditionOf3Faces(final StackConditionOf3Faces condition) {
		condition3s.add(condition);
	}

//	public Iterable<List<OriFace>> localLayerOrdersIterable() {
//		return localLayerOrders;
//	}
//
//	public Stream<List<OriFace>> localLayerOrdersStream() {
//		return localLayerOrders.stream();
//	}

//	public boolean isLocalLayerOrderDeterminedByGlobal() {
//		return localLayerOrderDeterminedByGlobal;
//	}

//	public int getLocalLayerOrderCount() {
//		return localLayerOrders.size();
//	}

	public boolean addParentFaces(final Collection<OriFace> faces) {
		return parentFaces.addAll(faces);
	}

	public Iterable<OriFace> ParentFacesIterable() {
		return parentFaces;
	}

	public OriFace getParentFace(final int index) {
		return parentFaces.get(index);
	}

	public boolean isParentFace(final OriFace face) {
		return parentFaces.contains(face);
	}

	public int getParentFaceCount() {
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