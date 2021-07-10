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

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePattern;
import oripa.value.OriLine;

public class LoaderCP implements DocLoader {

	@Override
	public Doc load(final String filePath) {
		var lines = new ArrayList<OriLine>();

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
				line = new OriLine();
				lines.add(line);

				try {
					var lineType = OriLine.Type.fromInt(Integer.parseInt(st.sval));
					switch (lineType) {
					case CUT:
					case MOUNTAIN:
					case VALLEY:
						line.setType(lineType);
						break;
					default:
						line.setType(OriLine.Type.AUX);
						break;
					}
				} catch (IllegalArgumentException e) {
					line.setType(OriLine.Type.AUX);
				}
//				System.out.println("line type " + line.getType());

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

//		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
//		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

//		for (OriLine line : lines) {
//			minV.x = Math.min(minV.x, line.p0.x);
//			minV.x = Math.min(minV.x, line.p1.x);
//			minV.y = Math.min(minV.y, line.p0.y);
//			minV.y = Math.min(minV.y, line.p1.y);
//
//			maxV.x = Math.max(maxV.x, line.p0.x);
//			maxV.x = Math.max(maxV.x, line.p1.x);
//			maxV.y = Math.max(maxV.y, line.p0.y);
//			maxV.y = Math.max(maxV.y, line.p1.y);
//		}
//
//		// size normalization
//		double size = 400;
//		Vector2d center = new Vector2d((minV.x + maxV.x) / 2.0,
//				(minV.y + maxV.y) / 2.0);
//		double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
//		for (OriLine line : lines) {
//			line.p0.x = (line.p0.x - center.x) / bboxSize * size;
//			line.p0.y = (line.p0.y - center.y) / bboxSize * size;
//			line.p1.x = (line.p1.x - center.x) / bboxSize * size;
//			line.p1.y = (line.p1.y - center.y) / bboxSize * size;
//		}

		// for (OriLine l : lines) {
		// doc.addLine(l);
		// System.out.println("Linenum=" + creasePattern.size());
		// }
		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePattern creasePattern = factory
				.createCreasePattern(lines);
		Doc doc = new Doc();
		doc.setCreasePattern(creasePattern);
		return doc;

	}
}
