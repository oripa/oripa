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
package oripa.gui.view.main;

import java.beans.PropertyChangeListener;

/**
 * @author OUCHI Koji
 *
 */
public interface PainterScreenSetting {

	String GRID_VISIBLE = "grid visible";
	String CROSS_LINE_VISIBLE = "cross line visible";
	String ZERO_LINE_WIDTH = "zero line width";
	String VERTEX_VISIBLE = "vertex visible";
	String MV_LINE_VISIBLE = "mv line visible";
	String AUX_LINE_VISIBLE = "aux line visible";

	void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	void setGridVisible(boolean gridVisible);

	void setCrossLineVisible(boolean visible);

	void setZeroLineWidth(boolean zeroLineWidth);

	void setVertexVisible(boolean vertexVisible);

	void setMVLineVisible(boolean mvLineVisible);

	void setAuxLineVisible(boolean auxLineVisible);

}