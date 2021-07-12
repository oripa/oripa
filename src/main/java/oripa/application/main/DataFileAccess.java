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

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.doc.CreasePatternFileTypeKey;
import oripa.persistence.filetool.FileAccessSupportFilter;
import oripa.persistence.filetool.FileChooserCanceledException;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * @author OUCHI Koji
 *
 *         interface between the {@code DocDOA} and the {@code Doc} classes
 */
public class DataFileAccess {
	private static final Logger logger = LoggerFactory.getLogger(DataFileAccess.class);

	private DataAccessObject<Doc> dao;

	@SuppressWarnings("unused")
	private DataFileAccess() {

	}

	public DataFileAccess(final DataAccessObject<Doc> dao) {
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
	public void saveProjectFile(final Doc doc, final String filePath,
			final CreasePatternFileTypeKey fileType)
			throws IOException, IllegalArgumentException {

		dao.save(doc, filePath, fileType);

		doc.setDataFilePath(filePath);
	}

	/**
	 * opens dialog for saving file with given parameters.
	 *
	 * @param document
	 * @param directory
	 * @param fileName
	 *            if empty "newFile.opx" is used
	 * @param owner
	 * @param filters
	 * @return the path of saved file. Empty if the file choosing is canceled.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	@SafeVarargs
	public final Optional<String> saveFile(final Doc document,
			final String directory, final String fileName, final Component owner,
			final FileAccessSupportFilter<Doc>... filters)
			throws IOException, IllegalArgumentException {

		File givenFile = new File(directory,
				(fileName.isEmpty()) ? "newFile.opx" : fileName);

		var filePath = givenFile.getCanonicalPath();

		try {
			String savedPath = dao.saveUsingGUI(document, filePath, owner, filters);
			return Optional.of(savedPath);
		} catch (FileChooserCanceledException e) {
			logger.info("File selection is canceled.");
			return Optional.empty();
		}
	}

	/**
	 * Opens dialog for saving given data to a file. Conducts foldability check
	 * before saving. The default file name is "export.xxx" where ".xxx" is the
	 * extension designated by the {@code filter}.
	 *
	 * @param document
	 * @param directory
	 * @param filter
	 * @param owner
	 * @throws FileChooserCanceledException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void saveFileWithModelCheck(final Doc doc,
			final String directory,
			final FileAccessSupportFilter<Doc> filter, final Component owner)
			throws IOException, IllegalArgumentException {
		File givenFile = new File(directory, "export" + filter.getExtensions()[0]);
		var filePath = givenFile.getCanonicalPath();

		CreasePattern creasePattern = doc.getCreasePattern();

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		var origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());
		var checker = new FoldabilityChecker();

		if (!checker.testLocalFlatFoldability(origamiModel)) {

			var selection = JOptionPane.showConfirmDialog(null,
					"Warning: Building a set of polygons from crease pattern "
							+ "was failed.",
					"Warning", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (selection == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		try {
			dao.saveUsingGUI(doc, filePath, owner, filter);
		} catch (FileChooserCanceledException e) {
			logger.info("File selection is canceled.");
		}
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 *
	 * @param filePath
	 * @param lastFilePath
	 * @param owner
	 * @param filters
	 * @return the path of loaded file. Empty if the file choosing is canceled.
	 */
	public Optional<Doc> loadFile(final String filePath, final String lastFilePath,
			final Component owner, final FileAccessSupportFilter<Doc>... filters)
			throws FileVersionError, IllegalArgumentException, WrongDataFormatException,
			IOException, FileNotFoundException {

		try {
			if (filePath != null) {
				return Optional.of(dao.load(filePath));
			} else {
				return Optional.of(dao.loadUsingGUI(
						lastFilePath, filters, owner));
			}
		} catch (FileChooserCanceledException cancel) {
			return Optional.empty();
		}
	}
}
