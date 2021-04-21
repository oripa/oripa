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

package oripa.persistent.svg;

import oripa.geom.RectangleDomain;
import oripa.value.OriPoint;

import javax.vecmath.Vector2d;

import static oripa.persistent.svg.SVGUtils.SVG_HALF_SIZE;

public abstract class SvgConverter {

    protected RectangleDomain domain;
    protected double scaleToFitDomain;

    /**
     * @param point initial point
     * @return point mapped in the domain
     */
    protected Vector2d mapToDomain(Vector2d point) {

        double x = (point.x - domain.getCenterX()) * scaleToFitDomain + SVG_HALF_SIZE;
        double y = -(point.y - domain.getCenterY()) * scaleToFitDomain + SVG_HALF_SIZE;

        return new Vector2d(x, y);
    }

    protected Vector2d mapToDomain(OriPoint oriPoint) {
        return mapToDomain(new Vector2d(oriPoint.x, oriPoint.y));
    }

}
