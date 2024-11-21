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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.appstate.ApplicationState;
import oripa.appstate.StatePopperFactory;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.estimation.EstimationResultFramePresenter;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenter;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationResult;
import oripa.gui.presenter.main.ModelComputationFacade.ComputationType;
import oripa.gui.presenter.model.ModelViewFramePresenter;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.FrameView;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.main.KeyProcessing;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class UIPanelPresenterTest {

	@Mock
	UIPanelView view;

	@Mock
	SubFrameFactory subFrameFactory;
	@Mock
	SubFramePresenterFactory subFramePresenterFactory;

	@Mock
	ModelIndexChangeListenerPutter modelIndexChangeListenerPutter;

	@Mock
	ByValueContext byValueContext;

	@Mock
	PainterScreenSetting mainScreenSetting;

	@Mock
	StatePopperFactory<EditMode> statePopperFactory;
	@Mock
	ViewScreenUpdater screenUpdater;
	@Mock
	KeyProcessing keyProcessing;
	@Mock
	PaintContext paintContext;
	@Mock
	CreasePatternViewContext creasePatternViewContext;

	@Mock
	TypeForChangeContext typeForChangeContext;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	ModelComputationFacadeFactory modelComputationFacadeFactory;

	@Nested
	class TestAddPlugin {
		@Test
		void pluginShouldBeAdded() {

			setupTypeForChangeContext();

			GraphicMouseActionPlugin plugin = mock();
			when(plugin.getName()).thenReturn("name");
			var plugins = List.of(plugin);

			ApplicationState<EditMode> state = mock();

			when(bindingFactory.createState(plugin)).thenReturn(state);
			when(bindingFactory.createState(anyString())).thenReturn(mock());

			var presenter = construct();
			presenter.addPlugins(plugins);

			verify(view).addMouseActionPluginListener(anyString(), any(), eq(keyProcessing));
		}

	}

	@Nested
	class TestShowCheckerWindow {
		@Captor
		ArgumentCaptor<Runnable> listenerCaptor;

		@Test
		void windowShouldBeShown() {
			setupTypeForChangeContext();
			setupBindingFactory();
			setupFrameView();

			FoldabilityCheckFrameView foldabilityFrame = setupFoldabilityWindow();

			FoldabilityCheckFramePresenter foldabilityPresenter = mock();
			when(subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
					eq(foldabilityFrame),
					any(),
					anyBoolean(),
					anyDouble()))
							.thenReturn(foldabilityPresenter);

			construct();

			verify(view).addCheckWindowButtonListener(listenerCaptor.capture());

			listenerCaptor.getValue().run();

			verify(foldabilityPresenter).setViewVisible(true);

		}
	}

	FoldabilityCheckFrameView setupFoldabilityWindow() {
		FoldabilityCheckFrameView foldabilityFrame = mock();
		when(subFrameFactory.createFoldabilityFrame(any())).thenReturn(foldabilityFrame);

		when(paintContext.getCreasePattern()).thenReturn(mock());
		when(paintContext.getPointEps()).thenReturn(0.1);

		when(creasePatternViewContext.isZeroLineWidth()).thenReturn(false);

		return foldabilityFrame;
	}

	@Nested
	class TestShowFoldedModelWindows {
		@Captor
		ArgumentCaptor<Runnable> computeModelCaptor;
		@Captor
		ArgumentCaptor<Runnable> showWindowCaptor;

		@Test
		void windowsShouldBeShownWhenNoComputationError() {
			setupTypeForChangeContext();
			setupBindingFactory();
			setupFrameView();

			try (var computationTypeStatic = mockStatic(ComputationType.class)) {

				computationTypeStatic.when(() -> ComputationType.fromString(anyString()))
						.thenReturn(Optional.of(ComputationType.FULL));

				setupModelComputation(true, true);

				ModelViewFrameView modelFrame = mock();
				when(subFrameFactory.createModelViewFrame(any(), any())).thenReturn(modelFrame);

				ModelViewFramePresenter modelPresenter = mock();
				when(subFramePresenterFactory.createModelViewFramePresenter(eq(modelFrame), any(), any(), anyDouble()))
						.thenReturn(modelPresenter);

				EstimationResultFrameView estimationFrame = mock();
				when(subFrameFactory.createResultFrame(any())).thenReturn(estimationFrame);

				EstimationResultFramePresenter estimationPresenter = mock();
				when(subFramePresenterFactory.createEstimationResultFramePresenter(
						eq(estimationFrame),
						any(),
						anyDouble(),
						// actually string but the value is not instantiated.
						isNull(),
						any())).thenReturn(estimationPresenter);

				construct();

				verify(view).setModelComputationListener(computeModelCaptor.capture());
				verify(view).setShowFoldedModelWindowsListener(showWindowCaptor.capture());

				computeModelCaptor.getValue().run();
				showWindowCaptor.getValue().run();

				verify(modelPresenter, atLeastOnce()).setViewVisible(true);
				verify(estimationPresenter, atLeastOnce()).setViewVisible(true);
			}
		}

		@Test
		void modelWindowAndFoldabilityWindowWhenNotGloballyFlatFoldable() {
			setupTypeForChangeContext();
			setupBindingFactory();
			setupFrameView();

			try (var computationTypeStatic = mockStatic(ComputationType.class)) {

				computationTypeStatic.when(() -> ComputationType.fromString(anyString()))
						.thenReturn(Optional.of(ComputationType.FULL));

				var computationResult = setupModelComputation(true, false);
				when(computationResult.getEstimationResultRules()).thenReturn(mock());

				ModelViewFrameView modelFrame = mock();
				when(subFrameFactory.createModelViewFrame(any(), any())).thenReturn(modelFrame);

				ModelViewFramePresenter modelPresenter = mock();
				when(subFramePresenterFactory.createModelViewFramePresenter(eq(modelFrame), any(), any(), anyDouble()))
						.thenReturn(modelPresenter);

				FoldabilityCheckFrameView foldabilityFrame = setupFoldabilityWindow();

				FoldabilityCheckFramePresenter foldabilityPresenter = mock();
				when(subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
						eq(foldabilityFrame),
						any(),
						any(),
						any(),
						anyBoolean(),
						anyDouble()))
								.thenReturn(foldabilityPresenter);

				construct();

				verify(view).setModelComputationListener(computeModelCaptor.capture());
				verify(view).setShowFoldedModelWindowsListener(showWindowCaptor.capture());

				computeModelCaptor.getValue().run();
				showWindowCaptor.getValue().run();

				verify(view).showNoAnswerMessage();

				verify(foldabilityPresenter).setViewVisible(true);
				verify(modelPresenter, atLeastOnce()).setViewVisible(true);
			}
		}

		@Test
		void foldabilityWindowWhenNotLocallyFlatFoldable() {
			setupTypeForChangeContext();
			setupBindingFactory();
			setupFrameView();

			try (var computationTypeStatic = mockStatic(ComputationType.class)) {

				computationTypeStatic.when(() -> ComputationType.fromString(anyString()))
						.thenReturn(Optional.of(ComputationType.FULL));

				setupModelComputation(false, false);

				FoldabilityCheckFrameView foldabilityFrame = setupFoldabilityWindow();

				FoldabilityCheckFramePresenter foldabilityPresenter = mock();
				when(subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
						eq(foldabilityFrame),
						any(),
						anyBoolean(),
						anyDouble()))
								.thenReturn(foldabilityPresenter);

				construct();

				verify(view).setModelComputationListener(computeModelCaptor.capture());
				verify(view).setShowFoldedModelWindowsListener(showWindowCaptor.capture());

				computeModelCaptor.getValue().run();
				showWindowCaptor.getValue().run();

				verify(view).showLocalFlatFoldabilityViolationMessage();

				verify(foldabilityPresenter).setViewVisible(true);
			}
		}

		ComputationResult setupModelComputation(final boolean locallyFlatFoldable,
				final boolean globallyFlatFoldable) {
			when(view.getComputationType()).thenReturn("type");

			when(paintContext.getPointEps()).thenReturn(0.1);
			when(paintContext.getCreasePattern()).thenReturn(mock());

			ComputationResult computationResult = mock();

			if (locallyFlatFoldable) {
				when(computationResult.allLocallyFlatFoldable()).thenReturn(locallyFlatFoldable);
				when(computationResult.origamiModels()).thenReturn(mock());
				when(view.getPaperDomainOfModelChangeListener()).thenReturn(mock());
				when(computationResult.foldedModels()).thenReturn(mock());
				when(computationResult.allGloballyFlatFoldable()).thenReturn(globallyFlatFoldable);
			}

			ModelComputationFacade computationFacade = mock();
			when(computationFacade.buildOrigamiModels(any())).thenReturn(mock());
			when(computationFacade.computeModels(any(), any())).thenReturn(computationResult);

			when(modelComputationFacadeFactory.createModelComputationFacade(eq(view), anyDouble()))
					.thenReturn(computationFacade);

			return computationResult;
		}

	}

	UIPanelPresenter construct() {
		return new UIPanelPresenter(
				view,
				subFrameFactory,
				subFramePresenterFactory,
				modelIndexChangeListenerPutter,
				modelComputationFacadeFactory,
				statePopperFactory,
				screenUpdater,
				keyProcessing,
				typeForChangeContext,
				creasePatternViewContext,
				paintContext,
				byValueContext,
				bindingFactory,
				mainScreenSetting);
	}

	void setupTypeForChangeContext() {
		when(typeForChangeContext.getTypeFrom()).thenReturn(mock());
		when(typeForChangeContext.getTypeTo()).thenReturn(mock());
	}

	void setupBindingFactory() {
		when(bindingFactory.createState(anyString())).thenReturn(mock());
	}

	void setupFrameView() {
		when(view.getTopLevelView()).thenReturn(mock(FrameView.class));
	}

	@Test
	void test() {
		assertNotNull("Not yet implemented");
	}

}
