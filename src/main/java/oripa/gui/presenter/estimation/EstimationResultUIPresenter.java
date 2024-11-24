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
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.estimation.FoldedModelSVGConfigFileAccess;
import oripa.domain.fold.FoldedModel;
import oripa.gui.presenter.estimation.logic.SubfaceToOverlapRelationIndicesFactory;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.persistence.entity.exporter.FoldedModelSVGConfig;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultUIPresenter {
	private static final Logger logger = LoggerFactory.getLogger(EstimationResultUIPresenter.class);

	private final EstimationResultUIView view;

	private final EstimationResultFilePresenter estimationResultFilePresenter;

	private String lastFilePath;
	private final Consumer<String> lastFilePathChangeListener;

	private final FoldedModelSVGConfigFileAccess svgConfigFileAccess = new FoldedModelSVGConfigFileAccess();

	public EstimationResultUIPresenter(
			final EstimationResultUIView view,
			final EstimationResultFilePresenter estimationResultFilePresenter,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {
		this.view = view;

		this.estimationResultFilePresenter = estimationResultFilePresenter;

		this.lastFilePath = lastFilePath;
		this.lastFilePathChangeListener = lastFilePathChangeListener;

		loadSVGConfig();

		addListener();
	}

	private void addListener() {
		view.addSaveSVGConfigButtonListener(this::saveSVGConfig);
		view.addExportButtonListener(this::export);
		view.setFilterInitializationListener(this::createSubfaceToOverlapRelationIndices);
	}

	/**
	 * open export dialog for current folded estimation
	 */
	private void export() {
		try {

			lastFilePath = estimationResultFilePresenter.export(view, lastFilePath);

			lastFilePathChangeListener.accept(lastFilePath);

		} catch (Exception ex) {
			logger.error("error: ", ex);
			view.showExportErrorMessage(ex);
		}
	}

	private void saveSVGConfig() {
		try {
			svgConfigFileAccess.save(estimationResultFilePresenter.createSVGConfig(view));
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

	private Map<Integer, List<Set<Integer>>> createSubfaceToOverlapRelationIndices(
			final FoldedModel foldedModel) {
		return new SubfaceToOverlapRelationIndicesFactory().create(foldedModel);
	}

}
