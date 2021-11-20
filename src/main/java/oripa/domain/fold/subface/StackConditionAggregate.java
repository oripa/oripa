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
package oripa.domain.fold.subface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;

/**
 * @author OUCHI Koji
 *
 */
class StackConditionAggregate {
	private final HashMap<OriFace, List<Integer>> stackConditionsOf2Faces = new HashMap<>();
	private final HashMap<OriFace, List<StackConditionOf3Faces>> stackConditionsOf3Faces = new HashMap<>();
	private final HashMap<OriFace, List<StackConditionOf4Faces>> stackConditionsOf4Faces = new HashMap<>();

	void prepareConditionsOf2Faces(
			final List<OriFace> parentFaces,
			final OverlapRelation overlapRelation) {
		for (OriFace f : parentFaces) {
			stackConditionsOf2Faces.put(f, new ArrayList<Integer>());

			var faceID = f.getFaceID();
			for (OriFace ff : parentFaces) {
				var anotherFaceID = ff.getFaceID();
				if (overlapRelation.isLower(faceID, anotherFaceID)) {
					stackConditionsOf2Faces.get(f).add(anotherFaceID);
				}
			}
		}

	}

	void prepareConditionsOf3Faces(
			final List<OriFace> parentFaces,
			final OverlapRelation overlapRelation,
			final List<StackConditionOf3Faces> condition3s) {
		for (OriFace f : parentFaces) {
			stackConditionsOf3Faces.put(f, new ArrayList<StackConditionOf3Faces>());

			var faceID = f.getFaceID();
			for (StackConditionOf3Faces cond : condition3s) {
				if (faceID == cond.other) {
					stackConditionsOf3Faces.get(f).add(cond);
				}
			}
		}

	}

	void prepareConditionsOf4Faces(
			final List<OriFace> parentFaces,
			final OverlapRelation overlapRelation,
			final List<StackConditionOf4Faces> condition4s) {
		for (OriFace f : parentFaces) {
			stackConditionsOf4Faces.put(f, new ArrayList<StackConditionOf4Faces>());

			var faceID = f.getFaceID();
			for (StackConditionOf4Faces cond : condition4s) {
				if (faceID == cond.upper1 || faceID == cond.upper2) {
					stackConditionsOf4Faces.get(f).add(cond);
				}
			}
		}
	}

	boolean satisfiesConditionOf2Faces(
			final boolean[] alreadyInLocalLayerOrder,
			final OriFace f) {
		return stackConditionsOf2Faces.get(f).stream()
				.allMatch(i -> alreadyInLocalLayerOrder[i]);
	}

	boolean satisfiesConditionOf3Faces(
			final boolean[] alreadyInLocalLayerOrder,
			final OriFace face) {
		if (stackConditionsOf3Faces.get(face).stream().anyMatch(cond -> alreadyInLocalLayerOrder[cond.lower]
				&& !alreadyInLocalLayerOrder[cond.upper])) {
			return false;
		}

		return true;
	}

	boolean satisfiesConditionOf4Faces(
			final List<OriFace> modelFaces,
			final boolean[] alreadyInLocalLayerOrder,
			final Map<OriFace, Integer> indexOnOrdering,
			final OriFace face) {
		// check condition4
		// aabb or abba or baab are good, but aba or bab are impossible

		// stack lower2 < lower1, without upper1 being stacked, dont stack
		// upper2
		// stack lower1 < lower2, without upper2 being stacked, dont stack
		// upper1

		if (stackConditionsOf4Faces.get(face).stream().anyMatch(cond -> face.getFaceID() == cond.upper2
				&& alreadyInLocalLayerOrder[cond.lower2]
				&& alreadyInLocalLayerOrder[cond.lower1]
				&& !alreadyInLocalLayerOrder[cond.upper1]
				&& indexOnOrdering.get(modelFaces.get(cond.lower2)) < indexOnOrdering
						.get(modelFaces.get(cond.lower1)))) {
			return false;
		}

		if (stackConditionsOf4Faces.get(face).stream().anyMatch(cond -> face.getFaceID() == cond.upper1
				&& alreadyInLocalLayerOrder[cond.lower2]
				&& alreadyInLocalLayerOrder[cond.lower1]
				&& !alreadyInLocalLayerOrder[cond.upper2]
				&& indexOnOrdering.get(modelFaces.get(cond.lower1)) < indexOnOrdering.get(modelFaces
						.get(cond.lower2)))) {
			return false;
		}

		return true;
	}

	int getCountOfConditionsOf2Faces(final OriFace face) {
		return stackConditionsOf2Faces.get(face).size();
	}
}
