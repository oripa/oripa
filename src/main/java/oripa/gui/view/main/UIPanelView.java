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

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JPanel;

import oripa.appstate.StateManager;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.view.View;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;

/**
 * @author OUCHI Koji
 *
 */
public interface UIPanelView extends View {
	default JPanel asPanel() {
		return (JPanel) this;
	}

	@Deprecated
	UIPanelSetting getUIPanelSetting();

	@Deprecated
	PaintContext getPaintContext();

	@Deprecated
	CreasePatternViewContext getViewContext();

	@Deprecated
	ViewScreenUpdater getScreenUpdater();

	@Deprecated
	StateManager<EditMode> getStateManager();

	@Deprecated
	MouseActionHolder getMouseActionHolder();

	void addGridSmallButtonListener(Runnable listener);

	void addGridLargeButtonListener(Runnable listener);

	void addGridChangeButtonListener(Consumer<Integer> listener);

	void setGridDivNum(int gridDivNum);

	void addBuildButtonListener(Runnable listener);

	void setEstimationResultColors(Color front, Color back);

	void setBuildButtonEnabled(boolean enabled);

	boolean getFullEstimation();

	Color getEstimationResultFrontColor();

	Color getEstimationResultBackColor();

	void addEditModeInputLineButtonListener(ActionListener listener, KeyListener keyListener);

	BiConsumer<Color, Color> getEstimationResultSaveColorsListener();

	PropertyChangeListener getPaperDomainOfModelChangeListener();

}
