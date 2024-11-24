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
package oripa.gui.presenter.main;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.main.logic.GridDivNumPresentationLogic;
import oripa.gui.presenter.main.logic.SubFramePresentationLogic;
import oripa.gui.presenter.main.logic.UIPanelPaintMenuListenerRegistration;
import oripa.gui.presenter.main.logic.ValuePanelPresentationLogic;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.UIPanelView;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class UIPanelPresenterTest {

	@Mock
	UIPanelView view;

	@Mock
	SubFramePresentationLogic subFramePresentationLogic;

	@Mock
	UIPanelPaintMenuListenerRegistration paintMenuListenerRegistration;
	@Mock
	GridDivNumPresentationLogic gridDivNumPresentationLogic;

	@Mock
	ValuePanelPresentationLogic valuePanelPresentationLogic;

	@Mock
	PainterScreenSetting mainScreenSetting;

	@Mock
	TypeForChangeContext typeForChangeContext;

	@Nested
	class TestAddPlugin {
		@Test
		void pluginShouldBeAdded() {

			setupTypeForChangeContext();

			GraphicMouseActionPlugin plugin = mock();
			var plugins = List.of(plugin);

			var presenter = construct();
			presenter.addPlugins(plugins);

			verify(paintMenuListenerRegistration).addPlugins(plugins);
		}

	}

	@Nested
	class TestShowCheckerWindow {
		@Captor
		ArgumentCaptor<Runnable> listenerCaptor;

		@Test
		void showCheckerWindowLogicShouldBeShown() {
			setupTypeForChangeContext();

			construct();

			verify(view).addCheckWindowButtonListener(listenerCaptor.capture());

			listenerCaptor.getValue().run();

			verify(subFramePresentationLogic).showCheckerWindow();

		}
	}

	@Nested
	class TestShowFoldedModelWindows {
		@Captor
		ArgumentCaptor<Runnable> showWindowCaptor;

		@Test
		void showFoldedModelWindowsLogicShouldBeCalled() {
			setupTypeForChangeContext();

			construct();

			verify(view).setShowFoldedModelWindowsListener(showWindowCaptor.capture());

			showWindowCaptor.getValue().run();

			verify(subFramePresentationLogic).showFoldedModelWindows();
		}

	}

	UIPanelPresenter construct() {

		return new UIPanelPresenter(
				view,
				subFramePresentationLogic,
				paintMenuListenerRegistration,
				gridDivNumPresentationLogic,
				valuePanelPresentationLogic,
				typeForChangeContext,
				mainScreenSetting);
	}

	void setupTypeForChangeContext() {
		when(typeForChangeContext.getTypeFrom()).thenReturn(mock());
		when(typeForChangeContext.getTypeTo()).thenReturn(mock());
	}

	void setupFrameView() {
		when(view.getTopLevelView()).thenReturn(mock(FrameView.class));
	}

	@Test
	void test() {
		assertNotNull("Not yet implemented");
	}

}
