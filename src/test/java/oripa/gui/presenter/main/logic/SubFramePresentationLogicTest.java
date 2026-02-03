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

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.estimation.EstimationResultFramePresenter;
import oripa.gui.presenter.foldability.FoldabilityCheckFramePresenter;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationResult;
import oripa.gui.presenter.main.logic.ModelComputationFacade.ComputationType;
import oripa.gui.presenter.model.ModelViewFramePresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
public class SubFramePresentationLogicTest {

    @InjectMocks
    SubFramePresentationLogic presentationLogic;

    @Mock
    UIPanelView view;

    @Mock
    ModelIndexChangeSupport modelIndexChangeSupport;

    @Mock
    SubFrameFactory subFrameFactory;
    @Mock
    SubFramePresenterFactory subFramePresenterFactory;

    @Mock
    ModelComputationFacadeFactory modelComputationFacadeFactory;

    @Mock
    PaintContext paintContext;

    @Nested
    class TestShowCheckerWindow {

        @Test
        void windowShouldBeShown() {

            setupFrameView();

            FoldabilityCheckFrameView foldabilityFrame = setupFoldabilityWindow();

            FoldabilityCheckFramePresenter foldabilityPresenter = mock();
            when(subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
                    eq(foldabilityFrame),
                    any(),
                    anyDouble()))
                            .thenReturn(foldabilityPresenter);

            presentationLogic.showCheckerWindow();

            verify(foldabilityPresenter).setViewVisible(true);

        }
    }

    FoldabilityCheckFrameView setupFoldabilityWindow() {
        FoldabilityCheckFrameView foldabilityFrame = mock();
        when(subFrameFactory.createFoldabilityFrame(any())).thenReturn(foldabilityFrame);

        when(paintContext.getCreasePattern()).thenReturn(mock());
        when(paintContext.getPointEps()).thenReturn(0.1);

        return foldabilityFrame;
    }

    @Nested
    class TestShowFoldedModelWindows {

        @Test
        void windowsShouldBeShownWhenNoComputationError() {

            setupFrameView();

            try (var computationTypeStatic = mockStatic(ComputationType.class)) {

                computationTypeStatic.when(() -> ComputationType.fromString(anyString()))
                        .thenReturn(Optional.of(ComputationType.FULL));

                setupModelComputation(true, true);

                ModelViewFrameView modelFrame = mock();
                when(subFrameFactory.createModelViewFrame(any())).thenReturn(modelFrame);

                ModelViewFramePresenter modelPresenter = mock();
                when(subFramePresenterFactory.createModelViewFramePresenter(eq(modelFrame), any(), anyDouble()))
                        .thenReturn(modelPresenter);

                EstimationResultFrameView estimationFrame = mock();
                when(subFrameFactory.createResultFrame(any())).thenReturn(estimationFrame);

                EstimationResultFramePresenter estimationPresenter = mock();
                when(subFramePresenterFactory.createEstimationResultFramePresenter(
                        eq(estimationFrame),
                        any(),
                        anyDouble(),
                        // actually string but the value is not instantiated in
                        // this test.
                        isNull(),
                        any())).thenReturn(estimationPresenter);

                presentationLogic.computeModels();
                presentationLogic.showFoldedModelWindows();

                verify(modelPresenter, atLeastOnce()).setViewVisible(true);
                verify(estimationPresenter, atLeastOnce()).setViewVisible(true);
            }
        }

        @Test
        void modelWindowAndFoldabilityWindowWhenNotGloballyFlatFoldable() {

            setupFrameView();

            try (var computationTypeStatic = mockStatic(ComputationType.class)) {

                computationTypeStatic.when(() -> ComputationType.fromString(anyString()))
                        .thenReturn(Optional.of(ComputationType.FULL));

                var computationResult = setupModelComputation(true, false);
                when(computationResult.getEstimationResultRules()).thenReturn(mock());

                ModelViewFrameView modelFrame = mock();
                when(subFrameFactory.createModelViewFrame(any())).thenReturn(modelFrame);

                ModelViewFramePresenter modelPresenter = mock();
                when(subFramePresenterFactory.createModelViewFramePresenter(eq(modelFrame), any(), anyDouble()))
                        .thenReturn(modelPresenter);

                FoldabilityCheckFrameView foldabilityFrame = setupFoldabilityWindow();

                FoldabilityCheckFramePresenter foldabilityPresenter = mock();
                when(subFramePresenterFactory.createFoldabilityCheckFrameViewPresenter(
                        eq(foldabilityFrame),
                        any(),
                        any(),
                        any(),
                        anyDouble()))
                                .thenReturn(foldabilityPresenter);

                presentationLogic.computeModels();
                presentationLogic.showFoldedModelWindows();

                verify(view).showNoAnswerMessage();

                verify(foldabilityPresenter).setViewVisible(true);
                verify(modelPresenter, atLeastOnce()).setViewVisible(true);
            }
        }

        @Test
        void foldabilityWindowWhenNotLocallyFlatFoldable() {

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
                        anyDouble()))
                                .thenReturn(foldabilityPresenter);

                presentationLogic.computeModels();
                presentationLogic.showFoldedModelWindows();

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

    void setupFrameView() {
        when(view.getTopLevelView()).thenReturn(mock(FrameView.class));
    }

}
