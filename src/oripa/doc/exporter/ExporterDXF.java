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
import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriLine;

public class ExporterDXF implements Exporter{

    public static void exportModel(Doc doc, String filepath) throws Exception {
        double scale = 6.0 / doc.size; // 6.0 inch width
        double center = 4.0; // inch
        FileWriter fw = new FileWriter(filepath);
        BufferedWriter bw = new BufferedWriter(fw);

        // Align the center of the model, combine scales
        Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
        Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
        Vector2d modelCenter = new Vector2d();
        for (OriFace face : ORIPA.doc.faces) {
            for (OriHalfedge he : face.halfedges) {
                maxV.x = Math.max(maxV.x, he.vertex.p.x);
                maxV.y = Math.max(maxV.y, he.vertex.p.y);
                minV.x = Math.min(minV.x, he.vertex.p.x);
                minV.y = Math.min(minV.y, he.vertex.p.y);
            }
        }

        modelCenter.x = (maxV.x + minV.x) / 2;
        modelCenter.y = (maxV.y + minV.y) / 2;

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

        for (OriFace face : ORIPA.doc.sortedFaces) {
            for (OriHalfedge he : face.halfedges) {

                bw.write("  0\n");
                bw.write("LINE\n");
                bw.write("  8\n");
                bw.write("_0-0_\n"); // Layer name
                bw.write("  6\n");
                bw.write("CONTINUOUS\n");  // Line type
                bw.write(" 62\n"); // 1＝red 2＝yellow 3＝green 4＝cyan 5＝blue 6＝magenta 7＝white
                int colorNumber = 250;

                bw.write("" + colorNumber + "\n");
                bw.write(" 10\n");
                bw.write("" + ((he.positionForDisplay.x - modelCenter.x) * scale + center) + "\n");
                bw.write(" 20\n");
                bw.write("" + (-(he.positionForDisplay.y - modelCenter.y) * scale + center) + "\n");
                bw.write(" 11\n");
                bw.write("" + ((he.next.positionForDisplay.x - modelCenter.x) * scale + center) + "\n");
                bw.write(" 21\n");
                bw.write("" + (-(he.next.positionForDisplay.y - modelCenter.y) * scale + center) + "\n");
            }
        }

        bw.write("  0\n");
        bw.write("ENDSEC\n");
        bw.write("  0\n");
        bw.write("EOF\n");

        bw.close();
    }

    public boolean export(Doc doc, String filepath) throws Exception {
        double scale = 6.0 / doc.size; // 6.0 inch width
        double center = 4.0; // inch
        FileWriter fw = new FileWriter(filepath);
        BufferedWriter bw = new BufferedWriter(fw);

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

        for (OriLine line : doc.creasePattern) {
            bw.write("  0\n");
            bw.write("LINE\n");
            bw.write("  8\n");
            String layerName = "noname";
            switch (line.typeVal) {
                case OriLine.TYPE_CUT:
                    layerName = "CutLine";
                    break;
                case OriLine.TYPE_RIDGE:
                    layerName = "MountainLine";
                    break;
                case OriLine.TYPE_VALLEY:
                    layerName = "ValleyLine";
            }
            bw.write(layerName + "\n");  // Layer name
            bw.write("  6\n");
            bw.write("CONTINUOUS\n");  // Line type
            bw.write(" 62\n"); // 1＝red 2＝yellow 3＝green 4＝cyan 5＝blue 6＝magenta 7＝white
            int colorNumber = 0;
            switch (line.typeVal) {
                case OriLine.TYPE_CUT:
                    colorNumber = 250; // 51,51,51
                    break;
                case OriLine.TYPE_RIDGE:
                    colorNumber = 5; // blue
                    break;
                case OriLine.TYPE_VALLEY:
                    colorNumber = 1; // red
            }

            bw.write("" + colorNumber + "\n");
            bw.write(" 10\n");
            bw.write("" + (line.p0.x * scale + center) + "\n");
            bw.write(" 20\n");
            bw.write("" + ((doc.size / 2 - line.p0.y) * scale + center) + "\n");
            bw.write(" 11\n");
            bw.write("" + (line.p1.x * scale + center) + "\n");
            bw.write(" 21\n");
            bw.write("" + ((doc.size / 2 - line.p1.y) * scale + center) + "\n");
        }

        bw.write("  0\n");
        bw.write("ENDSEC\n");
        bw.write("  0\n");
        bw.write("EOF\n");

        bw.close();
        
        return true;
    }
}
