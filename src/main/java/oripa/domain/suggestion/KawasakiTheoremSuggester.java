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
package oripa.domain.suggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.util.MathUtil;

/**
 * @author OUCHI Koji
 *
 */
public class KawasakiTheoremSuggester {

    /**
     *
     * @param vertex
     * @return angles of suggested lines. empty collection if the vertex is not
     *         inside of paper or there is no need to interpolate.
     */
    public Collection<Double> suggest(final OriVertex vertex) {

        int angleCount = vertex.edgeCount();

        if (angleCount % 2 == 0) {
            return List.of();
        }

        if (!vertex.isInsideOfPaper()) {
            return List.of();
        }

        var suggestions = new ArrayList<Double>();

        double evenSum = 0;

        for (int i = 0; i < angleCount; i += 2) {
            evenSum += vertex.getAngleDifference(i);
        }

        double oddSum = 2 * Math.PI - evenSum;

        evenSum -= vertex.getAngleDifference(0);

        // computes as if indices are re-assigned as current i-th is the new
        // 0th.
        for (var i = 0; i < angleCount; i++) {
            double theta = vertex.getAngleDifference(i);
            double t = evenSum + theta - Math.PI;

            final double EPS = MathUtil.angleRadianEps();
            if (evenSum < Math.PI - EPS && oddSum < Math.PI - EPS) {
                var baseAngle = vertex.getEdge(i).getAngle(vertex);
                suggestions.add((baseAngle + t));
            }

            var tmpEvenSum = evenSum;
            evenSum = oddSum + theta - vertex.getAngleDifference(i + 1);
            oddSum = tmpEvenSum;
        }

        return suggestions;
    }
}
