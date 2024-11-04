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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class SharedPointsMap<P extends PointAndLine> extends TreeMap<OriPoint, ArrayList<P>> {
	private static final Logger logger = LoggerFactory.getLogger(SharedPointsMap.class);

	public SharedPointsMap() {
		super();
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

	private Optional<OriPoint> findOppositeCandidate(final OriLine line, final OriPoint keyPoint,
			final double eps) {
		var oppositeKeyPoint = line.getP0().equals(keyPoint, eps)
				? findKeyPoint(line.getOriPoint1(), eps)
				: findKeyPoint(line.getOriPoint0(), eps);

		if (validateKeyPoints(keyPoint, oppositeKeyPoint, line, eps)) {
			return Optional.of(oppositeKeyPoint);
		} else {
			logger.trace("failed to get opposite key point quickly: line: " + line
					+ " oppositeKeyPoint: " + oppositeKeyPoint);
		}

		return Optional.empty();
	}

	public OriPoint findKeyPoint(final OriPoint p, final double eps) {
		var boundMap = CollectionUtil.rangeMap(
				this,
				new OriPoint(p.getX() - eps, p.getY() - eps),
				new OriPoint(p.getX() + eps, p.getY() + eps));

		if (boundMap.containsKey(p)) {
			return p;
		}
		return boundMap.keySet().stream()
				.filter(key -> key.equals(p, eps))
				.findFirst().get();
	}

	public Optional<OriPoint> findOppositeKeyPoint(final P point, final OriPoint keyPoint,
			final double eps) {

		var oppositeKeyPointOpt = findOppositeCandidate(point.getLine(), keyPoint, eps);
		if (oppositeKeyPointOpt.isPresent()) {
			return oppositeKeyPointOpt;
		}

		return this.keySet().parallelStream()
				.filter(opposite -> validateKeyPoints(keyPoint, opposite, point.getLine(), eps))
				.findFirst()
				.map(opposite -> {
					logger.trace("get opposite key point: keyPoint: " + keyPoint
							+ " oppositeKeyPoint: " + opposite);
					return opposite;
				});
	}

}
