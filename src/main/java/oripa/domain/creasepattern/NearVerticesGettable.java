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
package oripa.domain.creasepattern;

import java.util.Collection;

import oripa.vecmath.Vector2d;

/**
 * @author Koji
 *
 */
public interface NearVerticesGettable {

    /**
     * returns vertices in the area which the given vertex belongs to.
     *
     * @param v
     *            vertex
     * @return
     */
    Collection<Vector2d> getVerticesAround(Vector2d v);

    /**
     * similar to {@link #getVerticesAround(Vector2d v)}. this method returns
     * some areas in a large rectangle (x-distanse, y-distance, x+distance,
     * y+distance).
     *
     * @param x
     * @param y
     * @param distance
     * @return
     */
    Collection<Collection<Vector2d>> getVerticesInArea(double x,
            double y, double distance);

}