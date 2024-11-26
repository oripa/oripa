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
package oripa.application;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileDAO;
import oripa.persistence.dao.FileType;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessService<Data> {

	private final FileDAO<Data> fileDAO;

	// @Inject
	public FileAccessService(final FileDAO<Data> dao) {
		this.fileDAO = dao;
	}

	public FileSelectionService<Data> getFileSelectionService() {
		return new FileSelectionService<>(fileDAO.getFileSelectionSupportSelector());
	}

	public void setConfigToSavingAction(final FileType<Data> key, final Supplier<Object> configSupplier) {
		fileDAO.setConfigToSavingAction(key, configSupplier);
	}

	public boolean canLoad(final String filePath) {
		return fileDAO.canLoad(filePath);
	}

	/**
	 * save file with given parameters.
	 *
	 * @param data
	 * @param path
	 * @param type
	 *            null for auto detection.
	 *
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void saveFile(final Data data, final String path, final FileType<Data> type)
			throws DataAccessException, IllegalArgumentException {
		if (type == null) {
			fileDAO.save(data, path);
		} else {
			fileDAO.save(data, path, type);
		}

	}

	/**
	 * With auto type detection by file path extension.
	 *
	 * @param data
	 * @param path
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void saveFile(final Data data, final String path) throws DataAccessException, IllegalArgumentException {
		saveFile(data, path, null);
	}

	/**
	 * tries to read data from the path.
	 *
	 * @param filePath
	 * @return the Data of loaded file.
	 */
	public Optional<Data> loadFile(final String filePath)
			throws DataAccessException, IllegalArgumentException {

		if (!fileDAO.hasLoaders()) {
			throw new RuntimeException("Not implemented yet.");
		}

		return fileDAO.load(filePath);

	}
}
