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

import java.util.List;

import oripa.application.FileAccessService;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.persistence.dao.FileType;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewFilePresentationLogic {
	private final OrigamiModelFileSelectionPresenterFactory fileSelectionPresenterFactory;
	private final FileAccessService<OrigamiModel> fileAccessService;

	public ModelViewFilePresentationLogic(
			final OrigamiModelFileSelectionPresenterFactory fileSelectionPresenterFactory,
			final FileAccessService<OrigamiModel> fileAccessService

	) {

		this.fileSelectionPresenterFactory = fileSelectionPresenterFactory;
		this.fileAccessService = fileAccessService;
	}

	public void exportFile(
			final ModelViewFrameView view,
			final OrigamiModel origamiModel,
			final FileType<OrigamiModel> type) {

		try {
			var presenter = fileSelectionPresenterFactory.create(view, fileAccessService.getFileSelectionService());

			var selection = presenter.saveUsingGUI(null, List.of(type));

			if (selection.action() == UserAction.CANCELED) {
				return;
			}

			fileAccessService.saveFile(origamiModel, selection.path(), type);

		} catch (Exception e) {
			view.showExportErrorMessage(e);
		}
	}

}
