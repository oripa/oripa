package oripa.domain.fold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

public class OrigamiModelFactory {
	private static final Logger logger = LoggerFactory.getLogger(OrigamiModelFactory.class);
	int debugCount = 0;

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	public OrigamiModel createOrigamiModel(
			final Collection<OriLine> creasePattern, final double paperSize) {
		return this.createOrigamiModelImpl3(creasePattern, paperSize);
	}

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This method
	 * simply constructs the data structure and does not execute other
	 * operations like cleaning up given crease pattern. So there may be some
	 * error in the returned data.
	 *
	 * @param creasePattern
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	// TODO: change as: throw error if creation failed.
	OrigamiModel buildOrigami(
			final Collection<OriLine> creasePattern, final double paperSize) {
		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		edges.clear();
		vertices.clear();
		faces.clear();
		List<OriLine> precreases = new ArrayList<>();

		for (OriLine l : creasePattern) {
			if (l.isAux()) {
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

				OriFace face = makeFace(v, e);
				if (face == null) {
					return origamiModel;
				}
				faces.add(face);
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
		return vertices.parallelStream()
				.filter(v -> GeomUtil.distance(v.p, p) < CalculationResource.POINT_EPS)
				.findFirst()
				.orElseGet(() -> {
					var vtx = new OriVertex(p);
					vertices.add(vtx);
					return vtx;
				});
	}

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	// TODO: change as: return OrigamiModel. throw error if creation failed.
	private OrigamiModel createOrigamiModelImpl3(
			final Collection<OriLine> creasePattern, final double paperSize) {

		var startTime = System.currentTimeMillis();

		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		edges.clear();
		vertices.clear();
		faces.clear();

		debugCount = 0;

		// Create the edges and precreases from the vertexes
		List<OriLine> precreases = new ArrayList<>();
		for (OriLine l : creasePattern) {
			if (l.isAux()) {
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

		removeMeaninglessVertices(vertices, edges);

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
		// attach precrease lines to faces
		for (OriFace face : faces) {
			ListIterator<OriLine> iterator = precreases.listIterator();
			while (iterator.hasNext()) {
				OriLine precrease = iterator.next();
				if (OriGeomUtil.isOriLineCrossFace(face, precrease)) {
					face.precreases.add(precrease);
					iterator.remove();
				}
			}
		}
		origamiModel.setHasModel(true);

		logger.debug(
				"createOrigamiModelImpl3(): " + (System.currentTimeMillis() - startTime) + "[ms]");
		return origamiModel;
	}

	/**
	 * Searches vertices with 2 colinear edges with the same type. Then merges
	 * the edges split by the vertex and delete the vertex for efficiency.
	 *
	 * @param vertices
	 * @param edges
	 */
	private void removeMeaninglessVertices(
			final Collection<OriVertex> vertices, final Collection<OriEdge> edges) {
		ArrayList<OriVertex> tmpVVec = new ArrayList<OriVertex>();
		tmpVVec.addAll(vertices);

		for (var v : tmpVVec) {
			var eds = edges.parallelStream()
					.filter(e -> e.sv == v || e.ev == v)
					.collect(Collectors.toCollection(() -> new ArrayList<>()));

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

			if (GeomUtil.distance(dir0, dir1) > 0.001
					&& Math.abs(GeomUtil.distance(dir0, dir1) - 2.0) > 0.001) {
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

	}

	private OriFace makeFace(final OriVertex startingVertex, final OriEdge startingEdge) {
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

	private boolean isOppositeDirection(final OriHalfedge he0, final OriHalfedge he1) {
		return he0.vertex == he1.next.vertex && he0.next.vertex == he1.vertex;
	}

	private void makeEdges(final List<OriEdge> edges, final List<OriFace> faces) {
		edges.clear();

		ArrayList<OriHalfedge> halfedges = new ArrayList<OriHalfedge>();

		// Clear all the Halfedges
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				he.pair = null;
				he.edge = null;
				halfedges.add(he);
			}
		}

		// from all combinations of two half-edges, find half-edge pairs whose
		// directions are opposite (that's the definition of edge).
		int heNum = halfedges.size();
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he0 = halfedges.get(i);
			if (he0.pair != null) {
				continue;
			}

			for (int j = i + 1; j < heNum; j++) {
				OriHalfedge he1 = halfedges.get(j);
				if (isOppositeDirection(he0, he1)) {
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
					edge.type = OriLine.Type.AUX.toInt();
					// Pair is found. We can break here because something is
					// wrong if there are 2 or more opposite half-edges for he0.
					// We assume that everything is correct :)
					break;
				}
			}
		}

		// If the pair wasn't found it should be boundary of paper
		for (OriHalfedge he : halfedges) {
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
