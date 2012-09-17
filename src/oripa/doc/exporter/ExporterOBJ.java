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

import oripa.doc.Doc;
import oripa.geom.OriEdge;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriVertex;

public class ExporterOBJ implements Exporter{

    public boolean export(Doc doc, String filepath) throws Exception {
        FileWriter fw = new FileWriter(filepath);
        BufferedWriter bw = new BufferedWriter(fw);

        // Align the center of the model, combine scales
        bw.write("# Created by ORIPA\n");
        bw.write("\n");

        int id = 1;
        for (OriVertex vertex : doc.vertices) {
            bw.write("v " + vertex.preP.x + " " + vertex.preP.y + " 0.0\n");
            vertex.tmpInt = id;
            id++;
        }

        for (OriFace face : doc.faces) {
            bw.write("f");
            for (OriHalfedge he : face.halfedges) {
                bw.write(" " + he.vertex.tmpInt);
            }
            bw.write("\n");
        }

        for (OriEdge edge : doc.edges) {
            bw.write("#e " + edge.sv.tmpInt + " " + edge.ev.tmpInt + " " + edge.type + " 180\n");
        }
        bw.close();
        
        return true;
    }
}
