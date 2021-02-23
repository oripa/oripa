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
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.FaceOrderComparator;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.domain.fold.subface.SubFacesFactory;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.util.Matrices;
import oripa.value.OriLine;

public class Folder {
	private static final Logger logger = LoggerFactory.getLogger(Folder.class);

	private ArrayList<StackConditionOf4Faces> condition4s;
	private List<SubFace> subFaces;

	private final SubFacesFactory subFacesFactory;

	// helper object
	private final FolderTool folderTool = new FolderTool();

	public Folder(final SubFacesFactory subFacesFactory) {
		this.subFacesFactory = subFacesFactory;
	}

	/**
	 * Computes folded states.
	 *
	 * @param origamiModel
	 *            half-edge based data structure. It will be affected by this
	 *            method.
	 * @param foldedModelInfo
	 *            an object to store information of face layer ordering. It will
	 *            be affected by this method.
	 * @param fullEstimation
	 *            whether the algorithm should compute all possible folded
	 *            states or not.
	 * @return the number of flat foldable layer layouts. -1 if
	 *         {@code fullEstimation} is false.
	 */
	public int fold(final OrigamiModel origamiModel, final FoldedModelInfo foldedModelInfo,
			final boolean fullEstimation) {

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		List<OriFace> faces = origamiModel.getFaces();
		List<OriVertex> vertices = origamiModel.getVertices();
		List<OriEdge> edges = origamiModel.getEdges();

		List<int[][]> foldableOverlapRelations = foldedModelInfo.getFoldableOverlapRelations();
		foldableOverlapRelations.clear();

		simpleFoldWithoutZorder(faces, edges);

		sortedFaces.addAll(faces);
		folderTool.setFacesOutline(vertices, faces, false);

		if (!fullEstimation) {
			origamiModel.setFolded(true);
			return -1;
		}

		// After folding construct the subfaces
		double paperSize = origamiModel.getPaperSize();
		subFaces = subFacesFactory.createSubFaces(faces, paperSize);
		logger.debug("subFaces.size() = " + subFaces.size());

		int[][] overlapRelation = createOverlapRelation(faces, paperSize);

		// Set overlap relations based on valley/mountain folds information
		determineOverlapRelationByLineType(faces, overlapRelation);

		holdCondition3s(faces, paperSize, overlapRelation);

		condition4s = new ArrayList<>();
		holdCondition4s(edges, overlapRelation);

		estimation(faces, overlapRelation);

		for (SubFace sub : subFaces) {
			sub.sortFaceOverlapOrder(faces, overlapRelation);
		}

		findAnswer(faces, foldedModelInfo, 0, overlapRelation, true, paperSize);

		foldedModelInfo.setCurrentORmatIndex(0);
		if (foldableOverlapRelations.isEmpty()) {
			return 0;
		}

		folderTool.setFacesOutline(vertices, faces, false);

		origamiModel.setFolded(true);
		return foldableOverlapRelations.size();
	}

	/**
	 * Determines overlap relations which are left uncertain after using
	 * necessary conditions.
	 *
	 * @param faces
	 *            all faces of the origami model.
	 * @param foldedModelInfo
	 *            an object to store the result
	 * @param subFaceIndex
	 *            the index of subface to be updated
	 * @param orMat
	 *            overlap relation matrix
	 * @param orMatModified
	 *            whether {@code orMat} has been changed by the previous call.
	 *            {@code true} for the first call.
	 * @param paperSize
	 *            paper size
	 */
	private void findAnswer(
			final List<OriFace> faces,
			final FoldedModelInfo foldedModelInfo, final int subFaceIndex, final int[][] orMat,
			final boolean orMatModified, final double paperSize) {
		List<int[][]> foldableOverlapRelations = foldedModelInfo.getFoldableOverlapRelations();

		if (orMatModified) {
			if (detectPenetration(faces, orMat, paperSize)) {
				return;
			}
		}

		if (subFaceIndex == subFaces.size()) {
			var ansMat = Matrices.clone(orMat);
			foldableOverlapRelations.add(ansMat);
			return;
		}

		SubFace sub = subFaces.get(subFaceIndex);

		if (sub.allFaceOrderDecided) {
			var passMat = Matrices.clone(orMat);
			findAnswer(faces, foldedModelInfo, subFaceIndex + 1, passMat, false, paperSize);
			return;
		}

		for (ArrayList<OriFace> answerStack : sub.answerStacks) {
			int size = answerStack.size();
			if (!isCorrectStackOrder(answerStack, orMat)) {
				continue;
			}
			var passMat = Matrices.clone(orMat);

			// determine overlap relations according to stack
			for (int i = 0; i < size; i++) {
				int index_i = answerStack.get(i).faceID;
				for (int j = i + 1; j < size; j++) {
					int index_j = answerStack.get(j).faceID;
					passMat[index_i][index_j] = OverlapRelationValues.UPPER;
					passMat[index_j][index_i] = OverlapRelationValues.LOWER;
				}
			}

			findAnswer(faces, foldedModelInfo, subFaceIndex + 1, passMat, true, paperSize);
		}
	}

	/**
	 * Detects penetration. For face_i and its neighbor face_j, face_k
	 * penetrates the sheet of paper if face_k is between face_i and face_j in
	 * the folded state and if the connection edge of face_i and face_j is on
	 * face_k.
	 *
	 * @param faces
	 *            all faces.
	 * @param orMat
	 *            overlap relation matrix.
	 * @param paperSize
	 *            paper size.
	 * @return true if there is a face which penetrates the sheet of paper.
	 */
	private boolean detectPenetration(final List<OriFace> faces, final int[][] orMat,
			final double paperSize) {
		var checked = new boolean[faces.size()][faces.size()];

		for (int i = 0; i < faces.size(); i++) {
			for (var he : faces.get(i).halfedges) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}

				var index_i = he.getFace().faceID;
				var index_j = pair.getFace().faceID;

				if (checked[index_i][index_j]) {
					continue;
				}

				if (orMat[index_i][index_j] != OverlapRelationValues.LOWER &&
						orMat[index_i][index_j] != OverlapRelationValues.UPPER) {
					checked[index_i][index_j] = true;
					checked[index_j][index_i] = true;
					continue;
				}

				var penetrates = IntStream.range(0, faces.size()).parallel()
						.anyMatch(k -> {
							var face_k = faces.get(k);
							var index_k = face_k.faceID;
							if (index_i == index_k || index_j == index_k) {
								return false;
							}
							if (!OriGeomUtil.isLineCrossFace4(face_k, he, paperSize)) {
								return false;
							}
							if (orMat[index_i][index_j] == OverlapRelationValues.LOWER &&
									orMat[index_i][index_k] == OverlapRelationValues.LOWER &&
									orMat[index_j][index_k] == OverlapRelationValues.UPPER) {
								return true;
							} else if (orMat[index_i][index_j] == OverlapRelationValues.UPPER &&
									orMat[index_i][index_k] == OverlapRelationValues.UPPER &&
									orMat[index_j][index_k] == OverlapRelationValues.LOWER) {
								return true;
							}

							return false;
						});
				if (penetrates) {
					return true;
				}

				checked[index_i][index_j] = true;
				checked[index_j][index_i] = true;
			}
		}

		return false;
	}

	/**
	 * Whether the order of faces in {@code answerStack} is correct or not
	 * according to {@code orMat}.
	 *
	 * @param answerStack
	 *            stack of faces including the same subface.
	 * @param orMat
	 *            overlap relation matrix.
	 * @return true if the order is correct.
	 */
	private boolean isCorrectStackOrder(final List<OriFace> answerStack, final int[][] orMat) {
		int size = answerStack.size();

		for (int i = 0; i < size; i++) {
			int index_i = answerStack.get(i).faceID;
			for (int j = i + 1; j < size; j++) {
				int index_j = answerStack.get(j).faceID;
				// stack_index = 0 means the top of stack (looking down
				// the folded model).
				// therefore a face with smaller stack_index i should be
				// UPPER than stack_index j.
				if (orMat[index_i][index_j] == OverlapRelationValues.LOWER) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Determines overlap relations by necessary conditions.
	 *
	 * @param faces
	 *            all faces.
	 * @param orMat
	 *            overlap relation matrix
	 */
	private void estimation(final List<OriFace> faces, final int[][] orMat) {
		boolean changed;
		do {
			changed = false;
			changed |= estimate_by3faces(faces, orMat);
			changed |= estimate_by3faces2(orMat);
			changed |= estimate_by4faces(orMat);
		} while (changed);
	}

	/**
	 * Creates 3-face condition and sets to subfaces: If face[i] and face[j]
	 * touching edge is covered by face[k] then OR[i][k] = OR[j][k]
	 *
	 * @param faces
	 * @param paperSize
	 * @param overlapRelation
	 */
	private void holdCondition3s(
			final List<OriFace> faces, final double paperSize, final int[][] overlapRelation) {

		for (OriFace f_i : faces) {
			for (OriHalfedge he : f_i.halfedges) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}

				OriFace f_j = pair.getFace();
				if (overlapRelation[f_i.faceID][f_j.faceID] != OverlapRelationValues.LOWER) {
					continue;
				}
				for (OriFace f_k : faces) {
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (!OriGeomUtil.isLineCrossFace4(f_k, he, paperSize)) {
						continue;
					}
					StackConditionOf3Faces cond = new StackConditionOf3Faces();
					cond.upper = f_i.faceID;
					cond.lower = f_j.faceID;
					cond.other = f_k.faceID;

					// Add condition to all subfaces of the 3 faces
					for (SubFace sub : subFaces) {
						if (sub.parentFaces.contains(f_i) && sub.parentFaces.contains(f_j)
								&& sub.parentFaces.contains(f_k)) {
							sub.condition3s.add(cond);
						}
					}

				}
			}
		}
	}

	/**
	 * Creates 4-face condition and sets to subfaces.
	 *
	 * @param parentFaces
	 * @param paperSize
	 * @param overlapRelation
	 */
	private void holdCondition4s(
			final List<OriEdge> edges, final int[][] overlapRelation) {

		int edgeNum = edges.size();
		logger.debug("edgeNum = " + edgeNum);

		for (int i = 0; i < edgeNum; i++) {
			OriEdge e0 = edges.get(i);
			var e0Left = e0.getLeft();
			var e0Right = e0.getRight();

			if (e0Left == null || e0Right == null) {
				continue;
			}

			for (int j = i + 1; j < edgeNum; j++) {
				OriEdge e1 = edges.get(j);
				var e1Left = e1.getLeft();
				var e1Right = e1.getRight();
				if (e1Left == null || e1Right == null) {
					continue;
				}

				if (!GeomUtil.isLineSegmentsOverlap(e0Left.positionAfterFolded,
						e0Left.next.positionAfterFolded,
						e1Left.positionAfterFolded, e1Left.next.positionAfterFolded)) {
					continue;
				}

				var e0LeftFace = e0Left.getFace();
				var e0RightFace = e0Right.getFace();
				var e1LeftFace = e1Left.getFace();
				var e1RightFace = e1Right.getFace();

				StackConditionOf4Faces cond = new StackConditionOf4Faces();
				// Add condition to all subfaces of the 4 faces
				boolean bOverlap = false;
				for (SubFace sub : subFaces) {
					if (sub.parentFaces.contains(e0LeftFace)
							&& sub.parentFaces.contains(e0RightFace)
							&& sub.parentFaces.contains(e1LeftFace)
							&& sub.parentFaces.contains(e1RightFace)) {
						sub.condition4s.add(cond);
						bOverlap = true;
					}
				}

				var e0LeftFaceID = e0LeftFace.faceID;
				var e0RightFaceID = e0RightFace.faceID;
				var e1LeftFaceID = e1LeftFace.faceID;
				var e1RightFaceID = e1RightFace.faceID;

				if (overlapRelation[e0LeftFaceID][e0RightFaceID] == OverlapRelationValues.UPPER) {
					cond.upper1 = e0RightFaceID;
					cond.lower1 = e0LeftFaceID;
				} else {
					cond.upper1 = e0LeftFaceID;
					cond.lower1 = e0RightFaceID;
				}
				if (overlapRelation[e1LeftFaceID][e1RightFaceID] == OverlapRelationValues.UPPER) {
					cond.upper2 = e1RightFaceID;
					cond.lower2 = e1LeftFaceID;
				} else {
					cond.upper2 = e1LeftFaceID;
					cond.lower2 = e1RightFaceID;
				}

				if (bOverlap) {
					condition4s.add(cond);
				}
			}
		}
	}

	/**
	 * Sets {@code value} to {@code orMat[i][j]}. If {@code setsPairAtSameTime}
	 * is {@code true}, This method sets inversion of {@code value} to
	 * {@code orMat[j][i]}.
	 *
	 * @param orMat
	 *            overlap relation matrix
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @param value
	 *            a value of {@link OverlapRelationValues}
	 * @param setsPairAtSameTime
	 *            {@code true} if {@code orMat[j][i]} should be set to inversion
	 *            of {@code value} as well.
	 */
	private void setOR(final int[][] orMat, final int i, final int j, final int value,
			final boolean setsPairAtSameTime) {
		orMat[i][j] = value;
		if (!setsPairAtSameTime) {
			return;
		}

		if (value == OverlapRelationValues.LOWER) {
			orMat[j][i] = OverlapRelationValues.UPPER;
		} else {
			orMat[j][i] = OverlapRelationValues.LOWER;
		}
	}

	/**
	 *
	 * @param orMat
	 * @param i
	 * @param j
	 * @return true if LOWER and UPPER is set.
	 */
	private boolean setLowerValueIfUndefined(final int[][] orMat, final int i, final int j) {
		if (orMat[i][j] != OverlapRelationValues.UNDEFINED) {
			return false;
		}
		orMat[i][j] = OverlapRelationValues.LOWER;
		orMat[j][i] = OverlapRelationValues.UPPER;
		return true;
	}

	/**
	 * Determines overlap relation using 4-face condition.
	 *
	 * @param orMat
	 * @return
	 */
	private boolean estimate_by4faces(final int[][] orMat) {

		boolean changed = false;

		for (StackConditionOf4Faces cond : condition4s) {

			// if: lower1 > upper2, then: upper1 > upper2, upper1 > lower2,
			// lower1 > lower2
			if (orMat[cond.lower1][cond.upper2] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper1, cond.upper2);
				changed |= setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2);
				changed |= setLowerValueIfUndefined(orMat, cond.lower1, cond.lower2);
			}

			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			if (orMat[cond.lower2][cond.upper1] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper2, cond.upper1);
				changed |= setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1);
				changed |= setLowerValueIfUndefined(orMat, cond.lower2, cond.lower1);
			}

			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			if (orMat[cond.upper1][cond.upper2] == OverlapRelationValues.LOWER
					&& orMat[cond.upper2][cond.lower1] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2);
				changed |= setLowerValueIfUndefined(orMat, cond.lower2, cond.lower1);
			}

			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			if (orMat[cond.upper1][cond.lower2] == OverlapRelationValues.LOWER
					&& orMat[cond.lower2][cond.lower1] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper1, cond.upper2);
				changed |= setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1);
			}

			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			if (orMat[cond.upper2][cond.upper1] == OverlapRelationValues.LOWER
					&& orMat[cond.upper1][cond.lower2] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1);
				changed |= setLowerValueIfUndefined(orMat, cond.lower1, cond.lower2);
			}

			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			if (orMat[cond.upper2][cond.lower1] == OverlapRelationValues.LOWER
					&& orMat[cond.lower1][cond.lower2] == OverlapRelationValues.LOWER) {
				changed |= setLowerValueIfUndefined(orMat, cond.upper2, cond.upper1);
				changed |= setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2);
			}
		}

		return changed;
	}

	/**
	 * If the subface a>b and b>c then a>c
	 *
	 * @param orMat
	 *            overlap-relation matrix
	 * @return whether orMat is changed or not.
	 */
	private boolean estimate_by3faces2(final int[][] orMat) {
		boolean bChanged = false;

		for (SubFace sub : subFaces) {
			while (updateOverlapRelationBy3FaceStack(sub, orMat)) {
				bChanged = true;
			}
		}
		return bChanged;
	}

	/**
	 * Updates {@code orMat} by 3-face stack condition.
	 *
	 * @param sub
	 *            subface.
	 * @param orMat
	 *            overlap relation matrix.
	 * @return true if an update happens.
	 */
	private boolean updateOverlapRelationBy3FaceStack(final SubFace sub, final int[][] orMat) {

		for (int i = 0; i < sub.parentFaces.size(); i++) {
			for (int j = i + 1; j < sub.parentFaces.size(); j++) {

				// search for undetermined relations
				int index_i = sub.parentFaces.get(i).faceID;
				int index_j = sub.parentFaces.get(j).faceID;

				if (orMat[index_i][index_j] == OverlapRelationValues.NO_OVERLAP) {
					continue;
				}
				if (orMat[index_i][index_j] != OverlapRelationValues.UNDEFINED) {
					continue;
				}
				// Find the intermediary face
				for (int k = 0; k < sub.parentFaces.size(); k++) {
					if (k == i || k == j) {
						continue;
					}

					int index_k = sub.parentFaces.get(k).faceID;

					if (orMat[index_i][index_k] == OverlapRelationValues.UPPER
							&& orMat[index_k][index_j] == OverlapRelationValues.UPPER) {
						setOR(orMat, index_i, index_j, OverlapRelationValues.UPPER, true);
						return true;
					}
					if (orMat[index_i][index_k] == OverlapRelationValues.LOWER
							&& orMat[index_k][index_j] == OverlapRelationValues.LOWER) {
						setOR(orMat, index_i, index_j, OverlapRelationValues.LOWER, true);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * If face[i] and face[j] touching edge is covered by face[k] then OR[i][k]
	 * = OR[j][k]
	 *
	 * @param faces
	 * @param orMat
	 * @return whether orMat is changed or not.
	 */
	private boolean estimate_by3faces(
			final List<OriFace> faces,
			final int[][] orMat) {

		boolean bChanged = false;
		for (OriFace f_i : faces) {
			int index_i = f_i.faceID;
			for (OriHalfedge he : f_i.halfedges) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				OriFace f_j = pair.getFace();
				int index_j = f_j.faceID;

				for (OriFace f_k : faces) {
					int index_k = f_k.faceID;
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (!OriGeomUtil.isLineCrossFace(f_k, he, 0.0001)) {
						continue;
					}
					if (orMat[index_i][index_k] != OverlapRelationValues.UNDEFINED
							&& orMat[index_j][index_k] == OverlapRelationValues.UNDEFINED) {
						setOR(orMat, index_j, index_k, orMat[index_i][index_k], true);
						bChanged = true;
					} else if (orMat[index_j][index_k] != OverlapRelationValues.UNDEFINED
							&& orMat[index_i][index_k] == OverlapRelationValues.UNDEFINED) {
						setOR(orMat, index_i, index_k, orMat[index_j][index_k], true);
						bChanged = true;
					}
				}
			}
		}

		return bChanged;
	}

	private void simpleFoldWithoutZorder(
			final List<OriFace> faces, final List<OriEdge> edges) {

		int id = 0;
		for (OriFace face : faces) {
			face.faceFront = true;
			face.movedByFold = false;
			face.z_order = 0;
			face.faceID = id;
			id++;

			for (OriHalfedge he : face.halfedges) {
				he.tmpVec.set(he.getPosition());
			}
		}

		walkFace(faces.get(0));

		for (OriEdge e : edges) {
			var sv = e.getStartVertex();
			var ev = e.getEndVertex();

			sv.p.set(e.getLeft().tmpVec);

			var right = e.getRight();
			if (right != null) {
				ev.p.set(right.tmpVec);
			}
			sv.tmpFlg = false;
			ev.tmpFlg = false;
		}

		for (OriFace face : faces) {
			face.movedByFold = false;
			for (OriHalfedge he : face.halfedges) {
				he.positionAfterFolded.set(he.tmpVec);
			}
		}

	}

	// Recursive method that flips the faces, making the folds
	private void walkFace(final OriFace face) {
		face.movedByFold = true;

		for (OriHalfedge he : face.halfedges) {
			var pair = he.getPair();
			if (pair == null) {
				continue;
			}
			var pairFace = pair.getFace();
			if (pairFace.movedByFold) {
				continue;
			}

			flipFace(pairFace, he);
			pairFace.movedByFold = true;
			walkFace(pairFace);
		}
	}

	private void transformVertex(final Vector2d vertex, final Line preLine,
			final Vector2d afterOrigin, final Vector2d afterDir) {
		double param[] = new double[1];
		double d0 = GeomUtil.distance(vertex, preLine, param);
		double d1 = param[0];

		Vector2d footV = new Vector2d(afterOrigin);
		footV.x += d1 * afterDir.x;
		footV.y += d1 * afterDir.y;

		Vector2d afterDirFromFoot = new Vector2d();
		afterDirFromFoot.x = afterDir.y;
		afterDirFromFoot.y = -afterDir.x;

		vertex.x = footV.x + d0 * afterDirFromFoot.x;
		vertex.y = footV.y + d0 * afterDirFromFoot.y;
	}

	private void flipFace(final OriFace face, final OriHalfedge baseHe) {
		var basePair = baseHe.getPair();
		// (Maybe) baseHe.pair keeps the position before folding.
		Vector2d preOrigin = new Vector2d(basePair.next.tmpVec);
		// baseHe.tmpVec is the temporary position while folding along creases.
		Vector2d afterOrigin = new Vector2d(baseHe.tmpVec);

		// Creates the base unit vector for before the rotation
		Vector2d baseDir = new Vector2d();
		baseDir.sub(basePair.tmpVec, basePair.next.tmpVec);

		// Creates the base unit vector for after the rotation
		Vector2d afterDir = new Vector2d();
		afterDir.sub(baseHe.next.tmpVec, baseHe.tmpVec);
		afterDir.normalize();

		Line preLine = new Line(preOrigin, baseDir);

		// move the vertices of the face to keep the face connection
		// on baseHe
		for (OriHalfedge he : face.halfedges) {
			transformVertex(he.tmpVec, preLine, afterOrigin, afterDir);
		}

		for (OriLine precrease : face.precreases) {
			transformVertex(precrease.p0, preLine, afterOrigin, afterDir);
			transformVertex(precrease.p1, preLine, afterOrigin, afterDir);
		}

		// Inversion
		if (face.faceFront == baseHe.getFace().faceFront) {
			Vector2d ep = baseHe.next.tmpVec;
			Vector2d sp = baseHe.tmpVec;

			for (OriHalfedge he : face.halfedges) {
				flipVertex(he.tmpVec, sp, ep);
			}
			for (OriLine precrease : face.precreases) {
				flipVertex(precrease.p0, sp, ep);
				flipVertex(precrease.p1, sp, ep);
			}
			face.faceFront = !face.faceFront;
		}
	}

	/**
	 * creates the matrix overlapRelation and fills it with "no overlap" or
	 * "undefined"
	 *
	 * @param faces
	 * @param paperSize
	 * @return
	 */
	private int[][] createOverlapRelation(final List<OriFace> faces, final double paperSize) {

		int size = faces.size();
		int[][] overlapRelation = new int[size][size];

		for (int i = 0; i < size; i++) {
			overlapRelation[i][i] = OverlapRelationValues.NO_OVERLAP;
			for (int j = i + 1; j < size; j++) {
				if (OriGeomUtil.isFaceOverlap(faces.get(i), faces.get(j), paperSize * 0.00001)) {
					overlapRelation[i][j] = OverlapRelationValues.UNDEFINED;
					overlapRelation[j][i] = OverlapRelationValues.UNDEFINED;
				} else {
					overlapRelation[i][j] = OverlapRelationValues.NO_OVERLAP;
					overlapRelation[j][i] = OverlapRelationValues.NO_OVERLAP;
				}
			}
		}

		return overlapRelation;
	}

	/**
	 * Determines the overlap relations by mountain/valley.
	 *
	 * @param faces
	 * @param overlapRelation
	 */
	private void determineOverlapRelationByLineType(
			final List<OriFace> faces, final int[][] overlapRelation) {

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				OriFace pairFace = pair.getFace();

				// If the relation is already decided, skip
				if (overlapRelation[face.faceID][pairFace.faceID] == OverlapRelationValues.UPPER
						|| overlapRelation[face.faceID][pairFace.faceID] == OverlapRelationValues.LOWER) {
					continue;
				}

				if ((face.faceFront && he.getType() == OriLine.Type.MOUNTAIN.toInt())
						|| (!face.faceFront && he.getType() == OriLine.Type.VALLEY.toInt())) {
					overlapRelation[face.faceID][pairFace.faceID] = OverlapRelationValues.UPPER;
					overlapRelation[pairFace.faceID][face.faceID] = OverlapRelationValues.LOWER;
				} else {
					overlapRelation[face.faceID][pairFace.faceID] = OverlapRelationValues.LOWER;
					overlapRelation[pairFace.faceID][face.faceID] = OverlapRelationValues.UPPER;
				}
			}
		}
	}

	/**
	 * Computes position of each face after fold.
	 *
	 * @param model
	 *            half-edge based data structure. It will be affected by this
	 *            method.
	 */
	public void foldWithoutLineType(
			final OrigamiModel model) {
		List<OriVertex> vertices = model.getVertices();
		List<OriEdge> edges = model.getEdges();
		List<OriFace> faces = model.getFaces();

		for (OriFace face : faces) {
			face.faceFront = true;
			face.movedByFold = false;
		}

		faces.get(0).z_order = 0;

		walkFace(faces, faces.get(0), 0);

		Collections.sort(faces, new FaceOrderComparator());
		model.getSortedFaces().clear();
		model.getSortedFaces().addAll(faces);

		for (OriEdge e : edges) {
			var sv = e.getStartVertex();
			sv.p.set(e.getLeft().tmpVec);
			sv.tmpFlg = false;
		}

		folderTool.setFacesOutline(vertices, faces, false);
	}

	/**
	 * Make the folds by flipping the faces
	 *
	 * @param faces
	 * @param face
	 * @param walkFaceCount
	 */
	private void walkFace(final List<OriFace> faces, final OriFace face, final int walkFaceCount) {
		face.movedByFold = true;
		if (walkFaceCount > 1000) {
			logger.error("walkFace too deep");
			return;
		}
		for (OriHalfedge he : face.halfedges) {
			var pair = he.getPair();
			if (pair == null) {
				continue;
			}
			var pairFace = pair.getFace();
			if (pairFace.movedByFold) {
				continue;
			}

			flipFace2(faces, pairFace, he);
			pairFace.movedByFold = true;
			walkFace(faces, pairFace, walkFaceCount + 1);
		}
	}

	private void flipFace2(final List<OriFace> faces, final OriFace face,
			final OriHalfedge baseHe) {
		flipFace(face, baseHe);
		faces.remove(face);
		faces.add(face);
	}

	private void flipVertex(final Vector2d vertex, final Vector2d sp, final Vector2d ep) {
		var v = GeomUtil.getSymmetricPoint(vertex, sp, ep);

		vertex.x = v.x;
		vertex.y = v.y;
	}
}
