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
package oripa.renderer.estimation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.geom.RectangleDomain;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class Distortion {

	public record Result(
			List<Face> faces,
			OverlapRelation interpolatedOverlapRelation) {
	}

	private final RectangleDomain modelDomain;
	private final int width;
	private final int height;

	/**
	 *
	 * @param modelDomain
	 * @param width
	 *            of image
	 * @param height
	 *            of image
	 */
	public Distortion(final RectangleDomain modelDomain, final int width, final int height) {
		this.modelDomain = modelDomain;
		this.width = width;
		this.height = height;
	}

	public CoordinateConverter createCoordinateConverter(final DistortionMethod distortionMethod,
			final Vector2d distortionParameter, final double scale) {
		var converter = new CoordinateConverter(modelDomain, width, height);

		converter.setDistortionMethod(distortionMethod);
		converter.setDistortionParameter(distortionParameter);
		converter.setScale(scale);

		return converter;
	}

	/**
	 *
	 * @param origamiModel
	 * @param overlapRelation
	 * @param converter
	 * @param vertexDepths
	 *            can be empty map if converter does not use.
	 * @param eps
	 * @return
	 */
	public Result apply(final OrigamiModel origamiModel, final OverlapRelation overlapRelation,
			final CoordinateConverter converter,
			final Map<OriVertex, Integer> vertexDepths, final double eps) {

		var factory = new FaceFactory(converter, vertexDepths);
		var faces = origamiModel.getFaces().stream()
				.map(factory::create)
				.toList();

		var interpolated = new OverlapRelationInterpolater().interpolate(overlapRelation, faces, eps);

		return new Result(Collections.unmodifiableList(faces), interpolated);
	}
}
