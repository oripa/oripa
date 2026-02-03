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

import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.TwoPointSegmentAction;
import oripa.gui.view.main.UIPanelSetting;

/**
 * @author OUCHI Koji
 *
 */
public class TestMouseActionPlugin extends AbstractGraphicMouseActionPlugin {

    @Override
    public String getName() {
        return "Example";
    }

    @Override
    protected String getHint() {
        return "Draws a segment between selected points.";
    }

    @Override
    protected void configureChangeOnSelected(final UIPanelSetting uiPanelSetting) {
        uiPanelSetting.setAlterLineTypePanelVisible(false);
        uiPanelSetting.setAngleStepPanelVisible(false);
        uiPanelSetting.setByValuePanelVisible(false);
        uiPanelSetting.setLineSelectionPanelVisible(false);

        uiPanelSetting.setLineInputPanelVisible(true);
    }

    @Override
    public GraphicMouseAction getGraphicMouseAction() {
        return new TwoPointSegmentAction();
    }

}
