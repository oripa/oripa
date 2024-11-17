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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.util.file.FileFactory;

/**
 * An implementation of data file access.
 *
 * @author OUCHI Koji
 *
 */
public class FileDAO<Data> implements DataAccessObject<Data> {
	private static Logger logger = LoggerFactory.getLogger(FileDAO.class);

	private final FileSelectionSupportSelector<Data> fileSelectionSupportSelector;

	private final FileFactory fileFactory;

	public FileDAO(final FileSelectionSupportSelector<Data> selector, final FileFactory fileFactory) {
		this.fileSelectionSupportSelector = selector;
		this.fileFactory = fileFactory;
	}

	public FileSelectionSupportSelector<Data> getFileSelectionSupportSelector() {
		return fileSelectionSupportSelector;
	}

	public void setConfigToSavingAction(final FileType<Data> key, final Supplier<Object> configSupplier) {
		var supportOpt = fileSelectionSupportSelector.getFileSelectionSupport(key);

		supportOpt.ifPresent(support -> support.setConfigToSavingAction(configSupplier));
	}

	public boolean canLoad(final String filePath) {
		try {
			fileSelectionSupportSelector.getLoadableOf(filePath);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean hasLoaders() {
		return !fileSelectionSupportSelector.getLoadables().isEmpty();
	}

	@Override
	public Optional<Data> load(final String path)
			throws DataAccessException, IllegalArgumentException {

		var canonicalPath = canonicalPath(path);
		var file = fileFactory.create(canonicalPath);

		if (!file.exists()) {
			throw new DataAccessException(canonicalPath + " doesn't exist.");
		}
		var loadingAction = fileSelectionSupportSelector.getLoadableOf(canonicalPath).getLoadingAction();

		try {
			return loadingAction.load(canonicalPath);
		} catch (FileVersionError | IOException | WrongDataFormatException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void save(final Data data, final String path)
			throws DataAccessException, IllegalArgumentException {

		logger.info("save(): path = {}", path);

		var support = fileSelectionSupportSelector.getSavableOf(path);
		var savingAction = support.getSavingAction();

		try {
			savingAction.save(data, canonicalPath(path));
		} catch (IOException e) {
			throw new DataAccessException(e);
		}
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
	public void save(final Data data, final String path, final FileType<Data> type)
			throws DataAccessException, IllegalArgumentException {

		logger.info("save(): path = {}", path);

		var support = fileSelectionSupportSelector.getSavablesOf(List.of(type)).stream()
				.findFirst()
				.get();
		var savingAction = support.getSavingAction();

		try {
			savingAction.save(data, canonicalPath(path));
		} catch (IOException e) {
			throw new DataAccessException(e);
		}
	}

	private String canonicalPath(final String path) throws DataAccessException {
		try {
			return fileFactory.create(path).getCanonicalPath();
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}
}
