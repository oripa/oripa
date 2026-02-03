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

import oripa.geom.Ray;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
@Deprecated
public class PseudoRayFactory {

    public Segment create(final Ray ray, final double paperSize) {
        var v = ray.getEndPoint();
        var d = ray.getDirection().multiply(paperSize * 4);

        var ev = v.add(d);

        return new Segment(v, ev);
    }

    public Segment create(final Vector2d v, final double angle, final double paperSize) {
        return create(new Ray(v, angle), paperSize);
    }
}
