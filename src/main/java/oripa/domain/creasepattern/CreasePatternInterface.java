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
package oripa.domain.creasepattern;

import java.util.Collection;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * A collection of lines with vertices information. In implementation of this
 * interface, vertices information must be updated via Collection methods but
 * not guaranteed if a line in this object is update directly like
 * {@code line.p0 = new OriPoint(x, y);} or {@code line.p0.x = newX;} That is,
 * if you want to change an end point vertex or its coordinate value of line(s)
 * in this object, you have to remove the line before changing and add it after
 * changing.
 *
 * @author Koji
 *
 */
public interface CreasePatternInterface
		extends Collection<OriLine>, NearVerticesGettable {

	public abstract double getPaperSize();

	public abstract RectangleDomain getPaperDomain();

	/**
	 * move all creases as the coordinate (x, y) becomes (x + dx, y + dx).
	 *
	 * @param dx
	 *            amount of movement on x coordinate
	 * @param dy
	 *            amount of movement on y coordinate
	 */
	public void move(final double dx, final double dy);

	public abstract boolean cleanDuplicatedLines();
}