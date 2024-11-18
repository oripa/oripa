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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.projectprop.Property;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.dao.DataAccessException;
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

	@Mock
	MainFrameView view;

	@Mock
	ViewUpdateSupport viewUpdateSupport;

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
	ChildFrameManager childFrameManager;

	@Mock
	MainViewSetting viewSetting;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	Project project;

	@Mock
	PaintDomainContext domainContext;

	@Mock
	PaintContextModification paintContextModification;

	@Mock
	CutModelOutlinesHolder cutModelOutlinesHolder;

	@Mock
	CreasePatternPresentationContext presentationContext;

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

			var presentationLogic = construct();
			presentationLogic.updateMRUFilesMenuItem(0);

			verify(view).setMRUFilesMenuItem(0, filePaths.get(0));
		}

		@Test
		void setsEmptyStringWhenIndexIsGreaterThanOrEqualToSize() {
			List<String> filePaths = List.of("1", "2");
			when(fileHistory.getHistory()).thenReturn(filePaths);

			var presentationLogic = construct();
			presentationLogic.updateMRUFilesMenuItem(2);

			verify(view).setMRUFilesMenuItem(2, "");
		}
	}

	@Nested
	class TestExit {
		@Test
		void shouldSaveIniFile() {

			var presentationLogic = construct();

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

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			ViewScreenUpdater screenUpdater = mock();
			setupViewUpdateSupport(screenUpdater);

			setupGetTitleText("default", "");

			var presentationLogic = construct();
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

			var presentationLogic = construct();
			presentationLogic.updateMenu();

			verify(fileHistory).useFile("path");

			verify(view).buildFileMenu();
		}

		@Test
		void menuShouldNotBeUpdatedWhenProjectIsNotProjectFile() {
			when(project.getDataFilePath()).thenReturn("path");
			when(project.isProjectFile()).thenReturn(false);

			var presentationLogic = construct();
			presentationLogic.updateMenu();

			verify(fileHistory, never()).useFile("path");

			verify(view, never()).buildFileMenu();
		}

	}

	@Nested
	class TestSaveFileUsingGUIImpl {
		@SuppressWarnings("unchecked")
		@Test
		void succeedsWhenFileIsSelected() {

			PaintContext paintContext = mock();
			when(paintContext.getCreasePattern()).thenReturn(mock());
			setupDomainContext(paintContext);

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			when(project.getDataFileName()).thenReturn(Optional.of("file name"));
			when(project.getProperty()).thenReturn(mock());

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

			// execute
			var presentationLogic = construct();
			var selectedPath = presentationLogic.saveFileUsingGUIImpl();

			verify(dataFileAccess).saveFile(any(), eq("selected path"), any());

			assertEquals("selected path", selectedPath);
		}

		@SuppressWarnings("unchecked")
		@Test
		void noChangesWhenCanceled() {

			setupDomainContext();

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
			var presentationLogic = construct();
			var selectedPath = presentationLogic.saveFileUsingGUIImpl();

			verify(dataFileAccess, never()).saveFile(any(), anyString(), any());

			assertEquals("project path", selectedPath);
		}

		@SuppressWarnings("unchecked")
		@Test
		void noChangesWhenDataAccessErrors() {

			PaintContext paintContext = mock();
			when(paintContext.getCreasePattern()).thenReturn(mock());
			setupDomainContext(paintContext);

			when(fileHistory.getLastDirectory()).thenReturn("directory");

			when(project.getDataFileName()).thenReturn(Optional.of("file name"));
			when(project.getDataFilePath()).thenReturn("project path");
			when(project.getProperty()).thenReturn(mock());

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

			doThrow(DataAccessException.class).when(dataFileAccess).saveFile(any(), anyString(), any());

			// execute
			var presentationLogic = construct();
			var selectedPath = presentationLogic.saveFileUsingGUIImpl();

			verify(view).showSaveFailureErrorMessage(any());
			assertEquals("project path", selectedPath);
		}

	}

	@Nested
	class TestLoadFileImpl {

		@Test
		void succeedsWhenFileIsLoaded() {

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			setupViewUpdateSupport();

			setupDomainContext();

			String path = "path";
			Property loadedProperty = mock();
			Doc loadedDoc = mock();
			when(loadedDoc.getProperty()).thenReturn(loadedProperty);
			when(loadedDoc.getCreasePattern()).thenReturn(mock());
			when(dataFileAccess.loadFile(eq(path))).thenReturn(Optional.of(loadedDoc));

			Property currentProperty = mock();
			when(project.getProperty()).thenReturn(currentProperty);

			// execute
			var presentationLogic = construct();
			var loadedPath = presentationLogic.loadFileImpl(path);

			assertEquals(path, loadedPath);

			verify(childFrameManager).closeAll(view);

			verify(dataFileAccess).loadFile(path);
			verify(project).setProperty(loadedProperty);
			verify(project).setDataFilePath(anyString());
			verify(view).setEstimationResultColors(any(), any());
			verify(screenSetting).setGridVisible(false);
			verify(paintContextModification).setCreasePatternToPaintContext(any(), any(), eq(cutModelOutlinesHolder));
			verify(screenPresenter).updateCameraCenter();

		}

		@Test
		void noChangesWhenFileIsNotLoaded() {

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			setupViewUpdateSupport();

			setupDomainContext();

			String path = "path";
			// couldn't load
			when(dataFileAccess.loadFile(eq(path))).thenReturn(Optional.empty());

			// execute
			var presentationLogic = construct();
			var loadedPath = presentationLogic.loadFileImpl(path);

			assertNull(loadedPath);

			verify(childFrameManager).closeAll(view);

			verify(dataFileAccess).loadFile(path);

			verify(project, never()).setProperty(any());
			verify(project, never()).setDataFilePath(anyString());
			verify(view, never()).setEstimationResultColors(any(), any());
			verify(screenSetting, never()).setGridVisible(anyBoolean());
			verify(paintContextModification, never())
					.setCreasePatternToPaintContext(any(), any(), eq(cutModelOutlinesHolder));

		}

		@Test
		void noChangesWhenDataAccessErrors() {

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			setupViewUpdateSupport();

			setupDomainContext();

			doThrow(DataAccessException.class).when(dataFileAccess).loadFile(anyString());

			when(project.getDataFilePath()).thenReturn("project path");

			// execute
			var presentationLogic = construct();
			var loadedPath = presentationLogic.loadFileImpl("path");

			assertEquals("project path", loadedPath);

			verify(view).showLoadFailureErrorMessage(any());

			verify(childFrameManager).closeAll(view);
			verify(project, never()).setDataFilePath(anyString());
			verify(view, never()).setEstimationResultColors(any(), any());
			verify(screenSetting, never()).setGridVisible(false);
			verify(paintContextModification, never()).setCreasePatternToPaintContext(any(), any(),
					eq(cutModelOutlinesHolder));
			verify(screenPresenter, never()).updateCameraCenter();

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

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			setupViewUpdateSupport();

			setupDomainContext(mock());

			InitData initData = mock();
			when(initData.isZeroLineWidth()).thenReturn(isZeroLineWidth);
			when(initData.isMvLineVisible()).thenReturn(isMvLineVisible);
			when(initData.isAuxLineVisible()).thenReturn(isAuxLineVisible);
			when(initData.isVertexVisible()).thenReturn(isVertexVisible);

			setupIniFileAccess(initData);

			var presentationLogic = construct();
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

	MainFramePresentationLogic construct() {
		return new MainFramePresentationLogic(
				view,
				viewSetting,
				viewUpdateSupport,
				dialogFactory,
				subFrameFactory,
				screenPresenter,
				uiPanelPresenter,
				componentPresenterFactory,
				presentationContext,
				childFrameManager,
				bindingFactory,
				project,
				domainContext,
				paintContextModification,
				cutModelOutlinesHolder,
				fileHistory,
				iniFileAccess,
				dataFileAccess,
				fileFactory,
				resourceHolder);
	}

	void setupResourceHolder() {
		setupResourceHolder("");
	}

	void setupResourceHolder(final String value) {
		when(resourceHolder.getString(any(), anyString())).thenReturn(value);
	}

	void setupProject() {
		when(project.getDataFileName()).thenReturn(Optional.empty());
	}

	void setupView() {
		when(view.getPainterScreenView()).thenReturn(mock());
		when(view.getUIPanelView()).thenReturn(mock());
	}

	void setupViewSetting(final PainterScreenSetting screenSetting) {
		when(viewSetting.getPainterScreenSetting()).thenReturn(screenSetting);
	}

	void setupViewSetting() {
		setupViewSetting(mock());
	}

	void setupViewUpdateSupport(final ViewScreenUpdater screenUpdater) {
		when(viewUpdateSupport.getViewScreenUpdater()).thenReturn(screenUpdater);
	}

	void setupViewUpdateSupport() {
		setupViewUpdateSupport(mock());
	}

	void setupPresentationContext() {
		when(presentationContext.getViewContext()).thenReturn(mock());
		when(presentationContext.getActionHolder()).thenReturn(mock());

	}

	void setupDomainContext(final PaintContext paintContext) {
		when(domainContext.getPaintContext()).thenReturn(paintContext);
	}

	void setupDomainContext() {
		setupDomainContext(mock());
	}

	void setupComponentPresenterFactory(
			final PainterScreenPresenter screenPresenter,
			final UIPanelPresenter uiPanelPresenter) {
		when(componentPresenterFactory.createPainterScreenPresenter(any())).thenReturn(screenPresenter);
		when(componentPresenterFactory.createUIPanelPresenter(any())).thenReturn(uiPanelPresenter);
	}

	void setupComponentPresenterFactory() {
		setupComponentPresenterFactory(mock(), mock());
	}

	void setupBindingFactory() {
		when(bindingFactory.createState(anyString())).thenReturn(mock());
		when(bindingFactory.createState(anyString(), any(), any())).thenReturn(mock());
	}

	void setupIniFileAccess(final InitData initData) {
		when(iniFileAccess.load()).thenReturn(initData);
	}

	void setupIniFileAccess() {
		setupIniFileAccess(mock());
	}

	void setupFOLDConfigFactory(final CreasePatternFOLDConfig config) {
		when(foldConfigFactory.get()).thenReturn(config);
	}

	void setupFOLDConfigFactory() {
		setupFOLDConfigFactory(mock());
	}

}
