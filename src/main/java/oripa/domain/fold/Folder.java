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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.rule.Condition3;
import oripa.domain.fold.rule.Condition4;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

public class Folder {

	private final ArrayList<Condition4> condition4s = new ArrayList<>();
	private int workORmat[][];
	private ArrayList<SubFace> subFaces;

	// helper object
	private final FolderTool folderTool = new FolderTool();

	public Folder() {
	}

	/**
	 *
	 * @param origamiModel
	 * @param foldedModelInfo
	 * @param fullEstimation
	 * @return the number of flat foldable layer layouts. -1 if
	 *         <code>fullEstimation</code> is false;
	 */
	public int fold(final OrigamiModel origamiModel, final FoldedModelInfo foldedModelInfo,
			final boolean fullEstimation) {
//		OrigamiModel origamiModel = m_doc.getOrigamiModel();
//		FoldedModelInfo foldedModelInfo = m_doc.getFoldedModelInfo();

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		List<OriFace> faces = origamiModel.getFaces();
		List<OriVertex> vertices = origamiModel.getVertices();
		List<OriEdge> edges = origamiModel.getEdges();

		List<int[][]> foldableOverlapRelations = foldedModelInfo.getFoldableOverlapRelations();
		foldableOverlapRelations.clear();

		simpleFoldWithoutZorder(faces, edges);

		foldedModelInfo.setBoundBox(
				folderTool.calcFoldedBoundingBox(faces));

		sortedFaces.addAll(faces);
		folderTool.setFacesOutline(vertices, faces, false);

		if (!fullEstimation) {
			origamiModel.setFolded(true);
			return -1;
		}

		// After folding construct the sbfaces
		double paperSize = origamiModel.getPaperSize();
		subFaces = makeSubFaces(faces, paperSize);
		System.out.println("subFaces.size() = " + subFaces.size());

		foldedModelInfo.setOverlapRelation(
				createOverlapRelation(faces));

		int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
		// Set overlap relations based on valley/mountain folds information
		step1(faces, overlapRelation);

		holdCondition3s(faces, paperSize, overlapRelation);
		holdCondition4s(edges, overlapRelation);

		estimation(faces, overlapRelation);

		int size = faces.size();
		workORmat = new int[size][size];
		for (int i = 0; i < size; i++) {
			System.arraycopy(overlapRelation[i], 0, workORmat[i], 0, size);
		}

		ORIPA.tmpInt = 0;

		for (SubFace sub : subFaces) {
			sub.sortFaceOverlapOrder(faces, workORmat);
		}

		findAnswer(foldedModelInfo, 0, overlapRelation);

		foldedModelInfo.setCurrentORmatIndex(0);
		if (foldableOverlapRelations.isEmpty()) {
			return 0;
		} else {
			matrixCopy(foldableOverlapRelations.get(0), overlapRelation);
		}

		folderTool.setFacesOutline(vertices, faces, false);

		// Color the faces
		SecureRandom srand = new SecureRandom();
		for (OriFace face : faces) {

			int r = srand.nextInt(256); // (int) (rand.nextDouble() * 255);
			int g = srand.nextInt(256); // (int) (rand.nextDouble() * 255);
			int b = srand.nextInt(256); // (int) (rand.nextDouble() * 255);
//			if (r < 0) {
//				r = 0;
//			} else if (r > 255) {
//				r = 255;
//			}
//			if (g < 0) {
//				g = 0;
//			} else if (g > 255) {
//				g = 255;
//			}
//			if (b < 0) {
//				b = 0;
//			} else if (b > 255) {
//				b = 255;
//			}
			face.intColor = (r << 16) | (g << 8) | b | 0xff000000;
		}

		origamiModel.setFolded(true);
		return foldableOverlapRelations.size();
	}

	private void findAnswer(
			final FoldedModelInfo foldedModelInfo, final int subFaceIndex, final int[][] orMat) {
		// FoldedModelInfo foldedModelInfo = m_doc.getFoldedModelInfo();
		SubFace sub = subFaces.get(subFaceIndex);
		List<int[][]> foldableOverlapRelations = foldedModelInfo.getFoldableOverlapRelations();

		if (sub.allFaceOrderDecided) {
			int s = orMat.length;
			int[][] passMat = new int[s][s];
			for (int i = 0; i < s; i++) {
				System.arraycopy(orMat[i], 0, passMat[i], 0, s);
			}

			if (subFaceIndex == subFaces.size() - 1) {
				s = orMat.length;
				int[][] ansMat = new int[s][s];
				for (int i = 0; i < s; i++) {
					System.arraycopy(passMat[i], 0, ansMat[i], 0, s);
				}
				foldableOverlapRelations.add(ansMat);
			} else {
				findAnswer(foldedModelInfo, subFaceIndex + 1, passMat);
			}

		} else {
			for (ArrayList<OriFace> vec : sub.answerStacks) {
				int size = vec.size();

				boolean bOK = true;
				for (int i = 0; i < size; i++) {
					int index0 = vec.get(i).tmpInt;
					for (int j = i + 1; j < size; j++) {
						int index1 = vec.get(j).tmpInt;
						if (orMat[index0][index1] == FoldedModelInfo.LOWER) {
							bOK = false;
							break;
						}
					}
					if (!bOK) {
						break;
					}
				}
				if (!bOK) {
					continue;
				}
				int s = orMat.length;
				int[][] passMat = new int[s][s];
				for (int i = 0; i < s; i++) {
					System.arraycopy(orMat[i], 0, passMat[i], 0, s);
				}

				for (int i = 0; i < size; i++) {
					int index0 = vec.get(i).tmpInt;
					for (int j = i + 1; j < size; j++) {
						int index1 = vec.get(j).tmpInt;
						passMat[index0][index1] = FoldedModelInfo.UPPER;
						passMat[index1][index0] = FoldedModelInfo.LOWER;
					}
				}

				if (subFaceIndex == subFaces.size() - 1) {
					s = orMat.length;
					int[][] ansMat = new int[s][s];
					for (int i = 0; i < s; i++) {
						System.arraycopy(passMat[i], 0, ansMat[i], 0, s);
					}
					foldableOverlapRelations.add(ansMat);
				} else {
					findAnswer(foldedModelInfo, subFaceIndex + 1, passMat);
				}
			}
		}
	}

	private void estimation(
			final List<OriFace> faces, final int[][] orMat) {
		boolean bChanged;
		do {
			bChanged = false;
			if (estimate_by3faces(faces, orMat)) {
				bChanged = true;
			}
			if (estimate_by3faces2(orMat)) {
				bChanged = true;
			}
			if (estimate_by4faces(orMat)) {
				bChanged = true;
			}
		} while (bChanged);

	}

	// If face[i] and face[j] touching edge is covered by face[k]
	// then OR[i][k] = OR[j][k]
	private void holdCondition3s(
			final List<OriFace> faces, final double paperSize, final int[][] overlapRelation) {
		// OrigamiModel origamiModel = m_doc.getOrigamiModel();
		// FoldedModelInfo foldedModelInfo = m_doc.getFoldedModelInfo();

		;

		for (OriFace f_i : faces) {
			for (OriHalfedge he : f_i.halfedges) {
				if (he.pair == null) {
					continue;
				}

				OriFace f_j = he.pair.face;
				if (overlapRelation[f_i.tmpInt][f_j.tmpInt] != FoldedModelInfo.LOWER) {
					continue;
				}
				for (OriFace f_k : faces) {
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (folderTool.isLineCrossFace4(f_k, he, paperSize)) {
						Condition3 cond = new Condition3();
						cond.upper = f_i.tmpInt;
						cond.lower = f_j.tmpInt;
						cond.other = f_k.tmpInt;

						Condition3 cond3_f = new Condition3();
						cond3_f.lower = cond.lower;
						cond3_f.upper = cond.upper;
						cond3_f.other = cond.other;
						f_k.condition3s.add(cond3_f);

						// Add condition to all subfaces of the 3 faces
						for (SubFace sub : subFaces) {
							if (sub.faces.contains(f_i) && sub.faces.contains(f_j)
									&& sub.faces.contains(f_k)) {
								sub.condition3s.add(cond);
							}
						}

					}
				}
			}
		}
	}

	private void holdCondition4s(
			final List<OriEdge> edges, final int[][] overlapRelation) {
		// OrigamiModel origamiModel = m_doc.getOrigamiModel();
		// FoldedModelInfo foldedModelInfo = m_doc.getFoldedModelInfo();

		int edgeNum = edges.size();
		System.out.println("edgeNum = " + edgeNum);

		for (int i = 0; i < edgeNum; i++) {
			OriEdge e0 = edges.get(i);
			if (e0.left == null || e0.right == null) {
				continue;
			}
			for (int j = i + 1; j < edgeNum; j++) {
				OriEdge e1 = edges.get(j);
				if (e1.left == null || e1.right == null) {
					continue;
				}
				// TODO extract as function
				if (GeomUtil.isLineSegmentsOverlap(e0.left.positionAfterFolded,
						e0.left.next.positionAfterFolded,
						e1.left.positionAfterFolded, e1.left.next.positionAfterFolded)) {
					Condition4 cond_f;
					if (overlapRelation[e0.left.face.tmpInt][e0.right.face.tmpInt] == FoldedModelInfo.UPPER) {
						if (overlapRelation[e1.left.face.tmpInt][e1.right.face.tmpInt] == FoldedModelInfo.UPPER) {
							cond_f = new Condition4();
							cond_f.upper1 = e0.right.face.tmpInt;
							cond_f.lower1 = e0.left.face.tmpInt;
							cond_f.upper2 = e1.right.face.tmpInt;
							cond_f.lower2 = e1.left.face.tmpInt;
							e0.right.face.condition4s.add(cond_f);

							cond_f = new Condition4();
							cond_f.upper2 = e0.right.face.tmpInt;
							cond_f.lower2 = e0.left.face.tmpInt;
							cond_f.upper1 = e1.right.face.tmpInt;
							cond_f.lower1 = e1.left.face.tmpInt;
							e1.right.face.condition4s.add(cond_f);
						} else {
							cond_f = new Condition4();
							cond_f.upper1 = e0.right.face.tmpInt;
							cond_f.lower1 = e0.left.face.tmpInt;
							cond_f.upper2 = e1.left.face.tmpInt;
							cond_f.lower2 = e1.right.face.tmpInt;
							e0.right.face.condition4s.add(cond_f);

							cond_f = new Condition4();
							cond_f.upper2 = e0.right.face.tmpInt;
							cond_f.lower2 = e0.left.face.tmpInt;
							cond_f.upper1 = e1.left.face.tmpInt;
							cond_f.lower1 = e1.right.face.tmpInt;
							e1.left.face.condition4s.add(cond_f);
						}
					} else {
						if (overlapRelation[e1.left.face.tmpInt][e1.right.face.tmpInt] == FoldedModelInfo.UPPER) {
							cond_f = new Condition4();
							cond_f.upper1 = e0.left.face.tmpInt;
							cond_f.lower1 = e0.right.face.tmpInt;
							cond_f.upper2 = e1.right.face.tmpInt;
							cond_f.lower2 = e1.left.face.tmpInt;
							e0.left.face.condition4s.add(cond_f);

							cond_f.upper2 = e0.left.face.tmpInt;
							cond_f.lower2 = e0.right.face.tmpInt;
							cond_f.upper1 = e1.right.face.tmpInt;
							cond_f.lower1 = e1.left.face.tmpInt;
							e1.right.face.condition4s.add(cond_f);
						} else {
							cond_f = new Condition4();
							cond_f.upper1 = e0.left.face.tmpInt;
							cond_f.lower1 = e0.right.face.tmpInt;
							cond_f.upper2 = e1.left.face.tmpInt;
							cond_f.lower2 = e1.right.face.tmpInt;
							e0.left.face.condition4s.add(cond_f);

							cond_f.upper2 = e0.left.face.tmpInt;
							cond_f.lower2 = e0.right.face.tmpInt;
							cond_f.upper1 = e1.left.face.tmpInt;
							cond_f.lower1 = e1.right.face.tmpInt;
							e1.left.face.condition4s.add(cond_f);
						}
					}
					Condition4 cond = new Condition4();
					// Add condition to all subfaces of the 4 faces
					boolean bOverlap = false;
					for (SubFace sub : subFaces) {
						if (sub.faces.contains(e0.left.face) && sub.faces.contains(e0.right.face)
								&& sub.faces.contains(e1.left.face)
								&& sub.faces.contains(e1.right.face)) {
							sub.condition4s.add(cond);
							bOverlap = true;
						}
					}

					if (overlapRelation[e0.left.face.tmpInt][e0.right.face.tmpInt] == FoldedModelInfo.UPPER) {
						cond.upper1 = e0.right.face.tmpInt;
						cond.lower1 = e0.left.face.tmpInt;
					} else {
						cond.upper1 = e0.left.face.tmpInt;
						cond.lower1 = e0.right.face.tmpInt;
					}
					if (overlapRelation[e1.left.face.tmpInt][e1.right.face.tmpInt] == FoldedModelInfo.UPPER) {
						cond.upper2 = e1.right.face.tmpInt;
						cond.lower2 = e1.left.face.tmpInt;
					} else {
						cond.upper2 = e1.left.face.tmpInt;
						cond.lower2 = e1.right.face.tmpInt;
					}

					if (bOverlap) {
						condition4s.add(cond);
					}
				}

			}
		}
	}

	public static void matrixCopy(final int[][] from, final int[][] to) {
		int size = from.length;
		for (int i = 0; i < size; i++) {
			System.arraycopy(from[i], 0, to[i], 0, size);
		}
	}

	private void setOR(final int[][] orMat, final int i, final int j, final int value,
			final boolean bSetPairAtSameTime) {
		orMat[i][j] = value;
		if (bSetPairAtSameTime) {
			if (value == FoldedModelInfo.LOWER) {
				orMat[j][i] = FoldedModelInfo.UPPER;
			} else {
				orMat[j][i] = FoldedModelInfo.LOWER;
			}
		}
	}

	private void setLowerValueIfUndefined(final int[][] orMat, final int i, final int j,
			final boolean[] changed) {
		if (orMat[i][j] == FoldedModelInfo.UNDEFINED) {
			orMat[i][j] = FoldedModelInfo.LOWER;
			orMat[j][i] = FoldedModelInfo.UPPER;
			changed[0] = true;
		}
	}

	private boolean estimate_by4faces(final int[][] orMat) {

		boolean[] changed = new boolean[1];
		changed[0] = false;

		for (Condition4 cond : condition4s) {

			// if: lower1 > upper2, then: upper1 > upper2, upper1 > lower2,
			// lower1 > lower2
			if (orMat[cond.lower1][cond.upper2] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper1, cond.upper2, changed);
				setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2, changed);
				setLowerValueIfUndefined(orMat, cond.lower1, cond.lower2, changed);
			}

			// if: lower2 > upper1, then: upper2 > upper1, upper2 > lower1,
			// lower2 > lower1
			if (orMat[cond.lower2][cond.upper1] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper2, cond.upper1, changed);
				setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1, changed);
				setLowerValueIfUndefined(orMat, cond.lower2, cond.lower1, changed);
			}

			// if: upper1 > upper2 > lower1, then: upper1 > lower2, lower2 >
			// lower1
			if (orMat[cond.upper1][cond.upper2] == FoldedModelInfo.LOWER
					&& orMat[cond.upper2][cond.lower1] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2, changed);
				setLowerValueIfUndefined(orMat, cond.lower2, cond.lower1, changed);
			}

			// if: upper1 > lower2 > lower1, then: upper1 > upper2, upper2 >
			// lower1
			if (orMat[cond.upper1][cond.lower2] == FoldedModelInfo.LOWER
					&& orMat[cond.lower2][cond.lower1] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper1, cond.upper2, changed);
				setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1, changed);
			}

			// if: upper2 > upper1 > lower2, then: upper2 > lower1, lower1 >
			// lower2
			if (orMat[cond.upper2][cond.upper1] == FoldedModelInfo.LOWER
					&& orMat[cond.upper1][cond.lower2] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper2, cond.lower1, changed);
				setLowerValueIfUndefined(orMat, cond.lower1, cond.lower2, changed);
			}

			// if: upper2 > lower1 > lower2, then: upper2 > upper1, upper1 >
			// lower2
			if (orMat[cond.upper2][cond.lower1] == FoldedModelInfo.LOWER
					&& orMat[cond.lower1][cond.lower2] == FoldedModelInfo.LOWER) {
				setLowerValueIfUndefined(orMat, cond.upper2, cond.upper1, changed);
				setLowerValueIfUndefined(orMat, cond.upper1, cond.lower2, changed);
			}
		}

		return changed[0];
	}

	// If the subface a>b and b>c then a>c
	private boolean estimate_by3faces2(final int[][] orMat) {
		boolean bChanged = false;
		for (SubFace sub : subFaces) {

			boolean changed;

			while (true) {
				changed = false;

				boolean bFound = false;
				for (int i = 0; i < sub.faces.size(); i++) {
					for (int j = i + 1; j < sub.faces.size(); j++) {

						// seach for undertermined relations
						int index_i = sub.faces.get(i).tmpInt;
						int index_j = sub.faces.get(j).tmpInt;

						if (orMat[index_i][index_j] == FoldedModelInfo.NO_OVERLAP) {
							continue;
						}
						if (orMat[index_i][index_j] != FoldedModelInfo.UNDEFINED) {
							continue;
						}
						// Find the intermediary face
						for (int k = 0; k < sub.faces.size(); k++) {
							if (k == i) {
								continue;
							}
							if (k == j) {
								continue;
							}

							int index_k = sub.faces.get(k).tmpInt;

							if (orMat[index_i][index_k] == FoldedModelInfo.UPPER
									&& orMat[index_k][index_j] == FoldedModelInfo.UPPER) {
								orMat[index_i][index_j] = FoldedModelInfo.UPPER;
								orMat[index_j][index_i] = FoldedModelInfo.LOWER;
								bFound = true;
								changed = true;
								bChanged = true;
								break;
							}
							if (orMat[index_i][index_k] == FoldedModelInfo.LOWER
									&& orMat[index_k][index_j] == FoldedModelInfo.LOWER) {
								orMat[index_i][index_j] = FoldedModelInfo.LOWER;
								orMat[index_j][index_i] = FoldedModelInfo.UPPER;
								bFound = true;
								changed = true;
								bChanged = true;
								break;
							}
							if (bFound) {
								break;
							}
						}
						if (bFound) {
							break;
						}

					}

					if (bFound) {
						break;
					}
				}

				if (!changed) {
					break;
				}
			}
		}
		return bChanged;
	}

	// If face[i] and face[j] touching edge is covered by face[k]
	// then OR[i][k] = OR[j][k]
	private boolean estimate_by3faces(
			final List<OriFace> faces,
			final int[][] orMat) {

		boolean bChanged = false;
		for (OriFace f_i : faces) {
			for (OriHalfedge he : f_i.halfedges) {
				if (he.pair == null) {
					continue;
				}
				OriFace f_j = he.pair.face;

				for (OriFace f_k : faces) {
					if (f_k == f_i || f_k == f_j) {
						continue;
					}
					if (GeomUtil.isLineCrossFace(f_k, he, 0.0001)) {
						if (orMat[f_i.tmpInt][f_k.tmpInt] != FoldedModelInfo.UNDEFINED
								&& orMat[f_j.tmpInt][f_k.tmpInt] == FoldedModelInfo.UNDEFINED) {
							setOR(orMat, f_j.tmpInt, f_k.tmpInt, orMat[f_i.tmpInt][f_k.tmpInt],
									true);
							bChanged = true;
						} else if (orMat[f_j.tmpInt][f_k.tmpInt] != FoldedModelInfo.UNDEFINED
								&& orMat[f_i.tmpInt][f_k.tmpInt] == FoldedModelInfo.UNDEFINED) {
							setOR(orMat, f_i.tmpInt, f_k.tmpInt, orMat[f_j.tmpInt][f_k.tmpInt],
									true);
							bChanged = true;
						}
					}
				}
			}
		}

		return bChanged;
	}

	private ArrayList<SubFace> makeSubFaces(
			final List<OriFace> faces, final double paperSize) {
		// OrigamiModel origamiModel = m_doc.getOrigamiModel();

		CreasePatternFactory cpFactory = new CreasePatternFactory();
		OrigamiModelFactory modelFactory = new OrigamiModelFactory();

		CreasePatternInterface temp_creasePattern = cpFactory.createCreasePattern(paperSize);
		OrigamiModel temp_origamiModel = modelFactory.createOrigamiModel(paperSize);

		temp_creasePattern.clear();
		Painter painter = new Painter(temp_creasePattern);
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				OriLine line = new OriLine(he.positionAfterFolded, he.next.positionAfterFolded,
						OriLine.Type.RIDGE);
				painter.addLine(line);
			}
		}

		folderTool.cleanDuplicatedLines(temp_creasePattern);

//		if (Config.FOR_STUDY) {
//			try {
//				Exporter exporter = new ExporterEPS();
//				exporter.export(temp_doc, "c:\\_jun\\tmp\\te.eps");
//			} catch (Exception e) {
//			}
//		}
		System.out.println("debugging");
		Vector2d sp1 = new Vector2d(0.0, 0.0);
		Vector2d ep1 = new Vector2d(0.0, 10.0);
		Vector2d sp2 = new Vector2d(0.0, 0.0);
		Vector2d ep2 = new Vector2d(0.0, 5.0);
		Vector2d dummy1 = new Vector2d();
		Vector2d dummy2 = new Vector2d();
		int crossNum = GeomUtil.getCrossPoint(dummy1, dummy2, sp1, ep1, sp2, ep2);
		System.out.println("getCrossPoint results " + crossNum + "::::" + dummy1 + ", " + dummy2);

		temp_origamiModel = modelFactory.buildOrigami(temp_creasePattern, paperSize, false);
		// temp_doc.setOrigamiModel(temp_origamiModel);

		ArrayList<SubFace> localSubFaces = new ArrayList<>();

		List<OriFace> subFaceSources = temp_origamiModel.getFaces();
		for (OriFace face : subFaceSources) {
			localSubFaces.add(new SubFace(face));
		}

		for (SubFace sub : localSubFaces) {
			Vector2d innerPoint = sub.getInnerPoint();
			for (OriFace face : faces) {
				if (GeomUtil.isContainsPointFoldedFace(face, innerPoint, paperSize / 1000)) {
					sub.faces.add(face);
				}
			}
		}
		System.out.println("=---------------------=");

		// Check if the SubFace exactly equal to the Face
		ArrayList<SubFace> tmpFaces = new ArrayList<>();
		for (SubFace sub : localSubFaces) {
			boolean sameCase = false;
			for (SubFace s : tmpFaces) {
				boolean sameCk = true;
				if (sub.faces.size() != s.faces.size()) {
					sameCk = false;
				} else {

					for (OriFace face : sub.faces) {
						if (!s.faces.contains(face)) {
							sameCk = false;
							break;
						}
					}
				}

				if (sameCk) {
					sameCase = true;
					break;
				}
			}

			if (!sameCase) {
				tmpFaces.add(sub);
			} else {
			}
		}

		localSubFaces.clear();
		localSubFaces.addAll(tmpFaces);

		return localSubFaces;
	}

	private void simpleFoldWithoutZorder(
			final List<OriFace> faces, final List<OriEdge> edges) {
		// OrigamiModel origamiModel = m_doc.getOrigamiModel();
//		List<OriFace>   faces    = origamiModel.getFaces();
//        List<OriEdge>   edges    = origamiModel.getEdges();

		int id = 0;
		for (OriFace face : faces) {
			face.faceFront = true;
			face.tmpFlg = false;
			face.z_order = 0;
			face.tmpInt = id;
			id++;

			for (OriHalfedge he : face.halfedges) {
				he.tmpVec.set(he.vertex.p);
			}
		}

		walkFace(faces.get(0));

		for (OriEdge e : edges) {
			e.sv.p.set(e.left.tmpVec);
			if (e.right != null) {
				e.ev.p.set(e.right.tmpVec);
			}
			e.sv.tmpFlg = false;
			e.ev.tmpFlg = false;
		}

		for (OriFace face : faces) {
			face.tmpFlg = false;
			for (OriHalfedge he : face.halfedges) {
				he.positionAfterFolded.set(he.tmpVec);
			}
		}

	}

	// Recursive method that flips the faces, making the folds
	private void walkFace(final OriFace face) {
		face.tmpFlg = true;

		for (OriHalfedge he : face.halfedges) {
			if (he.pair == null) {
				continue;
			}
			if (he.pair.face.tmpFlg) {
				continue;
			}

			flipFace(he.pair.face, he);
			he.pair.face.tmpFlg = true;
			walkFace(he.pair.face);
		}
	}

	private void transformVertex(Vector2d vertex, Line preLine, Vector2d afterOrigin, Vector2d afterDir) {

		double param[] = new double[1];
		double d0 = GeomUtil.Distance(vertex, preLine, param);
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
		Vector2d preOrigin = new Vector2d(baseHe.pair.next.tmpVec);
		Vector2d afterOrigin = new Vector2d(baseHe.tmpVec);

		// Creates the base unit vector for before the rotation
		Vector2d baseDir = new Vector2d();
		baseDir.sub(baseHe.pair.tmpVec, baseHe.pair.next.tmpVec);

		// Creates the base unit vector for after the rotation
		Vector2d afterDir = new Vector2d();
		afterDir.sub(baseHe.next.tmpVec, baseHe.tmpVec);
		afterDir.normalize();

		Line preLine = new Line(preOrigin, baseDir);

		for (OriHalfedge he : face.halfedges) {
			transformVertex(he.tmpVec, preLine, afterOrigin, afterDir);
		}

		for (OriLine precrease : face.precreases) {
			transformVertex(precrease.p0, preLine, afterOrigin, afterDir);
			transformVertex(precrease.p1, preLine, afterOrigin, afterDir);
		}

		// Ivertion
		if (face.faceFront == baseHe.face.faceFront) {
			Vector2d ep = baseHe.next.tmpVec;
			Vector2d sp = baseHe.tmpVec;

			Vector2d b = new Vector2d();
			b.sub(ep, sp);
			for (OriHalfedge he : face.halfedges) {

				if (GeomUtil.Distance(he.tmpVec, new Line(sp, b)) < GeomUtil.EPS) {
					continue;
				}
				if (Math.abs(b.y) < GeomUtil.EPS) {
					Vector2d a = new Vector2d();
					a.sub(he.tmpVec, sp);
					a.y = -a.y;
					he.tmpVec.y = a.y + sp.y;
				} else {
					Vector2d a = new Vector2d();
					a.sub(he.tmpVec, sp);
					he.tmpVec.y = ((b.y * b.y - b.x * b.x) * a.y + 2 * b.x * b.y * a.x)
							/ b.lengthSquared();
					he.tmpVec.x = b.x / b.y * a.y - a.x + b.x / b.y * he.tmpVec.y;
					he.tmpVec.x += sp.x;
					he.tmpVec.y += sp.y;
				}
			}
			for (OriLine precrease : face.precreases) {
				flipVertex(precrease.p0, sp, b);
				flipVertex(precrease.p1, sp, b);
			}
			face.faceFront = !face.faceFront;
		}
	}

	// creates the matrix overlapRelation and fills it with "no overlap" or
	// "undifined"
	private int[][] createOverlapRelation(final List<OriFace> faces) {

		int size = faces.size();
		int[][] overlapRelation = new int[size][size];

		for (int i = 0; i < size; i++) {
			overlapRelation[i][i] = FoldedModelInfo.NO_OVERLAP;
			for (int j = i + 1; j < size; j++) {
				if (GeomUtil.isFaceOverlap(faces.get(i), faces.get(j), size * 0.00001)) {
					overlapRelation[i][j] = FoldedModelInfo.UNDEFINED;
					overlapRelation[j][i] = FoldedModelInfo.UNDEFINED;
				} else {
					overlapRelation[i][j] = FoldedModelInfo.NO_OVERLAP;
					overlapRelation[j][i] = FoldedModelInfo.NO_OVERLAP;
				}
			}
		}

		return overlapRelation;
	}

	// Determines the overlap relations
	private void step1(
			final List<OriFace> faces, final int[][] overlapRelation) {

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				if (he.pair == null) {
					continue;
				}
				OriFace pairFace = he.pair.face;

				// If the relation is already decided, skip
				if (overlapRelation[face.tmpInt][pairFace.tmpInt] == FoldedModelInfo.UPPER
						|| overlapRelation[face.tmpInt][pairFace.tmpInt] == FoldedModelInfo.LOWER) {
					continue;
				}

				if ((face.faceFront && he.edge.type == OriLine.Type.RIDGE.toInt())
						|| (!face.faceFront && he.edge.type == OriLine.Type.VALLEY.toInt())) {
					overlapRelation[face.tmpInt][pairFace.tmpInt] = FoldedModelInfo.UPPER;
					overlapRelation[pairFace.tmpInt][face.tmpInt] = FoldedModelInfo.LOWER;
				} else {
					overlapRelation[face.tmpInt][pairFace.tmpInt] = FoldedModelInfo.LOWER;
					overlapRelation[pairFace.tmpInt][face.tmpInt] = FoldedModelInfo.UPPER;
				}
			}
		}
	}

	public BoundBox foldWithoutLineType(
			final OrigamiModel model) {
		List<OriVertex> vertices = model.getVertices();
		List<OriEdge> edges = model.getEdges();
		List<OriFace> faces = model.getFaces();

		for (OriFace face : faces) {
			face.faceFront = true;
		}

		faces.get(0).z_order = 0;

		walkFace(faces, faces.get(0), 0);

		Collections.sort(faces, new FaceOrderComparator());
		model.getSortedFaces().clear();
		model.getSortedFaces().addAll(faces);

		for (OriEdge e : edges) {
			e.sv.p.set(e.left.tmpVec);
			e.sv.tmpFlg = false;
		}

		folderTool.setFacesOutline(vertices, faces, false);

		return folderTool.calcFoldedBoundingBox(faces);

	}

	// Make the folds by flipping the faces
	private void walkFace(final List<OriFace> faces, final OriFace face, final int walkFaceCount) {
		face.tmpFlg = true;
		if (walkFaceCount > 1000) {
			System.out.println("walkFace too deap");
			return;
		}
		for (OriHalfedge he : face.halfedges) {
			if (he.pair == null) {
				continue;
			}
			if (he.pair.face.tmpFlg) {
				continue;
			}

			flipFace2(faces, he.pair.face, he);
			he.pair.face.tmpFlg = true;
			walkFace(faces, he.pair.face, walkFaceCount + 1);
		}
	}

	// Method that doesnt use sin con
	private void flipFace2(final List<OriFace> faces, final OriFace face,
			final OriHalfedge baseHe) {

		Vector2d preOrigin = new Vector2d(baseHe.pair.next.tmpVec);
		Vector2d afterOrigin = new Vector2d(baseHe.tmpVec);

		// Creates the base unit vector for before the rotation
		Vector2d baseDir = new Vector2d();
		baseDir.sub(baseHe.pair.tmpVec, baseHe.pair.next.tmpVec);

		// Creates the base unit vector for after the rotation
		Vector2d afterDir = new Vector2d();
		afterDir.sub(baseHe.next.tmpVec, baseHe.tmpVec);
		afterDir.normalize();

		Line preLine = new Line(preOrigin, baseDir);

		for (OriHalfedge he : face.halfedges) {
			double param[] = new double[1];
			double d0 = GeomUtil.Distance(he.tmpVec, preLine, param);
			double d1 = param[0];

			Vector2d footV = new Vector2d(afterOrigin);
			footV.x += d1 * afterDir.x;
			footV.y += d1 * afterDir.y;

			Vector2d afterDirFromFoot = new Vector2d();
			afterDirFromFoot.x = afterDir.y;
			afterDirFromFoot.y = -afterDir.x;

			he.tmpVec.x = footV.x + d0 * afterDirFromFoot.x;
			he.tmpVec.y = footV.y + d0 * afterDirFromFoot.y;

		}

		// Ivertion
		if (face.faceFront == baseHe.face.faceFront) {
			Vector2d ep = baseHe.next.tmpVec;
			Vector2d sp = baseHe.tmpVec;

			Vector2d b = new Vector2d();
			b.sub(ep, sp);
			for (OriHalfedge he : face.halfedges) {
				flipVertex(he.tmpVec, sp, b);
			}

			for (OriLine precrease : face.precreases) {
				flipVertex(precrease.p0, sp, b);
				flipVertex(precrease.p1, sp, b);
			}

			face.faceFront = !face.faceFront;
		}

		faces.remove(face);
		faces.add(face);
	}

	private void flipVertex(Vector2d vertex, Vector2d sp, Vector2d b) {
		if (GeomUtil.Distance(vertex, new Line(sp, b)) < GeomUtil.EPS) {
			return;
		}
		if (Math.abs(b.y) < GeomUtil.EPS) {
			Vector2d a = new Vector2d();
			a.sub(vertex, sp);
			a.y = -a.y;
			vertex.y = a.y + sp.y;
		} else {
			Vector2d a = new Vector2d();
			a.sub(vertex, sp);
			vertex.y = ((b.y * b.y - b.x * b.x) * a.y + 2 * b.x * b.y * a.x)
					/ b.lengthSquared();
			vertex.x = b.x / b.y * a.y - a.x + b.x / b.y * vertex.y;
			vertex.x += sp.x;
			vertex.y += sp.y;
		}
	}

}
