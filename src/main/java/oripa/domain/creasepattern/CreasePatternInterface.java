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

import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public interface CreasePatternInterface
		extends Collection<OriLine>, NearVerticesGettable {

	public abstract void changePaperSize(final double paperSize);

	public abstract double getPaperSize();

	/**
	 * move all creases as the center coordinate becomes (0, 0).
	 *
	 * @param cx
	 *            x coordinate of current center
	 * @param cy
	 *            y coordinate of current center
	 */
	public void centerize(final double cx, final double cy);

}