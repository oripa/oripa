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
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Optional;

import oripa.doc.Doc;
import oripa.value.OriLine;

public class LoaderPDF implements DocLoader {

	@Override
	public Optional<Doc> load(final String filePath) {
		var dtos = new ArrayList<LineDto>();

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

			LineDto line;
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
					line = new LineDto();
					dtos.add(line);
					System.out.println("new Line " + st.sval);
					line.type = Integer.parseInt(st.sval) == 1 ? OriLine.Type.MOUNTAIN
							: OriLine.Type.VALLEY;

					System.out.println("line type " + line.type.toInt());
					token = st.nextToken(); // eat "w"

					token = st.nextToken();
					line.p0x = Double.parseDouble(st.sval);

					token = st.nextToken();
					line.p0y = Double.parseDouble(st.sval);

					token = st.nextToken(); // eat "m"

					token = st.nextToken();
					line.p1x = Double.parseDouble(st.sval);

					token = st.nextToken();
					line.p1y = Double.parseDouble(st.sval);

					token = st.nextToken(); // eat "l"
					token = st.nextToken(); // eat "S"
				}
			}

			System.out.println("end");

		} catch (Exception e) {
			e.printStackTrace();
		}

		var doc = new Doc(new LineDtoConverter().convert(dtos));

		return Optional.of(doc);

	}
}
