/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General License for more details.

    You should have received a copy of the GNU General License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.view.model;

import java.util.List;

import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public interface ObjectGraphicDrawer {
	void selectScissorsLineColor();

	void setTranslucent(final boolean translucent);

	void selectEdgeColor();

	void selectFaceColor();

	void selectDefaultStroke(final double scale);

	void selectScissorsLineStroke(final double scale);

	void selectPaperBoundaryStroke(final double scale);

	void selectFaceEdgeStroke(final double scale);

	void drawLine(OriLine line);

	void drawLine(Vector2d p0, Vector2d p1);

	void fillFace(final List<Vector2d> vertices);

}
