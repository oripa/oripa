package oripa.doc.exporter;

import org.json.JSONObject;
import oripa.doc.Doc;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriVertex;
import oripa.geom.GeomUtil;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;
import oripa.value.OriPoint;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExporterFold implements Exporter {
	private static final double FOLD_SPEC_VERSION = 1.1;

	public boolean export(Doc doc, String filepath) throws Exception {
		//Collections of useful bits of the OrigamiModel for convenience
		CreasePattern modelEdges = doc.getCreasePattern();
		List<OriFace> modelFaces = doc.getOrigamiModel().getFaces();
		List<OriPoint> modelVertices = new ArrayList<>();

		//Populate the list of vertices. Vertices can appear multiple times in the list of edges, but the positions can
		//be slightly different due to floating point junk, so we'll use a special indexOf() with epsilon comparisons.
		doc.getCreasePattern().stream()
				.flatMap(e -> Arrays.stream(new OriPoint[]{e.p0, e.p1}))
				.forEach(p -> {
					if (sloppyIndexOf(modelVertices, p) == -1) {
						modelVertices.add(p);
					}
				});

		//Convert vertices to .fold style arrays of floats
		ArrayList<double[]> foldVerticesCoordinates = new ArrayList<>();
		double paperSize = doc.getPaperSize();
		modelVertices.forEach(v -> {
			//Normal ORIPA models are one 400 unit-long squares, with the center of the square at (0,0)
			//Though not enforced in the spec, it seems like most fold files are on a unit square.
			//We'll convert to that to be more friendly to libraries like RabbitEar.
			double[] asArray = {
					(v.x + (paperSize / 2.0)) / paperSize,
					(v.y + (paperSize / 2.0)) / paperSize
			};
			foldVerticesCoordinates.add(asArray);
		});

		//Convert edges to .fold style arrays to vertex IDs
		ArrayList<int[]> foldEdgeVertices = new ArrayList<>();
		ArrayList<String> foldEdgeAssignments = new ArrayList<>();
		ArrayList<Integer> foldEdgeAngleAssignments = new ArrayList<>();
		for (OriLine e : modelEdges) {
			int[] vertexIds = {
					//For some reason, sloppyIndexOf() didn't cut it here, so we use another version that looks for
					//the closest point. For our purposes, that seems to work.
					indexWithMinimumDistance(modelVertices, e.p0),
					indexWithMinimumDistance(modelVertices, e.p1),
			};
			foldEdgeVertices.add(vertexIds);
			foldEdgeAssignments.add(convertToAssignmentString(e.typeVal));
			foldEdgeAngleAssignments.add(convertToFoldAngle(e.typeVal));
		}

		//Convert faces to .fold style arrays of vertex IDs
		ArrayList<int[]> foldFaceVertices = new ArrayList<>();
		ArrayList<int[]> foldFaceEdges = new ArrayList<>();
		for (OriFace f : modelFaces) {
			// Helpfully, each of an OriFace's HalfEdge's points go in a nice loop
			ArrayList<OriVertex> vertices = new ArrayList<>();
			ArrayList<OriEdge> edges = new ArrayList<>();
			f.halfedges.forEach(e -> {
				//Keep track of the edges indices
				edges.add(e.edge);

				// We want to add the vertex that is NOT present in the next half edge
				if (e.edge.sv == e.next.edge.sv || e.edge.sv == e.next.edge.ev) {
					vertices.add(e.edge.ev);
				} else {
					vertices.add(e.edge.sv);
				}
			});

			//Search through modelVertices/modelEdges to turn this into a list of IDs
			int[] vertexIds = vertices.stream()
					.map(v -> new OriPoint(v.p.x, v.p.y))
					.mapToInt(v -> indexWithMinimumDistance(modelVertices, v))
					.toArray();
			foldFaceVertices.add(vertexIds);

			int[] edgeIds = edges.stream()
					.mapToInt(e -> {
						int i = 0;
						double minimumDistances = Double.POSITIVE_INFINITY;
						int indexWithMinimumDistances = -1;
						for(OriLine line: modelEdges) {
							//We don't know if the endpoints are in the same order, so we need to check both ways
							double d1 = GeomUtil.Distance(line.p0, e.sv.p) + GeomUtil.Distance(line.p1, e.ev.p);
							double d2 = GeomUtil.Distance(line.p0, e.ev.p) + GeomUtil.Distance(line.p1, e.sv.p);
							double smallerDistance = Math.min(d1, d2);
							if (smallerDistance < minimumDistances) {
								minimumDistances = smallerDistance;
								indexWithMinimumDistances = i;
							}
							i++;
						}
						return indexWithMinimumDistances;
					})
					.toArray();
			foldFaceEdges.add(edgeIds);
		}

		//Build the JSON document
		JSONObject foldJson = new JSONObject();
		foldJson.put("file_spec", FOLD_SPEC_VERSION)
				.put("file_creator", doc.getEditorName())
				.put("file_author", doc.getOriginalAuthorName())
				.put("file_classes", new String[]{"singleModel"})
				.put("frame_title", "" + doc.getTitle())
				.put("frame_attributes", new String[]{"2D"})
				.put("frame_classes", new String[]{"creasePattern"})
				.put("vertices_coords", foldVerticesCoordinates)
				.put("faces_vertices", foldFaceVertices)
				.put("faces_edges", foldFaceEdges)
				.put("edges_vertices", foldEdgeVertices)
				.put("edges_assignment", foldEdgeAssignments)
				.put("edges_foldAngle", foldEdgeAngleAssignments);

		//Write the file
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(foldJson.toString());
		bw.close();

		return true;
	}

	/*
	Returns the index of the passed point, using epsilon comparisons rather than regular ones.
	 */
	private int sloppyIndexOf(List<OriPoint> list, OriPoint p) {
		for (int i = 0; i < list.size(); i++) {
			if (GeomUtil.Distance(p, list.get(i)) <= GeomUtil.EPS) {
				return i;
			}
		}
		return -1;
	}

	/*
	Returns the index of the closest point in the list.

	We use this where we probably should be using an epsilon comparison, but even with the added slop,
	it failed to match some edges/vertices with that approach. Since it should be safe to assume that
	the crease pattern is well-formed, so it *should* also be safe to assume that the closest point is
	the same point, at least for our purposes.
	 */
	private int indexWithMinimumDistance(List<OriPoint> list, OriPoint p) {
		double minDistance = Double.POSITIVE_INFINITY;
		int minDistanceIndex = -1;
		for (int i = 0; i < list.size(); i++) {
			Double distance = GeomUtil.Distance(p, list.get(i));
			if (distance < minDistance) {
				minDistance = distance;
				minDistanceIndex = i;
			}
		}
		return minDistanceIndex;
	}

	/*
	Oripa uses an enum for line types, while .fold uses a single-character string.
	This method converts a Oripa line type to a .fold assignment string.
	 */
	private String convertToAssignmentString(int type) {
		switch (type) {
			case OriLine.TYPE_NONE:
				return "U";
			case OriLine.TYPE_CUT:
				return "B";
			case OriLine.TYPE_RIDGE:
				return "M";
			case OriLine.TYPE_VALLEY:
				return "V";
			default:
				return "U";
		}
	}

	/*
	Since Oripa is only for flat folding models, we'll assume that mountains/valleys lines are folded all the way
	and that cut/aux lines are not folded at all.
	 */
	private int convertToFoldAngle(int type) {
		switch (type) {
			case OriLine.TYPE_RIDGE:
				return -180;
			case OriLine.TYPE_VALLEY:
				return 180;
			default:
				return 0;
		}
	}
}
