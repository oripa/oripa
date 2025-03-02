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
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * Create this instance via {@link SharedPointsMapFactory}.
 *
 * @author OUCHI Koji
 *
 */
public class SharedPointsMap<P extends PointAndOriLine> extends TreeMap<OriPoint, ArrayList<P>> {
	private static final Logger logger = LoggerFactory.getLogger(SharedPointsMap.class);

	private final BiFunction<OriPoint, OriLine, P> factory;

	SharedPointsMap(final BiFunction<OriPoint, OriLine, P> factory) {
		super();
		this.factory = factory;
	}

	private boolean validateKeyPoints(final OriPoint keyPoint,
			final OriPoint oppositeKeyPoint, final OriLine line,
			final double eps) {
		if (line.getP0().equals(keyPoint, eps) && line.getP1().equals(oppositeKeyPoint, eps)) {
			return true;
		}

		if (line.getP0().equals(oppositeKeyPoint, eps) && line.getP1().equals(keyPoint, eps)) {
			return true;
		}

		return false;
	}

	private Optional<OriPoint> findOppositeCandidate(final P point, final OriPoint keyPoint,
			final double eps) {
		var oppositeKeyPoint = findKeyPoint(point.getOppsitePoint(keyPoint, eps), eps);

		var line = point.getLine();
		if (validateKeyPoints(keyPoint, oppositeKeyPoint, line, eps)) {
			return Optional.of(oppositeKeyPoint);
		} else {
			logger.trace("failed to get opposite key point quickly: line: " + line
					+ " oppositeKeyPoint: " + oppositeKeyPoint);
		}

		return Optional.empty();
	}

	public OriPoint findKeyPoint(final OriPoint p, final double eps) {
		var boundMap = CollectionUtil.rangeMapInclusive(
				this,
				new OriPoint(p.getX() - eps, p.getY() - eps),
				new OriPoint(p.getX() + eps, p.getY() + eps));

		if (boundMap.containsKey(p)) {
			return p;
		}

		var pointOpt = p.findNearest(boundMap.keySet());

		var distance = pointOpt.get().distance(p);
		if (distance > eps) {
			logger.error("nearest of {} is {} (distance {}) from boundMapKeys {}", p, pointOpt.get(), distance,
					boundMap.keySet());
		}

		return pointOpt.get();
	}

	public Optional<OriPoint> findOppositeKeyPoint(final P point, final OriPoint keyPoint,
			final double eps) {

		var oppositeKeyPointOpt = findOppositeCandidate(point, keyPoint, eps);
		if (oppositeKeyPointOpt.isPresent()) {
			return oppositeKeyPointOpt;
		}

		var fromKeySet = keySet().stream()
				.filter(opposite -> validateKeyPoints(keyPoint, opposite, point.getLine(), eps))
				.findFirst()
				.map(opposite -> {
					logger.trace("get opposite key point: keyPoint: " + keyPoint
							+ " oppositeKeyPoint: " + opposite);
					return opposite;
				});

		if (fromKeySet.isPresent()) {
			return fromKeySet;
		}

		return Optional.empty();
	}

	/**
	 * Removes {@code point} and its opposite from this object using OriLine
	 * quality.
	 *
	 * @param point
	 */
	public void removeBothSides(final P point) {
		get(point.getKeyPoint()).removeIf(point::lineEquals);
		get(point.getOppositeKeyPoint()).removeIf(point::lineEquals);
	}

	/**
	 * Adds both sides of given {@code line} to this object.
	 *
	 * @param line
	 * @param pointEps
	 */
	public void addBothSidesOfLine(
			final OriLine line,
			final double pointEps) {
		var endPoints = line.oriPointStream()
				.map(point -> factory.apply(point, line))
				.toList();

		var keyPoints = line.oriPointStream()
				.map(point -> findKeyPoint(point, pointEps))
				.toList();

		endPoints.get(0).setKeyPoint(keyPoints.get(0));
		endPoints.get(0).setOppositeKeyPoint(keyPoints.get(1));
		endPoints.get(1).setKeyPoint(keyPoints.get(1));
		endPoints.get(1).setOppositeKeyPoint(keyPoints.get(0));

		IntStream.range(0, endPoints.size()).forEach(i -> {
			get(keyPoints.get(i)).add(endPoints.get(i));
		});

	}

}
