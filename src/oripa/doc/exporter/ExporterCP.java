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
import oripa.geom.OriLine;

public class ExporterCP implements Exporter{
    public boolean export(Doc doc, String filepath) throws Exception {
        FileWriter fw = new FileWriter(filepath);
        BufferedWriter bw = new BufferedWriter(fw);

        for(OriLine line : ORIPA.doc.creasePattern) {
            if(line.typeVal == OriLine.TYPE_NONE) continue;
            bw.write(line.typeVal + " " + line.p0.x + " " + line.p0.y + " " + line.p1.x + " " + line.p1.y + "\n");
        }
        bw.close();
        
        return true;
    } 
}
