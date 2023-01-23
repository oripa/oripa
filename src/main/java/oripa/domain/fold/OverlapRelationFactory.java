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
package oripa.domain.fold;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.domain.fold.origeom.OverlapRelation;

/**
 * @author OUCHI Koji
 *
 */
class OverlapRelationFactory {
	private static final Logger logger = LoggerFactory.getLogger(OverlapRelationFactory.class);

	/**
	 * Determines the overlap relations by mountain/valley.
	 *
	 * @param overlapRelation
	 *            overlap relation matrix
	 */
	public OverlapRelation createOverlapRelationByLineType(
			final List<OriFace> faces, final double eps) {
		var overlapRelation = createOverlapRelation(faces, eps);
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedgeIterable()) {
				var pair = he.getPair();
				if (pair == null) {
					continue;
				}
				OriFace pairFace = pair.getFace();
				var faceID = face.getFaceID();
				var pairFaceID = pairFace.getFaceID();

				// If the relation is already decided, skip
				if (overlapRelation.isUpper(faceID, pairFaceID)
						|| overlapRelation.isLower(faceID, pairFaceID)) {
					continue;
				}

				var edge = he.getEdge();
				if ((face.isFaceFront() && edge.isMountain())
						|| (!face.isFaceFront() && edge.isValley())) {
					overlapRelation.setUpper(faceID, pairFaceID);
				} else {
					overlapRelation.setLower(faceID, pairFaceID);
				}
			}
		}

		return overlapRelation;
	}

	/**
	 * creates the matrix overlapRelation and fills it with "no overlap" or
	 * "undefined"
	 *
	 * @param paperSize
	 *            paper size before fold
	 * @return initialized overlap relation matrix
	 */
	private OverlapRelation createOverlapRelation(
			final List<OriFace> faces, final double eps) {

		int size = faces.size();
		OverlapRelation overlapRelation = new OverlapRelation(size);

		int countOfZeros = 0;
		for (int i = 0; i < size; i++) {
			overlapRelation.setNoOverlap(i, i);
			countOfZeros++;
			for (int j = i + 1; j < size; j++) {
				if (OriGeomUtil.isFaceOverlap(faces.get(i), faces.get(j), eps)) {
					overlapRelation.setUndefined(i, j);
				} else {
					overlapRelation.setNoOverlap(i, j);
					countOfZeros += 2;
				}
			}
		}

		double rate = ((double) countOfZeros) / (size * size);
		logger.debug("sparsity of overlap relation matrix = {}", rate);
		// One element in dictionary of keys for byte value needs at least 16
		// bytes.
		if (rate > 15.0 / 16) {
			logger.debug("use sparse matrix for overlap relation.");
			overlapRelation.switchToSparseMatrix();
		}

		return overlapRelation;
	}

}
