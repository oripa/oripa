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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

import oripa.geom.GeomUtil;
import oripa.persistent.doc.Doc;
import oripa.persistent.foldformat.CreasePatternElementConverter;
import oripa.persistent.foldformat.CreasePatternFOLDFormat;
import oripa.value.OriLine;
import oripa.value.OriPoint;

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

		var creasePattern = cleanUp(doc.getCreasePattern());
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

	private void substituteToP1IfClose(final OriPoint p0, final OriPoint p1) {
		if (GeomUtil.Distance(p0, p1) < 1e-4) {
			p1.x = p0.x;
			p1.y = p0.y;
		}
	}

	private Collection<OriLine> cleanUp(final Collection<OriLine> lines) {

		List<OriLine> cleaned = new ArrayList<OriLine>();
		for (var line : lines) {
			cleaned.add(new OriLine(line.p0, line.p1, line.getType()));
		}

		for (int i = 0; i < cleaned.size(); i++) {
			var p00 = cleaned.get(i).p0;
			var p01 = cleaned.get(i).p1;
			for (int j = i + 1; j < cleaned.size(); j++) {
				var p10 = cleaned.get(j).p0;
				var p11 = cleaned.get(j).p1;

				substituteToP1IfClose(p00, p10);
				substituteToP1IfClose(p00, p11);

				substituteToP1IfClose(p01, p10);
				substituteToP1IfClose(p01, p11);
			}
		}

		cleaned = cleaned.stream()
				.filter(line -> GeomUtil.Distance(line.p0, line.p1) > 1e-4)
				.collect(Collectors.toList());

		return cleaned;
	}

}
