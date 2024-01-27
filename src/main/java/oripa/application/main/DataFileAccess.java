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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.doc.Doc;
import oripa.persistence.dao.AbstractFileDAO;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.filetool.AbstractSavingAction;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * A service object between the {@link DataAccessObject} and the {@link Doc}.
 *
 * @author OUCHI Koji
 *
 */
public class DataFileAccess extends FileAccessService<Doc> {
	private static final Logger logger = LoggerFactory.getLogger(DataFileAccess.class);

	private final AbstractFileDAO<Doc> dao;

	public DataFileAccess(
			final AbstractFileDAO<Doc> dao) {
		this.dao = dao;
	}

	public void setFileSavingAction(final AbstractSavingAction<Doc> action, final FileTypeProperty<Doc> type) {
		dao.getFileAccessSupportSelector()
				.getFileAccessSupport(type)
				.orElseThrow(() -> new IllegalArgumentException("The type is not supported."))
				.setSavingAction(action);
	}

	@Override
	protected AbstractFileDAO<Doc> getFileDAO() {
		return dao;
	}

	@Override
	public final void saveFile(final Doc document,
			final String path, final FileTypeProperty<Doc> type)
			throws IOException, IllegalArgumentException {

		if (type == null) {
			dao.save(document, path);
		} else {
			dao.save(document, path, type);
		}
	}

	@Override
	public Optional<Doc> loadFile(final String filePath)
			throws FileVersionError, IllegalArgumentException, WrongDataFormatException,
			IOException, FileNotFoundException {

		return dao.load(filePath);
	}
}
