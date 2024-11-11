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

import oripa.application.FileAccessService;
import oripa.persistence.dao.FileDAO;
import oripa.persistence.doc.CreasePatternFileTypeKey;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileAccessSupportSelector;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;

/**
 * @author OUCHI Koji
 *
 */
public class CreasePatternFileConverter {
	private static final Logger logger = LoggerFactory.getLogger(CreasePatternFileConverter.class);

	public void convert(final String inputFilePath, final String outputFilePath, final double eps) {
		var creasePatternFileAccess = new FileAccessService<Doc>(new FileDAO<>(new DocFileAccessSupportSelector()));

		try {
			creasePatternFileAccess.setConfigToSavingAction(CreasePatternFileTypeKey.FOLD,
					() -> {
						var config = new CreasePatternFOLDConfig();
						config.setEps(eps);
						return config;
					});
			var input = creasePatternFileAccess.loadFile(inputFilePath);
			creasePatternFileAccess.saveFile(input.get(), outputFilePath);
		} catch (Exception e) {
			logger.error("conversion error", e);
		}
	}
}
