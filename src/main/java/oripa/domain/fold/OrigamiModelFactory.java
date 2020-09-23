package oripa.domain.fold;

import java.util.*;

import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

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
	// public boolean hasModel = false;

	public OrigamiModel createOrigamiModel(
			final Collection<OriLine> creasePattern, final double paperSize) {
		return this.createOrigamiModelImpl3(creasePattern, paperSize, false);
	}

	public OrigamiModel createOrigamiModelNoDuplicateLines(
			final Collection<OriLine> creasePattern, final double paperSize) {
		return this.createOrigamiModelImpl3(creasePattern, paperSize, true);
	}

	public OrigamiModel createOrigamiModel(final double paperSize) {
		return new OrigamiModel(paperSize);
	}

	// TODO: change as: throw error if creation failed.
	public OrigamiModel buildOrigami(
			final Collection<OriLine> creasePattern, final double paperSize,
			final boolean needCleanUp) {
		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		edges.clear();
		vertices.clear();
		faces.clear();
		List<OriLine> precreases = new ArrayList<>();

		for (OriLine l : creasePattern) {
			if (l.getType() == OriLine.Type.NONE) {
				precreases.add(l);
				continue;
			}

			OriVertex sv = addAndGetVertexFromVVec(vertices, l.p0);
			OriVertex ev = addAndGetVertexFromVVec(vertices, l.p1);
			OriEdge eg = new OriEdge(sv, ev, l.getType().toInt());
			edges.add(eg);
			sv.addEdge(eg);
			ev.addEdge(eg);
		}

		for (OriVertex v : vertices) {

			for (OriEdge e : v.edges) {

				if (e.type == OriLine.Type.CUT.toInt()) {
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

		return origamiModel;

	}

	private OriVertex addAndGetVertexFromVVec(
			final List<OriVertex> vertices, final Vector2d p) {
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
	// TODO: change as: return OrigamiModel. throw error if creation failed.
	private OrigamiModel createOrigamiModelImpl3(
			final Collection<OriLine> creasePattern, final double paperSize,
			final boolean needCleanUp) {

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
						null, "Removing multiples edges with the same position ",
						"Simplifying CP", JOptionPane.INFORMATION_MESSAGE);
			}

		}

		// Create the edges from the vertexes
		List<OriLine> precreases = new ArrayList<>();
		for (OriLine l : creasePattern) {
			if (l.getType() == OriLine.Type.NONE) {
				Vector2d p0 = new Vector2d(l.p0);
				Vector2d p1 = new Vector2d(l.p1);
				precreases.add(new OriLine(p0, p1, l.getType()));
				continue;
			}

			OriVertex sv = addAndGetVertexFromVVec(vertices, l.p0);
			OriVertex ev = addAndGetVertexFromVVec(vertices, l.p1);
			OriEdge eg = new OriEdge(sv, ev, l.getType().toInt());
			edges.add(eg);
			sv.addEdge(eg);
			ev.addEdge(eg);
		}

		// Check if there are vertexes with just 2 collinear edges with same
		// type
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
		List<OriEdge> outlineEdges = new ArrayList<>();
		for (OriVertex v : vertices) {

			for (OriEdge e : v.edges) {

				if (e.type == OriLine.Type.CUT.toInt()) {
					outlineEdges.add(e);
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

				OriFace face = makeFace(v, e);
				if (face == null) {
					return origamiModel;
				}
				faces.add(face);
			}
		}
		if (faces.isEmpty()) { // happens when there is no crease
			OriEdge outlineEdge = outlineEdges.get(0);
			OriVertex v = outlineEdge.sv;

			OriFace face = makeFace(v, outlineEdge);
			if (face == null) {
				return origamiModel;
			}
			faces.add(face);
		}

		makeEdges(edges, faces);
		for (OriEdge e : edges) {
			e.type = e.left.tmpInt;
		}
		for (OriFace face : faces) {
			ListIterator<OriLine> iterator = precreases.listIterator();
			while (iterator.hasNext()) {
				OriLine precrease = iterator.next();
				if (GeomUtil.isOriLineCrossFace(face, precrease)) {
					face.precreases.add(precrease);
					iterator.remove();
				}
			}
		}
		origamiModel.setHasModel(true);
		return origamiModel;
	}

	private OriFace makeFace(OriVertex startingVertex, OriEdge startingEdge) {
		OriFace face = new OriFace();
		OriVertex walkV = startingVertex;
		OriEdge walkE = startingEdge;
		debugCount = 0;
		do {
			if (debugCount++ > 100) {
				System.out.println("ERROR");
//						throw new UnfoldableModelException("algorithmic error");
				return null;
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
		} while (walkV != startingVertex);
		face.makeHalfedgeLoop();
		face.setOutline();
		face.setPreOutline();
		return face;
	}

	// boolean sortFinished = false;

	private void makeEdges(final List<OriEdge> edges, final List<OriFace> faces) {
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
					edge.type = OriLine.Type.NONE.toInt();// OriEdge.TYPE_NONE;
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
				edge.type = OriLine.Type.CUT.toInt();
			}
		}
	}
}
