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
package oripa.persistent.dao;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;

import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.persistent.filetool.FileTypeProperty;
import oripa.persistent.filetool.FileVersionError;
import oripa.persistent.filetool.WrongDataFormatException;

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

	/**
	 * open save dialog for file types in {@code filters}
	 *
	 * @param data
	 *            to be saved
	 * @param homePath
	 *            starting path to display
	 * @param parent
	 * @param filters
	 * @return chosen path
	 * @throws FileChooserCanceledException
	 * @throws IOException
	 *             file IO trouble.
	 * @throws IllegalArgumentException
	 *             the filter chosen from {@code filters} by user accepts the
	 *             selected file but is not for saving the file.
	 */
	String saveUsingGUI(Data data, String homePath,
			Component parent,
			FileAccessSupportFilter<Data>... filters)
			throws FileChooserCanceledException, IOException, IllegalArgumentException;

	/**
	 * open dialog to load file
	 *
	 * @param homePath
	 *            starting path
	 * @param filters
	 *            supported file types
	 * @param parent
	 * @return loaded data.
	 * @throws FileVersionError
	 * @throws FileChooserCanceledException
	 * @throws IllegalArgumentException
	 *             the filter chosen from {@code filters} by user accepts the
	 *             selected file but is not for loading the file.
	 * @throws IOException
	 *             file IO trouble.
	 * @throws FileNotFoundException
	 *             selected file doesn't exist.
	 * @throws WrongDataFormatException
	 *             loading failed because of data format problem.
	 */
	Data loadUsingGUI(String homePath,
			FileAccessSupportFilter<Data>[] filters, Component parent)
			throws FileVersionError, FileChooserCanceledException, IllegalArgumentException,
			IOException, FileNotFoundException, WrongDataFormatException;

}