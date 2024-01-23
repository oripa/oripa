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
package oripa.swing.drawer.java2d;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class GraphicItemConverter {
	public Line2D.Double toLine2D(final OriLine line) {
		var g2dLine = new Line2D.Double();

		var p0 = line.getP0();
		var p1 = line.getP1();

		g2dLine.x1 = p0.getX();
		g2dLine.y1 = p0.getY();

		g2dLine.x2 = p1.getX();
		g2dLine.y2 = p1.getY();

		return g2dLine;
	}

	public Line2D.Double toLine2D(final Vector2d p0, final Vector2d p1) {
		return new Line2D.Double(p0.getX(), p0.getY(), p1.getX(), p1.getY());
	}

	public Rectangle2D.Double toRectangle2D(final Vector2d vertex, final double vertexSize) {
		final double vertexHalfSize = vertexSize / 2;
		return new Rectangle2D.Double(
				vertex.getX() - vertexHalfSize, vertex.getY() - vertexHalfSize,
				vertexSize, vertexSize);
	}

	public Rectangle2D.Double toRectangle2D(final Vector2d p0, final Vector2d p1) {
		double sx = Math.min(p0.getX(), p1.getX());
		double sy = Math.min(p0.getY(), p1.getY());
		double w = Math.abs(p0.getX() - p1.getX());
		double h = Math.abs(p0.getY() - p1.getY());
		return new Rectangle2D.Double(sx, sy, w, h);
	}

	public Path2D.Double toPath2D(final List<Vector2d> vertices) {
		var path = new Path2D.Double();
		path.moveTo(vertices.get(0).getX(), vertices.get(0).getY());
		for (int i = 1; i < vertices.size(); i++) {
			path.lineTo(vertices.get(i).getX(), vertices.get(i).getY());
		}
		path.closePath();
		return path;
	}
}
