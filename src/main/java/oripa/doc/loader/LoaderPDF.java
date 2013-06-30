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
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;

public class LoaderPDF implements Loader {
	
    public static ArrayList<OriLine> lines = new ArrayList<>();

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
            int status = 0;
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                if (token == StreamTokenizer.TT_WORD && st.sval.equals("stream")) {
                    status = 1;
                    continue;
                }
                if (token == StreamTokenizer.TT_WORD && st.sval.equals("endstream")) {
                    status = 0;
                    continue;
                }
                if (status == 1) {
                    line = new OriLine();
                    lines.add(line);
                    System.out.println("new Line " + st.sval);
                    line.typeVal = Integer.parseInt(st.sval) == 1 ? OriLine.TYPE_RIDGE : OriLine.TYPE_VALLEY;

                    System.out.println("line type " + line.typeVal);
                    token = st.nextToken(); // eat "w"

                    token = st.nextToken();
                    line.p0.x = Double.parseDouble(st.sval);

                    token = st.nextToken();
                    line.p0.y = Double.parseDouble(st.sval);

                    token = st.nextToken(); // eat "m"

                    token = st.nextToken();
                    line.p1.x = Double.parseDouble(st.sval);

                    token = st.nextToken();
                    line.p1.y = Double.parseDouble(st.sval);

                    token = st.nextToken(); // eat "l"
                    token = st.nextToken(); // eat "S"
                }
            }

            System.out.println("end");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Doc doc = new Doc(400);
        doc.creasePattern.clear();

        for (OriLine l : lines) {
            doc.creasePattern.add(l);
        }


        for (OriLine line : doc.creasePattern) {
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
        doc.size = size;
        Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0, (minV.y + maxV.y) / 2.0);
        double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
        for (OriLine line : doc.creasePattern) {
            line.p0.x = (line.p0.x - center.x) / bboxSize * size;
            line.p0.y = (line.p0.y - center.y) / bboxSize * size;
            line.p1.x = (line.p1.x - center.x) / bboxSize * size;
            line.p1.y = (line.p1.y - center.y) / bboxSize * size;
        }


        // Delete duplicate lines

        ArrayList<OriLine> delLines = new ArrayList<>();
        int lineNum = doc.creasePattern.size();

        OriLine[] lines = new OriLine[lineNum];
        doc.creasePattern.toArray(lines);
        
        for (int i = 0; i < lineNum; i++) {
            for (int j = i + 1; j < lineNum; j++) {
                OriLine l0 = lines[i];
                OriLine l1 = lines[j];

                if ((GeomUtil.Distance(l0.p0, l1.p0) < 0.01 && GeomUtil.Distance(l0.p1, l1.p1) < 0.01)
                        || (GeomUtil.Distance(l0.p1, l1.p0) < 0.01 && GeomUtil.Distance(l0.p0, l1.p1) < 0.01)) {

                    delLines.add(l0);
                }
            }
        }

        for (OriLine delLine : delLines) {
            doc.creasePattern.remove(delLine);
        }


        return doc;

    }
}
