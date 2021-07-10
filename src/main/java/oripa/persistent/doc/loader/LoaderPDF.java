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

package oripa.persistent.doc.loader;

import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class LoaderPDF implements DocLoader {

	@Override
	public Doc load(final String filePath) {
		var lines = new ArrayList<OriLine>();

		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

		try (var r = new FileReader(filePath)) {
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
					line.setType(Integer.parseInt(st.sval) == 1 ? OriLine.Type.MOUNTAIN
							: OriLine.Type.VALLEY);

					System.out.println("line type " + line.getType().toInt());
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

		double size = 400;
		Doc doc = new Doc(size);
		CreasePattern creasePattern = doc.getCreasePattern();
		creasePattern.clear();

		for (OriLine l : lines) {
			creasePattern.add(l);
		}

		for (OriLine line : creasePattern) {
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

		Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0, (minV.y + maxV.y) / 2.0);
		double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
		for (OriLine line : creasePattern) {
			line.p0.x = (line.p0.x - center.x) / bboxSize * size;
			line.p0.y = (line.p0.y - center.y) / bboxSize * size;
			line.p1.x = (line.p1.x - center.x) / bboxSize * size;
			line.p1.y = (line.p1.y - center.y) / bboxSize * size;
		}

		// Delete duplicate lines

		ArrayList<OriLine> delLines = new ArrayList<>();
		int lineNum = creasePattern.size();

		OriLine[] linesArray = new OriLine[lineNum];
		creasePattern.toArray(linesArray);

		for (int i = 0; i < lineNum; i++) {
			for (int j = i + 1; j < lineNum; j++) {
				OriLine l0 = linesArray[i];
				OriLine l1 = linesArray[j];

				if ((GeomUtil.distance(l0.p0, l1.p0) < 0.01
						&& GeomUtil.distance(l0.p1, l1.p1) < 0.01)
						|| (GeomUtil.distance(l0.p1, l1.p0) < 0.01
								&& GeomUtil.distance(l0.p0, l1.p1) < 0.01)) {

					delLines.add(l0);
				}
			}
		}

		for (OriLine delLine : delLines) {
			creasePattern.remove(delLine);
		}

		return doc;

	}
}
