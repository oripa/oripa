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
package oripa.persistence.entity.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import com.google.gson.GsonBuilder;

import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.filetool.Exporter;
import oripa.persistence.foldformat.FoldedModelElementConverter;
import oripa.persistence.foldformat.Frame;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelAllExporterFOLD implements Exporter<FoldedModelEntity> {

	@Override
	public boolean export(final FoldedModelEntity entity, final String filePath, final Object configObj)
			throws IOException, IllegalArgumentException {
		var elementConverter = new FoldedModelElementConverter();
		var factory = new FoldedModelFOLDFactory(elementConverter);

		var origamiModel = entity.getOrigamiModel();
		var overlapRelations = entity.getOverlapRelations();

		var foldFormat = factory.createWithoutFaceOrders(origamiModel);

		var frames = new ArrayList<Frame>();
		overlapRelations.forEach(relation -> {
			var frame = new Frame();
			frame.setFrameInherit(true);
			frame.setFrameParent(0);
			frame.setFaceOrders(elementConverter.toFaceOrders(origamiModel, relation));
			frames.add(frame);
		});

		foldFormat.setFileFrames(frames);

		try (var writer = Files.newBufferedWriter(Path.of(filePath))) {
			var gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(foldFormat, writer);
			writer.flush();
		}

		return true;
	}

}
