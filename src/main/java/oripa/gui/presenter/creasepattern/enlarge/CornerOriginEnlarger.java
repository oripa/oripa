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
package oripa.gui.presenter.creasepattern.enlarge;

import javax.vecmath.Vector2d;

import oripa.geom.RectangleDomain;
import oripa.value.CalculationResource;

/**
 * @author OUCHI Koji
 *
 */
class CornerOriginEnlarger extends AbstractEnlarger {

	@Override
	public Vector2d createOriginOfEnlargement(final RectangleDomain originalDomain, final Vector2d mouseStartPoint) {

		return getOppositePoint(originalDomain, mouseStartPoint);
	}

	private Vector2d getOppositePoint(final RectangleDomain domain, final Vector2d p) {
		if (p.epsilonEquals(domain.getLeftTop(), CalculationResource.POINT_EPS)) {
			return domain.getRightBottom();
		} else if (p.epsilonEquals(domain.getLeftBottom(), CalculationResource.POINT_EPS)) {
			return domain.getRightTop();
		} else if (p.epsilonEquals(domain.getRightTop(), CalculationResource.POINT_EPS)) {
			return domain.getLeftBottom();
		} else if (p.epsilonEquals(domain.getRightBottom(), CalculationResource.POINT_EPS)) {
			return domain.getLeftTop();
		}

		return null;
	}

	@Override
	public RectangleDomain createEnlargedDomain(final Vector2d mousePoint, final Vector2d originOfEnlargement,
			final Vector2d mouseStartPoint) {

		var currentPoint = scalePosition(mouseStartPoint, mousePoint, originOfEnlargement, mouseStartPoint);

		var diff = new Vector2d();
		diff.sub(currentPoint, originOfEnlargement);

		return new RectangleDomain(
				originOfEnlargement.getX(), originOfEnlargement.getY(),
				currentPoint.getX(), currentPoint.getY());
	}

	@Override
	protected Vector2d scalePosition(final Vector2d p, final Vector2d mousePoint, final Vector2d originOfEnlargement,
			final Vector2d mouseStartPoint) {

		var scales = computeScales(mousePoint, originOfEnlargement, mouseStartPoint);
		double absScale = Math.min(Math.abs(scales.getX()), Math.abs(scales.getY()));

		double signX = Math.signum(scales.getX());
		double signY = Math.signum(scales.getY());

		var scaledDiff = new Vector2d();
		scaledDiff.setX(Math.abs(p.getX() - originOfEnlargement.getX()) * absScale * signX);
		scaledDiff.setY(Math.abs(p.getY() - originOfEnlargement.getY()) * absScale * signY);

		var scaled = new Vector2d();
		scaled.add(originOfEnlargement, scaledDiff);

		return scaled;
	}

	private Vector2d computeScales(final Vector2d mousePoint, final Vector2d originOfEnlargement,
			final Vector2d mouseStartPoint) {
		var diff = new Vector2d();

		diff.sub(mousePoint, originOfEnlargement);

		double scaleX = diff.getX() / Math.abs(mouseStartPoint.getX() - originOfEnlargement.getX());
		double scaleY = diff.getY() / Math.abs(mouseStartPoint.getY() - originOfEnlargement.getY());

		return new Vector2d(scaleX, scaleY);
	}

}
