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
package oripa.gui.presenter.main.logic;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.main.PaintContextService;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.project.Project;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class ClearActionPresentationLogicTest {
    @InjectMocks
    ClearActionPresentationLogic presentationLogic;

    @Mock
    MainFrameView view;

    @Mock
    PainterScreenPresenter screenPresenter;

    @Mock
    PainterScreenSetting screenSetting;

    @Mock
    ChildFrameManager childFrameManager;

    @Mock
    Project project;

    @Mock
    PaintContext paintContext;

    @Mock
    CutModelOutlinesHolder cutModelOutlinesHolder;

    @Mock
    PaintContextService paintContextService;

    @Nested
    class TestClear {
        @Test
        void clearAll() {

            presentationLogic.clearAll();

            verify(paintContextService).clearAll();
            verify(project).clear();

            verify(screenSetting).setGridVisible(true);

            verify(childFrameManager).closeAll(view);

            verify(screenPresenter).clearPaperDomainOfModel();
            verify(screenPresenter).updateScreen();
        }

        @Test
        void clearCreasePattern() {

            presentationLogic.clearLines();

            verify(paintContextService).clearLines();
            verify(project).clear();

            verify(screenSetting).setGridVisible(true);

            verify(childFrameManager).closeAll(view);

            verify(screenPresenter).clearPaperDomainOfModel();
            verify(screenPresenter).updateScreen();
        }
    }
}
