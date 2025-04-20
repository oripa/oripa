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
package oripa.domain.cptool.compgeom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class SharedPointsMapFactory<P extends PointAndOriLine> {
	private static final Logger logger = LoggerFactory.getLogger(SharedPointsMapFactory.class);

	private ArrayList<P> createXOrderPoints(
			final ArrayList<OriLine> lines,
			final BiFunction<OriPoint, OriLine, P> factory) {
		var points = new ArrayList<P>(lines.size() * 2);

		for (var line : lines) {
			points.add(factory.apply(line.getOriPoint0(), line));
			points.add(factory.apply(line.getOriPoint1(), line));
		}

		points.sort(Comparator.comparing(PointAndOriLine::getX));

		return points;
	}

	/**
	 * the returned map keeps the both side of each line as an object holding
	 * the end point and the line. The map key is ordered by x-coordinate first
	 * and then by y-coordinate. In the values of each key are ordered by
	 * y-coordinate. The lines in returned map values and the one given to the
	 * factory are canonicalized, i.e., line.getP0() is smaller than
	 * line.getP1().
	 *
	 * @param creasePattern
	 * @param factory
	 * @param eps
	 * @return
	 */
	public SharedPointsMap<P> create(
			final Collection<OriLine> creasePattern,
			final BiFunction<OriPoint, OriLine, P> factory,
			final double eps) {
		// Sweep-line approach
		// (sweep along x axis)

		var canonicalLines = creasePattern.stream()
				.map(OriLine::createCanonical)
				.collect(Collectors.toCollection(ArrayList::new));

		var xOrderPoints = createXOrderPoints(canonicalLines, factory);
		var hashFactory = new HashFactory();
		var xOrderHash = hashFactory.create(xOrderPoints, P::getX, eps);

		for (var byX : xOrderHash) {
			byX.sort(Comparator.comparing(P::getY));
		}

		// this map keeps the both sides of each line as an object holding the
		// end point and the line.
		var sharedPointsMap = new SharedPointsMap<P>(factory);

		var setCount = 0;
		// build a map and set keyPoint
		for (var byX : xOrderHash) {
			var yHash = hashFactory.create(byX, P::getY, eps);
			for (var xyPoints : yHash) {
				if (xyPoints.isEmpty()) {
					continue;
				}
				var keyPoint = xyPoints.get(0).getPoint();
				sharedPointsMap.put(keyPoint, xyPoints);
				xyPoints.forEach(p -> p.setKeyPoint(keyPoint));
				setCount += xyPoints.size();
			}
		}

		if (setCount != creasePattern.size() * 2) {
			logger.error("wrong hashing. map creation fails.");
		}

		// set oppositeKeyPoint
		sharedPointsMap.forEach((keyPoint, points) -> {
			for (P point : points) {
				sharedPointsMap.findOppositeKeyPoint(point, keyPoint, eps)
						.ifPresentOrElse(
								opposite -> point.setOppositeKeyPoint(opposite),
								() -> logger.error(
										"failed to get opposite key point of {}, eps:{}, line: {}",
										keyPoint,
										eps,
										point.getLine()));
			}
		});

		return sharedPointsMap;
	}
}
