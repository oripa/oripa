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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.persistence.entity.exporter.FoldedModelEntity;
import oripa.persistence.entity.exporter.FoldedModelExporterSVG;
import oripa.persistence.entity.loader.FoldedModelLoaderFOLD;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelImageExporter {
	private static final Logger logger = LoggerFactory.getLogger(FoldedModelImageExporter.class);

	public void export(final String inputFilePath, final int index, final boolean reverse,
			final String outputFilePath) {

		if (!outputFilePath.endsWith(".svg")) {
			throw new IllegalArgumentException("Output format is not supported. acceptable format: svg");
		}

		var inputFileLoader = new FoldedModelLoaderFOLD();
		var outputFileExporter = new FoldedModelExporterSVG(reverse);

		try {
			var foldedModel = inputFileLoader.load(inputFilePath);
			var entity = new FoldedModelEntity(foldedModel, index);

			outputFileExporter.export(entity, outputFilePath);

		} catch (Exception e) {
			logger.error("image error", e);
		}
	}
}
