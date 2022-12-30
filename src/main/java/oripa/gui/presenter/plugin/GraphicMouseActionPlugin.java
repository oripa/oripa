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
package oripa.gui.presenter.plugin;

import oripa.domain.paint.ActionState;
import oripa.domain.paint.CreasePatternUndoManager;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

/**
 * @author OUCHI Koji
 *
 */
public interface GraphicMouseActionPlugin {
	void setMainFrameSetting(MainFrameSetting setting);

	void setUIPanelSetting(UIPanelSetting setting);

	String getName();

	String getHint();

	/**
	 *
	 * @return {@link ChangeViewSetting} that sets a hint text of this plugin's
	 *         action to {@link MainFrameSetting}.
	 */
	ChangeViewSetting getChangeHint();

	/**
	 *
	 * @return {@link ChangeViewSetting} that configures visibilities of view
	 *         panels by setting values to {@link UIPanelSetting}.
	 */
	ChangeViewSetting getChangeOnSelected();

	/**
	 * This method should return {@link GraphicMouseAction} that satisfies the
	 * conditions below:
	 * <ul>
	 * <li>{@code getEditMode()} should return {@link EditMode#INPUT}.</li>
	 * <li>It should manage undo/redo registration by
	 * {@link CreasePatternUndoManager} that is provided by {@code paintContext}
	 * given as a parameter of each method. Typically you need do that in
	 * {@link GraphicMouseAction#onLeftClick(CreasePatternViewContext, PaintContext, boolean)}
	 * and
	 * {@link GraphicMouseAction#onRightClick(CreasePatternViewContext, PaintContext, boolean)}.</li>
	 * </ul>
	 *
	 * It is recommended to extend {@link AbstractGraphicMouseAction} and use
	 * {@link ActionState}, which offer a variant of State pattern to follow the
	 * requirements easily.
	 *
	 * @return {@link GraphicMouseAction}.
	 */
	GraphicMouseAction getGraphicMouseAction();

}
