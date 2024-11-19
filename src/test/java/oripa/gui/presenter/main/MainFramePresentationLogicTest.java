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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.ApplicationState;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
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
public class MainFramePresentationLogicTest {

	@InjectMocks
	MainFramePresentationLogic presentationLogic;

	@Mock
	MainFrameView view;

	@Mock
	ViewScreenUpdater screenUpdater;

	@Mock
	MainFrameDialogFactory dialogFactory;

	@Mock
	SubFrameFactory subFrameFactory;

	@Mock
	PainterScreenPresenter screenPresenter;

	@Mock
	UIPanelPresenter uiPanelPresenter;

	@Mock
	MainComponentPresenterFactory componentPresenterFactory;

	@Mock
	FileAccessPresentationLogic fileAccessPresentationLogic;

	@Mock
	ChildFrameManager childFrameManager;

	@Mock
	PainterScreenSetting screenSetting;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	Project project;

	@Mock
	PaintContext paintContext;

	@Mock
	PaintContextModification paintContextModification;

	@Mock
	CutModelOutlinesHolder cutModelOutlinesHolder;

	@Mock
	CreasePatternViewContext creasePatternViewContext;

	@Mock
	FileHistory fileHistory;

	@Mock
	IniFileAccess iniFileAccess;

	@Mock
	FileAccessService<Doc> dataFileAccess;

	@Mock
	FileFactory fileFactory;

	@Mock
	Supplier<CreasePatternFOLDConfig> foldConfigFactory;

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

			verify(iniFileAccess).save(eq(fileHistory), any());

			verify(doExit).run();
		}
	}

	@Nested
	class TestClear {
		@Test
		void succeeds() {

			setupGetTitleText("default", "");

			presentationLogic.clear();

			verify(paintContextModification).clear(any(), eq(cutModelOutlinesHolder));
			verify(project).clear();

			verify(screenSetting).setGridVisible(true);

			verify(childFrameManager).closeAll(view);

			verify(screenUpdater).updateScreen();

			verifyUpdateTitleText("default");
		}
	}

	void setupGetTitleText(final String defaultText, final String projectGetDataFileNameValue) {
		when(resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID)).thenReturn(defaultText);
		when(project.getDataFileName()).thenReturn(Optional.of(projectGetDataFileNameValue));
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

			verify(dataFileAccess, never()).saveFile(any(), anyString(), any());

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

			CreasePattern creasePattern = mock();
			when(paintContext.getCreasePattern()).thenReturn(creasePattern);
			when(paintContext.getPointEps()).thenReturn(0.1);

			DocFileSelectionPresenter selectionPresenter = mock();
			String selectedPath = "selected path";
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createSelectedForSave(
							selectedPath,
							mock());

			when(selectionPresenter.saveFileWithModelCheck(
					eq(creasePattern), anyString(), any(), any(), any(), anyDouble()))
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

			CreasePattern creasePattern = mock();
			when(paintContext.getCreasePattern()).thenReturn(creasePattern);
			when(paintContext.getPointEps()).thenReturn(0.1);

			DocFileSelectionPresenter selectionPresenter = mock();
			FileSelectionResult<Doc> selectionResult = FileSelectionResult
					.createCanceled();

			when(selectionPresenter.saveFileWithModelCheck(
					eq(creasePattern), anyString(), any(), any(), any(), anyDouble()))
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

		@MethodSource("createIniFileShouldBeLoadedArguments")
		@ParameterizedTest
		void iniFileShouldBeLoaded(
				final boolean isZeroLineWidth,
				final boolean isMvLineVisible,
				final boolean isAuxLineVisible,
				final boolean isVertexVisible) {

			InitData initData = mock();
			when(initData.isZeroLineWidth()).thenReturn(isZeroLineWidth);
			when(initData.isMvLineVisible()).thenReturn(isMvLineVisible);
			when(initData.isAuxLineVisible()).thenReturn(isAuxLineVisible);
			when(initData.isVertexVisible()).thenReturn(isVertexVisible);

			setupIniFileAccess(initData);

			presentationLogic.loadIniFile();

			verify(iniFileAccess).load();

			verify(fileHistory).loadFromInitData(initData);

			verify(screenSetting).setZeroLineWidth(isZeroLineWidth);
			verify(screenSetting).setMVLineVisible(isMvLineVisible);
			verify(screenSetting).setAuxLineVisible(isAuxLineVisible);
			verify(screenSetting).setVertexVisible(isVertexVisible);
		}

		static List<Arguments> createIniFileShouldBeLoadedArguments() {
			var booleanValues = List.of(true, false);

			var args = new ArrayList<Arguments>();

			for (var zeroWidth : booleanValues) {
				for (var mvLine : booleanValues) {
					for (var auxLine : booleanValues) {
						for (var vertex : booleanValues) {
							args.add(Arguments.of(zeroWidth, mvLine, auxLine, vertex));
						}
					}
				}
			}
			return args;
		}
	}

	void setupIniFileAccess(final InitData initData) {
		when(iniFileAccess.load()).thenReturn(initData);
	}

}
