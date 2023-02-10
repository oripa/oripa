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
package oripa.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DataFileAccess;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.geom.GeomUtil;
import oripa.persistence.doc.DocDAO;
import oripa.persistence.doc.DocFileAccessSupportSelector;
import oripa.persistence.entity.exporter.FoldedModelEntity;
import oripa.persistence.entity.exporter.FoldedModelExporterFOLD;

/**
 * @author OUCHI Koji
 *
 */
public class CommandLineFolder {
	private static final Logger logger = LoggerFactory.getLogger(CommandLineFolder.class);

	public void fold(final String inputFilePath, final boolean split, final String outputFilePath) {

		if (!outputFilePath.endsWith(".fold")) {
			throw new IllegalArgumentException("Output format is not supported. acceptable format: fold");
		}

		var creasePatternFileAccess = new DataFileAccess(new DocDAO(new DocFileAccessSupportSelector()));
		var foldedModelExporter = new FoldedModelExporterFOLD();

		try {
			var creasePattern = creasePatternFileAccess.loadFile(inputFilePath).get().getCreasePattern();
			OrigamiModelFactory modelFactory = new OrigamiModelFactory();

			var pointEps = GeomUtil.pointEps();

			List<OrigamiModel> origamiModels = modelFactory.createOrigamiModels(creasePattern, pointEps);

			if (origamiModels.size() > 1) {
				throw new IllegalArgumentException("Input should be a single model.");
			}

			var folder = new FolderFactory().create();
			var foldedModel = folder.fold(origamiModels.get(0), true);

			if (split) {
				var digitLength = Integer.toString(foldedModel.getFoldablePatternCount()).length();
				for (int i = 0; i < foldedModel.getFoldablePatternCount(); i++) {
					var paddedNumber = "0".repeat(digitLength - Integer.toString(i).length()) + i;
					var outputName = outputFilePath.replaceFirst("[.]fold$", "." + paddedNumber + ".fold");
					foldedModelExporter.export(
							new FoldedModelEntity(foldedModel, i), outputName);
				}
			} else {
				foldedModelExporter.export(
						new FoldedModelEntity(foldedModel), outputFilePath);
			}

		} catch (Exception e) {
			logger.error("folding error", e);
		}
	}
}
