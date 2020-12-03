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
package oripa.application.main;

import oripa.ORIPA;
import oripa.domain.paint.PaintContextInterface;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.file.InitDataBuilder;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;

/**
 * @author OUCHI Koji
 *
 */
public class IniFileAccess {
	private static IniFileAccess instance = null;

	private IniFileAccess() {

	}

	public static IniFileAccess get() {
		if (instance == null) {
			instance = new IniFileAccess();
		}

		return instance;
	}

	public void save(final FileHistory fileHistory, final PaintContextInterface paintContext)
			throws IllegalStateException {
		var builder = new InitDataBuilder();

		var ini = builder.setLastUsedFile(fileHistory.getLastPath())
				.setMRUFiles(fileHistory.getHistory())
				.setZeroLineWidth(paintContext.isZeroLineWidth())
				.setMVLineVisible(paintContext.isMVLineVisible())
				.setAuxLineVisible(paintContext.isAuxLineVisible())
				.setVertexVisible(paintContext.isVertexVisible())
				.get();

		var writer = new InitDataFileWriter();
		writer.write(ini, ORIPA.iniFilePath);
	}

	public InitData load() {
		var reader = new InitDataFileReader();
		var ini = reader.read(ORIPA.iniFilePath);

		return ini;
	}
}
