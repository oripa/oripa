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
import java.util.function.Supplier;

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * @author OUCHI Koji
 *
 */
public interface FileAccessService<Data> {

	public AbstractFileAccessSupportSelector<Data> getFileAccessSupportSelector();

	default void setConfigToSavingAction(final FileTypeProperty<Data> key, final Supplier<Object> configSupplier) {
		var support = getFileAccessSupportSelector().getFileAccessSupport(key);
		if (support == null) {
			return;
		}

		support.setConfigToSavingAction(configSupplier);
	}

	default boolean canLoad(final String filePath) {
		try {
			getFileAccessSupportSelector().getLoadableOf(filePath);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * save file with given parameters.
	 *
	 * @param document
	 * @param path
	 * @throws IllegalArgumentException
	 */
	void saveFile(final Data data, final String path)
			throws IOException, IllegalArgumentException;

	/**
	 * tries to read data from the path.
	 *
	 * @param filePath
	 * @return the Data of loaded file.
	 */
	Optional<Data> loadFile(final String filePath)
			throws FileVersionError, IllegalArgumentException, WrongDataFormatException,
			IOException, FileNotFoundException;
}
