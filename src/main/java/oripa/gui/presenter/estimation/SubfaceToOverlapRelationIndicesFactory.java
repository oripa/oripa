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
import oripa.util.Pair;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;

/**
 * @author OUCHI Koji
 *
 */
class SubfaceToOverlapRelationIndicesFactory {
	private static final Logger logger = LoggerFactory.getLogger(SubfaceToOverlapRelationIndicesFactory.class);

	private class OrderValue extends Pair<List<Integer>, Byte> {

		public OrderValue(final int i, final int j, final byte value) {
			super(List.of(i, j), value);
		}
	}

	public Map<Integer, List<Set<Integer>>> create(final FoldedModel foldedModel) {

		var watch = new StopWatch(true);
		logger.debug("start");

		var map = new ConcurrentHashMap<Integer, List<Set<Integer>>>();
		var orders = new ConcurrentHashMap<Integer, Map<Set<OrderValue>, Set<Integer>>>();

		var subfaces = foldedModel.getSubfaces();
		var overlapRelations = foldedModel.getOverlapRelations();

		// initialize
		for (int s = 0; s < subfaces.size(); s++) {
			orders.put(s, new ConcurrentHashMap<>());
		}

		// shortcut
		if (subfaces.size() == 1) {
			var list = new ArrayList<Set<Integer>>();
			map.put(0, list);
			var set = new HashSet<Integer>();
			list.add(set);
			for (int k = 0; k < overlapRelations.size(); k++) {
				set.add(k);
			}
			return map;
		}

		IntStream.range(0, overlapRelations.size()).parallel().forEach(k -> {
			var overlapRelation = overlapRelations.get(k);

			IntStream.range(0, subfaces.size()).parallel().forEach(s -> {

				var orderKey = createOrderKey(subfaces.get(s), overlapRelation);
				var indices = orders.get(s).get(orderKey);
				if (indices == null) {
					indices = CollectionUtil.newConcurrentHashSet();
					orders.get(s).put(orderKey, indices);
				}
				indices.add(k);
			});
		});

		IntStream.range(0, subfaces.size()).parallel().forEach(s -> {
			var orderToOverlapRelationIndices = orders.get(s);
			map.put(s, new ArrayList<>());

			for (var orderKey : orderToOverlapRelationIndices.keySet()) {
				var indices = orderToOverlapRelationIndices.get(orderKey);
				map.get(s).add(indices);
			}
		});

		logger.debug("end: {}[ms]", watch.getMilliSec());

		return map;
	}

	private Set<OrderValue> createOrderKey(final SubFace subface, final OverlapRelation overlapRelation) {
		var order = new HashSet<OrderValue>();

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

		return order;
	}
}
