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
package oripa.persistence.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * An implementation of data file access.
 *
 * @author OUCHI Koji
 *
 */
public class FileDAO<Data> implements DataAccessObject<Data> {
	private static Logger logger = LoggerFactory.getLogger(FileDAO.class);

	private final FileAccessSupportSelector<Data> fileAccessSupportSelector;

	public FileDAO(final FileAccessSupportSelector<Data> selector) {
		this.fileAccessSupportSelector = selector;
	}

	public FileAccessSupportSelector<Data> getFileAccessSupportSelector() {
		return fileAccessSupportSelector;
	}

	public void setConfigToSavingAction(final FileTypeProperty<Data> key, final Supplier<Object> configSupplier) {
		var supportOpt = fileAccessSupportSelector.getFileAccessSupport(key);

		supportOpt.ifPresent(support -> support.setConfigToSavingAction(configSupplier));
	}

	/**
	 *
	 * @param key
	 * @param beforeSave
	 *            a consumer whose parameters are data and file path.
	 */
	public void setBeforeSave(final FileTypeProperty<Data> key, final BiConsumer<Data, String> beforeSave) {
		var supportOpt = fileAccessSupportSelector.getFileAccessSupport(key);

		supportOpt.ifPresent(support -> support.setBeforeSave(beforeSave));
	}

	/**
	 *
	 * @param key
	 * @param afterSave
	 *            a consumer whose parameters are data and file path.
	 */
	public void setAfterSave(final FileTypeProperty<Data> key, final BiConsumer<Data, String> afterSave) {
		var supportOpt = fileAccessSupportSelector.getFileAccessSupport(key);

		supportOpt.ifPresent(support -> support.setAfterSave(afterSave));
	}

	public boolean canLoad(final String filePath) {
		try {
			fileAccessSupportSelector.getLoadableOf(filePath);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean hasLoader() {
		return !fileAccessSupportSelector.getLoadables().isEmpty();
	}

	@Override
	public Optional<Data> load(final String path)
			throws FileVersionError, IOException, FileNotFoundException, IllegalArgumentException,
			WrongDataFormatException {
		var canonicalPath = canonicalPath(path);
		var file = new File(canonicalPath);

		if (!file.exists()) {
			throw new FileNotFoundException(canonicalPath + " doesn't exist.");
		}

		var loadingAction = fileAccessSupportSelector.getLoadableOf(canonicalPath).getLoadingAction();

		return loadingAction.load(canonicalPath);
	}

	@Override
	public void save(final Data data, final String path)
			throws IOException, IllegalArgumentException {

		logger.info("save(): path = {}", path);

		var support = fileAccessSupportSelector.getSavableOf(path);
		var savingAction = support.getSavingAction();

		savingAction.save(data, canonicalPath(path));
	}

	/**
	 * Extended version of {@link #save(Object, String)} for specifying saving
	 * action.
	 *
	 * @param data
	 * @param path
	 * @param type
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void save(final Data data, final String path, final FileTypeProperty<Data> type)
			throws IOException, IllegalArgumentException {

		logger.info("save(): path = {}", path);

		var support = fileAccessSupportSelector.getSavablesOf(List.of(type)).stream().findFirst().get();
		var savingAction = support.getSavingAction();

		savingAction.save(data, canonicalPath(path));
	}

	private String canonicalPath(final String path) throws IOException {
		return (new File(path)).getCanonicalPath();
	}
}
