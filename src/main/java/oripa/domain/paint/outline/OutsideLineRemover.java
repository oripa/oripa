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
package oripa.domain.paint.outline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class OutsideLineRemover {
	private static final Logger logger = LoggerFactory.getLogger(OutsideLineRemover.class);

	private final IsOnTempOutlineLoop isOnTempOutlineLoop;
	private final IsOutsideOfTempOutlineLoop isOutsideOfTempOutlineLoop;

	public OutsideLineRemover(final IsOnTempOutlineLoop isOnTempOutlineLoop,
			final IsOutsideOfTempOutlineLoop isOutsideOfTempOutlineLoop) {
		this.isOnTempOutlineLoop = isOnTempOutlineLoop;
		this.isOutsideOfTempOutlineLoop = isOutsideOfTempOutlineLoop;
	}

	public void removeLinesOutsideOfOutlines(final Painter painter,
			final Collection<Vector2d> outlineVertices) {
		var creasePattern = painter.getCreasePattern();
		var toBeRemoved = new ArrayList<OriLine>();

		for (OriLine line : creasePattern) {
			if (line.isBoundary()) {
				continue;
			}
			double eps = creasePattern.getPaperSize() * 0.001;
			var onPoint0 = isOnTempOutlineLoop.execute(outlineVertices, line.getP0(), eps);
			var onPoint1 = isOnTempOutlineLoop.execute(outlineVertices, line.getP1(), eps);

			logger.debug("line = " + line);
			logger.debug("onPoint0 = " + onPoint0);
			logger.debug("onPoint1 = " + onPoint1);
			// meaningless?
//			if (onPoint0 != null && onPoint0 == onPoint1) {
//				toBeRemoved.add(line);
//				logger.debug("line is removed: it's on contour.");
//			}

			var isOutsideP0 = isOutsideOfTempOutlineLoop.execute(outlineVertices, line.getP0());
			var isOutsideP1 = isOutsideOfTempOutlineLoop.execute(outlineVertices, line.getP1());

			logger.debug(String.join(",", outlineVertices.stream()
					.map(v -> v.toString()).collect(Collectors.toList())));
			logger.debug("isOutsideP0 = " + isOutsideP0);
			logger.debug("isOutsideP1 = " + isOutsideP1);

			if ((!onPoint0 && isOutsideP0) || (!onPoint1 && isOutsideP1)) {
				toBeRemoved.add(line);
				logger.debug("line is removed: it's outside of contour.");
			}
		}

		painter.removeLines(toBeRemoved);
	}

}
