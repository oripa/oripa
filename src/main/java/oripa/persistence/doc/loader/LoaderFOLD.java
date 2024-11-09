/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.docprop.Property;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.persistence.foldformat.CreasePatternElementConverter;
import oripa.persistence.foldformat.CreasePatternFOLDFormat;
import oripa.persistence.foldformat.FrameClass;

/**
 * @author OUCHI Koji
 *
 */
public class LoaderFOLD implements DocLoader {

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.Loader#load(java.lang.String)
	 */
	@Override
	public Optional<Doc> load(final String filePath) throws IOException, WrongDataFormatException {

		var gson = new Gson();
		CreasePatternFOLDFormat foldFormat;

		try {
			foldFormat = gson.fromJson(
					Files.readString(Paths.get(filePath)),
					CreasePatternFOLDFormat.class);
		} catch (JsonSyntaxException e) {
			throw new WrongDataFormatException(
					"The file does not follow JSON style."
							+ " Note that FOLD format is based on JSON.",
					e);
		}

		if (!foldFormat.frameClassesContains(FrameClass.CREASE_PATTERN)) {
			throw new WrongDataFormatException(
					"frame_classes does not contain " + FrameClass.CREASE_PATTERN + ".");
		}

		if (foldFormat.getEdgesVertices() == null) {
			throw new WrongDataFormatException("edges_vertices property is needed in the file.");
		}
		if (foldFormat.getEdgesAssignment() == null) {
			throw new WrongDataFormatException("edges_assignment property is needed in the file.");
		}
		if (foldFormat.getVerticesCoords() == null) {
			throw new WrongDataFormatException("vertices_coords property is needed in the file.");
		}

		var converter = new CreasePatternElementConverter();
		var lines = converter.fromEdges(
				foldFormat.getEdgesVertices(),
				foldFormat.getEdgesAssignment(),
				foldFormat.getVerticesCoords());

		var factory = new CreasePatternFactory();
		var cp = factory.createCreasePattern(lines);

		var property = new Property()
				.setEditorName(foldFormat.getFileAuthor())
				.setTitle(foldFormat.getFrameTitle())
				.setMemo(foldFormat.getFrameDescription());

		var doc = new Doc(cp, property, filePath);

		return Optional.of(doc);
	}

}
