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

import jakarta.inject.Inject;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.file.InitDataBuilder;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.resource.Constants;

/**
 * handle {@link oripa.file.IniFile} save and load
 *
 * @author OUCHI Koji
 *
 */
public class IniFileAccess {
	private final InitDataFileReader reader;
	private final InitDataFileWriter writer;

	/**
	 *
	 * @param r
	 *            reader instance
	 * @param w
	 *            writer instance
	 */
	@Inject
	public IniFileAccess(final InitDataFileReader r, final InitDataFileWriter w) {
		reader = r;
		writer = w;
	}

	/**
	 * builds IniFile Object and saves it with writer instance
	 *
	 * @param fileHistory
	 *            to be used
	 * @param paintContext
	 *            to read UI Settings
	 * @throws IllegalStateException
	 *             in case some of the fields are not set
	 */
	public void save(final FileHistory fileHistory, final CreasePatternViewContext viewContext)
			throws IllegalStateException {
		var builder = new InitDataBuilder();

		var ini = builder.setLastUsedFile(fileHistory.getLastPath())
				.setMRUFiles(fileHistory.getHistory())
				.setZeroLineWidth(viewContext.isZeroLineWidth())
				.setMVLineVisible(viewContext.isMVLineVisible())
				.setAuxLineVisible(viewContext.isAuxLineVisible())
				.setVertexVisible(viewContext.isVertexVisible())
				.get();

		writer.write(ini, Constants.INI_FILE_PATH);
	}

	public InitData load() {
		var ini = reader.read(Constants.INI_FILE_PATH);
		return ini;
	}
}
