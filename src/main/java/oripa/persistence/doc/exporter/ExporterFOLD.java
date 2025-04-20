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
package oripa.persistence.doc.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

import oripa.domain.cptool.LineAdder;
import oripa.domain.cptool.PointsMerger;
import oripa.persistence.doc.Doc;
import oripa.persistence.foldformat.CreasePatternElementConverter;
import oripa.persistence.foldformat.CreasePatternFOLDFormat;

/**
 * @author OUCHI Koji
 *
 */
public class ExporterFOLD implements DocExporter {
	private static final Logger logger = LoggerFactory.getLogger(ExporterFOLD.class);

	/**
	 * {@code configObj} is required.
	 *
	 * @param configObj
	 *            {@link CreasePatternFOLDConfig} instance.
	 */
	@Override
	public boolean export(final Doc doc, final String filePath, final Object configObj)
			throws IOException, IllegalArgumentException {
		logger.info("start exporting FOLD file.");

		var config = (CreasePatternFOLDConfig) configObj;

		final double pointEps = config.getEps();

		var pointsMerger = new PointsMerger(new LineAdder());
		var creasePattern = pointsMerger.mergeClosePoints(doc.getCreasePattern(), pointEps);

		var property = doc.getProperty();

		var foldFormat = new CreasePatternFOLDFormat();

		var converter = new CreasePatternElementConverter();

		foldFormat.setFileAuthor(property.getEditorName());
		foldFormat.setFrameTitle(property.getTitle());
		foldFormat.setFrameDescription(property.getMemo());

		foldFormat.setVerticesCoords(converter.toVerticesCoords(creasePattern));
		logger.info("size of vertices_coords: " + foldFormat.getVerticesCoords().size());

		foldFormat.setEdgesVertices(converter.toEdgesVertices(creasePattern));
		logger.info("size of edges_vertices: " + foldFormat.getEdgesVertices().size());

		foldFormat.setEdgesAssignment(converter.toEdgesAssignment(creasePattern));
		logger.info("size of edges_assignment: " + foldFormat.getEdgesAssignment().size());

		// Information of faces will be exported only if the crease pattern is
		// completed.
		try {
			foldFormat.setFacesVertices(converter.toFacesVertices(creasePattern));
			logger.info("size of faces_vertices: " + foldFormat.getFacesVertices().size());
		} catch (IllegalArgumentException e) {
			logger.info("Faces are not created. (eps = " + pointEps + ")", e);
		}

		try (var writer = Files.newBufferedWriter(Path.of(filePath))) {
			var gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(foldFormat, writer);
			writer.flush();
		}

		logger.info("end exporting FOLD file.");

		return true;
	}
}
