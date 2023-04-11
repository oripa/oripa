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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.exception.UserCanceledException;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.file.FileFilterProperty;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessPresenter<Data> {

	private static Logger logger = LoggerFactory.getLogger(FileAccessPresenter.class);

	private final FrameView parent;
	private final FileChooserFactory chooserFactory;
	private final FileAccessService<Data> fileAccessService;

	public FileAccessPresenter(
			final FrameView parent,
			final FileChooserFactory chooserFactory,
			final FileAccessService<Data> fileAccessService) {
		this.parent = parent;
		this.chooserFactory = chooserFactory;
		this.fileAccessService = fileAccessService;
	}

	public Optional<String> saveUsingGUI(final Data data, final String path) throws UserCanceledException {

		return saveUsingGUIImpl(data, path, fileAccessService.getSavableSupports());
	}

	public Optional<String> saveUsingGUI(final Data data, final String path, final List<FileTypeProperty<Data>> types)
			throws UserCanceledException {

		return saveUsingGUIImpl(data, path, fileAccessService.getSavableSupportsOf(types));
	}

	private Optional<String> saveUsingGUIImpl(final Data data, final String path,
			final List<FileAccessSupport<Data>> savableSupports)
			throws UserCanceledException {

		var chooser = chooserFactory.createForSaving(
				path,
				toFileFilterProperties(savableSupports));

		if (!chooser.showDialog(parent)) {
			throw new UserCanceledException();
		}

		var file = chooser.getSelectedFile();

		String filePath = file.getPath();

		var correctedPath = correctExtension(filePath, chooser.getSelectedFilterExtensions());
		var correctedFile = new File(correctedPath);

		// TODO make a wrapper of File: exists() depends on file system and it's
		// not testable.
		if (correctedFile.exists()) {
			if (!chooser.showOverwriteConfirmMessage()) {
				throw new UserCanceledException();
			}
		}

		logger.debug("saving {}", correctedPath);

		try {
			fileAccessService.saveFile(data, correctedPath);
		} catch (Exception e) {
			chooser.showErrorMessage(e);
			return Optional.empty();
		}

		return Optional.of(correctedPath);
	}

	public Optional<Data> loadUsingGUI(final String lastFilePath) throws UserCanceledException {

		var chooser = chooserFactory.createForLoading(
				lastFilePath,
				toFileFilterProperties(fileAccessService.getLoadableSupportsWithMultiType()));

		if (!chooser.showDialog(parent)) {
			throw new UserCanceledException();
		}

		var file = chooser.getSelectedFile();

		String filePath = file.getPath();

		try {
			return fileAccessService.loadFile(filePath);
		} catch (Exception e) {
			chooser.showErrorMessage(e);
			return Optional.empty();
		}
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
}
