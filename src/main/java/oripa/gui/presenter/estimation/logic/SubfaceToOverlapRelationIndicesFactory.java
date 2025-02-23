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
package oripa.gui.presenter.estimation.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.subface.SubFace;
import oripa.util.BitSet;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
public class SubfaceToOverlapRelationIndicesFactory {
	private static final Logger logger = LoggerFactory.getLogger(SubfaceToOverlapRelationIndicesFactory.class);

	private record OrderKey(List<Integer> faceIDs) {
	}

	/**
	 *
	 * @param foldedModel
	 * @return mapping subface index to a list of index sets on overlap relation
	 *         list. the first set of each list contains all indices as a "no
	 *         filtering" option.
	 */
	public Map<Integer, List<BitSet>> create(final FoldedModel foldedModel) {

		var watch = new StopWatch(true);
		logger.debug("start");

		var map = new HashMap<Integer, List<BitSet>>();
		var orders = new ConcurrentHashMap<Integer, Map<OrderKey, BitSet>>();

		var subfaces = foldedModel.subfaces();
		var overlapRelations = foldedModel.overlapRelations();

		// initialize
		for (int s = 0; s < subfaces.size(); s++) {
			orders.put(s, new ConcurrentHashMap<>());
			map.put(s, new ArrayList<BitSet>());
		}

		// set "no filtering" option
		IntStream.range(0, subfaces.size()).forEach(s -> {
			var list = map.get(s);
			var indices = new BitSet(overlapRelations.size());
			for (int k = 0; k < overlapRelations.size(); k++) {
				indices.add(k);
			}
			list.add(indices);
		});

		// shortcut
		if (subfaces.size() == 1) {
			logger.debug("shortcut (only one subface)");
			return map;
		}

		// O(n^3 log n S) time and O(n^3 S) space to store the result
		// n: #face, S #overlapRelation
		IntStream.range(0, overlapRelations.size()).parallel().forEach(k -> {
			var overlapRelation = overlapRelations.get(k);

			// Parallelization on s is overkilling for usual PC since
			// parallelization on k works very well.
			IntStream.range(0, subfaces.size()).forEach(s -> {

				var orderKey = createOrderKey(subfaces.get(s), overlapRelation);

				var order = orders.get(s);
				order.putIfAbsent(orderKey, new BitSet(overlapRelations.size()));
				var indices = order.get(orderKey);

				indices.addSync(k);
			});
		});

		IntStream.range(0, subfaces.size()).forEach(s -> {
			var list = map.get(s);
			orders.get(s).forEach((orderKey, indices) -> list.add(indices));
		});

		logger.debug("end: {}[ms]", watch.getMilliSec());

		return map;
	}

	/**
	 * If we focus on a subface, the order of its parent faces is totally given
	 * by the overlap relation matrix. Therefore we can sort the parent faces.
	 *
	 * @param subface
	 * @param overlapRelation
	 * @return
	 */
	private OrderKey createOrderKey(final SubFace subface, final OverlapRelation overlapRelation) {

		var parentFaceIDs = new ArrayList<Integer>(subface.getParentFaceCount());
		for (int i = 0; i < subface.getParentFaceCount(); i++) {
			var faceID = subface.getParentFace(i).getFaceID();
			parentFaceIDs.add(faceID);
		}

		parentFaceIDs.sort((faceID_i, faceID_j) -> {
			if (faceID_i == faceID_j) {
				return 0;
			}
			if (overlapRelation.isUpper(faceID_i, faceID_j)) {
				return 1;
			}

			return -1;
		});

		return new OrderKey(parentFaceIDs);
	}
}
