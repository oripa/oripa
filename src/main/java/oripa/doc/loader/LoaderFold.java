package oripa.doc.loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oripa.doc.Doc;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;

import javax.vecmath.Vector2d;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LoaderFold implements Loader {

	@Override
	public Doc load(String filePath) {
		//Load the .fold JSON, build out a list of lines we can add to our blank ORIPA canvas
		ArrayList<OriLine> lines = new ArrayList<>();
		String fileCreator = "";
		String fileAuthor = "";
		String frameTitle = "";
		try {
			File file = new File(filePath);
			FileInputStream inputStream = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			int fileStatus = inputStream.read(data);
			if(fileStatus == -1) {
				System.err.println("Unable to read file \"" + filePath + "\"!");
				return null;
			}
			inputStream.close();
			String jsonSource = new String(data);
			JSONObject foldJson = new JSONObject(jsonSource);

			//Load in metadata
			try {
				fileCreator = foldJson.getString("file_creator");
				fileAuthor = foldJson.getString("file_author");
				frameTitle = foldJson.getString("frame_title");
			} catch (JSONException e) {
				System.err.println("Missing recommended properties");
				e.printStackTrace();
			}

			//Load in line information
			JSONArray foldVertices = foldJson.getJSONArray("vertices_coords");
			JSONArray foldEdges = foldJson.getJSONArray("edges_vertices");
			JSONArray foldEdgeAssignments = foldJson.getJSONArray("edges_assignment");
			for(int i = 0; i < foldEdges.length(); i++) {
				int vertexId1 = foldEdges.getJSONArray(i).getInt(0);
				int vertexId2 = foldEdges.getJSONArray(i).getInt(1);

				OriLine line = new OriLine();
				line.p0.x = foldVertices.getJSONArray(vertexId1).getDouble(0);
				line.p0.y = foldVertices.getJSONArray(vertexId1).getDouble(1);

				line.p1.x = foldVertices.getJSONArray(vertexId2).getDouble(0);
				line.p1.y = foldVertices.getJSONArray(vertexId2).getDouble(1);

				line.typeVal = convertFromAssignmentString(foldEdgeAssignments.getString(i));
				lines.add(line);
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		//Figure out the paper size for the crease pattern
		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
		for (OriLine line : lines) {
			minV.x = Math.min(minV.x, line.p0.x);
			minV.x = Math.min(minV.x, line.p1.x);
			minV.y = Math.min(minV.y, line.p0.y);
			minV.y = Math.min(minV.y, line.p1.y);

			maxV.x = Math.max(maxV.x, line.p0.x);
			maxV.x = Math.max(maxV.x, line.p1.x);
			maxV.y = Math.max(maxV.y, line.p0.y);
			maxV.y = Math.max(maxV.y, line.p1.y);
		}

		//Size normalization
		double size = 400;
		Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0, (minV.y + maxV.y) / 2.0);
		double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
		for (OriLine line : lines) {
			line.p0.x = (line.p0.x - center.x) / bboxSize * size;
			line.p0.y = (line.p0.y - center.y) / bboxSize * size;
			line.p1.x = (line.p1.x - center.x) / bboxSize * size;
			line.p1.y = (line.p1.y - center.y) / bboxSize * size;
		}

		//Build the ORIPA document, add the normalized lines
		Doc doc = new Doc(400);
		CreasePattern creasePattern = doc.getCreasePattern();
		creasePattern.clear();
		lines.forEach(doc::addLine);


		//Add metadata to the doucment
		doc.setOriginalAuthorName(fileAuthor);
		doc.setEditorName(fileCreator);
		doc.setTitle(frameTitle);

		return doc;
	}

	/*
	Oripa uses an enum for line types, while .fold uses a single-character string.
	This method converts a .fold assignment string to an Oripa line type.
	 */
	private int convertFromAssignmentString(String type) {
		switch (type) {
			case "B":
				return OriLine.TYPE_CUT;
			case "M":
				return OriLine.TYPE_RIDGE;
			case "V":
				return OriLine.TYPE_VALLEY;
			default:
				return OriLine.TYPE_NONE;
		}
	}
}
