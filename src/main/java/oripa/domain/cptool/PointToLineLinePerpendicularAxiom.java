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

import java.util.Optional;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * Axiom 7.
 *
 * @author OUCHI Koji
 *
 */
public class PointToLineLinePerpendicularAxiom {
    public Optional<Line> createFoldLine(final Vector2d p, final Segment s, final Segment perpendicular) {

        if (s.getLine().isParallel(perpendicular.getLine())) {
            return Optional.empty();
        }

        var perpendicularDirection = perpendicular.getLine().getDirection();

        var motionLine = new Line(p, perpendicularDirection);

        var crossPointOpt = GeomUtil.getCrossPoint(motionLine, s);

        return crossPointOpt
                .map(crossPoint -> new PerpendicularBisectorFactory().create(p, crossPoint))
                .filter(line -> GeomUtil.getCrossPoint(line, perpendicular).isPresent());
    }
}
