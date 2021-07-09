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

/**
 * @author OUCHI Koji
 *
 */
public interface CreasePatternViewContext {
	// =================================================================================
	// Properties not used by action state
	// =================================================================================

	// ---------------------------------------------------------------
	// View-related things

	public abstract void setGridVisible(boolean dispGrid);

	public abstract boolean isGridVisible();

	public abstract void setMVLineVisible(boolean visible);

	public abstract boolean isMVLineVisible();

	public abstract void setVertexVisible(boolean visible);

	public abstract boolean isVertexVisible();

	public abstract void setAuxLineVisible(boolean visible);

	public abstract boolean isAuxLineVisible();

	public abstract void setCrossLineVisible(boolean visible);

	public abstract boolean isCrossLineVisible();

	public abstract void setZeroLineWidth(final boolean zeroLineWidth);

	public abstract boolean isZeroLineWidth();
}
