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
package oripa.domain.paint.suggestion;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.MultipleRaySnapPointFactory;

/**
 * @author OUCHI Koji
 *
 */
class SuggestionSnapPointFactory {
	/**
	 * Ignores the end points of the lines incident to {@code sp}.
	 *
	 * @param context
	 * @param sp
	 *            the end point of suggested ray.
	 * @param angles
	 * @return snap points for suggestions.
	 */
	public Collection<Vector2d> createSnapPoints(final PaintContext context, final Vector2d sp,
			final Collection<Double> angles) {

		return new MultipleRaySnapPointFactory().createSnapPoints(context.getCreasePattern(), sp, angles,
				context.pointEps());
	}

}
