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

package oripa.doc.loader;

import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import javax.vecmath.Vector2d;

import oripa.doc.Doc;
import oripa.geom.OriLine;

public class LoaderCP implements Loader{

    public ArrayList<OriLine> lines = new ArrayList<>();

    public Doc load(String filePath) {
        Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
        Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

        try {
            Reader r = new FileReader(filePath);
            StreamTokenizer st = new StreamTokenizer(r);
            st.resetSyntax();
            st.wordChars('0', '9');
            st.wordChars('.', '.');
            st.wordChars('0', '\u00FF');
            st.wordChars('-', '-');
            st.whitespaceChars(' ', ' ');
            st.whitespaceChars('\t', '\t');
            st.whitespaceChars('\n', '\n');
            st.whitespaceChars('\r', '\r');

            int token;

            OriLine line;
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                line = new OriLine();
                lines.add(line);

                line.typeVal = Integer.parseInt(st.sval);// == 1 ? OriLine.TYPE_RIDGE : OriLine.TYPE_VALLEY;
                System.out.println("line type " + line.typeVal);

                token = st.nextToken();
                line.p0.x = Double.parseDouble(st.sval);

                token = st.nextToken();
                line.p0.y = Double.parseDouble(st.sval);


                token = st.nextToken();
                line.p1.x = Double.parseDouble(st.sval);

                token = st.nextToken();
                line.p1.y = Double.parseDouble(st.sval);

            }

            System.out.println("end");

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (OriLine line : lines) {
            minV.x = Math.min(minV.x, line.p0.x);
            minV.x = Math.min(minV.x, line.p1.x);
            minV.y = Math.min(minV.y, line.p0.y);
            minV.y = Math.min(minV.y, line.p1.y);

            maxV.x = Math.max(maxV.x, line.p0.x);
            maxV.x = Math.max(maxV.x, line.p1.x);
            maxV.y = Math.max(maxV.y, line.p0.y);
            maxV.y = Math.max(maxV.y, line.p1.y);
        }

        // size normalization
        double size = 400;
        Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0, (minV.y + maxV.y) / 2.0);
        double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
        for (OriLine line : lines) {
            line.p0.x = (line.p0.x - center.x) / bboxSize * size;
            line.p0.y = (line.p0.y - center.y) / bboxSize * size;
            line.p1.x = (line.p1.x - center.x) / bboxSize * size;
            line.p1.y = (line.p1.y - center.y) / bboxSize * size;
        }

        Doc doc = new Doc(400);
        doc.creasePattern.clear();

        for (OriLine l : lines) {
            doc.addLine(l);
            System.out.println("Linenum=" + doc.creasePattern.size());
        }
        return doc;

    }
}
