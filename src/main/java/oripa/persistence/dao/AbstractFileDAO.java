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

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import oripa.persistence.filetool.AbstractSavingAction;
import oripa.persistence.filetool.FileAccessActionProvider;
import oripa.persistence.filetool.FileAccessSupportFilter;
import oripa.persistence.filetool.FileChooser;
import oripa.persistence.filetool.FileChooserCanceledException;
import oripa.persistence.filetool.FileChooserFactory;
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

	/**
	 *
	 * @return filter selector managing available file type. {@code null} is
	 *         acceptable if you don't use {@link #load(String)} and
	 *         {@link #save(Data, String, FileTypeProperty)}.
	 */
	protected abstract AbstractFilterSelector<Data> getFilterSelector();

	@Override
	public Data load(final String path)
			throws FileVersionError, IOException, FileNotFoundException, IllegalArgumentException,
			WrongDataFormatException {
		var canonicalPath = nullableCanonicalPath(path);
		var file = new File(canonicalPath);

		if (!file.exists()) {
			throw new FileNotFoundException(canonicalPath + " doesn't exist.");
		}

		var loadingAction = getFilterSelector().getLoadableFilterOf(canonicalPath).getLoadingAction();

		return loadingAction.setPath(canonicalPath).load();
	}

	@Override
	public void save(final Data data, final String path, final FileTypeProperty<Data> type)
			throws IOException, IllegalArgumentException {

		var savingAction = getFilterSelector().getFilter(type).getSavingAction();

		savingAction.setPath(nullableCanonicalPath(path)).save(data);
	}

	private String nullableCanonicalPath(final String path) throws IOException {
		return path == null ? null : (new File(path)).getCanonicalPath();
	}

	@Override
	public String saveUsingGUI(final Data data, final String homePath,
			final Component parent,
			final FileAccessSupportFilter<Data>... filters)
			throws FileChooserCanceledException, IOException, IllegalArgumentException {
		FileChooserFactory<Data> chooserFactory = new FileChooserFactory<>();

		var canonicalPath = nullableCanonicalPath(homePath);
		FileAccessActionProvider<Data> chooser = chooserFactory.createChooser(
				canonicalPath, filters);

		try {
			AbstractSavingAction<Data> saver = chooser.getActionForSavingFile(parent);
			saver.save(data);
			return saver.getPath();
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Wrong filter(s) is(are) given.", e);
		}
	}

	@Override
	public Data loadUsingGUI(final String homePath,
			final FileAccessSupportFilter<Data>[] filters, final Component parent)
			throws FileVersionError, FileChooserCanceledException, IllegalArgumentException,
			IOException, FileNotFoundException, WrongDataFormatException {
		FileChooserFactory<Data> factory = new FileChooserFactory<>();

		var canonicalPath = nullableCanonicalPath(homePath);
		FileChooser<Data> fileChooser = factory.createChooser(
				canonicalPath, filters);

		try {
			return fileChooser.getActionForLoadingFile(parent).load();
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Wrong filter(s) is(are) given.", e);
		}

	}

}
