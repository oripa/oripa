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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.main.DocFileAccess;
import oripa.domain.paint.PaintContext;
import oripa.domain.projectprop.Property;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.UIPanelPresenter;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameView;
import oripa.persistence.dao.FileType;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class MainFramePresentationLogicTest {

	@InjectMocks
	MainFramePresentationLogic presentationLogic;

	@Mock
	MainFrameView view;

	@Mock
	ViewScreenUpdater screenUpdater;

	@Mock
	PainterScreenPresenter screenPresenter;

	@Mock
	UIPanelPresenter uiPanelPresenter;

	@Mock
	ClearActionPresentationLogic clearActionPresentationLogic;

	@Mock
	UndoRedoPresentationLogic undoRedoPresentationLogic;

	@Mock
	MainFrameFilePresentationLogic mainFrameFilePresentationLogic;

	@Mock
	FileAccessPresentationLogic fileAccessPresentationLogic;

	@Mock
	IniFileAccessPresentationLogic iniFileAccessPresentationLogic;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	Project project;

	@Mock
	PaintContext paintContext;

	@Mock
	FileHistory fileHistory;

	@Mock
	DocFileAccess docFileAccess;

	@Mock
	FileFactory fileFactory;

	@Mock
	ResourceHolder resourceHolder;

	@Nested
	class TestUpdateMRUFilesMenuItem {
		@Test
		void setsHistoryValueWhenIndexIsSmallerThanSize() {
			List<String> filePaths = List.of("1", "2");
			when(fileHistory.getHistory()).thenReturn(filePaths);

			presentationLogic.updateMRUFilesMenuItem(0);

			verify(view).setMRUFilesMenuItem(0, filePaths.get(0));
		}

		@Test
		void setsEmptyStringWhenIndexIsGreaterThanOrEqualToSize() {
			List<String> filePaths = List.of("1", "2");
			when(fileHistory.getHistory()).thenReturn(filePaths);

			presentationLogic.updateMRUFilesMenuItem(2);

			verify(view).setMRUFilesMenuItem(2, "");
		}
	}

	@Nested
	class TestExit {
		@Test
		void shouldSaveIniFile() {

			Runnable doExit = mock();
			presentationLogic.exit(doExit);

			verify(iniFileAccessPresentationLogic).saveIniFile();

			verify(doExit).run();
		}
	}

	@Nested
	class TestClear {
		@Test
		void succeeds() {

			when(resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID)).thenReturn("default");
			when(project.getDataFileName()).thenReturn(Optional.empty());

			presentationLogic.clear();

			verify(clearActionPresentationLogic).clear();
			verifyUpdateTitleText("default");
		}
	}

	void verifyUpdateTitleText(final String dataFileName) {
		verify(resourceHolder).getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		verify(project).getDataFileName();
		verify(view).setFileNameToTitle(dataFileName);
	}

	@Nested
	class TestUpdateMenu {
		@Test
		void menuShouldBeUpdatedWhenProjectIsProjectFile() {
			when(project.getDataFilePath()).thenReturn("path");
			when(project.isProjectFile()).thenReturn(true);

			presentationLogic.updateMenu();

			verify(fileHistory).useFile("path");

			verify(view).buildFileMenu();
		}

		@Test
		void menuShouldNotBeUpdatedWhenProjectIsNotProjectFile() {
			when(project.getDataFilePath()).thenReturn("path");
			when(project.isProjectFile()).thenReturn(false);

			presentationLogic.updateMenu();

			verify(fileHistory, never()).useFile("path");

			verify(view, never()).buildFileMenu();
		}

	}

	@Nested
	class TestUndo {

		@Test
		void undoLogicShouldBeCalled() {

			presentationLogic.undo();

			verify(undoRedoPresentationLogic).undo();
		}
	}

	@Nested
	class TestRedo {

		@Test
		void redoLogicShouldBeCalled() {

			presentationLogic.redo();

			verify(undoRedoPresentationLogic).redo();
		}
	}

	@Nested
	class TestModifySavingActions {

		@Test
		void modifySavingActionsLogicShouldBeCalled() {
			// execute
			presentationLogic.modifySavingActions();

			verify(mainFrameFilePresentationLogic).modifySavingActions();
		}
	}

	@Nested
	class TestSaveFileToCurrentPath {
		@Test
		void saveFileToCurrentPathLogicShouldBeCalled() {
			// execute
			presentationLogic.saveFileToCurrentPath(mock());

			verify(mainFrameFilePresentationLogic).saveFileToCurrentPath(any());
		}
	}

	@Nested
	class TestSaveFileUsingGUI {
		@SuppressWarnings("unchecked")
		@Test
		void saveFileUsingGUILogicShouldBeCalled() {

			// execute
			presentationLogic.saveFileUsingGUI();

			verify(mainFrameFilePresentationLogic).saveFileUsingGUI(any(FileType[].class));
		}

	}

	@Nested
	class TestExportFileUsingGUIWithModelCheck {
		@Test
		void ExportFileUsingGUIWithModelCheckLogicShouldBeCalled() throws IOException {

			// execute
			presentationLogic.exportFileUsingGUIWithModelCheck(mock());

			verify(mainFrameFilePresentationLogic).exportFileUsingGUIWithModelCheck(any());
		}

	}

	@Nested
	class TestLoadFile {

		@Test
		void loadFileLogicShouldBeCalled() {

			String path = "path";

			// execute
			presentationLogic.loadFile(path);

			verify(mainFrameFilePresentationLogic).loadFile(path);
		}

	}

	@Nested
	class TestLoadFileUsingGUI {

		@Test
		void loadFileUsingGUILogicShouldBeCalled() {

			// execute
			presentationLogic.loadFileUsingGUI();

			verify(mainFrameFilePresentationLogic).loadFileUsingGUI();

		}
	}

	@Nested
	class TestImportFileUsingGUI {

		@Test
		void importFileUsingGUILogicShouldBeCalled() {
			Runnable stateAction = mock();

			// execute
			presentationLogic.importFileUsingGUI(stateAction);

			var inOrder = inOrder(mainFrameFilePresentationLogic, stateAction);

			inOrder.verify(mainFrameFilePresentationLogic).importFileUsingGUI();
			inOrder.verify(stateAction).run();
		}
	}

	@Nested
	class TestLoadIniFile {

		@Test
		void iniFileShouldBeLoaded() {

			presentationLogic.loadIniFile();

			verify(iniFileAccessPresentationLogic).loadIniFile();

		}
	}

	@Nested
	class TestSetEstimationResultSaveColors {
		@Captor
		ArgumentCaptor<BiConsumer<Color, Color>> listenerCaptor;

		@Test
		void putColorCodeLogicShouldBeCalled() {

			Property property = mock();
			when(project.getProperty()).thenReturn(property);

			Color front = mock();
			Color back = mock();

			presentationLogic.setEstimationResultSaveColors(front, back);

			verify(property).putFrontColorCode(anyString());
			verify(property).putBackColorCode(anyString());
		}
	}

}
