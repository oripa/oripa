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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.estimation.EstimationResultFileAccess;
import oripa.application.estimation.FoldedModelSVGConfigFileAccess;
import oripa.domain.fold.FoldedModel;
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
import oripa.util.Pair;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;

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
		view.setFilterInitializationListener(this::createSubfaceToOverlapRelationIndices);
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

	private class OrderValue extends Pair<List<Integer>, Byte> {

		public OrderValue(final int i, final int j, final byte value) {
			super(List.of(i, j), value);
		}
	}

	private Map<Integer, List<Set<Integer>>> createSubfaceToOverlapRelationIndices(
			final FoldedModel foldedModel) {

		var watch = new StopWatch(true);
		logger.debug("createSubfaceToOverlapRelationIndices() start");

		var map = new ConcurrentHashMap<Integer, List<Set<Integer>>>();
		var orders = new ConcurrentHashMap<Integer, Map<Set<OrderValue>, Set<Integer>>>();

		var subfaces = foldedModel.getSubfaces();
		var overlapRelations = foldedModel.getOverlapRelations();

		// initialize
		for (int s = 0; s < subfaces.size(); s++) {
			orders.put(s, new ConcurrentHashMap<>());
		}

		IntStream.range(0, overlapRelations.size()).parallel().forEach(k -> {
			var overlapRelation = overlapRelations.get(k);

			IntStream.range(0, subfaces.size()).forEach(s -> {
				var subface = subfaces.get(s);
				Set<OrderValue> order = CollectionUtil.newConcurrentHashSet();

				for (int i = 0; i < subface.getParentFaceCount(); i++) {
					var face_i = subface.getParentFace(i);
					for (int j = i + 1; j < subface.getParentFaceCount(); j++) {
						var face_j = subface.getParentFace(j);

						var smallerIndex = Math.min(face_i.getFaceID(), face_j.getFaceID());
						var largerIndex = Math.max(face_i.getFaceID(), face_j.getFaceID());
						var relation = overlapRelation.get(smallerIndex, largerIndex);

						var value = new OrderValue(smallerIndex, largerIndex, relation);

						order.add(value);
					}
				}

				var indices = orders.get(s).get(order);
				if (indices == null) {
					indices = CollectionUtil.newConcurrentHashSet();
					orders.get(s).put(order, indices);
				}
				indices.add(k);
			});
		});

		IntStream.range(0, subfaces.size()).parallel().forEach(s -> {
			var orderToOverlapRelationIndices = orders.get(s);
			map.put(s, Collections.synchronizedList(new ArrayList<>()));

			for (var order : orderToOverlapRelationIndices.keySet()) {
				var indices = orderToOverlapRelationIndices.get(order);
				map.get(s).add(indices);
			}
		});

		logger.debug("createSubfaceToOverlapRelationIndices() end: {}[ms]", watch.getMilliSec());

		return map;
	}

}
