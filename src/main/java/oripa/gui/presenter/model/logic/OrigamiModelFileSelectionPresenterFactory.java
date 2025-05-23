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
package oripa.gui.presenter.model.logic;

import jakarta.inject.Inject;
import oripa.application.FileSelectionService;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.file.FileSelectionPresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelFileSelectionPresenterFactory {

	private final FileChooserFactory fileChooserFactory;
	private final FileFactory fileFactory;
	private final ExtensionCorrector extensionCorrector;

	@Inject
	public OrigamiModelFileSelectionPresenterFactory(
			final FileChooserFactory fileChooserFactory,
			final FileFactory fileFactory,
			final ExtensionCorrector extensionCorrector) {

		this.fileChooserFactory = fileChooserFactory;
		this.fileFactory = fileFactory;
		this.extensionCorrector = extensionCorrector;
	}

	public FileSelectionPresenter<OrigamiModel> create(
			final FrameView parent,
			final FileSelectionService<OrigamiModel> fileSelectionService) {
		return new FileSelectionPresenter<>(
				parent,
				fileChooserFactory,
				fileFactory,
				fileSelectionService,
				extensionCorrector);
	}
}
