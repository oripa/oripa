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
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.RectangleDomain;
import oripa.persistent.filetool.Exporter;

/**
 * @author Koji
 *
 */
public class OrigamiModelExporterDXF implements Exporter<OrigamiModel> {

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.Exporter#export(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean export(final OrigamiModel origamiModel, final String filePath)
			throws IOException, IllegalArgumentException {
		double paperSize = origamiModel.getPaperSize();

		double scale = 6.0 / paperSize; // 6.0 inch width
		double center = 4.0; // inch

		try (var fw = new FileWriter(filePath);
				var bw = new BufferedWriter(fw);) {

			// Align the center of the model, combine scales
			Vector2d modelCenter = new Vector2d();

			List<OriFace> faces = origamiModel.getFaces();
			List<OriFace> sortedFaces = origamiModel.getSortedFaces();

			var domain = new RectangleDomain();
			for (OriFace face : faces) {
				face.halfedgeStream().forEach(he -> {
					domain.enlarge(he.getPosition());
				});
			}

			modelCenter.x = domain.getCenterX();
			modelCenter.y = domain.getCenterY();

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

			for (OriFace face : sortedFaces) {
				for (var he : (Iterable<OriHalfedge>) () -> face.halfedgeStream().iterator()) {

					bw.write("  0\n");
					bw.write("LINE\n");
					bw.write("  8\n");
					bw.write("_0-0_\n"); // Layer name
					bw.write("  6\n");
					bw.write("CONTINUOUS\n"); // Line type
					bw.write(" 62\n"); // 1＝red 2＝yellow 3＝green 4＝cyan 5＝blue
										// 6＝magenta 7＝white
					int colorNumber = 250;
					var position = he.getPositionForDisplay();
					var nextPosition = he.getNext().getPositionForDisplay();

					bw.write("" + colorNumber + "\n");
					bw.write(" 10\n");
					bw.write(""
							+ ((position.x - modelCenter.x)
									* scale + center)
							+ "\n");
					bw.write(" 20\n");
					bw.write(""
							+ (-(position.y - modelCenter.y)
									* scale + center)
							+ "\n");
					bw.write(" 11\n");
					bw.write(""
							+ ((nextPosition.x - modelCenter.x)
									* scale + center)
							+ "\n");
					bw.write(" 21\n");
					bw.write(""
							+ (-(nextPosition.y - modelCenter.y)
									* scale + center)
							+ "\n");
				}
			}

			bw.write("  0\n");
			bw.write("ENDSEC\n");
			bw.write("  0\n");
			bw.write("EOF\n");
		}

		return true;
	}
}
