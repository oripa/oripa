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
package oripa.domain.fold.subface;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.CrossingLineSplitter;
import oripa.domain.cptool.OverlappingLineMerger;
import oripa.domain.cptool.PointsMerger;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.fold.halfedge.OriFace;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FacesToCreasePatternConverter {
    private static final Logger logger = LoggerFactory
            .getLogger(FacesToCreasePatternConverter.class);

    private final CreasePatternFactory cpFactory;
    private final CrossingLineSplitter lineSplitter;
    private final PointsMerger pointsMerger;
    private final OverlappingLineMerger overlapMerger;

    /**
     * Constructor
     */
    public FacesToCreasePatternConverter(final CreasePatternFactory cpFactory,
            final CrossingLineSplitter lineSplitter,
            final PointsMerger pointsMerger,
            final OverlappingLineMerger overlapMerger) {
        this.cpFactory = cpFactory;
        this.lineSplitter = lineSplitter;
        this.pointsMerger = pointsMerger;
        this.overlapMerger = overlapMerger;
    }

    /**
     * construct edge structure after folding as a crease pattern for easy
     * calculation to obtain subfaces.
     *
     * @param faces
     *            faces after fold without layer ordering.
     * @return
     */
    public CreasePattern convertToCreasePattern(final List<OriFace> faces, final double paperSize,
            final double pointEps) {
        logger.info("toCreasePattern(): construct edge structure after folding");

        var filteredFaces = faces.stream()
                .map(face -> face.remove180degreeVertices(pointEps))
                .map(face -> face.removeDuplicatedVertices(pointEps))
                .filter(face -> face.halfedgeCount() >= 3)
                .toList();

        Collection<OriLine> faceLines = new HashSet<OriLine>();
        for (OriFace face : filteredFaces) {
            faceLines.addAll(face.halfedgeStream()
                    .map(he -> new OriLine(he.getPosition(), he.getNext().getPosition(),
                            OriLine.Type.MOUNTAIN).createCanonical())
                    .toList());
        }

//		try {
//			var creasePattern = cpFactory.createCreasePattern(faceLines);
//			// creasePattern.forEach(line -> logger.debug("{}", line));
//			new ExporterCP().export(oripa.persistence.doc.Doc.forSaving(creasePattern, null), "debug_convert.cp", null);
//		} catch (IllegalArgumentException | IOException e) {
//		}

        logger.info("merge ignoring type");
        // put segments in a collection
        faceLines = overlapMerger.mergeIgnoringType(faceLines, pointEps);

//		try {
//			var creasePattern = cpFactory.createCreasePattern(faceLines);
//			// creasePattern.forEach(line -> logger.debug("{}", line));
//			new ExporterCP().export(oripa.persistence.doc.Doc.forSaving(creasePattern, null), "debug_merge.cp", null);
//		} catch (IllegalArgumentException | IOException e) {
//		}

        // make cross
        logger.info("split {} lines", faceLines.size());
        faceLines = lineSplitter.splitIgnoringType(faceLines, pointEps);

        logger.info("merge close points");
        faceLines = pointsMerger.mergeClosePoints(faceLines, pointEps);

//		try {
//			var creasePattern = cpFactory.createCreasePattern(faceLines);
//			// creasePattern.forEach(line -> logger.debug("{}", line));
//			new ExporterCP().export(oripa.persistence.doc.Doc.forSaving(creasePattern, null), "debug_split.cp", null);
//		} catch (IllegalArgumentException | IOException e) {
//		}

        CreasePattern creasePattern = cpFactory.createCreasePattern(faceLines);

        logger.debug("toCreasePattern(): {} segments", creasePattern.size());

        return creasePattern;
    }

}
