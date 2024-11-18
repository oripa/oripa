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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopperFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.projectprop.Property;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.geom.RectangleDomain;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PropertyDialogView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
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
	SubFrameFactory subFrameFactory;

	@Mock
	MainFramePresentationLogic presentationLogic;

	@Mock
	MainComponentPresenterFactory componentPresenterFactory;

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
	StatePopperFactory<EditMode> statePopperFactory;

	@Mock
	FileHistory fileHistory;

	@Mock
	IniFileAccess iniFileAccess;

	@Mock
	FileAccessService<Doc> dataFileAccess;

	@Mock
	List<GraphicMouseActionPlugin> plugins;

	@Mock
	Supplier<CreasePatternFOLDConfig> foldConfigFactory;

	@Nested
	class Constructor {

		@Nested
		class TestLoadIniFile {

			@Test
			void iniFileShouldBeLoaded() {

				setupDomainContext(mock());

				setupBindingFactory();

				construct();

				verify(presentationLogic).loadIniFile();

			}

		}

		@Nested
		class TestModifySavingActions {
			@Captor
			ArgumentCaptor<Supplier<Object>> foldConfigCaptor;

			@Test
			void saveConfigurationOfFOLDShouldBeDone() {

				PaintContext paintContext = mock();
				when(paintContext.getPointEps()).thenReturn(1e-8);
				setupDomainContext(paintContext);

				setupBindingFactory();

				CreasePatternFOLDConfig config = mock();
				setupFOLDConfigFactory(config);

				construct();

				verify(dataFileAccess).setConfigToSavingAction(eq(DocFileTypes.fold()), foldConfigCaptor.capture());

				var createdConfig = foldConfigCaptor.getValue().get();

				assertEquals(config, createdConfig);

				verify(config).setEps(anyDouble());
			}
		}

		@Nested
		class TestAddPlugins {
			@Test
			void givenPluginsShouldBeAdded() {

				setupDomainContext();

				setupBindingFactory();

				construct();

				verify(presentationLogic).addPlugins(plugins);
			}
		}

		@Nested
		class TestBuildFileMenu {
			@Test
			void buildFileMenuShouldBeCalled() {

				setupDomainContext();

				setupBindingFactory();

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

				setupDomainContext();

				setupBindingFactory();

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
					setupDomainContext();

					setupBindingFactory();

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
					setupDomainContext();

					setupBindingFactory();

					construct();

					verify(view).addClearButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).clear();

				}
			}

			@Nested
			class TestUndo {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void undoLogicShouldBeCalled() {
					setupDomainContext();

					MouseActionHolder actionHolder = mock();
					GraphicMouseAction action = mock();
					when(actionHolder.getMouseAction()).thenReturn(Optional.of(action));
					setupPresentationContext(actionHolder);

					setupBindingFactory();

					construct();

					verify(view).addUndoButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(action).undo(any());
					verify(presentationLogic).updateScreen();

				}
			}

			@Nested
			class TestRedo {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void redoLogicShouldBeCalled() {
					setupDomainContext();

					MouseActionHolder actionHolder = mock();
					GraphicMouseAction action = mock();
					when(actionHolder.getMouseAction()).thenReturn(Optional.of(action));
					setupPresentationContext(actionHolder);

					setupBindingFactory();

					construct();

					verify(view).addRedoButtonListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(action).redo(any());
					verify(presentationLogic).updateScreen();

				}
			}

			@Nested
			class TestShowPropertyDIalog {
				@Captor
				ArgumentCaptor<Runnable> listenerCaptor;

				@Test
				void propertyDialogShouldBeShown() {
					setupDomainContext();

					setupBindingFactory();

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
					PaintContext paintContext = mock();
					when(paintContext.countSelectedLines()).thenReturn(1);
					setupDomainContext(paintContext);

					setupBindingFactory();

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
					PaintContext paintContext = mock();
					when(paintContext.countSelectedLines()).thenReturn(0);
					setupDomainContext(paintContext);

					setupBindingFactory();

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
					PaintContext paintContext = mock();
					when(paintContext.countSelectedLines()).thenReturn(1);
					setupDomainContext(paintContext);

					setupBindingFactory();

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
					PaintContext paintContext = mock();
					when(paintContext.countSelectedLines()).thenReturn(0);
					setupDomainContext(paintContext);

					setupBindingFactory();

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
					setupDomainContext();

					setupBindingFactory();

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
					setupDomainContext();

					setupBindingFactory();

					Property property = mock();
					when(project.getProperty()).thenReturn(property);

					construct();

					verify(view).setEstimationResultSaveColorsListener(listenerCaptor.capture());

					Color front = mock();
					Color back = mock();
					listenerCaptor.getValue().accept(front, back);

					verify(property).putFrontColorCode(anyString());
					verify(property).putBackColorCode(anyString());
				}
			}

			@Nested
			class TestSetPaperDomainOfModelChange {
				@Captor
				ArgumentCaptor<Consumer<RectangleDomain>> listenerCaptor;

				@Test
				void setPaperDomainOfModelLogicShouldBeCalled() {
					setupDomainContext();

					setupBindingFactory();

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
					PaintContext paintContext = mock();
					when(paintContext.creasePatternChangeExists()).thenReturn(false);
					setupDomainContext(paintContext);

					setupBindingFactory();

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveIniFile();
				}

				@Test
				void saveIniWhenSaveCPIsCanceled() {
					PaintContext paintContext = mock();
					when(paintContext.creasePatternChangeExists()).thenReturn(true);
					setupDomainContext(paintContext);

					setupBindingFactory();

					when(view.showSaveOnCloseDialog()).thenReturn(false);

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveIniFile();
				}

				@SuppressWarnings("unchecked")
				@Test
				void saveCPAndSaveIniWhenChangeOnCPAndFileIsSelected() {
					PaintContext paintContext = mock();
					when(paintContext.creasePatternChangeExists()).thenReturn(true);
					setupDomainContext(paintContext);

					setupBindingFactory();

					when(view.showSaveOnCloseDialog()).thenReturn(true);

					construct();

					verify(view).addWindowClosingListener(listenerCaptor.capture());

					listenerCaptor.getValue().run();

					verify(presentationLogic).saveFileUsingGUIImpl();
					verify(presentationLogic).saveIniFile();

				}

			}

		}

		MainFramePresenter construct() {
			return new MainFramePresenter(
					view,
					dialogFactory,
					subFrameFactory,
					presentationLogic,
					componentPresenterFactory,
					presentationContext,
					bindingFactory,
					statePopperFactory,
					project,
					domainContext,
					paintContextModification,
					fileHistory,
					dataFileAccess,
					plugins,
					foldConfigFactory);
		}

		void setupProject() {
			when(project.getDataFileName()).thenReturn(Optional.empty());
		}

		void setupView() {
			when(view.getPainterScreenView()).thenReturn(mock());
			when(view.getUIPanelView()).thenReturn(mock());
		}

		void setupPresentationContext(
				final MouseActionHolder actionHolder) {
			when(presentationContext.getActionHolder()).thenReturn(actionHolder);
		}

		void setupPresentationContext() {
			setupPresentationContext(mock());
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

	@Test
	void test() {
		assertNotNull("Not yet implemented");
	}

}
