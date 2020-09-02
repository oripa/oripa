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
package oripa.persistent.doc.exporter;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

import oripa.persistent.doc.Doc;
import oripa.persistent.foldformat.CreasePatternElementConverter;
import oripa.persistent.foldformat.CreasePatternFOLDFormat;
import oripa.persistent.foldformat.PointsMerger;

/**
 * @author OUCHI Koji
 *
 */
public class ExporterFOLD implements DocExporter {
	private static final Logger logger = LoggerFactory.getLogger(ExporterFOLD.class);

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.Exporter#export(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean export(final Doc doc, final String filePath)
			throws IOException, IllegalArgumentException {
		logger.info("start exporting FOLD file.");

		var pointsMerger = new PointsMerger();
		var creasePattern = pointsMerger.mergeClosePoints(doc.getCreasePattern());

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

		foldFormat.setFacesVertices(converter.toFacesVertices(creasePattern));
		logger.info("size of faces_vertices: " + foldFormat.getFacesVertices().size());

		try (var writer = new FileWriter(filePath)) {
			var gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(foldFormat, writer);
			writer.flush();
		}

		logger.info("end exporting FOLD file.");

		return true;
	}
}
