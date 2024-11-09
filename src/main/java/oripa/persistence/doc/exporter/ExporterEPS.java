/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.persistence.doc.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import oripa.domain.creasepattern.CreasePattern;
import oripa.persistence.doc.Doc;
import oripa.value.OriLine;

public class ExporterEPS implements DocExporter {

	@Override
	public boolean export(final Doc doc, final String filepath, final Object configObj)
			throws IOException, IllegalArgumentException {
		try (var fw = new FileWriter(filepath);
				var bw = new BufferedWriter(fw);) {

			// Align the center of the model, combine scales
			bw.write("%!PS-Adobe EPSF-3\n");
			bw.write("%%BoundingBox:-200 -200 400 400\n");
			bw.write("\n");

			CreasePattern creasePattern = doc.getCreasePattern();

			for (OriLine line : creasePattern) {
				var p0 = line.getP0();
				var p1 = line.getP1();

				bw.write("[] 0 setdash\n");
				bw.write("" + p0.getX() + " " + p0.getY() + " moveto\n");
				bw.write("" + p1.getX() + " " + p1.getY() + " lineto\n");
				bw.write("stroke\n");
			}
		}
		return true;
	}
}
