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
package oripa.gui.presenter.estimation;

import java.util.List;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.estimation.EstimationResultFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultFramePresenter {
	private final EstimationResultFrameView view;
	private final List<FoldedModel> foldedModels;
	private FoldedModel foldedModel;

	public EstimationResultFramePresenter(
			final EstimationResultFrameView view,
			final List<FoldedModel> foldedModels) {
		this.view = view;

		this.foldedModels = foldedModels;

		var uiPresenter = new EstimationResultUIPresenter(view.getUI());

		addListeners();

		setToView();
	}

	private void setToView() {
		view.setModelCount(foldedModels.size());
	}

	private void addListeners() {
		view.addModelSwitchListener(index -> {
			foldedModel = foldedModels.get(index);
			view.setModel(foldedModel);
		});
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
