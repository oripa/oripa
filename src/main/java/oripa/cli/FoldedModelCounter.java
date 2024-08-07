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

import oripa.persistence.entity.loader.FoldedModelLoaderFOLD;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelCounter {
	private static final Logger logger = LoggerFactory.getLogger(FoldedModelCounter.class);

	public int count(final String inputFilePath) {
		var inputFileLoader = new FoldedModelLoaderFOLD();
		try {
			var inputModelEntityOpt = inputFileLoader.load(inputFilePath);

			return inputModelEntityOpt.orElseThrow().getOverlapRelations().size();
		} catch (Exception e) {
			logger.error("count error.", e);
		}
		return -1;
	}

}
