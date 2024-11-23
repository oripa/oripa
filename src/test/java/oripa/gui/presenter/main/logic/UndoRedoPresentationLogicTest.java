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

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.view.ViewScreenUpdater;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class UndoRedoPresentationLogicTest {

	@InjectMocks
	UndoRedoPresentationLogic presentationLogic;

	@Mock
	ViewScreenUpdater screenUpdater;

	@Mock
	MouseActionHolder mouseActionHolder;

	@Mock
	PaintContext paintContext;

	@Nested
	class TestUndo {

		@Test
		void undoLogicShouldBeCalled() {

			GraphicMouseAction action = mock();
			when(mouseActionHolder.getMouseAction()).thenReturn(Optional.of(action));

			presentationLogic.undo();

			verify(action).undo(paintContext);
			verify(screenUpdater).updateScreen();

		}
	}

	@Nested
	class TestRedo {

		@Test
		void redoLogicShouldBeCalled() {

			GraphicMouseAction action = mock();
			when(mouseActionHolder.getMouseAction()).thenReturn(Optional.of(action));

			presentationLogic.redo();

			verify(action).redo(paintContext);
			verify(screenUpdater).updateScreen();

		}
	}

}
