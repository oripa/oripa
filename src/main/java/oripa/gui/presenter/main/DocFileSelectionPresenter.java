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
package oripa.gui.presenter.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import oripa.application.FileSelectionService;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.exception.UserCanceledException;
import oripa.gui.presenter.file.FileSelectionPresenter;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class DocFileSelectionPresenter extends FileSelectionPresenter<Doc> {
	private final TestedOrigamiModelFactory modelFactory;

	public DocFileSelectionPresenter(
			final FrameView parent,
			final FileChooserFactory chooserFactory,
			final TestedOrigamiModelFactory modelFactory,
			final FileFactory fileFactory,
			final FileSelectionService<Doc> fileSelectionService,
			final ExtensionCorrector extensionCorrector) {
		super(parent, chooserFactory, fileFactory, fileSelectionService, extensionCorrector);

		this.modelFactory = modelFactory;
	}

	/**
	 * Opens dialog for saving given data to a file. Conducts foldability check
	 * before saving. The default file name is "export.xxx" where "xxx" is the
	 * extension designated by the {@code type}.
	 *
	 * @param document
	 * @param directory
	 * @param type
	 * @param owner
	 * @throws FileChooserCanceledException
	 * @throws IOException
	 * @throws UserCanceledException
	 */
	public FileSelectionResult<Doc> saveFileWithModelCheck(
			final CreasePattern creasePattern,
			final String directory,
			final FileType<Doc> type, final FrameView owner,
			final Supplier<Boolean> acceptModelError,
			final double pointEps)
			throws IOException {
		File givenFile = fileFactory.create(directory, "export." + type.getExtensions()[0]);
		var filePath = givenFile.getCanonicalPath();

		var origamiModel = modelFactory.createOrigamiModel(
				creasePattern, pointEps);

		if (!origamiModel.isLocallyFlatFoldable()) {
			if (!acceptModelError.get()) {
				return FileSelectionResult.createCanceled();
			}
		}

		return saveUsingGUI(filePath, List.of(type));
	}

}
