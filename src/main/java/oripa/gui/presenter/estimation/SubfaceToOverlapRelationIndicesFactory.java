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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.subface.SubFace;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;

/**
 * @author OUCHI Koji
 *
 */
class SubfaceToOverlapRelationIndicesFactory {
	private static final Logger logger = LoggerFactory.getLogger(SubfaceToOverlapRelationIndicesFactory.class);

	private record OrderValue(List<Integer> v1, Byte v2) {

		public static OrderValue create(final int i, final int j, final byte value) {
			return new OrderValue(List.of(i, j), value);
		}
	}

	/**
	 *
	 * @param foldedModel
	 * @return mapping subface index to a list of index sets on overlap relation
	 *         list. the first set of each list contains all indices as a "no
	 *         filtering" option.
	 */
	public Map<Integer, List<Set<Integer>>> create(final FoldedModel foldedModel) {

		var watch = new StopWatch(true);
		logger.debug("start");

		var map = new HashMap<Integer, List<Set<Integer>>>();
		var orders = new ConcurrentHashMap<Integer, Map<Set<OrderValue>, Set<Integer>>>();

		var subfaces = foldedModel.subfaces();
		var overlapRelations = foldedModel.overlapRelations();

		// initialize
		for (int s = 0; s < subfaces.size(); s++) {
			orders.put(s, new ConcurrentHashMap<>());
			map.put(s, new ArrayList<Set<Integer>>());
		}

		// set "no filtering" option
		IntStream.range(0, subfaces.size()).forEach(s -> {
			var list = map.get(s);
			var indices = new HashSet<Integer>();
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

		IntStream.range(0, overlapRelations.size()).parallel().forEach(k -> {
			var overlapRelation = overlapRelations.get(k);

			// Parallelization on s is overkilling for usual PC since
			// parallelization on k works very well.
			IntStream.range(0, subfaces.size()).forEach(s -> {

				var orderKey = createOrderKey(subfaces.get(s), overlapRelation);

				var order = orders.get(s);
				order.putIfAbsent(orderKey, CollectionUtil.newConcurrentHashSet());
				var indices = order.get(orderKey);

				indices.add(k);
			});
		});

		IntStream.range(0, subfaces.size()).forEach(s -> {
			var list = map.get(s);
			orders.get(s).forEach((orderKey, indices) -> list.add(indices));
		});

		logger.debug("end: {}[ms]", watch.getMilliSec());

		return map;
	}

	private Set<OrderValue> createOrderKey(final SubFace subface, final OverlapRelation overlapRelation) {
		var orderKey = new HashSet<OrderValue>();

		for (int i = 0; i < subface.getParentFaceCount(); i++) {
			var faceID_i = subface.getParentFace(i).getFaceID();
			for (int j = i + 1; j < subface.getParentFaceCount(); j++) {
				var faceID_j = subface.getParentFace(j).getFaceID();

				var smallerIndex = Math.min(faceID_i, faceID_j);
				var largerIndex = Math.max(faceID_i, faceID_j);
				var relation = overlapRelation.get(smallerIndex, largerIndex);

				orderKey.add(OrderValue.create(smallerIndex, largerIndex, relation));
			}
		}

		return orderKey;
	}
}
