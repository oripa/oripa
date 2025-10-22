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

import oripa.domain.paint.PaintContext;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public interface CreasePatternViewContext {
	// =================================================================================
	// Properties not used by action state
	// =================================================================================
	// ---------------------------------------------------------------
	// Mouse position
	/**
	 *
	 * @return the point of mouse on screen
	 */
	Vector2d getLogicalMousePoint();

	/**
	 *
	 * @param logicalPoint
	 *            set the point of mouse on screen
	 */
	void setLogicalMousePoint(Vector2d logicalPoint);

	// ---------------------------------------------------------------
	// View-related things

	void setScale(double scale);

	double getScale();

	/**
	 * Should update grid by calling {@link PaintContext#updateGrids()} if
	 * {@code gridVisible} is true, and should clear grid if {@code gridVisible}
	 * is false.
	 *
	 * @param dispGrid
	 */
	void setGridVisible(boolean gridVisible);

	boolean isGridVisible();

	void setMVLineVisible(boolean visible);

	boolean isMVLineVisible();

	void setVertexVisible(boolean visible);

	boolean isVertexVisible();

	void setAuxLineVisible(boolean visible);

	boolean isAuxLineVisible();

	void setCrossLineVisible(boolean visible);

	boolean isCrossLineVisible();

	void setZeroLineWidth(final boolean zeroLineWidth);

	boolean isZeroLineWidth();
}
