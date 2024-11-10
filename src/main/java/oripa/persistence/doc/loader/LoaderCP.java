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

package oripa.persistence.doc.loader;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Optional;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.value.OriLine;

public class LoaderCP implements DocLoader {

	@Override
	public Optional<Doc> load(final String filePath) throws IOException, WrongDataFormatException {
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

			OriLine line;
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				OriLine.Type lineType;
				try {
					lineType = OriLine.Type.fromInt(Integer.parseInt(st.sval));
					switch (lineType) {
					case CUT, MOUNTAIN, VALLEY:
						break;
					default:
						lineType = OriLine.Type.AUX;
						break;
					}
				} catch (IllegalArgumentException e) {
					lineType = OriLine.Type.AUX;
				}

				st.nextToken();
				var p0x = Double.parseDouble(st.sval);

				st.nextToken();
				var p0y = Double.parseDouble(st.sval);

				st.nextToken();
				var p1x = Double.parseDouble(st.sval);

				st.nextToken();
				var p1y = Double.parseDouble(st.sval);

				line = new OriLine(p0x, p0y, p1x, p1y, lineType);
				lines.add(line);
			}

			System.out.println("end");

		} catch (NumberFormatException e) {
			throw new WrongDataFormatException("Parse error.", e);
		}

		// for (OriLine l : lines) {
		// doc.addLine(l);
		// System.out.println("Linenum=" + creasePattern.size());
		// }
		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePattern creasePattern = factory
				.createCreasePattern(lines);
		var doc = new Doc(creasePattern);
		return Optional.of(doc);

	}
}
