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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import oripa.persistence.dao.FileDAO;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessService<Data> {

	private final FileDAO<Data> fileDAO;

	/**
	 * The descriptions among the {@link FileAccessSupport}s contained by
	 * {@code dao} should be unique.
	 *
	 * @param dao
	 */
	public FileAccessService(final FileDAO<Data> dao) {
		this.fileDAO = dao;
	}

	public FileSelectionService<Data> getFileSelectionService() {
		return new FileSelectionService<>(fileDAO.getFileAccessSupportSelector());
	}

	public void setConfigToSavingAction(final FileTypeProperty<Data> key, final Supplier<Object> configSupplier) {
		fileDAO.setConfigToSavingAction(key, configSupplier);
	}

	/**
	 *
	 * @param key
	 * @param beforeSave
	 *            a consumer whose parameters are data and file path.
	 */
	public void setBeforeSave(final FileTypeProperty<Data> key, final BiConsumer<Data, String> beforeSave) {
		fileDAO.setBeforeSave(key, beforeSave);
	}

	/**
	 *
	 * @param key
	 * @param afterSave
	 *            a consumer whose parameters are data and file path.
	 */
	public void setAfterSave(final FileTypeProperty<Data> key, final BiConsumer<Data, String> afterSave) {
		fileDAO.setAfterSave(key, afterSave);
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
	public void saveFile(final Data data, final String path, final FileTypeProperty<Data> type)
			throws IOException, IllegalArgumentException {
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
	public void saveFile(final Data data, final String path) throws IOException, IllegalArgumentException {
		saveFile(data, path, null);
	}

	/**
	 * tries to read data from the path.
	 *
	 * @param filePath
	 * @return the Data of loaded file.
	 */
	public Optional<Data> loadFile(final String filePath)
			throws FileVersionError, IllegalArgumentException, WrongDataFormatException,
			IOException, FileNotFoundException {

		if (!fileDAO.hasLoaders()) {
			throw new RuntimeException("Not implemented yet.");
		}

		return fileDAO.load(filePath);

	}
}
