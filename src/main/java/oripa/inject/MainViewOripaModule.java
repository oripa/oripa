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
package oripa.inject;

import java.util.function.Consumer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import jakarta.inject.Singleton;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.main.logic.SwitcherBetweenPasteAndChangeOrigin;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.ViewScreenUpdaterFactory;
import oripa.gui.view.main.KeyProcessing;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.PainterScreenView;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.viewsetting.main.KeyProcessingImpl;
import oripa.gui.viewsetting.main.KeyProcessingImpl.KeyOnOffListener;
import oripa.gui.viewsetting.main.MainFrameSettingImpl;
import oripa.gui.viewsetting.main.PainterScreenSettingImpl;
import oripa.gui.viewsetting.main.UIPanelSettingImpl;

/**
 * @author OUCHI Koji
 *
 */
public class MainViewOripaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainFrameSetting.class).to(MainFrameSettingImpl.class);
        bind(PainterScreenSetting.class).to(PainterScreenSettingImpl.class);
        bind(UIPanelSetting.class).to(UIPanelSettingImpl.class);
        bind(KeyProcessing.class).to(KeyProcessingImpl.class);

    }

    @Provides
    @Singleton
    ViewScreenUpdater createViewScreenUpdater() {
        return new ViewScreenUpdaterFactory().create();
    }

    @Provides
    @KeyOnOffListener
    Consumer<Boolean> provideKeyOnOffListener(final MouseActionHolder mouseActionHolder) {
        return new SwitcherBetweenPasteAndChangeOrigin(mouseActionHolder);
    }

    // assuming that main frame is singleton.

    @Provides
    PainterScreenView getPainterScreenView(final MainFrameView mainFrame) {
        return mainFrame.getPainterScreenView();
    }

    @Provides
    UIPanelView getUIPanelView(final MainFrameView mainFrame) {
        return mainFrame.getUIPanelView();
    }

}
