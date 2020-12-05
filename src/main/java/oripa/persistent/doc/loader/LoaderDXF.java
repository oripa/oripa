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
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class LoaderDXF implements DocLoader {

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
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				if (token == StreamTokenizer.TT_WORD && st.sval.equals("LINE")) {
					line = new OriLine();
					System.out.println("new Line");

					while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
						if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("0")) {
							lines.add(line);
							break;
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("62")) {
							st.nextToken();
							int color = Integer.parseInt(st.sval);
							System.out.println("color = " + color);
							if (color == 1 || (9 < color && color < 40)) {
								// Reds are mountains
								line.setType(OriLine.Type.MOUNTAIN);
							} else if (color == 2 || color == 5
									|| (139 < color && color < 200)) {
								// Blues are valleys
								line.setType(OriLine.Type.VALLEY);
							} else if (color == 3
									|| (59 < color && color < 130)) {
								// greens are cuts
								line.setType(OriLine.Type.CUT);
							} else {
								line.setType(OriLine.Type.AUX);
							}
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("10")) {
							st.nextToken();
							line.p0.x = Double.parseDouble(st.sval);
							minV.x = Math.min(line.p0.x, minV.x);
							maxV.x = Math.max(line.p0.x, maxV.x);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("20")) {
							st.nextToken();
							line.p0.y = Double.parseDouble(st.sval);
							minV.y = Math.min(line.p0.y, minV.y);
							maxV.y = Math.max(line.p0.y, maxV.y);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("11")) {
							st.nextToken();
							line.p1.x = Double.parseDouble(st.sval);
							minV.x = Math.min(line.p1.x, minV.x);
							maxV.x = Math.max(line.p1.x, maxV.x);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("21")) {
							st.nextToken();
							line.p1.y = Double.parseDouble(st.sval);
							minV.y = Math.min(line.p1.y, minV.y);
							maxV.y = Math.max(line.p1.y, maxV.y);

							System.out.println("line " + line.p0 + ", "
									+ line.p1);

							if (GeomUtil.distance(line.p0, line.p1) < 0.001) {
								System.out.println("########### NULL EDGE");
								lines.remove(line);
							}

						} else {
							System.out.println("skip" + st.sval);
							st.nextToken();
						}
					}
				}
			}

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		if (lines.isEmpty()) {
			return null;
		}

		final double size = 400;
		Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0,
				(minV.y + maxV.y) / 2.0);
		double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
		// size normalization
		for (OriLine line : lines) {
			line.p0.x = (line.p0.x - center.x) / bboxSize * size;
			line.p0.y = (line.p0.y - center.y) / bboxSize * size;
			line.p1.x = (line.p1.x - center.x) / bboxSize * size;
			line.p1.y = (line.p1.y - center.y) / bboxSize * size;
		}

		ArrayList<OriLine> delLines = new ArrayList<>();
		int lineNum = lines.size();

		for (int i = 0; i < lineNum; i++) {
			for (int j = i + 1; j < lineNum; j++) {
				OriLine l0 = lines.get(i);
				OriLine l1 = lines.get(j);

				if ((GeomUtil.distance(l0.p0, l1.p0) < 0.01 && GeomUtil
						.distance(l0.p1, l1.p1) < 0.01)
						|| (GeomUtil.distance(l0.p1, l1.p0) < 0.01 && GeomUtil
								.distance(l0.p0, l1.p1) < 0.01)) {

					delLines.add(l0);
				}
			}
		}

		for (OriLine delLine : delLines) {
			lines.remove(delLine);
		}

		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePatternInterface creasePattern = factory
				.createCreasePattern(lines);
		Doc doc = new Doc();
		doc.setCreasePattern(creasePattern);

		return doc;
	}

}
