package oripa.persistent.doc.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.OriFace;
import oripa.domain.fold.OriHalfedge;
import oripa.domain.fold.OrigamiModel;
import oripa.value.OriLine;

public class ExporterSVGFactory {

	static final int size = 1000;
	final static String head = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n"
			+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
			+ "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n"
			+ "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
			+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\"\n"
			+ " width=\"" + size + "px\" height=\"" + size + "px\"\n"
			+ " viewBox=\"0 0 " + size + " " + size + "\" >\n";
	final static String end = "</svg>";
	final static String gradient = " <linearGradient id=\"Gradient1\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
			+ " <stop offset=\"5%\" stop-color=\"#DDEEFF\" />\n"
			+ " <stop offset=\"95%\" stop-color=\"#7788FF\" />\n"
			+ " </linearGradient>\n"
			+
			" <linearGradient id=\"Gradient2\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
			+ " <stop offset=\"5%\" stop-color=\"#FFFFEE\" />\n"
			+ " <stop offset=\"95%\" stop-color=\"#DDDDDD\" />\n"
			+ " </linearGradient>\n";
	final static String lineStart = " <line stroke=\"blue\" stroke-width=\"2\" ";
	final static String polygonStart = "<path style=\"fill:url(#Gradient1);"
			+ "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
			+ "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";
	final static String polygonStart2 = "<path style=\"fill:url(#Gradient2);"
			+ "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
			+ "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";

	private static class CreasePatternExporter implements DocExporter {

		@Override
		public boolean export(final Doc doc, final String filepath)
				throws IOException, IllegalArgumentException {
			CreasePatternInterface creasePattern = doc.getCreasePattern();
			double paperSize = creasePattern.getPaperSize();

			double scale = size / paperSize;
			double center = size / 2;
			FileWriter fw = new FileWriter(filepath);
			try (BufferedWriter bw = new BufferedWriter(fw)) {
				bw.write(head);
				for (OriLine line : creasePattern) {
					bw.write(" <line style=\"");
					String style = "stroke:gray;stroke-width:1;";
					switch (line.getType()) {
					case CUT:
						style = "stroke:black;stroke-width:4;";
						break;
					case RIDGE:
						style = "stroke:red;stroke-width:2;";
						break;
					case VALLEY:
						style = "stroke:blue;stroke-width:2;stroke-opacity:1";
						break;
					default:
					}
					bw.write(style + "\" ");
					bw.write("x1=\"");
					bw.write("" + (line.p0.x * scale + center) + "\"");
					bw.write(" y1=\"");
					bw.write("" + ((paperSize / 2 - line.p0.y) * scale) + "\"");
					bw.write(" x2=\"");
					bw.write("" + (line.p1.x * scale + center) + "\"");
					bw.write(" y2=\"");
					bw.write("" + ((paperSize / 2 - line.p1.y) * scale)
							+ "\" />\n");
				}
				bw.write(end);
			}

			return true;
		}

	}

	private static class FoldedModelExporter implements DocExporter {
		private final boolean faceOrderFlip;

		/**
		 * Constructor
		 */
		public FoldedModelExporter(final boolean faceOrderFlip) {
			this.faceOrderFlip = faceOrderFlip;
		}

		@Override
		public boolean export(final Doc doc, final String filepath)
				throws IOException, IllegalArgumentException {
			OrigamiModel origamiModel = doc.getOrigamiModel();
			FoldedModelInfo foldedModelInfo = doc.getFoldedModelInfo();
			double paperSize = origamiModel.getPaperSize();

			double scale = (size - 5) / paperSize;
			double center = size / 2;
			FileWriter fw = new FileWriter(filepath);
			try (BufferedWriter bw = new BufferedWriter(fw)) {
				Vector2d maxV = new Vector2d(-Double.MAX_VALUE,
						-Double.MAX_VALUE);
				Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
				Vector2d modelCenter = new Vector2d();

				List<OriFace> faces = origamiModel.getFaces();

				for (OriFace face : faces) {
					for (OriHalfedge he : face.halfedges) {
						maxV.x = Math.max(maxV.x, he.vertex.p.x);
						maxV.y = Math.max(maxV.y, he.vertex.p.y);
						minV.x = Math.min(minV.x, he.vertex.p.x);
						minV.y = Math.min(minV.y, he.vertex.p.y);
					}
				}
				modelCenter.x = (maxV.x + minV.x) / 2;
				modelCenter.y = (maxV.y + minV.y) / 2;
				bw.write(head);
				bw.write(gradient);

				ArrayList<OriFace> sortedFaces = new ArrayList<>();
				boolean[] isSorted = new boolean[faces.size()];
				for (int i = 0; i < faces.size(); i++) {
					int[][] overlapRelation = foldedModelInfo
							.getOverlapRelation();

					for (int j = 0; j < overlapRelation.length; j++) {
						int numberOf2 = 0;
						if (!isSorted[j]) {
							for (int k = 0; k < isSorted.length; k++) {
								if ((!isSorted[k])
										&& overlapRelation[j][k] == 2) {
									numberOf2++;
								}
							}
							if (numberOf2 == 0) {
								isSorted[j] = true;
								sortedFaces.add(faces.get(j));
								break;
							}
						}
					}
				}

				for (int i = 0; i < sortedFaces.size(); i++) {
					// FIXME: Global method!
					OriFace face = faceOrderFlip ? sortedFaces.get(i)
							: sortedFaces.get(sortedFaces.size() - i - 1);
					java.util.ArrayList<Vector2d> points = new java.util.ArrayList<>();
					for (OriHalfedge he : face.halfedges) {

						if (he.vertex.p.x > maxV.x) {
							throw new IllegalArgumentException(
									"Size of vertices exceeds maximum");
						}

						double x1 = (he.vertex.p.x - modelCenter.x) * scale
								+ center;
						double y1 = -(he.vertex.p.y - modelCenter.y) * scale
								+ center;
						double x2 = (he.next.vertex.p.x - modelCenter.x)
								* scale
								+ center;
						double y2 = -(he.next.vertex.p.y - modelCenter.y)
								* scale
								+ center;
						if (!points.contains(new Vector2d(x1, y1))) {
							points.add(new Vector2d(x1, y1));
						}
						if (!points.contains(new Vector2d(x2, y2))) {
							points.add(new Vector2d(x2, y2));
						}
					}
					if (!face.precreases.isEmpty()) {
						bw.write("<g>");
					}
					if ((!face.faceFront && faceOrderFlip)
							|| (face.faceFront && !faceOrderFlip)) {
						bw.write(polygonStart);
					} else {
						bw.write(polygonStart2);
					}
					for (Vector2d p : points) {
						bw.write(p.x + "," + p.y + " ");
					}
					bw.write(" z\" />\n");
					for (OriLine oriLine : face.precreases) {
						double x1 = (oriLine.p0.x - modelCenter.x) * scale
							+ center;
						double y1 = -(oriLine.p0.y - modelCenter.y) * scale
								+ center;
						double x2 = (oriLine.p1.x - modelCenter.x)
								* scale
								+ center;
						double y2 = -(oriLine.p1.y - modelCenter.y)
								* scale
								+ center;
						bw.write("<line x1=\"" + x1);
						bw.write("\" y1=\"" + y1);
						bw.write("\" x2=\"" + x2);
						bw.write("\" y2=\"" + y2);
						bw.write("\" style=\"stroke:black;stroke-width:2px;\"/>\n");
					}
					if (!face.precreases.isEmpty()) {
						bw.write("</g>");
					}
				}
				bw.write(end);
			}
			return true;

		}

	}

	public static DocExporter createCreasePatternExporter() {
		return new CreasePatternExporter();
	}

	public static DocExporter createFoldedModelExporter(final boolean faceOrderFlip) {
		return new FoldedModelExporter(faceOrderFlip);
	}
}
