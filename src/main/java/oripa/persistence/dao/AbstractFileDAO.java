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
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * A template for data file access.
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractFileDAO<Data> implements DataAccessObject<Data> {
	private static Logger logger = LoggerFactory.getLogger(AbstractFileDAO.class);

	/**
	 *
	 * @return a selector managing available file types.
	 */
	public abstract AbstractFileAccessSupportSelector<Data> getFileAccessSupportSelector();

	public void setConfigToSavingAction(final FileTypeProperty<Data> key, final Supplier<Object> configSupplier) {
		var supportOpt = getFileAccessSupportSelector().getFileAccessSupport(key);

		supportOpt.ifPresent(support -> support.setConfigToSavingAction(configSupplier));
	}

	public boolean canLoad(final String filePath) {
		try {
			getFileAccessSupportSelector().getLoadableOf(filePath);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public Optional<Data> load(final String path)
			throws FileVersionError, IOException, FileNotFoundException, IllegalArgumentException,
			WrongDataFormatException {
		var canonicalPath = nullableCanonicalPath(path);
		var file = new File(canonicalPath);

		if (!file.exists()) {
			throw new FileNotFoundException(canonicalPath + " doesn't exist.");
		}

		var loadingAction = getFileAccessSupportSelector().getLoadableOf(canonicalPath).getLoadingAction();

		return loadingAction.setPath(canonicalPath).load();
	}

	@Override
	public void save(final Data data, final String path)
			throws IOException, IllegalArgumentException {

		logger.info("save(): path = {}", path);

		var support = getFileAccessSupportSelector().getSavableOf(path);
		var savingAction = support.getSavingAction();

		savingAction.setPath(nullableCanonicalPath(path)).save(data);
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

		var support = getFileAccessSupportSelector().getSavablesOf(List.of(type)).stream().findFirst().get();
		var savingAction = support.getSavingAction();

		savingAction.setPath(nullableCanonicalPath(path)).save(data);
	}

	private String nullableCanonicalPath(final String path) throws IOException {
		return path == null ? null : (new File(path)).getCanonicalPath();
	}
}
