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

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.estimation.EstimationResultFileAccess;
import oripa.application.estimation.FoldedModelSVGConfigFileAccess;
import oripa.exception.UserCanceledException;
import oripa.gui.presenter.file.FileAccessPresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.persistence.entity.FoldedModelDAO;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.entity.FoldedModelFileAccessSupportSelector;
import oripa.persistence.entity.FoldedModelFileTypeKey;
import oripa.persistence.entity.exporter.FoldedModelSVGConfig;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultUIPresenter {
	private static final Logger logger = LoggerFactory.getLogger(EstimationResultUIPresenter.class);

	private final EstimationResultUIView view;

	final FileChooserFactory fileChooserFactory;

	private String lastFilePath;
	private final Consumer<String> lastFilePathChangeListener;

	private final FoldedModelSVGConfigFileAccess svgConfigFileAccess = new FoldedModelSVGConfigFileAccess();

	public EstimationResultUIPresenter(
			final EstimationResultUIView view,
			final FileChooserFactory fileChooserFactory,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {
		this.view = view;

		this.fileChooserFactory = fileChooserFactory;

		this.lastFilePath = lastFilePath;
		this.lastFilePathChangeListener = lastFilePathChangeListener;

		loadSVGConfig();

		addListener();
	}

	private void addListener() {
		view.addSaveSVGCofigButtonListener(this::saveSVGConfig);
		view.addExportButtonListener(this::export);
	}

	/**
	 * open export dialog for current folded estimation
	 */
	private void export() {
		try {
			var supportSelector = new FoldedModelFileAccessSupportSelector(view.isFaceOrderFlipped());
			var dao = new FoldedModelDAO(supportSelector);
			var fileAccessService = new EstimationResultFileAccess(dao);

			fileAccessService.setConfigToSavingAction(
					FoldedModelFileTypeKey.SVG_FOLDED_MODEL, this::createSVGConfig);
			fileAccessService.setConfigToSavingAction(
					FoldedModelFileTypeKey.SVG_FOLDED_MODEL_FLIP, this::createSVGConfig);

			var foldedModel = view.getModel();

			var entity = new FoldedModelEntity(foldedModel, view.getOverlapRelationIndex());

			var presenter = new FileAccessPresenter<FoldedModelEntity>((FrameView) view.getTopLevelView(),
					fileChooserFactory, fileAccessService);

			lastFilePath = presenter.saveUsingGUI(entity, lastFilePath).get();

			lastFilePathChangeListener.accept(lastFilePath);
		} catch (UserCanceledException e) {

		} catch (Exception ex) {
			logger.error("error: ", ex);
			view.showExportErrorMessage(ex);
		}
	}

	private FoldedModelSVGConfig createSVGConfig() {
		var svgConfig = new FoldedModelSVGConfig();

		svgConfig.setFaceStrokeWidth(view.getSVGFaceStrokeWidth());
		svgConfig.setPrecreaseStrokeWidth(view.getSVGPrecreaseStrokeWidth());

		return svgConfig;
	}

	private void saveSVGConfig() {
		try {
			svgConfigFileAccess.save(createSVGConfig());
		} catch (Exception e) {
			view.showErrorMessage(e);
		}
	}

	private void loadSVGConfig() {
		try {
			var configOpt = svgConfigFileAccess.load();

			var config = configOpt.orElse(new FoldedModelSVGConfig());

			view.setSVGFaceStrokeWidth(config.getFaceStrokeWidth());
			view.setSVGPrecreaseStrokeWidth(config.getPrecreaseStrokeWidth());

		} catch (Exception e) {
			view.showErrorMessage(e);
		}
	}
}
