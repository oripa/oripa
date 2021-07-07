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
package oripa.drawer.java2d;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class GraphicItemConverter {
	public Line2D.Double toLine2D(final OriLine line) {
		var g2dLine = new Line2D.Double();
		g2dLine.x1 = line.p0.x;
		g2dLine.y1 = line.p0.y;

		g2dLine.x2 = line.p1.x;
		g2dLine.y2 = line.p1.y;

		return g2dLine;
	}

	public Line2D.Double toLine2D(final Vector2d p0, final Vector2d p1) {
		return new Line2D.Double(p0.x, p0.y, p1.x, p1.y);
	}

	public Rectangle2D.Double toRectangle2D(final Vector2d vertex, final double vertexSize) {
		final double vertexHalfSize = vertexSize / 2;
		return new Rectangle2D.Double(
				vertex.x - vertexHalfSize, vertex.y - vertexHalfSize,
				vertexSize, vertexSize);
	}

	public Rectangle2D.Double toRectangle2D(final Vector2d p0, final Vector2d p1) {
		double sx = Math.min(p0.x, p1.x);
		double sy = Math.min(p0.y, p1.y);
		double w = Math.abs(p0.x - p1.x);
		double h = Math.abs(p0.y - p1.y);
		return new Rectangle2D.Double(sx, sy, w, h);
	}
}
