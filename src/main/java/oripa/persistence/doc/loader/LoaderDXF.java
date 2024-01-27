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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.doc.Doc;
import oripa.geom.GeomUtil;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.value.OriLine;

public class LoaderDXF implements DocLoader {

	private static Logger logger = LoggerFactory.getLogger(LoaderDXF.class);

	@Override
	public Optional<Doc> load(final String filePath) throws WrongDataFormatException {
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

			LineDto dto;
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				if (token == StreamTokenizer.TT_WORD && st.sval.equals("LINE")) {
					dto = new LineDto();
					System.out.println("new Line");

					while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
						if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("0")) {
							dtos.add(dto);
							break;
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("62")) {
							st.nextToken();
							int color = Integer.parseInt(st.sval);
							System.out.println("color = " + color);
							if (color == 1 || (9 < color && color < 40)) {
								// Reds are mountains
								dto.type = OriLine.Type.MOUNTAIN;
							} else if (color == 2 || color == 5
									|| (139 < color && color < 200)) {
								// Blues are valleys
								dto.type = OriLine.Type.VALLEY;
							} else if (color == 3
									|| (59 < color && color < 130)) {
								// greens are cuts
								dto.type = OriLine.Type.CUT;
							} else {
								dto.type = OriLine.Type.AUX;
							}
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("10")) {
							st.nextToken();
							dto.p0x = Double.parseDouble(st.sval);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("20")) {
							st.nextToken();
							dto.p0y = Double.parseDouble(st.sval);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("11")) {
							st.nextToken();
							dto.p1x = Double.parseDouble(st.sval);
						} else if (token == StreamTokenizer.TT_WORD
								&& st.sval.equals("21")) {
							st.nextToken();
							dto.p1y = Double.parseDouble(st.sval);

							if (GeomUtil.distance(dto.getP0(), dto.getP1()) < 0.001) {
								dtos.remove(dto);
							}

						} else {
							System.out.println("skip" + st.sval);
							st.nextToken();
						}
					}
				}
			}

		} catch (IOException | NumberFormatException e) {
			logger.error("parse error", e);
			throw new WrongDataFormatException("parse error", e);
		}

		if (dtos.isEmpty()) {
			return Optional.empty();
		}

		var doc = new Doc();
		doc.setCreasePattern(new LineDtoConverter().convert(dtos));

		return Optional.of(doc);
	}

}
