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

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.paint.AngleStep;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.main.logic.GridDivNumPresentationLogic;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationType;
import oripa.gui.presenter.main.logic.SubFramePresentationLogic;
import oripa.gui.presenter.main.logic.UIPanelPaintMenuListenerRegistration;
import oripa.gui.presenter.main.logic.ValuePanelPresentationLogic;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.UIPanelView;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPresenter {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelPresenter.class);

	private final UIPanelView view;

	private final SubFramePresentationLogic subFramePresentationLogic;
	private final UIPanelPaintMenuListenerRegistration paintMenuListenerRegistration;
	private final GridDivNumPresentationLogic gridDivNumPresentationLogic;
	private final ValuePanelPresentationLogic valuePanelPresentationLogic;

	private final TypeForChange[] alterLineComboDataFrom = {
			TypeForChange.EMPTY, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT };
	private final TypeForChange[] alterLineComboDataTo = {
			TypeForChange.FLIP, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.UNASSIGNED,
			TypeForChange.AUX, TypeForChange.CUT, TypeForChange.DELETE, };

	private final ComputationType[] computationTypeComboData = {
			ComputationType.FULL, ComputationType.FIRST_ONLY, ComputationType.X_RAY };

	private final PainterScreenSetting mainScreenSetting;

	@Inject
	public UIPanelPresenter(final UIPanelView view,
			final SubFramePresentationLogic subFramePresentationLogic,
			final UIPanelPaintMenuListenerRegistration paintMenuListenerRegistration,
			final GridDivNumPresentationLogic gridDivNumPresentationLogic,
			final ValuePanelPresentationLogic valuePanelPresentationLogic,
			final TypeForChangeContext typeForChangeContext,
			final PainterScreenSetting mainScreenSetting) {

		this.view = view;

		this.subFramePresentationLogic = subFramePresentationLogic;

		this.paintMenuListenerRegistration = paintMenuListenerRegistration;
		this.gridDivNumPresentationLogic = gridDivNumPresentationLogic;
		this.valuePanelPresentationLogic = valuePanelPresentationLogic;

		this.mainScreenSetting = mainScreenSetting;

		Stream.of(alterLineComboDataFrom).forEach(item -> view.addItemOfAlterLineComboFrom(item.toString()));
		Stream.of(alterLineComboDataTo).forEach(item -> view.addItemOfAlterLineComboTo(item.toString()));
		Stream.of(computationTypeComboData).forEach(item -> view.addItemOfComputationTypeCombo(item.toString()));
		Stream.of(AngleStep.values()).forEach(item -> view.addItemOfAngleStepCombo(item.toString()));

		addListeners();

		typeForChangeContext.setTypeFrom(alterLineComboDataFrom[0]);
		typeForChangeContext.setTypeTo(alterLineComboDataTo[0]);

		view.initializeButtonSelection(AngleStep.PI_OVER_8.toString(),
				typeForChangeContext.getTypeFrom().toString(),
				typeForChangeContext.getTypeTo().toString(),
				ComputationType.FULL.toString());

		updateValuePanelFractionDigits();
	}

	public void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
		paintMenuListenerRegistration.addPlugins(plugins);
	}

	private void addListeners() {

		paintMenuListenerRegistration.register();

		// ------------------------------------------------------------
		// grid setting

		view.addDispGridCheckBoxListener(checked -> {
			mainScreenSetting.setGridVisible(checked);
		});
		view.addGridSmallButtonListener(gridDivNumPresentationLogic::makeGridSizeHalf);
		view.addGridLargeButtonListener(gridDivNumPresentationLogic::makeGridSizeTwiceLarge);
		view.addGridChangeButtonListener(gridDivNumPresentationLogic::updateGridDivNum);

		// ------------------------------------------------------------
		// display setting

		view.addDispVertexCheckBoxListener(checked -> {
			logger.debug("vertexVisible at listener: {}", checked);
			mainScreenSetting.setVertexVisible(checked);
		});

		view.addDispMVLinesCheckBoxListener(checked -> {
			logger.debug("mvLineVisible at listener: {}", checked);
			mainScreenSetting.setMVLineVisible(checked);
		});

		view.addDispAuxLinesCheckBoxListener(checked -> {
			logger.debug("auxLineVisible at listener: {}", checked);
			mainScreenSetting.setAuxLineVisible(checked);
		});

		view.addZeroLineWidthCheckBoxListener(checked -> {
			mainScreenSetting.setZeroLineWidth(checked);
		});

		// ------------------------------------------------------------
		// fold

		view.addCheckWindowButtonListener(subFramePresentationLogic::showCheckerWindow);
		view.setModelComputationListener(subFramePresentationLogic::computeModels);
		view.setShowFoldedModelWindowsListener(subFramePresentationLogic::showFoldedModelWindows);
	}

	/**
	 * Updates text fields' format setting based on eps in context.
	 */
	public void updateValuePanelFractionDigits() {
		valuePanelPresentationLogic.updateValuePanelFractionDigits();
	}
}
