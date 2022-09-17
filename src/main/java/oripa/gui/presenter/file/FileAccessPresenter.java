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
package oripa.gui.presenter.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.exception.UserCanceledException;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.file.FileFilterProperty;
import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessPresenter<Data> {

	private static Logger logger = LoggerFactory.getLogger(FileAccessPresenter.class);

	private final FrameView parent;
	private final FileChooserFactory chooserFactory;
	private final AbstractFileAccessSupportSelector<Data> selector;

	public FileAccessPresenter(
			final FrameView parent,
			final FileChooserFactory chooserFactory,
			final AbstractFileAccessSupportSelector<Data> selector) {
		this.parent = parent;
		this.chooserFactory = chooserFactory;
		this.selector = selector;
	}

	public Optional<String> saveUsingGUI(final Data data, final String path) throws UserCanceledException {
		var types = selector.getTargetTypes(selector.getSavables());

		return saveUsingGUI(data, path, types);
	}

	public Optional<String> saveUsingGUI(final Data data, final String path, final List<FileTypeProperty<Data>> types)
			throws UserCanceledException {

		var supports = selector.getSavables().stream()
				.filter(support -> types.contains(support.getTargetType()))
				.collect(Collectors.toList());

		var chooser = chooserFactory.createForSaving(
				path,
				toFileFilterProperties(supports));

		if (!chooser.showDialog(parent)) {
			throw new UserCanceledException();
		}

		var file = chooser.getSelectedFile();

		String filePath = nullableCanonicalPath(file);
		if (filePath == null) {
			throw new IllegalStateException("Failed to get canonical path.");
		}

		var correctedPath = correctExtension(filePath, chooser.getSelectedFilterExtensions());
		var correctedFile = new File(correctedPath);

		if (correctedFile.exists()) {
			if (!chooser.showOverwriteConfirmMessage()) {
				throw new UserCanceledException();
			}
		}

		logger.debug("saving {}", correctedPath);

		types.stream()
				.filter(type -> type.extensionsMatch(correctedPath))
				.findFirst()
				.map(type -> selector.findFirst(selector.getSavables(), type))
				.map(support -> {
					try {
						return support.getSavingAction().setPath(correctedPath).save(data);
					} catch (Exception e) {
						chooser.showErrorMessage(e);
						return false;
					}
				});

		return Optional.of(correctedPath);
	}

	public Optional<Data> loadUsingGUI(final String lastFilePath) throws UserCanceledException, FileNotFoundException {

		var types = selector.getTargetTypes(selector.getLoadables());

		var chooser = chooserFactory.createForLoading(
				lastFilePath,
				toFileFilterProperties(selector.getLoadablesWithMultiType()));

		if (!chooser.showDialog(parent)) {
			throw new UserCanceledException();
		}

		var file = chooser.getSelectedFile();

		if (!file.exists()) {
			throw new FileNotFoundException("Selected file doesn't exist.");
		}

		String filePath = nullableCanonicalPath(file);
		if (filePath == null) {
			throw new IllegalStateException("Failed to get canonical path.");
		}

		var data = types.stream()
				.filter(type -> type.extensionsMatch(filePath))
				.findFirst()
				.map(type -> selector.findFirst(selector.getLoadables(), type))
				.map(support -> {
					try {
						return support.getLoadingAction().setPath(filePath).load();
					} catch (WrongDataFormatException | IOException | FileVersionError e) {
						chooser.showErrorMessage(e);
						return null;
					}
				});

		return data;
	}

	private List<FileFilterProperty> toFileFilterProperties(final List<FileAccessSupport<Data>> supports) {
		return supports.stream()
				.map(support -> new FileFilterProperty(support.getDescription(), support.getExtensions()))
				.collect(Collectors.toList());
	}

	private String replaceExtension(final String path, final String ext) {

		String path_new;

		// drop the old extension
		path_new = path.replaceAll("\\.\\w+$", "");

		// append the new extension
		path_new += "." + ext;

		return path_new;
	}

	/**
	 * this method does not change {@code path}.
	 *
	 * @param path
	 * @param extensions
	 *            ex) ".png"
	 * @return path string with new extension
	 */
	private String correctExtension(final String path, final String[] extensions) {

		String path_new = new String(path);

		logger.debug("extensions[0] for correction: {}", extensions[0]);

		var filtered = Arrays.asList(extensions).stream()
				.filter(ext -> path.endsWith("." + ext))
				.collect(Collectors.toList());

		// the path's extension is not in the targets.
		if (filtered.isEmpty()) {
			path_new = replaceExtension(path_new, extensions[0]);
		}

		return path_new;
	}

	private String nullableCanonicalPath(final File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

}
