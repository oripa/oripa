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
package oripa.persistence.entity.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.geom.RectangleDomain;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.Loader;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.persistence.foldformat.FoldedModelElementConverter;
import oripa.persistence.foldformat.FoldedModelFOLDFormat;
import oripa.persistence.foldformat.FrameClass;

/**
 * Does not restore all of data but enough for exporting SVG.
 *
 * @author OUCHI Koji
 *
 */
public class FoldedModelLoaderFOLD implements Loader<FoldedModelEntity> {

	@Override
	public Optional<FoldedModelEntity> load(final String filePath)
			throws FileVersionError, IOException, WrongDataFormatException {
		var gson = new Gson();
		FoldedModelFOLDFormat foldFormat;

		try {
			foldFormat = gson.fromJson(
					Files.readString(Paths.get(filePath)),
					FoldedModelFOLDFormat.class);
		} catch (JsonSyntaxException e) {
			throw new WrongDataFormatException(
					"The file does not follow JSON style."
							+ " Note that FOLD format is based on JSON.",
					e);
		}

		if (!foldFormat.frameClassesContains(FrameClass.FOLDED_FORM)) {
			throw new WrongDataFormatException("frame_classes does not contain " + FrameClass.FOLDED_FORM + ".");
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

		var converter = new FoldedModelElementConverter();

		var vertices = converter.fromVerticesCoords(foldFormat.getVerticesCoords());
		var edges = converter.fromEdges(foldFormat.getEdgesVertices(), foldFormat.getEdgesAssignment(), vertices);
		var faces = converter.fromFacesVertices(foldFormat.getFacesVertices(), vertices);

		var precreases = foldFormat.getFacesPrecreases();
		if (precreases != null) {
			converter.restorePrecreases(precreases, edges, faces);
		}

		var positions = vertices.stream().map(OriVertex::getPosition).toList();
		var domain = RectangleDomain.createFromPoints(positions);

		// tentative value
		var origamiModel = new OrigamiModel(domain.maxWidthHeight() * 1.1);

		origamiModel.setVertices(vertices);
		origamiModel.setEdges(edges);
		origamiModel.setFaces(faces);

		var overlapRelations = new ArrayList<OverlapRelation>();
		var frameCount = foldFormat.getFileFrames() == null ? 1 : foldFormat.getFileFrames().size() + 1;
		for (int i = 0; i < frameCount; i++) {
			var frame = foldFormat.getFrame(i);
			if (frame.getFaceOrders() != null) {
				overlapRelations.add(converter.fromFaceOrders(frame.getFaceOrders(), faces));
			}
		}

		return Optional.of(new FoldedModelEntity(new FoldedModel(origamiModel, overlapRelations, List.of())));
	}

}
