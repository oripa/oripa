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
package oripa.domain.paint.angle;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.MultipleRaySnapPointFactory;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class AngleSnapPointFactory {
    public Collection<Vector2d> createSnapPoints(final PaintContext context) {
        var step = context.getAngleStep();

        var spOpt = context.peekVertex();
        var angles = IntStream.range(0, step.getDivNum() * 2)
                .mapToDouble(i -> i * step.getRadianStep())
                .boxed()
                .toList();

        return spOpt
                .map(sp -> new MultipleRaySnapPointFactory()
                        .createSnapPoints(context.getCreasePattern(), sp, angles,
                                context.getPointEps()))
                .orElse(List.of());
    }
}
