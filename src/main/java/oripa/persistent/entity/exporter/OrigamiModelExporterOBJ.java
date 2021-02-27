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

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistent.filetool.Exporter;

/**
 * @author Koji
 *
 */
public class OrigamiModelExporterOBJ implements Exporter<OrigamiModel> {

	@Override
	public boolean export(final OrigamiModel origamiModel, final String filePath)
			throws IOException, IllegalArgumentException {
		List<OriFace> faces = origamiModel.getFaces();
		List<OriVertex> vertices = origamiModel.getVertices();
		List<OriEdge> edges = origamiModel.getEdges();

		try (var fw = new FileWriter(filePath);
				var bw = new BufferedWriter(fw);) {

			// Align the center of the model, combine scales
			bw.write("# Created by ORIPA\n");
			bw.write("\n");

			int id = 1;
			for (OriVertex vertex : vertices) {
				var position = vertex.getPosition();
				bw.write("v " + position.x + " " + position.y + " 0.0\n");
				vertex.setVertexID(id);
				id++;
			}

			for (OriFace face : faces) {
				bw.write("f");
				for (var he : face.halfedgeIterable()) {
					bw.write(" " + he.getVertex().getVertexID());
				}
				bw.write("\n");
			}

			for (OriEdge edge : edges) {
				bw.write("e " + edge.getStartVertex().getVertexID() + " " + edge.getEndVertex().getVertexID() + " "
						+ edge.getType() + " 180\n");
			}
		}

		return true;

	}
}
