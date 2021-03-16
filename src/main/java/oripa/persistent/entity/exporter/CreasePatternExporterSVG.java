/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.persistent.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.filetool.Exporter;
import oripa.persistent.svg.SVGConstants;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class CreasePatternExporterSVG implements Exporter<CreasePatternInterface> {
	@Override
	public boolean export(final CreasePatternInterface creasePattern, final String filepath)
			throws IOException, IllegalArgumentException {
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
