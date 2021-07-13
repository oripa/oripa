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
package oripa.gui.presenter.creasepattern;

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * An interface for drawing on view. This encapsulates the detail of view
 * framework and implementation. Implementation of this interface is expected to
 * be outside of domain package.
 *
 * @author OUCHI Koji
 *
 */
public interface ObjectGraphicDrawer {
	void selectColor(final OriLine.Type lineType);

	void selectStroke(final OriLine.Type lineType, final double scale,
			final boolean zeroWidth);

	void selectSelectedItemColor();

	void selectSelectedLineStroke(final double scale, final boolean zeroWidth);

	void selectCandidateItemColor();

	void selectCandidateLineStroke(final double scale, final boolean zeroWidth);

	void selectEditingOutlineColor();

	void selectEditingOutlineStroke(final double scale);

	void selectAssistLineColor();

	void selectAreaSelectionColor();

	void selectAreaSelectionStroke(final double scale);

	void selectOverlappingLineHighlightColor();

	void selectOverlappingLineHighlightStroke(final double scale);

	void selectNormalVertexColor();

	void selectViolatingVertexColor();

	void selectNormalFaceColor();

	void selectViolatingFaceColor();

	void selectNormalVertexSize(final double scale);

	void selectViolatingVertexSize(final double scale);

	void selectMouseActionVertexSize(final double scale);

	void drawVertex(Vector2d p);

	void drawLine(OriLine line);

	void drawLine(Vector2d p0, Vector2d p1);

	void drawLine(double x0, double y0, double x1, double y1);

	void drawRectangle(final Vector2d p0, final Vector2d p1);

	void fillFace(final List<Vector2d> vertices);

	void drawString(String text, float x, float y);
}
