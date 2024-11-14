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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileSelectionService;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.file.FileFilterProperty;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class FileSelectionPresenter<Data> {

	private static Logger logger = LoggerFactory.getLogger(FileSelectionPresenter.class);

	private final FrameView parent;
	private final FileChooserFactory chooserFactory;
	protected final FileFactory fileFactory;
	private final FileSelectionService<Data> fileSelectionService;
	private final ExtensionCorrector extensionCorrector;

	public FileSelectionPresenter(
			final FrameView parent,
			final FileChooserFactory chooserFactory,
			final FileFactory fileFactory,
			final FileSelectionService<Data> fileSelectionService,
			final ExtensionCorrector extensionCorrector) {
		this.parent = parent;
		this.chooserFactory = chooserFactory;
		this.fileFactory = fileFactory;
		this.fileSelectionService = fileSelectionService;
		this.extensionCorrector = extensionCorrector;
	}

	public FileSelectionResult<Data> saveUsingGUI(final String path) {

		return saveUsingGUIImpl(path, fileSelectionService.getSavableSupports());
	}

	public FileSelectionResult<Data> saveUsingGUI(final String path, final List<FileTypeProperty<Data>> types) {

		return saveUsingGUIImpl(path, fileSelectionService.getSavableSupportsOf(types));
	}

	private FileSelectionResult<Data> saveUsingGUIImpl(
			final String path, final List<FileAccessSupport<Data>> savableSupports) {

		var chooser = chooserFactory.createForSaving(
				path,
				toFileFilterProperties(savableSupports));

		if (!chooser.showDialog(parent)) {
			return FileSelectionResult.createCancel();
		}

		var file = chooser.getSelectedFile();

		String filePath = file.getPath();

		var correctedPath = extensionCorrector.correct(filePath, chooser.getSelectedFilterExtensions());
		var correctedFile = fileFactory.create(correctedPath);

		if (correctedFile.exists()) {
			if (!chooser.showOverwriteConfirmMessage()) {
				return FileSelectionResult.createCancel();
			}
		}

		logger.debug("saving {}", correctedPath);

		return FileSelectionResult.createSelectedForSave(correctedPath,
				fileSelectionService.getSavableTypeByDescription(chooser.getSelectedFilterDescription()));
	}

	public FileSelectionResult<Data> loadUsingGUI(final String lastFilePath) {

		var chooser = chooserFactory.createForLoading(
				lastFilePath,
				toFileFilterProperties(fileSelectionService.getLoadableSupportsWithMultiType()));

		if (!chooser.showDialog(parent)) {
			return FileSelectionResult.createCancel();
		}

		var file = chooser.getSelectedFile();

		var loadedPath = file.getPath();

		return FileSelectionResult.createSelectedForLoad(loadedPath);
	}

	private List<FileFilterProperty> toFileFilterProperties(final List<FileAccessSupport<Data>> supports) {
		return supports.stream()
				.map(support -> new FileFilterProperty(
						support.getFileTypeKeyText(), support.getDescription(), support.getExtensions()))
				.toList();
	}

}
