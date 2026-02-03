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

import oripa.persistence.doc.Doc;
import oripa.value.OriLine;

public class ExporterCP implements DocExporter {

    @Override
    public boolean export(final Doc doc, final String filepath, final Object configObj)
            throws IOException, IllegalArgumentException {

        if (doc.getCreasePattern().isUnassigned()) {
            throw new IllegalArgumentException("Unassigned crease pattern is not allowed.");
        }

        try (var fw = new FileWriter(filepath);
                var bw = new BufferedWriter(fw);) {

            for (OriLine line : doc.getCreasePattern()) {
                if (line.isAux()) {
                    continue;
                }
                var p0 = line.getP0();
                var p1 = line.getP1();
                bw.write(
                        line.getType().toInt() + " " + p0.getX() + " " + p0.getY() + " " + p1.getX()
                                + " " + p1.getY() + "\n");
            }
        }

        return true;
    }
}
