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

import java.util.Optional;

import oripa.geom.RectangleDomain;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class CornerOriginEnlarger extends AbstractEnlarger {

    @Override
    public Optional<Vector2d> createOriginOfEnlargement(final RectangleDomain originalDomain,
            final Vector2d mouseStartPoint) {
        // It is natural to fix the epsilon for mouse interaction.
        final double eps = 1e-5;
        return getOppositePoint(originalDomain, mouseStartPoint, eps);
    }

    private Optional<Vector2d> getOppositePoint(final RectangleDomain domain, final Vector2d p, final double eps) {
        if (p.equals(domain.getLeftTop(), eps)) {
            return Optional.of(domain.getRightBottom());

        } else if (p.equals(domain.getLeftBottom(), eps)) {
            return Optional.of(domain.getRightTop());

        } else if (p.equals(domain.getRightTop(), eps)) {
            return Optional.of(domain.getLeftBottom());

        } else if (p.equals(domain.getRightBottom(), eps)) {
            return Optional.of(domain.getLeftTop());
        }

        return Optional.empty();
    }

    @Override
    public RectangleDomain createEnlargedDomain(final Vector2d mousePoint, final Vector2d originOfEnlargement,
            final Vector2d mouseStartPoint) {

        var currentPoint = scalePosition(mouseStartPoint, mousePoint, originOfEnlargement, mouseStartPoint);

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

        var scaledDiffX = (Math.abs(p.getX() - originOfEnlargement.getX()) * absScale * signX);
        var scaledDiffY = (Math.abs(p.getY() - originOfEnlargement.getY()) * absScale * signY);
        var scaledDiff = new Vector2d(scaledDiffX, scaledDiffY);

        var scaled = originOfEnlargement.add(scaledDiff);

        return scaled;
    }

    private Vector2d computeScales(final Vector2d mousePoint, final Vector2d originOfEnlargement,
            final Vector2d mouseStartPoint) {
        var diff = mousePoint.subtract(originOfEnlargement);

        double scaleX = diff.getX() / Math.abs(mouseStartPoint.getX() - originOfEnlargement.getX());
        double scaleY = diff.getY() / Math.abs(mouseStartPoint.getY() - originOfEnlargement.getY());

        return new Vector2d(scaleX, scaleY);
    }

}
