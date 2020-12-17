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
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class SharedPointsMapFactory<T extends PointAndLine> {

	private ArrayList<T> createXOrderPoints(
			final ArrayList<OriLine> lines,
			final BiFunction<OriPoint, OriLine, T> factory) {
		var points = new ArrayList<T>(lines.size() * 2);

		for (var line : lines) {
			points.add(factory.apply(line.p0, line));
			points.add(factory.apply(line.p1, line));
		}

		points.sort(Comparator.comparing(PointAndLine::getX));

		return points;
	}

	/**
	 * the returned map keeps the both side of each line as an object holding
	 * the end point and the line. The map is ordered by x-coordinate and then
	 * y-coordinate. The lines in returned map values and given to the factory
	 * are canonicalized, i.e., line.p0 is smaller than line.p1.
	 *
	 * @param creasePattern
	 * @param factory
	 * @param eps
	 * @return
	 */

	public TreeMap<OriPoint, ArrayList<T>> create(
			final Collection<OriLine> creasePattern,
			final BiFunction<OriPoint, OriLine, T> factory,
			final double eps) {
		// Sweep-line approach
		// (sweep along x axis)

		var canonicalLines = creasePattern.stream()
				.map(line -> line.createCanonical())
				.collect(Collectors.toCollection(() -> new ArrayList<>()));

		var xOrderPoints = createXOrderPoints(canonicalLines, factory);
		var hashFactory = new HashFactory();
		var xOrderHash = hashFactory.create(xOrderPoints, T::getX, eps);

		for (var byX : xOrderHash) {
			byX.sort(Comparator.comparing(T::getY));
		}

		// this map keeps the both side of each line as an object holding the
		// end point and the line object.
		var sharedPointsMap = new TreeMap<OriPoint, ArrayList<T>>();

		// build a map and set keyPoint0
		for (var byX : xOrderHash) {
			var yHash = hashFactory.create(byX, T::getY, eps);
			for (var xyPoints : yHash) {
				var point0 = xyPoints.get(0);
				sharedPointsMap.put(point0.getPoint(), xyPoints);
				xyPoints.forEach(p -> p.setKeyPoint0(point0.getPoint()));
			}
		}

		// set keyPoint1(opposite end point for map's key)
		sharedPointsMap.forEach((keyPoint, points) -> {
			for (var point : points) {
				var line = point.getLine();
				var keyPoint1 = GeomUtil.distance(line.p0, keyPoint) < eps
						? sharedPointsMap.floorKey(line.p1)
						: sharedPointsMap.floorKey(line.p0);
				point.setKeyPoint1(keyPoint1);
			}
		});

		return sharedPointsMap;
	}
}
