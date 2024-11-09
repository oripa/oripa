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

import oripa.application.FileAccessService;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.exception.UserCanceledException;
import oripa.gui.presenter.file.FileAccessPresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileTypeProperty;

/**
 * @author OUCHI Koji
 *
 */
public class DocFileAccessPresenter extends FileAccessPresenter<Doc> {

	public DocFileAccessPresenter(
			final FrameView parent,
			final FileChooserFactory chooserFactory,
			final FileAccessService<Doc> fileAccessService) {
		super(parent, chooserFactory, fileAccessService);
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
	public void saveFileWithModelCheck(final Doc doc,
			final String directory,
			final FileTypeProperty<Doc> type, final FrameView owner,
			final Supplier<Boolean> acceptModelError,
			final double pointEps)
			throws IOException, UserCanceledException {
		File givenFile = new File(directory, "export." + type.getExtensions()[0]);
		var filePath = givenFile.getCanonicalPath();

		CreasePattern creasePattern = doc.getCreasePattern();

		var modelFactory = new TestedOrigamiModelFactory();
		var origamiModel = modelFactory.createOrigamiModel(
				creasePattern, pointEps);

		if (!origamiModel.isLocallyFlatFoldable()) {
			if (!acceptModelError.get()) {
				return;
			}
		}

		saveUsingGUI(doc, filePath, List.of(type));
	}

}
