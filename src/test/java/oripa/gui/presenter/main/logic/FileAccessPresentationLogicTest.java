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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.PaintContextService;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.projectprop.Property;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.doc.Doc;
import oripa.project.Project;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FileAccessPresentationLogicTest {
	@InjectMocks
	FileAccessPresentationLogic presentationLogic;

	@Mock
	MainFrameView view;

	@Mock
	PainterScreenPresenter screenPresenter;

	@Mock
	PainterScreenSetting screenSetting;

	@Mock
	ChildFrameManager childFrameManager;

	@Mock
	PaintContextService paintContextService;

	@Mock
	FileAccessService<Doc> dataFileAccess;

	@Mock
	PaintContext paintContext;
	@Mock
	CutModelOutlinesHolder cutModelOutlinesHolder;
	@Mock
	Project project;

	@Nested
	class TestSaveFile {
		@Test
		void succeeds() {

			// execute
			var selectedPath = presentationLogic.saveFile("path", mock());

			verify(dataFileAccess).saveFile(any(), eq("path"), any());

			assertEquals("path", selectedPath);
		}

	}

	@Nested
	class TestLoadFile {

		@Test
		void succeedsWhenFileIsLoaded() {

			String path = "path";
			Property loadedProperty = mock();
			Doc loadedDoc = mock();
			when(loadedDoc.getProperty()).thenReturn(loadedProperty);
			when(loadedDoc.getCreasePattern()).thenReturn(mock());
			when(dataFileAccess.loadFile(eq(path))).thenReturn(Optional.of(loadedDoc));

			Property currentProperty = mock();
			when(project.getProperty()).thenReturn(currentProperty);

			// execute
			var loadedPath = presentationLogic.loadFile(path);

			assertEquals(path, loadedPath);

			verify(childFrameManager).closeAll(view);

			verify(dataFileAccess).loadFile(path);
			verify(project).setProperty(loadedProperty);
			verify(project).setDataFilePath(anyString());
			verify(view).setEstimationResultColors(any(), any());
			verify(screenSetting).setGridVisible(false);
			verify(paintContextService).setCreasePatternToPaintContext(any());
			verify(screenPresenter).updateCameraCenter();

		}

		@Test
		void noChangesWhenFileIsNotLoaded() {

			String path = "path";
			// couldn't load
			when(dataFileAccess.loadFile(eq(path))).thenReturn(Optional.empty());

			// execute

			var loadedPath = presentationLogic.loadFile(path);

			assertNull(loadedPath);

			verify(childFrameManager).closeAll(view);

			verify(dataFileAccess).loadFile(path);

			verify(project, never()).setProperty(any());
			verify(project, never()).setDataFilePath(anyString());
			verify(view, never()).setEstimationResultColors(any(), any());
			verify(screenSetting, never()).setGridVisible(anyBoolean());
			verify(paintContextService, never())
					.setCreasePatternToPaintContext(any());

		}

		@Test
		void noChangesWhenDataAccessErrors() {

			doThrow(DataAccessException.class).when(dataFileAccess).loadFile(anyString());

			when(project.getDataFilePath()).thenReturn("project path");

			// execute
			var loadedPath = presentationLogic.loadFile("path");

			assertEquals("project path", loadedPath);

			verify(view).showLoadFailureErrorMessage(any());

			verify(childFrameManager).closeAll(view);
			verify(project, never()).setDataFilePath(anyString());
			verify(view, never()).setEstimationResultColors(any(), any());
			verify(screenSetting, never()).setGridVisible(false);
			verify(paintContextService, never()).setCreasePatternToPaintContext(any());
			verify(screenPresenter, never()).updateCameraCenter();

		}

	}

}
