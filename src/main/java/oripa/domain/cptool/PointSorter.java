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

import java.util.Comparator;
import java.util.List;

import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class PointSorter {
    public List<Vector2d> sortPointsOnLine(final List<Vector2d> points, final OriLine line) {
        var p0 = line.getP0();
        var p1 = line.getP1();
        boolean sortByX = Math.abs(p0.getX() - p1.getX()) > Math.abs(p0.getY() - p1.getY());

        return points.stream()
                .sorted(Comparator.comparing(sortByX ? Vector2d::getX : Vector2d::getY))
                .toList();
    }

}
