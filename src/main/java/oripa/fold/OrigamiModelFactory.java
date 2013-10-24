package oripa.fold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.fold.rule.ConjunctionLoop;
import oripa.fold.rule.FaceIsConvex;
import oripa.fold.rule.KawasakiTheorem;
import oripa.fold.rule.MaekawaTheorem;
import oripa.geom.GeomUtil;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

public class OrigamiModelFactory {
	final public static int NO_OVERLAP = 0;
	final public static int UPPER = 1;
	final public static int LOWER = 2;
	final public static int UNDEFINED = 9;


	int debugCount = 0;

	// should not be in this class
	//public boolean hasModel = false;



	public OrigamiModel createOrigamiModel(
			Collection<OriLine> creasePattern, double paperSize) {
		return this.createOrigamiModelImpl3(creasePattern, paperSize, false);
	}

	public OrigamiModel createOrigamiModelNoDuplicateLines(
			Collection<OriLine> creasePattern, double paperSize) {	
		return this.createOrigamiModelImpl3(creasePattern, paperSize, true);
	}

	
	public OrigamiModel createOrigamiModel(double paperSize) {
		return new OrigamiModel(paperSize);
	}



	//TODO: change as: throw error if creation failed.
	public OrigamiModel buildOrigami(
			Collection<OriLine> creasePattern, double paperSize, boolean needCleanUp) {
		OrigamiModel origamiModel = new OrigamiModel(paperSize);
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
						return origamiModel;
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
		origamiModel.setProbablyFoldable(true);
		
		return origamiModel;

	}
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

	
	/**
	 * 
	 * @param creasePattern
	 * @param paperSize
	 * @param needCleanUp
	 * @return A model data converted from crease pattern.
	 */
	//TODO: change as: return OrigamiModel. throw error if creation failed.
	private OrigamiModel createOrigamiModelImpl3(
			Collection<OriLine> creasePattern, double paperSize, boolean needCleanUp) {	

		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		sortedFaces.clear();

		edges.clear();
		vertices.clear();
		faces.clear();

		// Remove lines with the same position
		debugCount = 0;
		if (needCleanUp) {
			FolderTool tool = new FolderTool();
			if (tool.cleanDuplicatedLines(creasePattern)) {
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
//						throw new UnfoldableModelException("algorithmic error");
						return origamiModel;
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
		origamiModel.setProbablyFoldable(checkPatternValidity(edges, vertices, faces));

		return origamiModel;
	}



	private boolean checkPatternValidity(
			List<OriEdge>   edges, List<OriVertex> vertices,
			List<OriFace>   faces) {

		boolean isOK = true;

		//--------
		// test convex-face condition
		
		ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
				new FaceIsConvex());

		isOK &= convexRuleConjunction.holds(faces);

		for (OriFace face :convexRuleConjunction.getViolations()) {
			face.hasProblem = true;
		}

		//--------
		// test Maekawa's theorem

		ConjunctionLoop<OriVertex> maekawaConjunction = new ConjunctionLoop<>(
				new MaekawaTheorem());

		isOK &= maekawaConjunction.holds(vertices);

		for (OriVertex vertex : maekawaConjunction.getViolations()) {
			vertex.hasProblem = true;
		}
		

		//--------
		// test Kawasaki's theorem

		ConjunctionLoop<OriVertex> kawasakiConjunction = new ConjunctionLoop<>(
				new KawasakiTheorem());

		isOK &= kawasakiConjunction.holds(vertices);
		
		for (OriVertex vertex : kawasakiConjunction.getViolations()) {
			vertex.hasProblem = true;
		}


		return isOK;
	}
	//boolean sortFinished = false;

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






}
