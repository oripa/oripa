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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.main.DocFileAccess;
import oripa.appstate.ApplicationState;
import oripa.domain.paint.PaintContext;
import oripa.domain.projectprop.Property;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.presenter.main.DocFileSelectionPresenter;
import oripa.gui.presenter.main.MainComponentPresenterFactory;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.UIPanelPresenter;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameView;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
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
	MainComponentPresenterFactory componentPresenterFactory;

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
		@Captor
		ArgumentCaptor<Supplier<Object>> foldConfigCaptor;

		@Test
		void saveConfigurationOfFOLDShouldBeDone() {
			// execute
			presentationLogic.modifySavingActions();

			verify(docFileAccess).setupFOLDConfigForSaving();
		}
	}

	@Nested
	class TestSsveFileToCurrentPath {
		@Test
		void succeeds() {
			var path = "path";

			when(project.getDataFilePath()).thenReturn(path);
			FileType<Doc> type = mock();

			// execute
			presentationLogic.saveFileToCurrentPath(type);

			verify(fileAccessPresentationLogic).saveFile(path, type);
		}
	}

	@Nested
	class TestSaveFileUsingGUI {
		@SuppressWarnings("unchecked")
		@Test
		void succeedsWhenFileIsSelected() {

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			when(project.getDataFileName()).thenReturn(Optional.of("file name"));

			File defaultFile = mock();
			when(defaultFile.getPath()).thenReturn("path");
			when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

			DocFileSelectionPresenter selectionPresenter = mock();
			String selectedPath = "selected path";
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createSelectedForSave(
							selectedPath,
							mock());
			when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			when(fileAccessPresentationLogic.saveFile(eq(selectedPath), any())).thenReturn(selectedPath);

			// execute

			var returnedPath = presentationLogic.saveFileUsingGUI();

			assertEquals(selectedPath, returnedPath);
		}

		@SuppressWarnings("unchecked")
		@Test
		void newFileOpxWhenProjectIsNotLoaded() {

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			when(project.getDataFileName()).thenReturn(Optional.empty());

			File defaultFile = mock();
			when(defaultFile.getPath()).thenReturn("path");
			when(fileFactory.create("directory", "newFile.opx")).thenReturn(defaultFile);

			DocFileSelectionPresenter selectionPresenter = mock();
			String selectedPath = "selected path";
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createSelectedForSave(
							selectedPath,
							mock());
			when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			when(fileAccessPresentationLogic.saveFile(eq(selectedPath), any())).thenReturn(selectedPath);

			// execute

			var returnedPath = presentationLogic.saveFileUsingGUI();

			assertEquals(selectedPath, returnedPath);
		}

		@SuppressWarnings("unchecked")
		@Test
		void noChangesWhenCanceled() {

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			when(project.getDataFileName()).thenReturn(Optional.of("file name"));
			when(project.getDataFilePath()).thenReturn("project path");

			File defaultFile = mock();
			when(defaultFile.getPath()).thenReturn("path");
			when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

			DocFileSelectionPresenter selectionPresenter = mock();
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createCanceled();
			when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			// execute

			var selectedPath = presentationLogic.saveFileUsingGUI();

			verify(docFileAccess, never()).saveFile(any(), anyString(), any());

			assertEquals("project path", selectedPath);
		}

		@SuppressWarnings("unchecked")
		@Test
		void noChangesWhenDataAccessErrors() {

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			String projectPath = "project path";
			when(project.getDataFileName()).thenReturn(Optional.of("file name"));
			when(project.getDataFilePath()).thenReturn(projectPath);

			File defaultFile = mock();
			when(defaultFile.getPath()).thenReturn("path");
			when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

			DocFileSelectionPresenter selectionPresenter = mock();
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createSelectedForSave(
							"selected path",
							mock());
			when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			doThrow(DataAccessException.class).when(fileAccessPresentationLogic).saveFile(anyString(), any());

			// execute

			var returnedPath = presentationLogic.saveFileUsingGUI();

			assertEquals(projectPath, returnedPath);
		}

	}

	@Nested
	class TestExportFileUsingGUIWithModelCheck {
		@Test
		void succeedsWhenCheckIsPassed() throws IOException {
			when(fileHistory.getLastDirectory()).thenReturn("directory");

			DocFileSelectionPresenter selectionPresenter = mock();
			String selectedPath = "selected path";
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createSelectedForSave(
							selectedPath,
							mock());

			when(selectionPresenter.saveFileWithModelCheck(
					anyString(), any(), any()))
							.thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			FileType<Doc> type = mock();

			// execute

			presentationLogic.exportFileUsingGUIWithModelCheck(type);

			verify(fileAccessPresentationLogic).saveFile(anyString(), eq(type));
		}

		@Test
		void noCHangessWhenCanceledByUserOrCheck() throws IOException {
			when(fileHistory.getLastDirectory()).thenReturn("directory");

			DocFileSelectionPresenter selectionPresenter = mock();
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createCanceled();

			when(selectionPresenter.saveFileWithModelCheck(
					anyString(), any(), any()))
							.thenReturn(selectionResult);
			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			FileType<Doc> type = mock();

			// execute

			presentationLogic.exportFileUsingGUIWithModelCheck(type);

			verify(fileAccessPresentationLogic, never()).saveFile(anyString(), eq(type));
		}

	}

	@Nested
	class TestLoadFile {

		@Test
		void succeedsWhenFileIsLoaded() {

			String path = "path";

			when(fileAccessPresentationLogic.loadFile(eq(path))).thenReturn(path);

			// execute

			var loadedPath = presentationLogic.loadFile(path);

			assertEquals(path, loadedPath);
		}

		@Test
		void noChangesWhenFileIsNotLoaded() {

			String path = "path";
			// couldn't load
			when(fileAccessPresentationLogic.loadFile(eq(path))).thenReturn(null);

			// execute

			var loadedPath = presentationLogic.loadFile(path);

			assertNull(loadedPath);

		}

	}

	@Nested
	class TestLoadFileUsingGUI {

		@Test
		void succeedsWhenFileIsLoaded() {

			var lastPath = "last path";
			when(fileHistory.getLastPath()).thenReturn(lastPath);

			var selectedPath = "path";

			FileSelectionResult<Doc> selection = FileSelectionResult.createSelectedForLoad(selectedPath);
			DocFileSelectionPresenter selectionPresenter = mock();
			when(selectionPresenter.loadUsingGUI(lastPath)).thenReturn(selection);

			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			// execute

			presentationLogic.loadFileUsingGUI();

			verify(fileAccessPresentationLogic).loadFile(eq(selectedPath));

		}
	}

	@Nested
	class TestImport {

		@Test
		void succeedsWhenFileIsLoaded() {

			var lastPath = "last path";
			when(fileHistory.getLastPath()).thenReturn(lastPath);

			var selectedPath = "path";

			FileSelectionResult<Doc> selection = FileSelectionResult.createSelectedForLoad(selectedPath);
			DocFileSelectionPresenter selectionPresenter = mock();
			when(selectionPresenter.loadUsingGUI(lastPath)).thenReturn(selection);

			when(componentPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
					.thenReturn(selectionPresenter);

			ApplicationState<EditMode> state = mock();

			InOrder inOrder = inOrder(fileAccessPresentationLogic, state);

			// execute

			presentationLogic.importFileUsingGUI(state);

			inOrder.verify(fileAccessPresentationLogic).importFile(eq(selectedPath));
			inOrder.verify(state).performActions();
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
