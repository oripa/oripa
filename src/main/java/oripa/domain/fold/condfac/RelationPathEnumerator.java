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
package oripa.domain.fold.condfac;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.origeom.OverlapRelation;
import oripa.util.Matrices;

/**
 * Floyd-Warshall algorithm. for indices i and j (i < j) on the obtained path
 * hold isUpper(path[i], path[j]) or isUndefined(path[i], path[j]).
 *
 * @author OUCHI Koji
 *
 */
public class RelationPathEnumerator {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private int faceCount;

	private int infinity() {
		return faceCount * 2;
	}

	private int[][] distance;
	private int[][] prevIndices;

	public void findPaths(final OverlapRelation overlapRelation) {
		faceCount = overlapRelation.getSize();

		distance = new int[faceCount][faceCount];
		prevIndices = new int[faceCount][faceCount];

		for (int i = 0; i < faceCount; i++) {
			for (int j = 0; j < faceCount; j++) {
				if (overlapRelation.isUpper(i, j) || overlapRelation.isUndefined(i, j)) {
					distance[i][j] = 1;
					prevIndices[i][j] = i;
				} else {
					distance[i][j] = infinity();
					prevIndices[i][j] = -1;
				}
			}
		}

		for (int i = 0; i < faceCount; i++) {
			distance[i][i] = 0;
			prevIndices[i][i] = i;
		}

		for (int k = 0; k < faceCount; k++) {
			for (int i = 0; i < faceCount; i++) {
				for (int j = 0; j < faceCount; j++) {
					if (distance[i][j] > distance[i][k] + distance[k][j]) {
						distance[i][j] = distance[i][k] + distance[k][j];
						prevIndices[i][j] = prevIndices[k][j];
					}
				}

			}
		}

		logger.debug(Matrices.toString(distance));
	}

	public List<Integer> getPath(final int i, final int j) {
		if (prevIndices[i][j] == -1) {
			return List.of();
		}
		var path = new LinkedList<Integer>();
		path.add(j);

		int v = j;
		while (v != i) {
			v = prevIndices[i][v];
			path.addFirst(v);
		}

		return new ArrayList<>(path);
	}

	public boolean isOnCycle(final int i, final int j) {
		if (distance[i][j] < infinity() && distance[j][i] < infinity()) {

			if (distance[i][j] + distance[j][i] <= 2) {
				return false;
			}

			return true;
		}
		return false;
	}

	public List<Integer> getCycle(final int i, final int j) {
		if (distance[i][j] < infinity() && distance[j][i] < infinity()) {
			var path = getPath(i, j);
			path.removeLast();
			path.addAll(getPath(j, i));
			path.removeLast();
			return path;
		}
		return List.of();
	}
}
