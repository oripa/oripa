/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License; or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful;
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not; see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.presenter.main;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.main.logic.MainFramePaintMenuListenerFactory;
import oripa.gui.presenter.main.logic.MainFramePresentationLogic;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PropertyDialogView;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.project.Project;
import oripa.resource.ResourceKey;
import oripa.swing.view.main.ArrayCopyDialog;
import oripa.swing.view.main.CircleCopyDialog;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class MainFramePresenterTest {

	@Mock
	MainFrameView view;

	@Mock
	MainFrameDialogFactory dialogFactory;

	@Mock
	MainFramePresentationLogic presentationLogic;

	@Mock
	MainComponentPresenterFactory componentPresenterFactory;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	Project project;

	@Mock
	PaintContext paintContext;

	@Mock
	MainFramePaintMenuListenerFactory paintMenuListenerFactory;

	@Mock
	List<GraphicMouseActionPlugin> plugins;

	@Nested
	class Constructor {

		@Nested
		class TestLoadIniFile {

			@Test
			void iniFileShouldBeLoaded() {

				construct();

				verify(presentationLogic).loadIniFile();

			}

		}

		@Nested
		class TestAddPlugins {
			@Test
			void givenPluginsShouldBeAdded() {

				construct();

				verify(presentationLogic).addPlugins(plugins);
			}
		}

		@Nested
		class TestBuildFileMenu {
			@Test
			void buildFileMenuShouldBeCalled() {

				construct();

				verify(view).buildFileMenu();
			}
		}

		@Nested
		class TestUpdateTitleText {
			@Captor
			ArgumentCaptor<ResourceKey> resourceKeyCaptor;

			@Captor
			ArgumentCaptor<String> textIdCaptor;

			@Test
			void titleTextShouldBeUpdatedWithDefaultText() {

				construct();

				verify(presentationLogic).updateTitleText();

			}
		}

		@Nested
		class TestAddListeners {

			@Nested
			class TestAddExit {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void exitLogicShouldBeCalled() {

					construct();

					verify(view).addExitButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).exit(any());
				}
			}

			@Nested
			class TestClear {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void clearLogicShouldBeCalled() {

					construct();

					verify(view).addClearButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).clear();

				}
			}

			@Nested
			class TestShowPropertyDIalog {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void propertyDialogShouldBeShown() {

					PropertyDialogView dialog = mock();
					when(dialogFactory.createPropertyDialog(view)).thenReturn(dialog);

					PropertyDialogPresenter dialogPresenter = mock();
					when(componentPresenterFactory.createPropertyDialogPresenter(dialog, project))
							.thenReturn(dialogPresenter);

					construct();

					verify(view).addPropertyButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(dialogPresenter).setViewVisible(true);
				}
			}

			@Nested
			class TestShowArrayCopyDialog {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void arrayCopyDialogShouldBeShownWhenLinesAreSelected() {

					when(paintContext.countSelectedLines()).thenReturn(1);

					ArrayCopyDialog dialog = mock();
					when(dialogFactory.createArrayCopyDialog(view)).thenReturn(dialog);

					ArrayCopyDialogPresenter dialogPresenter = mock();
					when(componentPresenterFactory.createArrayCopyDialogPresenter(dialog))
							.thenReturn(dialogPresenter);

					construct();

					verify(view).addRepeatCopyButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(dialogPresenter).setViewVisible(true);
				}

				@Test
				void warningShouldBeShownWhenLinesAreNotSelected() {

					when(paintContext.countSelectedLines()).thenReturn(0);

					construct();

					verify(view).addRepeatCopyButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(view).showNoSelectionMessageForArrayCopy();
				}

			}

			@Nested
			class TestShowCircleCopyDialog {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void circleCopyDialogShouldBeShownWhenLinesAreSelected() {

					when(paintContext.countSelectedLines()).thenReturn(1);

					CircleCopyDialog dialog = mock();
					when(dialogFactory.createCircleCopyDialog(view)).thenReturn(dialog);

					CircleCopyDialogPresenter dialogPresenter = mock();
					when(componentPresenterFactory.createCircleCopyDialogPresenter(dialog))
							.thenReturn(dialogPresenter);

					construct();

					verify(view).addCircleCopyButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(dialogPresenter).setViewVisible(true);
				}

				@Test
				void warningShouldBeShownWhenLinesAreNotSelected() {

					when(paintContext.countSelectedLines()).thenReturn(0);

					construct();

					verify(view).addCircleCopyButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(view).showNoSelectionMessageForCircleCopy();
				}

			}

			@Nested
			class TestMRUFilesMenuItemUpdate {
				@Captor
				ArgumentCaptor<Consumer<Integer>> listenerCaptor;

				@Test
				void updateLogicShouldBeCalled() {

					construct();

					verify(view).addMRUFilesMenuItemUpdateListener(listenerCaptor.capture());

					listenerCaptor.getValue().accept(0);

					verify(presentationLogic).updateMRUFilesMenuItem(0);
				}
			}

			@Nested
			class TestSetEstimationResultSaveColors {
				@Captor
				ArgumentCaptor<BiConsumer<Color, Color>> listenerCaptor;

				@Test
				void putColorCodeLogicShouldBeCalled() {

					construct();

					verify(view).setEstimationResultSaveColorsListener(listenerCaptor.capture());

					Color front = mock();
					Color back = mock();
					listenerCaptor.getValue().accept(front, back);

					verify(presentationLogic).setEstimationResultSaveColors(front, back);
				}
			}

			@Nested
			class TestSetPaperDomainOfModelChange {
				@Captor
				ArgumentCaptor<Consumer<RectangleDomain>> listenerCaptor;

				@Test
				void setPaperDomainOfModelLogicShouldBeCalled() {

					construct();

					verify(view).setPaperDomainOfModelChangeListener(listenerCaptor.capture());

					RectangleDomain domain = mock();
					listenerCaptor.getValue().accept(domain);

					verify(presentationLogic).setPaperDomainOfModel(domain);
				}
			}

			@Nested
			class TestWindowClosing {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void saveIniWhenNoChangeOnCP() {

					when(paintContext.creasePatternChangeExists()).thenReturn(false);

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveIniFile();
				}

				@Test
				void saveIniWhenSaveCPIsCanceled() {

					when(paintContext.creasePatternChangeExists()).thenReturn(true);

					when(view.showSaveOnCloseDialog()).thenReturn(false);

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveIniFile();
				}

				@SuppressWarnings("unchecked")
				@Test
				void saveCPAndSaveIniWhenCPChangedAndFileIsSelected() {

					when(paintContext.creasePatternChangeExists()).thenReturn(true);

					when(view.showSaveOnCloseDialog()).thenReturn(true);

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveFileUsingGUI();
					verify(presentationLogic).saveIniFile();

				}

			}

			@Nested
			class TestUndo {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void undoLogicShouldBeCalled() {

					construct();

					verify(view).addUndoButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).undo();
				}
			}

			@Nested
			class TestRedo {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void redoLogicShouldBeCalled() {

					construct();

					verify(view).addRedoButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).redo();
				}
			}

			@Nested
			class TestSave {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void saveToCurrentPathLogicShouldBeCalledWhenProjectIsProjectFile() {

					try (var projectStatic = mockStatic(Project.class)) {

						String path = "path";
						FileType<Doc> fileType = mock();
						when(project.getProjectFileType()).thenReturn(Optional.of(fileType));
						projectStatic.when(() -> Project.projectFileTypeMatch(path)).thenReturn(true);

						when(presentationLogic.saveFileToCurrentPath(fileType)).thenReturn(path);

						construct();

						verify(view).addSaveButtonListener(listenerCaptor.capture());

						listenerCaptor.getValue().run();

						// verify before save
						verifyBeforeSave();

						verify(presentationLogic).saveFileToCurrentPath(fileType);

						// verify after save
						verifyAfterSave(true, path, paintContext);
					}

				}

				@SuppressWarnings("unchecked")
				@Test
				void saveUsingGUILogicShouldBeCalledWhenProjectIsNotProjectFile() {

					try (var projectStatic = mockStatic(Project.class)) {

						String path = "path";

						when(project.getProjectFileType()).thenReturn(Optional.empty());
						projectStatic.when(() -> Project.projectFileTypeMatch(path)).thenReturn(false);

						when(presentationLogic.saveFileUsingGUI()).thenReturn(path);

						construct();

						verify(view).addSaveButtonListener(listenerCaptor.capture());

						listenerCaptor.getValue().run();

						// verify before save
						verifyBeforeSave();

						verify(presentationLogic).saveFileUsingGUI();

						// verify after save
						verifyAfterSave(false, path, null);
					}

				}

			}

			@Nested
			class TestSaveAs {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@SuppressWarnings("unchecked")
				@Test
				void succeeds() {

					try (var projectStatic = mockStatic(Project.class)) {
						String path = "path";

						projectStatic.when(() -> Project.projectFileTypeMatch(path)).thenReturn(true);

						when(presentationLogic.saveFileUsingGUI()).thenReturn(path);

						construct();

						verify(view).addSaveAsButtonListener(listenerCaptor.capture());

						listenerCaptor.getValue().run();

						// verify before save
						verifyBeforeSave();

						verify(presentationLogic).saveFileUsingGUI();

						// verify after save
						verifyAfterSave(true, path, paintContext);
					}
				}

			}

			void verifyBeforeSave() {
				verify(presentationLogic).modifySavingActions();
			}

			void verifyAfterSave(
					final boolean projectFileTypeMatch,
					final String path,
					final PaintContext paintContext) {
				if (projectFileTypeMatch) {
					verify(paintContext).clearCreasePatternChanged();
					verify(project).setDataFilePath(path);
				}
				verify(presentationLogic).updateMenu();
				verify(presentationLogic, atLeastOnce()).updateTitleText();

			}

			@Nested
			class TestExportFOLD {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void succeeds() {

					construct();

					verify(view).addExportFOLDButtonListener(listenerCaptor.capture());

					testExportWithTypeTemplate(DocFileTypes.fold(), true, listenerCaptor.getValue());
				}
			}

			@Nested
			class TestExportPicture {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void succeeds() {

					construct();

					verify(view).addSaveAsImageButtonListener(listenerCaptor.capture());

					testExportWithTypeTemplate(DocFileTypes.pictutre(), false, listenerCaptor.getValue());
				}
			}

			@SuppressWarnings("unchecked")
			void testExportWithTypeTemplate(final FileType<Doc> type, final boolean isProjectFile,
					final Runnable listener) {

				try (var projectStatic = mockStatic(Project.class)) {
					listener.run();

					String path = "path";

					projectStatic.when(() -> Project.projectFileTypeMatch(path)).thenReturn(isProjectFile);

					// verify before save
					verify(presentationLogic).modifySavingActions();

					verify(presentationLogic).saveFileUsingGUI(eq(type));
					verifyNoCallsAfterExport(isProjectFile, path, paintContext);
				}

			}

			@Nested
			class TestExportDXF {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void succeeds() {

					construct();

					verify(view).addExportDXFButtonListener(listenerCaptor.capture());

					testExportWithModelCheckTemplate(DocFileTypes.dxf(), false, listenerCaptor.getValue());
				}
			}

			@Nested
			class TestExportCP {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void succeeds() {

					construct();

					verify(view).addExportCPButtonListener(listenerCaptor.capture());

					testExportWithModelCheckTemplate(DocFileTypes.cp(), false, listenerCaptor.getValue());
				}
			}

			@Nested
			class TestExportSVG {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void succeeds() {

					construct();

					verify(view).addExportSVGButtonListener(listenerCaptor.capture());

					testExportWithModelCheckTemplate(DocFileTypes.svg(), false, listenerCaptor.getValue());
				}
			}

			void testExportWithModelCheckTemplate(final FileType<Doc> type, final boolean isProjectFile,
					final Runnable listener) {

				try (var projectStatic = mockStatic(Project.class)) {
					listener.run();

					String path = "path";

					projectStatic.when(() -> Project.projectFileTypeMatch(path)).thenReturn(isProjectFile);

					verify(presentationLogic).exportFileUsingGUIWithModelCheck(type);
					verifyNoCallsAfterExport(isProjectFile, path, paintContext);
				}

			}

			void verifyNoCallsAfterExport(
					final boolean projectFileTypeMatch,
					final String path,
					final PaintContext paintContext) {
				if (projectFileTypeMatch) {
					verify(paintContext, never()).clearCreasePatternChanged();
					verify(project, never()).setDataFilePath(path);
				}
				verify(presentationLogic, never()).updateMenu();
				verify(presentationLogic).updateTitleText();

			}

			@Nested
			class TestOpen {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void loadUsingGUILogicShouldBeCalled() {

					construct();

					verify(view).addOpenButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).loadFileUsingGUI();

					verifyAfterLoad();
				}
			}

			@Nested
			class TestMRUFileLoad {
				@Captor
				ArgumentCaptor<Consumer<String>> listenerCaptor;

				@Test
				void loadLogicShouldBeCalled() {

					construct();

					verify(view).addMRUFileButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().accept("path");

					verify(presentationLogic).loadFile("path");

					verifyAfterLoad();
				}
			}

			void verifyAfterLoad() {
				verify(presentationLogic).updateScreen();
				verify(presentationLogic).updateMenu();
				verify(presentationLogic, atLeastOnce()).updateTitleText();
				verify(presentationLogic).updateValuePanelFractionDigits();

			}

			@Nested
			class TestImport {

				@Test
				void listenerShouldBeAdded() {

					construct();

					verify(view).addImportButtonListener(any());
				}
			}
		}

	}

	MainFramePresenter construct() {
		return new MainFramePresenter(
				view,
				dialogFactory,
				presentationLogic,
				componentPresenterFactory,
				paintMenuListenerFactory,
				project,
				paintContext,
				plugins);
	}

}
