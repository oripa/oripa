package oripa.persistent.doc.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.svg.SVGConstants;
import oripa.value.OriLine;

public class ExporterSVGFactory {

	private static class CreasePatternExporter implements DocExporter {

		@Override
		public boolean export(final Doc doc, final String filepath)
				throws IOException, IllegalArgumentException {
			CreasePatternInterface creasePattern = doc.getCreasePattern();
			double paperSize = creasePattern.getPaperSize();

			double scale = SVGConstants.size / paperSize;
			double center = SVGConstants.size / 2;

			double cpCenterX = creasePattern.getPaperDomain().getCenterX();
			double cpCenterY = creasePattern.getPaperDomain().getCenterY();

			try (var fw = new FileWriter(filepath);
					var bw = new BufferedWriter(fw);) {
				bw.write(SVGConstants.head);
				for (OriLine line : creasePattern) {
					bw.write(" <line style=\"");
					String style = "stroke:gray;stroke-width:1;";
					switch (line.getType()) {
					case CUT:
						style = "stroke:black;stroke-width:4;";
						break;
					case MOUNTAIN:
						style = "stroke:red;stroke-width:2;";
						break;
					case VALLEY:
						style = "stroke:blue;stroke-width:2;stroke-opacity:1";
						break;
					default:
					}
					bw.write(style + "\" ");
					bw.write("x1=\"");
					bw.write("" + ((line.p0.x - cpCenterX) * scale + center) + "\"");
					bw.write(" y1=\"");
					bw.write("" + ((paperSize / 2 - (line.p0.y - cpCenterY)) * scale) + "\"");
					bw.write(" x2=\"");
					bw.write("" + ((line.p1.x - cpCenterX) * scale + center) + "\"");
					bw.write(" y2=\"");
					bw.write("" + ((paperSize / 2 - (line.p1.y - cpCenterY)) * scale)
							+ "\" />\n");
				}
				bw.write(SVGConstants.end);
			}

			return true;
		}

	}

	public static DocExporter createCreasePatternExporter() {
		return new CreasePatternExporter();
	}
}
