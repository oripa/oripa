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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class AnalyticLineHashFactory {
	private final double eps;
	private final HashFactory hashFactory = new HashFactory();

	public AnalyticLineHashFactory(final double eps) {
		this.eps = eps;
	}

	/**
	 * sort lines by angle for creating hash easily.
	 *
	 * @param lineArray
	 * @return
	 */
	private List<AnalyticLine> createAnalyticLines(final ArrayList<OriLine> lineArray) {
		return lineArray.parallelStream()
				.map(line -> new AnalyticLine(line))
				.toList();
	}

	private ArrayList<ArrayList<AnalyticLine>> createHash(
			final List<AnalyticLine> lines,
			final Function<AnalyticLine, Double> keyExtractor) {
		return hashFactory.create(lines, keyExtractor, eps);
	}

	/**
	 * make a hash table whose keys are index of lines ordered by angle. if
	 * angles are equal, then lines can overlap.
	 *
	 * @param lines
	 *            should be sorted by angle.
	 * @return a hash table whose keys are index of lines ordered by angle.
	 */
	private ArrayList<ArrayList<AnalyticLine>> createAngleHash(
			final List<AnalyticLine> lines) {

		return createHash(lines, AnalyticLine::getAngle);
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

		return angleHash.stream()
				.map(byAngle -> createHash(byAngle, AnalyticLine::getIntercept))
				.collect(Collectors.toCollection(ArrayList::new));
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
