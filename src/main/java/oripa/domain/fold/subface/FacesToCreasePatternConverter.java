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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.LineAdder;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FacesToCreasePatternConverter {
	private static final Logger logger = LoggerFactory
			.getLogger(FacesToCreasePatternConverter.class);

	private final CreasePatternFactory cpFactory;
	private final LineAdder lineAdder;

	/**
	 * Constructor
	 */
	public FacesToCreasePatternConverter(final CreasePatternFactory cpFactory,
			final LineAdder lineAdder) {
		this.cpFactory = cpFactory;
		this.lineAdder = lineAdder;
	}

	/**
	 * construct edge structure after folding as a crease pattern for easy
	 * calculation to obtain subfaces.
	 *
	 * @param faces
	 *            faces after fold without layer ordering.
	 * @return
	 */
	public CreasePatternInterface convertToCreasePattern(final List<OriFace> faces) {
		logger.debug("toCreasePattern(): construct edge structure after folding");

		var lines = new ArrayList<OriLine>();
		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				OriLine line = new OriLine(he.getPosition(), he.getNext().getPosition(),
						OriLine.Type.MOUNTAIN);
				// make cross every time to divide the faces.
				// addLines() cannot make cross among given lines.
				lineAdder.addLine(line, lines);
			}
		}
		CreasePatternInterface creasePattern = cpFactory.createCreasePattern(lines);
		creasePattern.cleanDuplicatedLines();

		logger.debug("toCreasePattern(): end");

		return creasePattern;
	}

}
