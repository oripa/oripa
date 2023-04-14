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
package oripa.persistence.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import oripa.domain.creasepattern.CreasePattern;
import oripa.persistence.filetool.Exporter;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class CreasePatternExporterDXF implements Exporter<CreasePattern> {

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.Exporter#export(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean export(final CreasePattern creasePattern, final String filePath, final Object configObj)
			throws IOException, IllegalArgumentException {

		if (creasePattern.isUnassigned()) {
			throw new IllegalArgumentException("Unassigned crease pattern is not allowed.");
		}

		double paperSize = creasePattern.getPaperSize();
		double scale = 6.0 / paperSize; // 6.0 inch width
		double center = 4.0; // inch

		try (var fw = new FileWriter(filePath);
				var bw = new BufferedWriter(fw);) {

			bw.write("  0\n");
			bw.write("SECTION\n");
			bw.write("  2\n");
			bw.write("HEADER\n");
			bw.write("  9\n");
			bw.write("$ACADVER\n");
			bw.write("  1\n");
			bw.write("AC1009\n");
			bw.write("  0\n");
			bw.write("ENDSEC\n");
			bw.write("  0\n");
			bw.write("SECTION\n");
			bw.write("  2\n");
			bw.write("ENTITIES\n");

			for (OriLine line : creasePattern) {
				bw.write("  0\n");
				bw.write("LINE\n");
				bw.write("  8\n");
				String layerName = "noname";
				switch (line.getType()) {
				case CUT:
					layerName = "CutLine";
					break;
				case MOUNTAIN:
					layerName = "MountainLine";
					break;
				case VALLEY:
					layerName = "ValleyLine";
				default:
				}
				bw.write(layerName + "\n"); // Layer name
				bw.write("  6\n");
				bw.write("CONTINUOUS\n"); // Line type
				bw.write(" 62\n"); // 1＝red 2＝yellow 3＝green 4＝cyan 5＝blue
									// 6＝magenta
									// 7＝white
				int colorNumber = 0;
				switch (line.getType()) {
				case CUT:
					colorNumber = 3; // green
					break;
				case MOUNTAIN:
					colorNumber = 1; // red
					break;
				case VALLEY:
					colorNumber = 5; // blue
					break;
				default:
				}

				bw.write("" + colorNumber + "\n");
				bw.write(" 10\n");
				bw.write("" + (line.p0.x * scale + center) + "\n");
				bw.write(" 20\n");
				bw.write("" + ((paperSize / 2 - line.p0.y) * scale + center)
						+ "\n");
				bw.write(" 11\n");
				bw.write("" + (line.p1.x * scale + center) + "\n");
				bw.write(" 21\n");
				bw.write("" + ((paperSize / 2 - line.p1.y) * scale + center)
						+ "\n");
			}

			bw.write("  0\n");
			bw.write("ENDSEC\n");
			bw.write("  0\n");
			bw.write("EOF\n");

		}

		return true;
	}
}
