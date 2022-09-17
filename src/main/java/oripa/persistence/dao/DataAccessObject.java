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

import java.io.FileNotFoundException;
import java.io.IOException;

import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * Provides data access methods supporting file I/O.
 *
 * @author OUCHI Koji
 *
 */
public interface DataAccessObject<Data> {

	/**
	 * try loading data from {@code path}
	 *
	 * @param path
	 *            for the data to be loaded.
	 * @return loaded data.
	 * @throws FileVersionError
	 * @throws IOException
	 *             file IO trouble.
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             {@code path} is not correct.
	 * @throws WrongDataFormatException
	 */
	Data load(String path)
			throws FileVersionError, IOException, FileNotFoundException, IllegalArgumentException,
			WrongDataFormatException;

	/**
	 * save without dialog
	 *
	 * @param data
	 *            to be saved.
	 * @param path
	 *            for the place to save the {@code data}.
	 * @param type
	 *            file type.
	 * @throws IOException
	 *             file IO trouble.
	 * @throws IllegalArgumentException
	 *             {@code data} can't be saved as the suggested file type.
	 */
	void save(Data data, String path, FileTypeProperty<Data> type)
			throws IOException, IllegalArgumentException;
}