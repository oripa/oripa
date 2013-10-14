package oripa.fold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.CalculationResource;
import oripa.doc.core.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.OriEdge;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriVertex;
import oripa.value.OriLine;

public class FolderTool {
	final public static int NO_OVERLAP = 0;
	final public static int UPPER = 1;
	final public static int LOWER = 2;
	final public static int UNDEFINED = 9;


	int debugCount = 0;

	// should not be in this class
	//public boolean hasModel = false;




	private OriVertex addAndGetVertexFromVVec(
			List<OriVertex> vertices, Vector2d p) {
		OriVertex vtx = null;
		for (OriVertex v : vertices) {
			if (GeomUtil.Distance(v.p, p) < CalculationResource.POINT_EPS) {
				vtx = v;
			}
		}

		if (vtx == null) {
			vtx = new OriVertex(p);
			vertices.add(vtx);
		}

		return vtx;
	}


	// Turn the model over
	public void filpAll(OrigamiModel origamiModel) {
		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);

		List<OriFace> faces = origamiModel.getFaces();
		for (OriFace face : faces) {
			face.z_order = -face.z_order;
			for (OriHalfedge he : face.halfedges) {
				maxV.x = Math.max(maxV.x, he.vertex.p.x);
				maxV.y = Math.max(maxV.y, he.vertex.p.y);
				minV.x = Math.min(minV.x, he.vertex.p.x);
				minV.y = Math.min(minV.y, he.vertex.p.y);
			}
		}

		double centerX = (maxV.x + minV.x) / 2;

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.positionForDisplay.x = 2 * centerX - he.positionForDisplay.x;
			}
		}

		for (OriFace face : faces) {
			face.faceFront = !face.faceFront;
			face.setOutline();
		}

		Collections.sort(faces, new FaceOrderComparator());

		Collections.reverse(origamiModel.getSortedFaces());


	}
	// 
	public void setFacesOutline(
			List<OriVertex> vertices, List<OriFace> faces, 
			boolean isSlide) {
		int minDepth = Integer.MAX_VALUE;
		int maxDepth = -Integer.MAX_VALUE;

		for (OriFace f : faces) {
			minDepth = Math.min(minDepth, f.z_order);
			maxDepth = Math.max(minDepth, f.z_order);
			for (OriHalfedge he : f.halfedges) {
				he.positionForDisplay.set(he.vertex.p);
			}
			f.setOutline();
		}


		if (isSlide) {
			double slideUnit = 10.0 / (maxDepth - minDepth);
			for (OriVertex v : vertices) {
				v.tmpFlg = false;
				v.tmpVec.set(v.p);
			}

			for (OriFace f : faces) {
				Vector2d faceCenter = new Vector2d();
				for (OriHalfedge he : f.halfedges) {
					faceCenter.add(he.vertex.p);
				}
				faceCenter.scale(1.0 / f.halfedges.size());

				for (OriHalfedge he : f.halfedges) {
					if (he.vertex.tmpFlg) {
						continue;
					}
					he.vertex.tmpFlg = true;

					he.vertex.tmpVec.x += slideUnit * f.z_order;
					he.vertex.tmpVec.y += slideUnit * f.z_order;

					Vector2d dirToCenter = new Vector2d(faceCenter);
					dirToCenter.sub(he.vertex.tmpVec);
					dirToCenter.normalize();
					dirToCenter.scale(6.0);
					he.vertex.tmpVec.add(dirToCenter);
				}
			}

			for (OriFace f : faces) {
				for (OriHalfedge he : f.halfedges) {
					he.positionForDisplay.set(he.vertex.tmpVec);
				}
				f.setOutline();
			}
		}
	}


	public boolean buildOrigami(
			CreasePattern creasePattern, OrigamiModel origamiModel, boolean needCleanUp) {

		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		edges.clear();
		vertices.clear();
		faces.clear();


		for (OriLine l : creasePattern) {
			if (l.typeVal == OriLine.TYPE_NONE) {
				continue;
			}

			OriVertex sv = addAndGetVertexFromVVec(vertices, l.p0);
			OriVertex ev = addAndGetVertexFromVVec(vertices, l.p1);
			OriEdge eg = new OriEdge(sv, ev, l.typeVal);
			edges.add(eg);
			sv.addEdge(eg);
			ev.addEdge(eg);
		}

		for (OriVertex v : vertices) {

			for (OriEdge e : v.edges) {

				if (e.type == OriLine.TYPE_CUT) {
					continue;
				}

				if (v == e.sv) {
					if (e.left != null) {
						continue;
					}
				} else {
					if (e.right != null) {
						continue;
					}
				}

				OriFace face = new OriFace();
				faces.add(face);
				OriVertex walkV = v;
				OriEdge walkE = e;
				debugCount = 0;
				while (true) {
					if (debugCount++ > 200) {
						System.out.println("ERROR");
						return false;
					}
					OriHalfedge he = new OriHalfedge(walkV, face);
					face.halfedges.add(he);
					he.tmpInt = walkE.type;
					if (walkE.sv == walkV) {
						walkE.left = he;
					} else {
						walkE.right = he;
					}
					walkV = walkE.oppositeVertex(walkV);
					walkE = walkV.getPrevEdge(walkE);

					if (walkV == v) {
						break;
					}
				}
				face.makeHalfedgeLoop();
				face.setOutline();
				face.setPreOutline();
			}
		}

		makeEdges(edges, faces);
		for (OriEdge e : edges) {
			e.type = e.left.tmpInt;
		}


		origamiModel.setHasModel(true);

		return true;

	}

	//	/**
	//	 * 
	//	 * @param creasePattern
	//	 * @param needCleanUp
	//	 * @return Folded model if success, otherwise null.
	//	 */
	//	public OrigamiModel buildOrigami(Collection<OriLine> creasePattern, double paperSize, boolean needCleanUp) {
	//
	//		OrigamiModel     model = new OrigamiModel(paperSize);
	//		
	//		List<OriEdge>   edges    = model.getEdges();
	//		List<OriVertex> vertices = model.getVertices();
	//		List<OriFace>   faces    = model.getFaces();
	//
	//
	//		for (OriLine l : creasePattern) {
	//			if (l.typeVal == OriLine.TYPE_NONE) {
	//				continue;
	//			}
	//
	//			OriVertex sv = addAndGetVertexFromVVec(vertices, l.p0);
	//			OriVertex ev = addAndGetVertexFromVVec(vertices, l.p1);
	//			OriEdge eg = new OriEdge(sv, ev, l.typeVal);
	//			edges.add(eg);
	//			sv.addEdge(eg);
	//			ev.addEdge(eg);
	//		}
	//
	//		for (OriVertex v : vertices) {
	//
	//			for (OriEdge e : v.edges) {
	//
	//				if (e.type == OriLine.TYPE_CUT) {
	//					continue;
	//				}
	//
	//				if (v == e.sv) {
	//					if (e.left != null) {
	//						continue;
	//					}
	//				} else {
	//					if (e.right != null) {
	//						continue;
	//					}
	//				}
	//
	//				OriFace face = new OriFace();
	//				faces.add(face);
	//				OriVertex walkV = v;
	//				OriEdge walkE = e;
	//				debugCount = 0;
	//				while (true) {
	//					if (debugCount++ > 200) {
	//						System.out.println("ERROR");
	//						return null;
	//					}
	//					OriHalfedge he = new OriHalfedge(walkV, face);
	//					face.halfedges.add(he);
	//					he.tmpInt = walkE.type;
	//					if (walkE.sv == walkV) {
	//						walkE.left = he;
	//					} else {
	//						walkE.right = he;
	//					}
	//					walkV = walkE.oppositeVertex(walkV);
	//					walkE = walkV.getPrevEdge(walkE);
	//
	//					if (walkV == v) {
	//						break;
	//					}
	//				}
	//				face.makeHalfedgeLoop();
	//				face.setOutline();
	//				face.setPreOutline();
	//			}
	//		}
	//
	//		this.makeEdges(edges, faces);
	//		for (OriEdge e : edges) {
	//			e.type = e.left.tmpInt;
	//		}
	//
	//		return model;
	//
	//	}

	public boolean buildOrigami3(Collection<OriLine> creasePattern, OrigamiModel origamiModel, boolean needCleanUp) {	
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		edges.clear();
		vertices.clear();
		faces.clear();

		// Remove lines with the same position
		debugCount = 0;
		if (needCleanUp) {
			if (cleanDuplicatedLines(creasePattern)) {
				JOptionPane.showMessageDialog(
						ORIPA.mainFrame, "Removing multiples edges with the same position ",
						"Simplifying CP", JOptionPane.INFORMATION_MESSAGE);
			}

		}

		// Create the edges from the vertexes
		for (OriLine l : creasePattern) {
			if (l.typeVal == OriLine.TYPE_NONE) {
				continue;
			}

			OriVertex sv = addAndGetVertexFromVVec(vertices, l.p0);
			OriVertex ev = addAndGetVertexFromVVec(vertices, l.p1);
			OriEdge eg = new OriEdge(sv, ev, l.typeVal);
			edges.add(eg);
			sv.addEdge(eg);
			ev.addEdge(eg);
		}


		// Check if there are vertexes with just 2 collinear edges with same type
		// merge the edges and delete the vertex for efficiency 
		ArrayList<OriEdge> eds = new ArrayList<OriEdge>();
		ArrayList<OriVertex> tmpVVec = new ArrayList<OriVertex>();
		tmpVVec.addAll(vertices);
		for (OriVertex v : tmpVVec) {
			eds.clear();
			for (OriEdge e : edges) {
				if (e.sv == v || e.ev == v) {
					eds.add(e);
				}
			}

			if (eds.size() != 2) {
				continue;
			}

			// If the types of the edges are different, do nothing
			if (eds.get(0).type != eds.get(1).type) {
				continue;
			}

			OriEdge e0 = eds.get(0);
			OriEdge e1 = eds.get(1);

			// Check if they are collinear
			Vector2d dir0 = new Vector2d(e0.ev.p.x - e0.sv.p.x, e0.ev.p.y - e0.sv.p.y);
			Vector2d dir1 = new Vector2d(e1.ev.p.x - e1.sv.p.x, e1.ev.p.y - e1.sv.p.y);

			dir0.normalize();
			dir1.normalize();

			if (GeomUtil.Distance(dir0, dir1) > 0.001
					&& Math.abs(GeomUtil.Distance(dir0, dir1) - 2.0) > 0.001) {
				continue;
			}

			// found mergeable edge 
			edges.remove(e0);
			edges.remove(e1);
			vertices.remove(v);
			e0.sv.edges.remove(e0);
			e0.ev.edges.remove(e0);
			e1.sv.edges.remove(e1);
			e1.ev.edges.remove(e1);
			if (e0.sv == v && e1.sv == v) {
				OriEdge ne = new OriEdge(e0.ev, e1.ev, e0.type);
				edges.add(ne);
				ne.sv.addEdge(ne);
				ne.ev.addEdge(ne);
			} else if (e0.sv == v && e1.ev == v) {
				OriEdge ne = new OriEdge(e0.ev, e1.sv, e0.type);
				edges.add(ne);
				ne.sv.addEdge(ne);
				ne.ev.addEdge(ne);
			} else if (e0.ev == v && e1.sv == v) {
				OriEdge ne = new OriEdge(e0.sv, e1.ev, e0.type);
				edges.add(ne);
				ne.sv.addEdge(ne);
				ne.ev.addEdge(ne);
			} else {
				OriEdge ne = new OriEdge(e0.sv, e1.sv, e0.type);
				edges.add(ne);
				ne.sv.addEdge(ne);
				ne.ev.addEdge(ne);
			}
		}

		// System.out.println("vnum=" + vertices.size());
		// System.out.println("enum=" + edges.size());


		// Construct the faces
		for (OriVertex v : vertices) {

			for (OriEdge e : v.edges) {

				if (e.type == OriLine.TYPE_CUT) {
					continue;
				}

				if (v == e.sv) {
					if (e.left != null) {
						continue;
					}
				} else {
					if (e.right != null) {
						continue;
					}
				}

				OriFace face = new OriFace();
				faces.add(face);
				OriVertex walkV = v;
				OriEdge walkE = e;
				debugCount = 0;
				while (true) {
					if (debugCount++ > 100) {
						System.out.println("ERROR");
						return false;
					}
					OriHalfedge he = new OriHalfedge(walkV, face);
					face.halfedges.add(he);
					he.tmpInt = walkE.type;
					if (walkE.sv == walkV) {
						walkE.left = he;
					} else {
						walkE.right = he;
					}
					walkV = walkE.oppositeVertex(walkV);
					walkE = walkV.getPrevEdge(walkE);
					if (walkV == v) {
						break;
					}
				}
				face.makeHalfedgeLoop();
				face.setOutline();
				face.setPreOutline();
			}
		}

		makeEdges(edges, faces);
		for (OriEdge e : edges) {
			e.type = e.left.tmpInt;
		}

		origamiModel.setHasModel(true);

		return checkPatternValidity(edges, vertices, faces);
	}


	private boolean cleanDuplicatedLines(Collection<OriLine> creasePattern) {
		debugCount = 0;
		System.out.println("pre cleanDuplicatedLines " + creasePattern.size());
		ArrayList<OriLine> tmpLines = new ArrayList<OriLine>();
		for (OriLine l : creasePattern) {
			OriLine ll = l;

			boolean bSame = false;
			// Test if the line is already in tmpLines to prevent duplicity
			for (OriLine line : tmpLines) {
				if (GeomUtil.isSameLineSegment(line, ll)) {
					bSame = true;
					break;
				}
			}
			if (bSame) {
				continue;
			}
			tmpLines.add(ll);
		}

		if (creasePattern.size() == tmpLines.size()) {
			return false;
		}

		creasePattern.clear();
		creasePattern.addAll(tmpLines);
		System.out.println("after cleanDuplicatedLines " + creasePattern.size());

		return true;
	}

	private boolean checkPatternValidity(
			List<OriEdge>   edges, List<OriVertex> vertices,
			List<OriFace>   faces) {
		boolean isOK = true;


		// Check if the faces are convex
		for (OriFace face : faces) {
			if (face.halfedges.size() == 3) {
				continue;
			}

			OriHalfedge baseHe = face.halfedges.get(0);
			boolean baseFlg = GeomUtil.CCWcheck(baseHe.prev.vertex.p, 
					baseHe.vertex.p, baseHe.next.vertex.p);

			for (int i = 1; i < face.halfedges.size(); i++) {
				OriHalfedge he = face.halfedges.get(i);
				if (GeomUtil.CCWcheck(he.prev.vertex.p, he.vertex.p, he.next.vertex.p) != baseFlg) {
					isOK = false;
					face.hasProblem = true;
					break;
				}

			}
		}

		// Check Maekawa's theorem for all vertexes
		for (OriVertex v : vertices) {
			int ridgeCount = 0;
			int valleyCount = 0;
			boolean isCorner = false;
			for (OriEdge e : v.edges) {
				if (e.type == OriLine.TYPE_RIDGE) {
					ridgeCount++;
				} else if (e.type == OriLine.TYPE_VALLEY) {
					valleyCount++;
				} else if (e.type == OriLine.TYPE_CUT) {
					isCorner = true;
					break;
				}
			}

			if (isCorner) {
				continue;
			}

			if (Math.abs(ridgeCount - valleyCount) != 2) {
				System.out.println("edge type count invalid: "+ v+" "+Math.abs(ridgeCount - valleyCount));
				v.hasProblem = true;
				isOK = false;
			}
		}

		// Check Kawasaki's theorem for every vertex

		for (OriVertex v : vertices) {
			if (v.hasProblem) {
				continue;
			}
			Vector2d p = v.p;
			double oddSum = 0;
			double evenSum = 0;
			boolean isCorner = false;
			for (int i = 0; i < v.edges.size(); i++) {
				OriEdge e = v.edges.get(i);
				if (e.type == OriLine.TYPE_CUT) {
					isCorner = true;
					break;
				}

				Vector2d preP = new Vector2d(v.edges.get(i).oppositeVertex(v).p);
				Vector2d nxtP = new Vector2d(v.edges.get((i + 1) % v.edges.size()).oppositeVertex(v).p);

				nxtP.sub(p);
				preP.sub(p);

				if (i % 2 == 0) {
					oddSum += preP.angle(nxtP);
				} else {
					evenSum += preP.angle(nxtP);
				}
			}

			if (isCorner) {
				continue;
			}

			//System.out.println("oddSum = " + oddSum + "/ evenSum = " + evenSum);
			if (Math.abs(oddSum - Math.PI) > Math.PI / 180 / 2) {
				System.out.println("edge angle sum invalid");
				v.hasProblem = true;
				isOK = false;
			}
		}


		return isOK;
	}
	boolean sortFinished = false;

	private void makeEdges(List<OriEdge> edges, List<OriFace> faces) {
		edges.clear();

		ArrayList<OriHalfedge> tmpHalfedges = new ArrayList<OriHalfedge>();

		// Clear all the Halfedges
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.pair = null;
				he.edge = null;
				tmpHalfedges.add(he);
			}
		}

		// Search the halfedge pair
		int heNum = tmpHalfedges.size();
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he0 = tmpHalfedges.get(i);
			if (he0.pair != null) {
				continue;
			}

			for (int j = i + 1; j < heNum; j++) {
				OriHalfedge he1 = tmpHalfedges.get(j);
				if (he0.vertex == he1.next.vertex && he0.next.vertex == he1.vertex) {
					OriEdge edge = new OriEdge();
					he0.pair = he1;
					he1.pair = he0;
					he0.edge = edge;
					he1.edge = edge;
					edge.sv = he0.vertex;
					edge.ev = he1.vertex;
					edge.left = he0;
					edge.right = he1;
					edges.add(edge);
					edge.type = OriLine.TYPE_NONE;//OriEdge.TYPE_NONE;
				}
			}
		}

		// If the pair wasnt found it should be an edge
		for (OriHalfedge he : tmpHalfedges) {
			if (he.pair == null) {
				OriEdge edge = new OriEdge();
				he.edge = edge;
				edge.sv = he.vertex;
				edge.ev = he.next.vertex;
				edge.left = he;
				edges.add(edge);
				edge.type = OriLine.TYPE_CUT;
			}
		}
	}

	boolean isLineCrossFace4(OriFace face, OriHalfedge heg, double size) {
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.next.positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		for (OriHalfedge he : face.halfedges) {
			// About the relation of contours (?)

			// Check if the line is on the countour of the face
			if (GeomUtil.DistancePointToLine(he.positionAfterFolded, heLine) < 1
					&& GeomUtil.DistancePointToLine(he.next.positionAfterFolded, heLine) < 1) {
				return false;
			}
		}

		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			// Checks if the line crosses any of the edges of the face
			Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded, he.next.positionAfterFolded, heg.positionAfterFolded, heg.next.positionAfterFolded);
			if (cp == null) {
				continue;
			}

			if (preCrossPoint == null) {
				preCrossPoint = cp;
			} else {
				if (GeomUtil.Distance(cp, preCrossPoint) > size * 0.001) {
					return true;
				}
			}
		}

		// Checkes if the line is in the interior of the face
		if (isOnFace(face, heg.positionAfterFolded, size)) {
			return true;
		}
		if (isOnFace(face, heg.next.positionAfterFolded, size)) {
			return true;
		}

		return false;
	}

	private boolean isOnFace(OriFace face, Vector2d v, double size) {

		int heNum = face.halfedges.size();

		// Return false if the vector is on the contour of the face
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.DistancePointToSegment(v, he.positionAfterFolded, he.next.positionAfterFolded) < size * 0.001) {
				return false;
			}
		}

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.positionAfterFolded, baseHe.next.positionAfterFolded, v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(he.positionAfterFolded, he.next.positionAfterFolded, v) != baseFlg) {
				return false;
			}
		}

		return true;
	}


//	public void setCrossLine(List<OriLine> crossLines, OriLine line, List<OriFace> sortedFaces) {
//		crossLines.clear();
//		for (OriFace face : sortedFaces) {
//			ArrayList<Vector2d> vv = new ArrayList<Vector2d>();
//			int crossCount = 0;
//			for (OriHalfedge he : face.halfedges) {
//				OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
//						he.next.positionForDisplay.x, he.next.positionForDisplay.y, PaintConfig.inputLineType);
//
//				double params[] = new double[2];
//				boolean res = GeomUtil.getCrossPointParam(line.p0, line.p1, l.p0, l.p1, params);
//				if (res == true && params[0] > -0.001 && params[1] > -0.001 && params[0] < 1.001 && params[1] < 1.001) {
//					double param = params[1];
//					crossCount++;
//
//					Vector2d crossV = new Vector2d();
//					crossV.x = (1.0 - param) * he.vertex.preP.x + param * he.next.vertex.preP.x;
//					crossV.y = (1.0 - param) * he.vertex.preP.y + param * he.next.vertex.preP.y;
//
//					boolean isNewPoint = true;
//					for (Vector2d v2d : vv) {
//						if (GeomUtil.Distance(v2d, crossV) < 1) {
//							isNewPoint = false;
//							break;
//						}
//					}
//					if (isNewPoint) {
//						vv.add(crossV);
//					}
//				}
//			}
//
//			if (vv.size() >= 2) {
//				crossLines.add(new OriLine(vv.get(0), vv.get(1), PaintConfig.inputLineType));
//			}
//		}
//
//	}




	BoundBox calcFoldedBoundingBox(List<OriFace> faces) {
		Vector2d foldedBBoxLT = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2d foldedBBoxRB = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				foldedBBoxLT.x = Math.min(foldedBBoxLT.x, he.tmpVec.x);
				foldedBBoxLT.y = Math.min(foldedBBoxLT.y, he.tmpVec.y);
				foldedBBoxRB.x = Math.max(foldedBBoxRB.x, he.tmpVec.x);
				foldedBBoxRB.y = Math.max(foldedBBoxRB.y, he.tmpVec.y);
			}
		}

		return new BoundBox(foldedBBoxLT, foldedBBoxRB);
	}





}
