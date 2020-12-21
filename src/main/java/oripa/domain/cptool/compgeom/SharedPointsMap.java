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

import oripa.geom.GeomUtil;
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

	public boolean validateKeyPoints(final OriPoint keyPoint,
			final OriPoint oppositeKeyPoint, final OriLine line,
			final double eps) {
		if (GeomUtil.distance(line.p0, keyPoint) < eps
				&& GeomUtil.distance(line.p1, oppositeKeyPoint) < eps) {
			return true;
		}

		if (GeomUtil.distance(line.p0, oppositeKeyPoint) < eps
				&& GeomUtil.distance(line.p1, keyPoint) < eps) {
			return true;
		}

		return false;
	}

	private OriPoint findOppositeCandidate(final OriLine line, final OriPoint keyPoint,
			final double eps) {
		var oppositeKeyPoint = GeomUtil.distance(line.p0, keyPoint) < eps
				? findKeyPoint(line.p1, eps)
				: findKeyPoint(line.p0, eps);

		if (validateKeyPoints(keyPoint, oppositeKeyPoint, line, eps)) {
			return oppositeKeyPoint;
		} else {
			logger.trace("failed to get opposite key point quickly: line: " + line
					+ " oppositeKeyPoint: " + oppositeKeyPoint);
		}

		return null;
	}

	public OriPoint findKeyPoint(final OriPoint p, final double eps) {
		var boundMap = this.headMap(new OriPoint(p.getX() + eps, p.getY() + eps))
				.tailMap(new OriPoint(p.getX() - eps, p.getY() - eps));
		if (boundMap.containsKey(p)) {
			return p;
		}
		return boundMap.keySet().stream()
				.filter(key -> GeomUtil.distance(key, p) < eps)
				.findFirst().get();
	}

	public Optional<OriPoint> findOppositeKeyPoint(final P point, final OriPoint keyPoint,
			final double eps) {

		var oppositeKeyPoint = findOppositeCandidate(point.getLine(), keyPoint, eps);
		if (oppositeKeyPoint != null) {
			return Optional.of(oppositeKeyPoint);
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
