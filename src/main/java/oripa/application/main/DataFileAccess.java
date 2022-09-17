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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.doc.Doc;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * A service object between the {@link DataAccessObject} and the {@link Doc}.
 *
 * @author OUCHI Koji
 *
 */
public class DataFileAccess {
	private static final Logger logger = LoggerFactory.getLogger(DataFileAccess.class);

	private final DataAccessObject<Doc> dao;

	public DataFileAccess(
			final DataAccessObject<Doc> dao) {
		this.dao = dao;
	}

	/**
	 * save the doc to given path and set the path to the doc.
	 *
	 * @param doc
	 * @param filePath
	 * @param fileType
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void saveProjectFile(final Doc doc, final String filePath)
			throws IOException, IllegalArgumentException {

		dao.save(doc, filePath);

		doc.setDataFilePath(filePath);
	}

	/**
	 * save file with given parameters.
	 *
	 * @param document
	 * @param directory
	 * @param fileName
	 *            if empty "newFile.opx" is used
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public final void saveFile(final Doc document,
			final String directory, final String fileName)
			throws IOException, IllegalArgumentException {

		File givenFile = new File(directory,
				(fileName.isEmpty()) ? "newFile.opx" : fileName);

		var filePath = givenFile.getCanonicalPath();

		dao.save(document, filePath);
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 *
	 * @param filePath
	 * @param lastFilePath
	 * @param owner
	 * @param accessSupports
	 * @return the path of loaded file. Empty if the file choosing is canceled.
	 */
	public Optional<Doc> loadFile(final String filePath)
			throws FileVersionError, IllegalArgumentException, WrongDataFormatException,
			IOException, FileNotFoundException {

		return Optional.of(dao.load(filePath));
	}
}
