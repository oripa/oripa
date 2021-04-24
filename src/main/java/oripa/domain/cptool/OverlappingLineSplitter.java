/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.domain.cptool;

import oripa.value.OriLine;
import oripa.value.OriPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static oripa.geom.GeomUtil.detectOverlap;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineSplitter {

	/**
	 * Split lines if overlapping, using the type of the new line for the parts that are part of it,
	 * and the type of the existing line for the part that are not
	 * @param existingLine Line that already existed before the action
	 * @param newLine line newly added by the action
	 * @return list of the sublines to add instead of the lines that are passed to the function
	 */
	public static List<OriLine> splitLinesIfOverlap(OriLine existingLine, OriLine newLine) {
		if(detectOverlap(existingLine, newLine)) {
			return splitOverlappingLines(existingLine, newLine);
		}
		return List.of(existingLine, newLine);
	}

	/**
	 * Splits the lines, assuming they are overlapping
	 * @param existingLine Line that already existed before the action
	 * @param newLine line newly added by the action
	 * @return list of the sublines with the right Type
	 */
	public static List<OriLine> splitOverlappingLines(OriLine existingLine, OriLine newLine) {
		List<OriPoint> oriPoints = List.of(existingLine.p0, existingLine.p1, newLine.p0, newLine.p1).stream()
				.sorted()
				.collect(Collectors.toList());

		List<OriLine> subLines = new ArrayList<>();

		for (int i=0; i<oriPoints.size()-1; i++) {
			if (oriPoints.get(i).equals(oriPoints.get(i+1))) continue;
			OriLine line = new OriLine(oriPoints.get(i), oriPoints.get(i + 1), existingLine.getType());
			if (newLine.contains(line.middlePoint())) line.setType(newLine.getType());
			subLines.add(line);
		}

		return subLines;
	}
}
