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
package oripa.gui.view.model;

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public interface ObjectGraphicDrawer {
	public void selectScissorsLineColor();

	public void setTranslucent(final boolean translucent);

	public void selectEdgeColor();

	public void selectFaceColor();

	public void selectDefaultStroke(final double scale);

	public void selectScissorsLineStroke(final double scale);

	public void selectPaperBoundaryStroke(final double scale);

	public void selectFaceEdgeStroke(final double scale);

	public void drawLine(OriLine line);

	public void drawLine(Vector2d p0, Vector2d p1);

	public void fillFace(final List<Vector2d> vertices);

}
