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

package oripa.doc.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriVertex;

// export folded model
public class ExporterORmat implements Exporter{

    public boolean export(Doc doc, String filepath) throws Exception {
        FileWriter fw = new FileWriter(filepath);
        BufferedWriter bw = new BufferedWriter(fw);

        // Align the center of the model, combine scale
        bw.write("# Created by ORIPA\n");
        bw.write("#\n");
        bw.write("# v (x) (y) (x: folded) (y: folded)\n");
        bw.write("# f (index list of contour vertices. Index number starts from 1.)\n");
        bw.write("\n");

        int id = 1;
        for (OriVertex vertex : doc.vertices) {
            bw.write("v " + vertex.preP.x + " " + vertex.preP.y + " " + vertex.p.x + " " + vertex.p.y + "\n");
            vertex.tmpInt = id;
            id++;
        }

        for (OriFace face : ORIPA.doc.faces) {
            bw.write("f");
            for (OriHalfedge he : face.halfedges) {
                bw.write(" " + he.vertex.tmpInt);
            }
            bw.write("\n");
        }

        int faceNum = doc.faces.size();
        bw.write("# overlap relation matrix\n");
        bw.write("# 0: NO_OVERLAP\n");
        bw.write("# 1: face[row_index] located LOWER than face[col_index]\n");
        bw.write("# 2: face[row_index] located UPPER than face[col_index]\n");
        bw.write("# 9: UNDEFINED (not used)\n");
        bw.write("# matrix size (face num) =" + faceNum + "\n");

        for (int f0 = 0; f0 < faceNum; f0++) {
            for (int f1 = 0; f1 < faceNum; f1++) {
                bw.write("" + doc.overlapRelation[f0][f1] + " ");
            }
            bw.write("\n");
        }

        bw.close();
        
        return true;
    }
}
