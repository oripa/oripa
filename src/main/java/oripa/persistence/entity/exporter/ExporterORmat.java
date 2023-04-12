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

package oripa.persistence.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.filetool.Exporter;

// export folded model
public class ExporterORmat implements Exporter<FoldedModelEntity> {

	@Override
	public boolean export(final FoldedModelEntity foldedModel, final String filepath, final Object configObj)
			throws IOException, IllegalArgumentException {
		OrigamiModel origamiModel = foldedModel.getOrigamiModel();
		OverlapRelation overlapRelation = foldedModel.getOverlapRelation();

		try (var fw = new FileWriter(filepath);
				var bw = new BufferedWriter(fw);) {

			// Align the center of the model, combine scale
			bw.write("# Created by ORIPA\n");
			bw.write("#\n");
			bw.write("# v (x) (y) (x: folded) (y: folded)\n");
			bw.write("# f (index list of contour vertices. Index number starts from 1.)\n");
			bw.write("\n");

			List<OriVertex> vertices = origamiModel.getVertices();
			List<OriFace> faces = origamiModel.getFaces();

			int id = 1;
			for (OriVertex vertex : vertices) {
				var positionBefore = vertex.getPositionBeforeFolding();
				var positionAfter = vertex.getPosition();
				bw.write("v " + positionBefore.x + " " + positionBefore.y + " "
						+ positionAfter.x + " " + positionAfter.y + "\n");
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

			int faceNum = faces.size();
			bw.write("# overlap relation matrix\n");
			bw.write("# 0: NO_OVERLAP\n");
			bw.write("# 1: face[row_index] located LOWER than face[col_index]\n");
			bw.write("# 2: face[row_index] located UPPER than face[col_index]\n");
			bw.write("# 9: UNDEFINED (not used)\n");
			bw.write("# matrix size (face num) =" + faceNum + "\n");

			for (int f0 = 0; f0 < faceNum; f0++) {
				for (int f1 = 0; f1 < faceNum; f1++) {
					bw.write("" + overlapRelation.get(f0, f1) + " ");
				}
				bw.write("\n");
			}
		}

		return true;
	}
}
