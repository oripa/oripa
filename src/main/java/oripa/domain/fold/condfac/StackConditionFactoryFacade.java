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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.stackcond.StackConditionOf3Faces;
import oripa.domain.fold.stackcond.StackConditionOf4Faces;
import oripa.domain.fold.subface.SubFace;
import oripa.util.StopWatch;

/**
 * @author OUCHI Koji
 *
 */
public class StackConditionFactoryFacade {
    private static final Logger logger = LoggerFactory.getLogger(StackConditionFactoryFacade.class);

    private final Map<OriFace, Set<SubFace>> subFacesOfEachFace;
    private final List<Integer>[][] overlappingFaceIndexIntersections;
    private final Map<OriHalfedge, Set<Integer>> faceIndicesOnHalfedge;

    private final List<OriFace> faces;
    private final List<OriEdge> edges;
    private final OverlapRelation overlapRelation;
    private final double eps;

    public StackConditionFactoryFacade(final List<OriFace> faces, final List<OriEdge> edges,
            final OverlapRelation overlapRelation,
            final List<SubFace> subfaces, final double eps) {

        this.faces = faces;
        this.edges = edges;
        this.overlapRelation = overlapRelation;
        this.eps = eps;

        var watch = new StopWatch(true);
        subFacesOfEachFace = new FaceToSubfacesFactory().create(faces, subfaces);
        logger.debug("create subfacesOfEachFace {}[ms]", watch.getMilliSec());

        watch.start();
        overlappingFaceIndexIntersections = new OverlappingFaceIndexIntersectionFactory().create(
                faces, overlapRelation);
        logger.debug("create overlappingFaceIndexIntersections {}[ms]", watch.getMilliSec());

        watch.start();
        faceIndicesOnHalfedge = new FaceIndicesOnHalfEdgeFactory().create(faces, eps);
        logger.debug("create faceIndicesOnHalfedge {}[ms]", watch.getMilliSec());

    }

    public List<StackConditionOf3Faces> create3FaceConditions() {
        return new StackConditionOf3FaceFactory().createAll(
                faces, overlapRelation, overlappingFaceIndexIntersections, faceIndicesOnHalfedge);
    }

    public List<StackConditionOf4Faces> create4FaceCondtions() {
        return new StackConditionOf4FaceFactory().createAll(
                faces, edges, overlapRelation, subFacesOfEachFace, eps);
    }

    public List<Integer>[][] getOverlappingFaceIndexIntersections() {
        return overlappingFaceIndexIntersections;
    }

    public Map<OriHalfedge, Set<Integer>> getFaceIndicesOnHalfedge() {
        return faceIndicesOnHalfedge;
    }
}
