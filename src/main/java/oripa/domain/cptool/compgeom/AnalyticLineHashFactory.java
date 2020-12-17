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
import java.util.function.Function;
import java.util.stream.Collectors;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class AnalyticLineHashFactory {
	private static final double EPS = 1e-5;
	private final HashFactory hashFactory = new HashFactory();

	/**
	 * sort lines by angle for creating hash easily.
	 *
	 * @param lineArray
	 * @return
	 */
	private ArrayList<AnalyticLine> createAnalyticLines(final ArrayList<OriLine> lineArray) {
		return lineArray.parallelStream()
				.map(line -> new AnalyticLine(line))
				.sorted(Comparator.comparing(AnalyticLine::getAngle))
				.collect(Collectors.toCollection(() -> new ArrayList<>()));
	}

	private ArrayList<ArrayList<AnalyticLine>> createHash(
			final ArrayList<AnalyticLine> sortedLines,
			final Function<AnalyticLine, Double> keyExtractor) {
		return hashFactory.create(sortedLines, keyExtractor, EPS);
//		var hash = new ArrayList<ArrayList<AnalyticLine>>();
//
//		int split_i = 0;
//		hash.add(new ArrayList<AnalyticLine>());
//		hash.get(split_i).add(sortedLines.get(0));
//		for (int i = 1; i < sortedLines.size(); i++) {
//			var line1 = sortedLines.get(i);
//			var line0 = hash.get(split_i).get(0);
//			if (keyExtractor.apply(line1) - keyExtractor.apply(line0) > EPS) {
//				split_i++;
//				hash.add(new ArrayList<AnalyticLine>());
//			}
//			hash.get(split_i).add(line1);
//		}
//
//		return hash;
	}

	/**
	 * make a hash table whose keys are index of lines ordered by angle. if
	 * angles are equal, then lines can overlap.
	 *
	 * @param sortedLines
	 *            should be sorted by angle.
	 * @return a hash table whose keys are index of lines ordered by angle.
	 */
	private ArrayList<ArrayList<AnalyticLine>> createAngleHash(
			final ArrayList<AnalyticLine> sortedLines) {

		return createHash(sortedLines, AnalyticLine::getAngle);
	}

	/**
	 * create hash tables by intercept for each angle.
	 *
	 * @param angleHash
	 *            a hash table created by {@link #createAngleHash(ArrayList)}.
	 * @return 3D hash table, e.g., hash[angle][intercept][lineIndex].
	 */
	private ArrayList<ArrayList<ArrayList<AnalyticLine>>> createInterceptHash(
			final ArrayList<ArrayList<AnalyticLine>> angleHash) {

		var hash = new ArrayList<ArrayList<ArrayList<AnalyticLine>>>();

		for (int i = 0; i < angleHash.size(); i++) {
			var byAngle = angleHash.get(i).stream()
					.sorted(Comparator.comparing(AnalyticLine::getIntercept))
					.collect(Collectors.toCollection(() -> new ArrayList<>()));

			var byIntercept = createHash(byAngle, AnalyticLine::getIntercept);

			hash.add(byIntercept);

		}
		return hash;
	}

	/**
	 *
	 * @param lines
	 * @return 3D hash table, e.g., hash[angle][intercept][lineIndex].
	 */
	public ArrayList<ArrayList<ArrayList<AnalyticLine>>> create(
			final Collection<OriLine> lines) {
		// convert collection to ensure fast access to lines.
		var lineArray = new ArrayList<OriLine>(lines);

		// make a data structure for fast computation.
		var analyticLines = createAnalyticLines(lineArray);
		var angleHash = createAngleHash(analyticLines);
		return createInterceptHash(angleHash);
	}
}
