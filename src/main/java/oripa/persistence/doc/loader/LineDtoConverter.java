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
package oripa.persistence.doc.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class LineDtoConverter {
	CreasePattern convert(final List<LineDto> dtos) {

		var minV = new MutablePoint(Double.MAX_VALUE, Double.MAX_VALUE);
		var maxV = new MutablePoint(-Double.MAX_VALUE, -Double.MAX_VALUE);

		for (var line : dtos) {
			minV.x = Math.min(minV.x, line.p0x);
			minV.x = Math.min(minV.x, line.p1x);
			minV.y = Math.min(minV.y, line.p0y);
			minV.y = Math.min(minV.y, line.p1y);

			maxV.x = Math.max(maxV.x, line.p0x);
			maxV.x = Math.max(maxV.x, line.p1x);
			maxV.y = Math.max(maxV.y, line.p0y);
			maxV.y = Math.max(maxV.y, line.p1y);
		}

		final double size = 400;
		var center = new Vector2d((minV.x + maxV.x) / 2.0,
				(minV.y + maxV.y) / 2.0);
		double bboxSize = Math.max(maxV.x - minV.x, maxV.y - minV.y);
		// size normalization
		for (LineDto dto : dtos) {
			dto.p0x = (dto.p0x - center.getX()) / bboxSize * size;
			dto.p0y = (dto.p0y - center.getY()) / bboxSize * size;
			dto.p1x = (dto.p1x - center.getX()) / bboxSize * size;
			dto.p1y = (dto.p1y - center.getY()) / bboxSize * size;
		}

		var delLines = new ArrayList<LineDto>();
		int lineNum = dtos.size();

		for (int i = 0; i < lineNum; i++) {
			for (int j = i + 1; j < lineNum; j++) {
				var l0 = dtos.get(i);
				var l1 = dtos.get(j);

				if ((GeomUtil.distance(l0.getP0(), l1.getP0()) < 0.01 && GeomUtil
						.distance(l0.getP1(), l1.getP1()) < 0.01)
						|| (GeomUtil.distance(l0.getP1(), l1.getP0()) < 0.01 && GeomUtil
								.distance(l0.getP0(), l1.getP1()) < 0.01)) {

					delLines.add(l0);
				}
			}
		}

		for (LineDto delLine : delLines) {
			dtos.remove(delLine);
		}

		var factory = new CreasePatternFactory();
		var creasePattern = factory
				.createCreasePattern(dtos.stream().map(d -> new OriLine(d.p0x, d.p0y, d.p1x, d.p1y, d.type))
						.collect(Collectors.toList()));

		return creasePattern;
	}
}
