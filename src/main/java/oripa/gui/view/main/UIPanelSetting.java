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
public interface UIPanelSetting {

	String LINE_SELECTION_PANEL_VISIBLE = "line-selection-panel-visible";
	String LINE_INPUT_PANEL_VISIBLE = "line-input-panel-visible";
	String BY_VALUE_PANEL_VISIBLE = "by-value panel visible";
	String ALTER_LINE_TYPE_PANEL_VISIBLE = "alter-line-type panel visible";
	String ANGLE_STEP_PANEL_VISIBLE = "angle step panel visible";
	String SELECTED_MODE = "selected mode";

	void addPropertyChangeListener(
			String propertyName, PropertyChangeListener listener);

	boolean isByValuePanelVisible();

	boolean isAlterLineTypePanelVisible();

	boolean isSelectLinePanelVisible();

	boolean isLineInputPanelVisible();

	boolean isAngleStepPanelVisible();

	void setByValuePanelVisible(boolean byValuePanelVisible);

	void setAlterLineTypePanelVisible(boolean alterLineTypePanelVisible);

	void setLineSelectionPanelVisible(boolean lineSelectionPanelVisible);

	void setLineInputPanelVisible(boolean lineInputPanelVisible);

	void setAngleStepPanelVisible(boolean angleStepVisible);

	void selectInputMode();

	void selectSelectMode();

	String getSelectedModeString();

}