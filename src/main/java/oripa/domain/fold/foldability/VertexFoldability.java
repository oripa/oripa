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
package oripa.domain.fold.foldability;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.util.rule.AbstractRule;

/**
 * An implementation of local flat foldablity test according to erik demaine's
 * book "Geometric Folding Algorithms - Linkages, Origami, Polyhedra".
 *
 * @author OUCHI Koji
 *
 */
public class VertexFoldability extends AbstractRule<OriVertex> {

	private static final Logger logger = LoggerFactory.getLogger(VertexFoldability.class);

	@Override
	public boolean holds(final OriVertex vertex) {
		var ring = createRing(vertex);
		var minimalIndices = new MinimalAngleIndexManager(ring);
		var helper = new AngleMinimalityHelper();

		int minimalIndex;

		while (ring.size() > 2) {
			do {
				do {
					if (minimalIndices.isEmpty()) {
						return false;
					}
					minimalIndex = minimalIndices.pop();
				} while (!ring.exists(minimalIndex));
			} while (!helper.isMinimal(ring, minimalIndex));

			logger.debug("fold {}th gap", minimalIndex);

			var mergedRingIndex = helper.foldPartially(ring, minimalIndex);

			minimalIndices.pushIfMinimal(ring, mergedRingIndex);
			minimalIndices.pushIfMinimal(ring, ring.getNext(mergedRingIndex).getRingIndex());
			minimalIndices.pushIfMinimal(ring, ring.getPrevious(mergedRingIndex).getRingIndex());

		}

		logger.debug(ring.toString());

		if (ring.size() == 0) {
			return true;
		}

		if (ring.size() != 2) {
			return false;
		}

		var head = ring.head();
		var tail = ring.tail();
		if (Math.abs(head.getAngle() - tail.getAngle()) > AngleMinimalityHelper.EPS) {
			return false;
		}
		if (head.getLineType() != tail.getLineType()) {
			return false;
		}

		return true;

//		bool isFoldable(mylib::RingArrayList<LineGap>& ring) {
//			MinimalAngleIndexManager minimals(ring);
//
//			while (ring.count() > 2) {
//				FoldabilityRingArrayHelper helper;
//
//				int minimalIndex;
//				//!!! the book lacks this check.
//				// ignore old items.
//				do {
//					do {
//						if (minimals.empty()) return false;
//						minimalIndex = minimals.pop();
//					} while (!ring.exists(minimalIndex));
//				} while (!helper.isMinimalToBeFolded(ring, minimalIndex));
//
//				minimalIndex = helper.foldPartially(ring, minimalIndex);
//
//				//!!! the book's explanation is too ambiguous.
//				// we have to check 3 angles: merged angle and its surroundings!
//
//				minimals.pushIfMinimalToBeFolded(ring, minimalIndex);
//				minimals.pushIfMinimalToBeFolded(ring, ring.nextIndexOf(minimalIndex));
//				minimals.pushIfMinimalToBeFolded(ring, ring.prevIndexOf(minimalIndex));
//			}
//
//			if (ring.count() == 0)
//				return true;
//
//			if (ring.count() == 2 && (ring.head() == ring.tail()))
//				return true;

	}

	private RingArrayList<LineGap> createRing(final OriVertex vertex) {
		var gaps = new ArrayList<LineGap>();

		for (int i = 0; i < vertex.edgeCount(); i++) {
			gaps.add(new LineGap(OriGeomUtil.getAngleDifference(vertex, i), vertex.getEdge(i).getType()));
		}

		return new RingArrayList<>(gaps);
	}

}
