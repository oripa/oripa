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
package oripa.view.main;

import java.awt.Component;

import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;
import oripa.file.FileHistory;
import oripa.viewsetting.main.MainFrameSettingDB;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

/**
 * @author Koji
 *
 */
public class MainMenuBarFactory {

	private final MainFrameSettingDB mainFrameSetting;
	private final UIPanelSettingDB uiPanelSetting;

	/**
	 * Constructor
	 */
	public MainMenuBarFactory(final MainFrameSettingDB mainFrameSetting,
			final UIPanelSettingDB uiPanelSetting) {
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
	}

	/**
	 *
	 * @param owner
	 *            a component which will own the menu bar.
	 * @return menu bar
	 */
	public MainMenuBar createBar(
			final Component owner,
			final MouseActionHolder actionHolder,
			final PaintContextInterface aContext,
			final ScreenUpdaterInterface screenUpdater,
			final FileHistory history) {

		MainMenuBar bar = new MainMenuBar(owner, actionHolder, aContext, screenUpdater,
				mainFrameSetting, uiPanelSetting);

		bar.setFileHistoryPaths(history);

		return bar;

	}

}
