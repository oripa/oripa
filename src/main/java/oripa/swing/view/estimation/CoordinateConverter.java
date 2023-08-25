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

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import oripa.geom.RectangleDomain;

/**
 * @author OUCHI Koji
 *
 */
class CoordinateConverter {
	private final double imageHeight;
	private final double imageWidth;

	private final RectangleDomain domain;

	private double scale;

	private DistortionMethod distortionMethod = DistortionMethod.NONE;
	private Vector2d distortionParameter = new Vector2d(0, 0);
	private final double cameraZ = 20;
	private final double zDiff = 0.5;

	public CoordinateConverter(final RectangleDomain domain, final double imageWidth, final double imageHeight) {
		this.domain = domain;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public Vector2d convert(final Vector2d pos, final int depth, final Vector2d cpPos) {
		Vector2d center = new Vector2d(domain.getCenterX(), domain.getCenterY());

		double x = (pos.x - center.x) * scale;
		double y = (pos.y - center.y) * scale;

		Vector2d distorted = distort(new Vector2d(x, y), depth, cpPos);

		double disX = distorted.getX();
		double disY = distorted.getY();

		double tX = disX + imageWidth * 0.5;
		double tY = disY + imageHeight * 0.5;

		return new Vector2d(tX, tY);
	}

	private Vector2d distort(final Vector2d pos, final int depth, final Vector2d cpPos) {
		switch (distortionMethod) {
		case DEPTH:
			return distortByDepth(pos, depth);

		case MORISUE:
			return distortByMorisueMethod(pos, cpPos);
		case NONE:
			return pos;
		default:
			return pos;
		}

	}

	private Vector2d distortByDepth(final Vector2d pos, final int depth) {
		var cameraXY = new Vector2d(this.distortionParameter);
		cameraXY.scale(Math.max(domain.getWidth(), domain.getHeight()) * 4);

		var d = new Vector3d(
				pos.getX() - cameraXY.getX(),
				pos.getY() - cameraXY.getY(),
				cameraZ + Math.log(1 + depth) * zDiff);

		d.normalize();

		d.scale(cameraZ / d.getZ());

		return new Vector2d(
				cameraXY.getX() + d.getX(),
				cameraXY.getY() + d.getY());
	}

	/**
	 * See
	 * https://github.com/kei-morisue/flat-folder/blob/main/General_Distortion.pdf
	 *
	 * @param pos
	 * @param cpPos
	 * @return
	 */
	private Vector2d distortByMorisueMethod(final Vector2d pos, final Vector2d cpPos) {
		var matrix = new double[2][2];

		var theta = 0.1 * distortionParameter.getX();
		var scale = 1 + distortionParameter.getY() / 5;

		// need to find optimal matrix
		matrix[0][0] = Math.cos(theta);
		matrix[0][1] = -Math.sin(theta);
		matrix[1][0] = Math.sin(theta);
		matrix[1][1] = Math.cos(theta);

		var diff = new Vector2d(
				cpPos.getX() * matrix[0][0] + cpPos.getY() * matrix[0][1],
				cpPos.getX() * matrix[1][0] + cpPos.getY() * matrix[1][1]);

		diff.scale(scale);

		diff.sub(cpPos);

		var distorted = new Vector2d(pos);
		distorted.add(diff);

		return distorted;
	}

	public void setDistortionMethod(final DistortionMethod method) {
		distortionMethod = method;
	}

	public void setDistortionParameter(final Vector2d d) {
		distortionParameter = d;
	}

	public void setScale(final double scale) {
		this.scale = scale;
	}

}
