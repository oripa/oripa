package oripa.doc.exporter;

import org.json.JSONObject;
import oripa.doc.Doc;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriVertex;
import oripa.value.OriLine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ExporterFold implements Exporter {
	private static final double FOLD_SPEC_VERSION = 1.1;

	public boolean export(Doc doc, String filepath) throws Exception {
		//Collections of useful bits of the OrigamiModel for convenience
		List<OriVertex> modelVertices = doc.getOrigamiModel().getVertices();
		List<OriEdge> modelEdges = doc.getOrigamiModel().getEdges();
		List<OriFace> modelFaces = doc.getOrigamiModel().getFaces();

		//Convert vertices to .fold style arrays of floats
		ArrayList<double[]> foldVerticesCoordinates = new ArrayList<>();
		modelVertices.forEach(v -> {
			double[] asArray = {v.p.x / doc.getPaperSize(), v.p.y / doc.getPaperSize()};
			foldVerticesCoordinates.add(asArray);
		});

		//Convert edges to .fold style arrays to vertex IDs
		ArrayList<int[]> foldEdgeVertices = new ArrayList<>();
		ArrayList<String> foldEdgeAssignments = new ArrayList<>();
		ArrayList<Integer> foldEdgeAngleAssignments = new ArrayList<>();
		for(OriEdge e: modelEdges) {
			int[] vertexIds = {
					modelVertices.indexOf(e.sv),
					modelVertices.indexOf(e.ev)
			};
			foldEdgeVertices.add(vertexIds);
			foldEdgeAssignments.add(convertToAssignmentString(e.type));
			foldEdgeAngleAssignments.add(convertToFoldAngle(e.type));
		}

		//Convert faces to .fold style arrays of vertex IDs
		ArrayList<int[]> foldFaceVertices = new ArrayList<>();
		ArrayList<int[]> foldFaceEdges = new ArrayList<>();
		for(OriFace f: modelFaces) {
			// Helpfully, each of an OriFace's HalfEdge's points go in a nice loop
			ArrayList<OriVertex> vertices = new ArrayList<>();
			ArrayList<OriEdge> edges = new ArrayList<>();
			f.halfedges.forEach(e -> {
				//Keep track of the edges indices
				edges.add(e.edge);

				// We want to add the vertex that is NOT present in the next half edge
				if(e.edge.sv == e.next.edge.sv || e.edge.sv == e.next.edge.ev) {
					vertices.add(e.edge.ev);
				} else {
					vertices.add(e.edge.sv);
				}
			});

			//Search through modelVertices/modelEdges to turn this into a list of IDs
			int[] vertexIds = vertices.stream()
					.mapToInt(modelVertices::indexOf)
					.toArray();
			foldFaceVertices.add(vertexIds);

			int[] edgeIds = edges.stream()
					.mapToInt(e -> {
						for(int i = 0; i < modelEdges.size(); i++) {
							if (modelEdges.get(i).sv.p.equals(e.sv.p) &
								modelEdges.get(i).ev.p.equals(e.ev.p)) {
								return i;
							}
						}
						return -1;
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
