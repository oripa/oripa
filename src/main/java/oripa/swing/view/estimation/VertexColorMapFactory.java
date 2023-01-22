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
package oripa.swing.view.estimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class VertexColorMapFactory {
	/**
	 *
	 * @param face
	 *            a face to be colored
	 * @param frontColorFactor
	 *            front-side color normalized as the values are 0.0 ~ 1.0 for
	 *            each index. value at 0 is for red, value at 1 is for green,
	 *            and value at 2 is for blue.
	 * @param backColorFactor
	 *            back-side color normalized as the values are 0.0 ~ 1.0 for
	 *            each index. value at 0 is for red, value at 1 is for green,
	 *            and value at 2 is for blue.
	 *
	 * @param flip
	 *            {@code true} if the model is flipped.
	 * @return a mapping halfedges of the given face to normalized colors.
	 */
	public Map<OriHalfedge, FloatingRGB> createVertexColors(
			final OriFace face,
			final List<Double> frontColorFactor,
			final List<Double> backColorFactor,
			final boolean flip) {
		var domain = new RectangleDomain();
		for (OriHalfedge he : face.halfedgeIterable()) {
			domain.enlarge(he.getPosition());
		}
		double minX = domain.getLeft();
		double minY = domain.getTop();

		double faceWidth = Math.sqrt(domain.getWidth() * domain.getWidth()
				+ domain.getHeight() * domain.getHeight());

		var map = new HashMap<OriHalfedge, FloatingRGB>();

		for (OriHalfedge he : face.halfedgeIterable()) {
			double val = 0;
			if (he.getType() == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (he.getType() == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			var prevHe = he.getPrevious();
			if (prevHe.getType() == OriLine.Type.MOUNTAIN.toInt()) {
				val += 1;
			} else if (prevHe.getType() == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			double vv = (val + 2) / 4.0;
			double v = (0.75 + vv * 0.25);

			var position = he.getPosition();
			var d = new Vector2d(position.x - minX, position.y - minY).length();
			v *= 0.9 + 0.15 * d / faceWidth;

			v = Math.min(1, v);

			if (face.isFaceFront() ^ flip) {
				map.put(he, new FloatingRGB(
						v * frontColorFactor.get(0),
						v * frontColorFactor.get(1),
						v * frontColorFactor.get(2)));
			} else {
				map.put(he, new FloatingRGB(
						v * backColorFactor.get(0),
						v * backColorFactor.get(1),
						v * backColorFactor.get(2)));
			}
		}

		return map;
	}

}
