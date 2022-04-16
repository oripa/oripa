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

import java.util.Collection;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
abstract class AbstractEnlarger implements Enlarger {

	@Override
	public Collection<OriLine> createEnlargedLines(final Vector2d mousePoint, final Vector2d originOfEnlargement,
			final Vector2d mouseStartPoint, final Collection<OriLine> pickedLines) {

		return pickedLines.stream()
				.map(line -> new OriLine(
						scalePosition(line.getP0(), mousePoint, originOfEnlargement, mouseStartPoint),
						scalePosition(line.getP1(), mousePoint, originOfEnlargement, mouseStartPoint),
						line.getType()))
				.collect(Collectors.toList());
	}

	protected Vector2d scalePosition(final Vector2d p, final Vector2d mousePoint, final Vector2d originOfEnlargement,
			final Vector2d mouseStartPoint) {

		var scales = computeScales(mousePoint, originOfEnlargement, mouseStartPoint);
		double absScale = Math.min(Math.abs(scales.getX()), Math.abs(scales.getY()));

		var scaledDiff = new Vector2d();
		scaledDiff.setX((p.getX() - originOfEnlargement.getX()) * absScale);
		scaledDiff.setY((p.getY() - originOfEnlargement.getY()) * absScale);

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
