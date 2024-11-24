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

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.model.logic.ModelViewFilePresentationLogic;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelDisplayMode;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.persistence.dao.FileType;
import oripa.persistence.entity.OrigamiModelFileTypes;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewFramePresenter {

	private final ModelViewFrameView view;
	private final ModelViewFilePresentationLogic filePresentationLogic;

	private final List<OrigamiModel> origamiModels;
	private OrigamiModel origamiModel;
	private final PainterScreenSetting mainScreenSetting;

	public ModelViewFramePresenter(
			final ModelViewFrameView view,
			final ModelViewFilePresentationLogic filePresentationLogic,
			final PainterScreenSetting mainScreenSetting,
			final List<OrigamiModel> origamiModels,
			final double eps) {
		this.view = view;
		this.filePresentationLogic = filePresentationLogic;

		this.mainScreenSetting = mainScreenSetting;
		this.origamiModels = origamiModels;

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

	private void exportFile(final FileType<OrigamiModel> type) {

		try {
			filePresentationLogic.exportFile(view, origamiModel, type);
		} catch (Exception e) {
			view.showExportErrorMessage(e);
		}
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
