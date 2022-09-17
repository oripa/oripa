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

import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.exception.UserCanceledException;
import oripa.gui.presenter.file.FileAccessPresenter;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelDisplayMode;
import oripa.gui.view.model.ModelViewFrameView;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.persistence.dao.DataAccessObject;
import oripa.persistence.entity.OrigamiModelDAO;
import oripa.persistence.entity.OrigamiModelFileAccessSupportSelector;
import oripa.persistence.entity.OrigamiModelFileTypeKey;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewFramePresenter {

	private final ModelViewFrameView view;
	private final FileChooserFactory fileChooserFactory;

	private final List<OrigamiModel> origamiModels;
	private OrigamiModel origamiModel;
	private final PainterScreenSetting mainScreenSetting;

	private final OrigamiModelFileAccessSupportSelector supportSelector = new OrigamiModelFileAccessSupportSelector();
	private final DataAccessObject<OrigamiModel> dao = new OrigamiModelDAO(supportSelector);

	public ModelViewFramePresenter(
			final ModelViewFrameView view,
			final FileChooserFactory fileChooserFactory,
			final PainterScreenSetting mainScreenSetting,
			final List<OrigamiModel> origamiModels,
			final CutModelOutlinesHolder lineHolder,
			final CallbackOnUpdate onUpdateScissorsLine) {
		this.view = view;
		this.fileChooserFactory = fileChooserFactory;

		this.mainScreenSetting = mainScreenSetting;
		this.origamiModels = origamiModels;

		var screenPresenter = new ModelViewScreenPresenter(view.getModelScreenView(), lineHolder, onUpdateScissorsLine);

		addListenersToComponents();

		setToView();
	}

	private void setToView() {
		view.setModelCount(origamiModels.size());
	}

	private void addListenersToComponents() {
		view.addFlipModelButtonListener(this::flipOrigamiModel);

		view.addCrossLineButtonListener(() -> mainScreenSetting.setCrossLineVisible(view.isCrossLineVisible()));

		view.addExportDXFButtonListener(() -> exportFile(OrigamiModelFileTypeKey.DXF_MODEL));

		view.addExportOBJButtonListener(() -> exportFile(OrigamiModelFileTypeKey.OBJ_MODEL));

		view.addExportSVGButtonListener(() -> exportFile(OrigamiModelFileTypeKey.SVG_MODEL));

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

	private void exportFile(final OrigamiModelFileTypeKey type) {

		try {
			var presenter = new FileAccessPresenter<>(view, fileChooserFactory, supportSelector, dao);

			presenter.saveUsingGUI(origamiModel, null, List.of(type));
		} catch (UserCanceledException e) {

		} catch (Exception e) {
			view.showExportErrorMessage(e);
		}
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
