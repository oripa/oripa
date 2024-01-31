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
package oripa.domain.fold.condfac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.geom.GeomUtil;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
public class StackConditionOf4FaceFactory {
	private static final Logger logger = LoggerFactory.getLogger(StackConditionOf4FaceFactory.class);

	/**
	 * Creates 4-face condition.
	 *
	 * @param faces
	 *            all faces of the model
	 * @param edges
	 *            all edges of the model
	 * @param overlapRelation
	 *            overlap relation matrix
	 * @param subFacesOfEachFace
	 *            mapping face to subface set
	 */
	public List<StackConditionOf4Faces> createAll(final List<OriFace> faces,
			final List<OriEdge> edges, final OverlapRelation overlapRelation,
			final Map<OriFace, Set<SubFace>> subFacesOfEachFace,
			final double eps) {
		var condition4s = new ArrayList<StackConditionOf4Faces>();

		int edgeNum = edges.size();
		logger.debug("edgeNum = " + edgeNum);

		var watch = new StopWatch(true);

		for (int i = 0; i < edgeNum; i++) {
			OriEdge e0 = edges.get(i);
			var e0LeftOpt = e0.getLeft();
			var e0RightOpt = e0.getRight();

			if (e0LeftOpt.isEmpty() || e0RightOpt.isEmpty()) {
				continue;
			}

			for (int j = i + 1; j < edgeNum; j++) {
				OriEdge e1 = edges.get(j);
				var e1LeftOpt = e1.getLeft();
				var e1RightOpt = e1.getRight();
				if (e1LeftOpt.isEmpty() || e1RightOpt.isEmpty()) {
					continue;
				}

				if (!GeomUtil.isOverlap(e0.toSegment(), e1.toSegment(), eps)) {
					continue;
				}

				var e0LeftFace = e0LeftOpt.get().getFace();
				var e0RightFace = e0RightOpt.get().getFace();
				var e1LeftFace = e1LeftOpt.get().getFace();
				var e1RightFace = e1RightOpt.get().getFace();

				var intersectionSubfaces = subFacesOfEachFace.get(e0LeftFace).stream()
						.filter(s -> subFacesOfEachFace.get(e0RightFace).contains(s))
						.filter(s -> subFacesOfEachFace.get(e1LeftFace).contains(s))
						.filter(s -> subFacesOfEachFace.get(e1RightFace).contains(s))
						.toList();

				if (intersectionSubfaces.isEmpty()) {
					continue;
				}

				StackConditionOf4Faces cond = new StackConditionOf4Faces();

				var e0LeftFaceID = e0LeftFace.getFaceID();
				var e0RightFaceID = e0RightFace.getFaceID();
				var e1LeftFaceID = e1LeftFace.getFaceID();
				var e1RightFaceID = e1RightFace.getFaceID();

				if (overlapRelation.isUpper(e0LeftFaceID, e0RightFaceID)) {
					cond.upper1 = e0RightFaceID;
					cond.lower1 = e0LeftFaceID;
				} else {
					cond.upper1 = e0LeftFaceID;
					cond.lower1 = e0RightFaceID;
				}
				if (overlapRelation.isUpper(e1LeftFaceID, e1RightFaceID)) {
					cond.upper2 = e1RightFaceID;
					cond.lower2 = e1LeftFaceID;
				} else {
					cond.upper2 = e1LeftFaceID;
					cond.lower2 = e1RightFaceID;
				}

				condition4s.add(cond);
			}
		}

		logger.debug("#condition4 = {}", condition4s.size());
		logger.debug("condition4s computation time {}[ms]", watch.getMilliSec());

		return condition4s;
	}

}
