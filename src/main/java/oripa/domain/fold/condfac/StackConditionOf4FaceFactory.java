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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

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
        var condition4s = new ConcurrentLinkedQueue<StackConditionOf4Faces>();

        int edgeNum = edges.size();
        logger.debug("edgeNum = " + edgeNum);

        var watch = new StopWatch(true);

        IntStream.range(0, edgeNum).parallel().forEach(i -> {
            OriEdge e0 = edges.get(i);
            var e0_v0V1Opt = e0.getHalfedgeV0V1();
            var e0_v1V0Opt = e0.getHalfedgeV1V0();

            if (e0_v0V1Opt.isEmpty() || e0_v1V0Opt.isEmpty()) {
                return;
            }

            for (int j = i + 1; j < edgeNum; j++) {
                OriEdge e1 = edges.get(j);
                var e1_v0V1Opt = e1.getHalfedgeV0V1();
                var e1_v1V0Opt = e1.getHalfedgeV1V0();
                if (e1_v0V1Opt.isEmpty() || e1_v1V0Opt.isEmpty()) {
                    continue;
                }

                if (!GeomUtil.isOverlap(e0.toSegment(), e1.toSegment(), eps)) {
                    continue;
                }

                var e0_v0V1Face = e0_v0V1Opt.get().getFace();
                var e0_v1V0Face = e0_v1V0Opt.get().getFace();
                var e1_v0V1Face = e1_v0V1Opt.get().getFace();
                var e1_v1V0Face = e1_v1V0Opt.get().getFace();

                var intersectionSubfaces = subFacesOfEachFace.get(e0_v0V1Face).stream()
                        .filter(s -> subFacesOfEachFace.get(e0_v1V0Face).contains(s))
                        .filter(s -> subFacesOfEachFace.get(e1_v0V1Face).contains(s))
                        .filter(s -> subFacesOfEachFace.get(e1_v1V0Face).contains(s))
                        .toList();

                if (intersectionSubfaces.isEmpty()) {
                    continue;
                }

                var e0_v0V1FaceID = e0_v0V1Face.getFaceID();
                var e0_v1V0FaceID = e0_v1V0Face.getFaceID();
                var e1_v0V1FaceID = e1_v0V1Face.getFaceID();
                var e1_v1V0FaceID = e1_v1V0Face.getFaceID();

                int upper1, lower1, upper2, lower2;

                if (overlapRelation.isUpper(e0_v0V1FaceID, e0_v1V0FaceID)) {
                    upper1 = e0_v1V0FaceID;
                    lower1 = e0_v0V1FaceID;
                } else {
                    upper1 = e0_v0V1FaceID;
                    lower1 = e0_v1V0FaceID;
                }
                if (overlapRelation.isUpper(e1_v0V1FaceID, e1_v1V0FaceID)) {
                    upper2 = e1_v1V0FaceID;
                    lower2 = e1_v0V1FaceID;
                } else {
                    upper2 = e1_v0V1FaceID;
                    lower2 = e1_v1V0FaceID;
                }

                condition4s.add(new StackConditionOf4Faces(upper1, lower1, upper2, lower2));
            }
        });

        logger.debug("#condition4 = {}", condition4s.size());
        logger.debug("condition4s computation time {}[ms]", watch.getMilliSec());

        return condition4s.stream().toList();
    }

}
