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
package oripa.gui.presenter.model;

import java.util.List;

import oripa.application.FileAccessService;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelDisplayMode;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.persistence.entity.OrigamiModelFileTypes;
import oripa.persistence.filetool.FileTypeProperty;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewFramePresenter {

	private final ModelViewFrameView view;
	private final OrigamiModelFileSelectionPresenterFactory fileSelectionPresenterFactory;

	private final List<OrigamiModel> origamiModels;
	private OrigamiModel origamiModel;
	private final PainterScreenSetting mainScreenSetting;

	private final FileAccessService<OrigamiModel> fileAccessService;

	public ModelViewFramePresenter(
			final ModelViewFrameView view,
			final ModelViewComponentPresenterFactory componentPresenterFactory,
			final OrigamiModelFileSelectionPresenterFactory fileSelectionPresenterFactory,
			final PainterScreenSetting mainScreenSetting,
			final List<OrigamiModel> origamiModels,
			final CutModelOutlinesHolder cutModelOutlineHolder,
			final CallbackOnUpdate onUpdateScissorsLine,
			final FileAccessService<OrigamiModel> fileAccessService,
			final double eps) {
		this.view = view;
		this.fileSelectionPresenterFactory = fileSelectionPresenterFactory;

		this.fileAccessService = fileAccessService;

		this.mainScreenSetting = mainScreenSetting;
		this.origamiModels = origamiModels;

		var screenPresenter = componentPresenterFactory.createScreenPresenter(
				view.getModelScreenView(),
				onUpdateScissorsLine,
				eps);

		addListenersToComponents();

		setToView();
	}

	private void setToView() {
		view.setModelCount(origamiModels.size());
	}

	private void addListenersToComponents() {
		view.addFlipModelButtonListener(this::flipOrigamiModel);

		view.addCrossLineButtonListener(() -> mainScreenSetting.setCrossLineVisible(view.isCrossLineVisible()));

		view.addExportDXFButtonListener(() -> exportFile(OrigamiModelFileTypes.dxf()));

		view.addExportOBJButtonListener(() -> exportFile(OrigamiModelFileTypes.obj()));

		view.addExportSVGButtonListener(() -> exportFile(OrigamiModelFileTypes.svg()));

		view.addFillAlphaButtonListener(() -> {
			view.setModelDisplayMode(ModelDisplayMode.FILL_ALPHA);
			view.repaint();
		});

		view.addFillNoneButtonListener(() -> {
			view.setModelDisplayMode(ModelDisplayMode.FILL_NONE);
			view.repaint();
		});

		view.addModelSwitchListener(index -> {
			origamiModel = origamiModels.get(index);
			view.setModel(origamiModel);
		});
	}

	private void flipOrigamiModel() {
		origamiModel.flipXCoordinates();
		view.repaint();
	}

	private void exportFile(final FileTypeProperty<OrigamiModel> type) {

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

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
