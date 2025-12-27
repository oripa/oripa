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
package oripa.domain.cptool;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.compgeom.AngleInterceptGettable;
import oripa.domain.cptool.compgeom.AngleInterceptHashFactory;
import oripa.geom.GeomUtil;
import oripa.util.Pair;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class AnalyticOverlappingLineMerger implements OverlappingLineMerger {

	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static class MyPointAndOriLineFactory {
		public static List<MyPointAndOriLine> createWithCanoincalization(final OriLine line) {

			var list = create(line).stream().sorted().toList();

			list.get(0).isLeft = true;
			list.get(1).isLeft = false;

			return list;
		}

		public static List<MyPointAndOriLine> create(final OriLine line) {
			var left = asLeft(line.getOriPoint0(), line);
			var right = asRight(line.getOriPoint1(), line);

			left.opposite = right;
			right.opposite = left;

			left.oppositeKey = right.getKey();
			right.oppositeKey = left.getKey();

			return List.of(left, right);
		}

		public static MyPointAndOriLine asLeft(final OriPoint point, final OriLine line) {
			var left = new MyPointAndOriLine(point, line, true);
			left.key = toSweepKey(left);

			return left;
		}

		public static MyPointAndOriLine asRight(final OriPoint point, final OriLine line) {
			var right = new MyPointAndOriLine(point, line, false);
			right.key = toSweepKey(right);

			return right;
		}

	}

	private static class MyPointAndOriLine implements Comparable<MyPointAndOriLine>, AngleInterceptGettable {
		private final OriPoint point;
		private final OriLine line;
		private boolean isLeft;

		private final double intercept;
		private final double coord;

		private Key key;
		private Key oppositeKey;

		private MyPointAndOriLine opposite;

		private MyPointAndOriLine(final OriPoint point, final OriLine line, final boolean isLeft) {
			this.point = point;
			this.line = line;
			this.isLeft = isLeft;

			if (line.pointStream().noneMatch(point::equals)) {
				throw new IllegalArgumentException(
						"point " + point + " should be equal to the one of the segment " + line + " end point.");
			}

			var p0 = line.getP0();

			if (line.isVertical()) {
				// use x-intercept
				intercept = p0.getX();
				coord = point.getY();
			} else {
				// use y-intercept
				intercept = line.getAffineYValueAt(0);
				coord = point.getX();
			}

		}

		public boolean isLeft() {
			return isLeft;
		}

		public boolean isRight() {
			return !isLeft;
		}

		public MyPointAndOriLine getLeft() {
			return isLeft ? this : opposite;
		}

		public MyPointAndOriLine getRight() {
			return isRight() ? this : opposite;
		}

		@Override
		public double getAngle() {
			return line.getAngle();
		}

		/**
		 * @return line
		 */
		public OriLine getLine() {
			return line;
		}

		public double getCoord() {
			return coord;
		}

		@Override
		public double getIntercept() {
			return intercept;
		}

		public Key getKey() {
			return key;
		}

		@Override
		public int compareTo(final MyPointAndOriLine o) {

			var comp = key.compareTo(o.key);

			if (comp == 0) {
				return oppositeKey.compareTo(o.oppositeKey);
			}

			return comp;
		}

		@Override
		public int hashCode() {
			return Objects.hash(point, opposite.point);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}

			if (obj instanceof MyPointAndOriLine other) {
				return compareTo(other) == 0;
			}
			return true;
		}

		@Override
		public String toString() {
			return (isLeft ? "left " : "right ") + line.toString();
		}
	}

	private static record Key(double intercept, double coord) implements Comparable<Key> {

		@Override
		public int compareTo(final Key other) {
//			if (angle != other.angle) {
//				return (int) Math.signum(angle - other.angle);
//			}
			if (coord != other.coord) {
				return (int) Math.signum(coord - other.coord);

			}

			return (int) Math.signum(intercept - other.intercept);
		}
	}

	private static Key toSweepKey(final MyPointAndOriLine p) {
		return new Key(p.intercept, p.getCoord());
	}

	@Override
	public Collection<OriLine> mergeIgnoringType(final Collection<OriLine> inputLines, final double eps) {

		return runNaive(inputLines, eps);
	}

	private Collection<OriLine> runNaive(final Collection<OriLine> inputLines, final double eps) {
		var results = new HashSet<OriLine>();

		var lines = inputLines.stream().filter(l -> l.length() >= eps).toList();

		var hashFactory = new AngleInterceptHashFactory<MyPointAndOriLine>(eps);
		var hash = hashFactory.create(lines, line -> MyPointAndOriLineFactory.createWithCanoincalization(line).get(0));

		for (var byAngle : hash) {
			for (var byIntercept : byAngle) {
				results.addAll(naive(byIntercept, eps));
			}
		}

		return results;

	}

	private Collection<OriLine> naive(final Collection<MyPointAndOriLine> byIntercept, final double eps) {

		var sortedPoints = byIntercept.stream().sorted((a, b) -> Double.compare(a.coord, b.coord)).toList();

		var indexToPointPairs = new HashMap<Integer, Pair<MyPointAndOriLine, MyPointAndOriLine>>();

		int i = 0;

		while (i < sortedPoints.size()) {
			var left = sortedPoints.get(i);
			// overlap with itself
			indexToPointPairs.put(i, new Pair<MyPointAndOriLine, MyPointAndOriLine>(left, left.opposite));

			BiFunction<Integer, Integer, MyPointAndOriLine> getRight = (a, b) -> {
				var newRight = sortedPoints.get(b).getRight();

				var oldRight = indexToPointPairs.get(a).v2();

				if (oldRight.coord + eps >= newRight.coord) {
					return oldRight;
				}

				return newRight;
			};

			// update
			int j = i + 1;
			while (j < sortedPoints.size()) {

				var oldPair = indexToPointPairs.get(i);
				var oldLine = new OriLine(oldPair.v1().point, oldPair.v2().point, OriLine.Type.MOUNTAIN);

				var p = sortedPoints.get(j);

				if (GeomUtil.isRelaxedOverlap(oldLine, p.line, eps)) {
					var right = getRight.apply(i, j);
					indexToPointPairs.put(i, new Pair<MyPointAndOriLine, MyPointAndOriLine>(left, right));
					j++;
				} else {
					break;
				}
			}
			i = j;
		}

		// restore from point pairs
		var results = new ArrayList<OriLine>();
		for (var points : indexToPointPairs.values()) {
			var line = new OriLine(points.v1().point, points.v2().point, OriLine.Type.MOUNTAIN);

			results.add(line);
		}

		return results;
	}

}
