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

import java.util.List;

import jakarta.inject.Inject;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.paint.AngleStep;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.creasepattern.byvalue.AngleMeasuringAction;
import oripa.gui.presenter.creasepattern.byvalue.LengthMeasuringAction;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.KeyProcessing;
import oripa.gui.view.main.UIPanelView;
import oripa.resource.StringID;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class UIPanelPaintMenuListenerRegistration {
    private final UIPanelView view;
    private final BindingObjectFactoryFacade bindingFactory;

    private final KeyProcessing keyProcessing;

    private final PaintContext paintContext;
    private final TypeForChangeContext typeForChangeContext;
    private final ByValueContext byValueContext;

    @Inject
    public UIPanelPaintMenuListenerRegistration(
            final UIPanelView view,
            final BindingObjectFactoryFacade bindingFactory,
            final KeyProcessing keyProcessing,
            final PaintContext paintContext,
            final TypeForChangeContext typeForChangeContext,
            final ByValueContext byValueContext) {

        this.view = view;
        this.bindingFactory = bindingFactory;
        this.keyProcessing = keyProcessing;
        this.paintContext = paintContext;
        this.typeForChangeContext = typeForChangeContext;
        this.byValueContext = byValueContext;
    }

    public void register() {
        // ------------------------------------------------------------
        // edit mode buttons

        view.addEditModeInputLineButtonListener(
                bindingFactory.createStatePopperForCommand(EditMode.INPUT),
                keyProcessing);

        view.addEditModeLineSelectionButtonListener(
                bindingFactory.createStatePopperForCommand(EditMode.SELECT),
                keyProcessing);

        var deleteLineState = bindingFactory.createState(StringID.DELETE_LINE_ID);
        view.addEditModeDeleteLineButtonListener(deleteLineState::performActions, keyProcessing);

        var lineTypeState = bindingFactory.createState(StringID.CHANGE_LINE_TYPE_ID);
        view.addEditModeLineTypeButtonListener(lineTypeState::performActions, keyProcessing);

        view.addAlterLineComboFromSelectionListener(
                item -> typeForChangeContext.setTypeFrom(TypeForChange.fromString(item).get()));
        view.addAlterLineComboToSelectionListener(
                item -> typeForChangeContext.setTypeTo(TypeForChange.fromString(item).get()));

        var addVertexState = bindingFactory.createState(StringID.ADD_VERTEX_ID);
        view.addEditModeAddVertexButtonListener(addVertexState::performActions, keyProcessing);

        var deleteVertexState = bindingFactory.createState(StringID.DELETE_VERTEX_ID);
        view.addEditModeDeleteVertexButtonListener(deleteVertexState::performActions, keyProcessing);

        // ------------------------------------------------------------
        // selection command buttons

        var selectLineState = bindingFactory.createState(StringID.SELECT_LINE_ID);
        view.addSelectionButtonListener(selectLineState::performActions, keyProcessing);

        var enlargementState = bindingFactory.createState(StringID.ENLARGE_ID);
        view.addEnlargementButtonListener(enlargementState::performActions, keyProcessing);

        // ------------------------------------------------------------
        // input command buttons

        var directVState = bindingFactory.createState(StringID.DIRECT_V_ID);
        view.addLineInputDirectVButtonListener(directVState::performActions, keyProcessing);

        var onVState = bindingFactory.createState(StringID.ON_V_ID);
        view.addLineInputOnVButtonListener(onVState::performActions, keyProcessing);

        var verticalLineState = bindingFactory.createState(StringID.VERTICAL_ID);
        view.addLineInputVerticalLineButtonListener(verticalLineState::performActions,
                keyProcessing);

        var angleBisectorState = bindingFactory.createState(StringID.BISECTOR_ID);
        view.addLineInputAngleBisectorButtonListener(angleBisectorState::performActions,
                keyProcessing);

        var lineToLineState = bindingFactory.createState(StringID.LINE_TO_LINE_ID);
        view.addLineInputLineToLineAxiomButtonListener(lineToLineState::performActions, keyProcessing);

        var p2ltpState = bindingFactory.createState(StringID.POINT_TO_LINE_THROUGH_POINT_ID);
        view.addLineInputP2LTPAxiomButtonListener(p2ltpState::performActions, keyProcessing);

        var p2lp2lState = bindingFactory.createState(StringID.POINT_TO_LINE_POINT_TO_LINE_ID);
        view.addLineInputP2LP2LAxiomButtonListener(p2lp2lState::performActions, keyProcessing);

        var p2llState = bindingFactory.createState(StringID.POINT_TO_LINE_LINE_PERPENDICULAR_ID);
        view.addLineInputP2LLAxiomButtonListener(p2llState::performActions, keyProcessing);

        var triangleSplitState = bindingFactory.createState(StringID.TRIANGLE_ID);
        view.addLineInputTriangleSplitButtonListener(triangleSplitState::performActions,
                keyProcessing);

        var symmetricState = bindingFactory.createState(StringID.SYMMETRIC_ID);
        view.addLineInputSymmetricButtonListener(symmetricState::performActions, keyProcessing);

        var mirrorState = bindingFactory.createState(StringID.MIRROR_ID);
        view.addLineInputMirrorButtonListener(mirrorState::performActions, keyProcessing);

        var byValueState = bindingFactory.createState(StringID.BY_VALUE_ID);
        view.addLineInputByValueButtonListener(byValueState::performActions, keyProcessing);

        view.addLengthButtonListener(
                bindingFactory.createActionSetter(new LengthMeasuringAction(byValueContext)));
        view.addAngleButtonListener(
                bindingFactory.createActionSetter(new AngleMeasuringAction(byValueContext)));
        view.addLengthTextFieldListener(byValueContext::setLength);
        view.addAngleTextFieldListener(byValueContext::setAngle);

        var pbisecState = bindingFactory.createState(StringID.PERPENDICULAR_BISECTOR_ID);
        view.addLineInputPBisectorButtonListener(pbisecState::performActions, keyProcessing);

        var angleSnapState = bindingFactory.createState(StringID.ANGLE_SNAP_ID);
        view.addLineInputAngleSnapButtonListener(angleSnapState::performActions, keyProcessing);

        view.addAngleStepComboListener(step -> paintContext.setAngleStep(AngleStep.fromString(step).get()));

        var suggestionState = bindingFactory.createState(StringID.SUGGESTION_ID);
        view.addLineInputSuggestionButtonListener(suggestionState::performActions, keyProcessing);

        view.addLineTypeMountainButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.MOUNTAIN));
        view.addLineTypeValleyButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.VALLEY));
        view.addLineTypeUnassignedButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.UNASSIGNED));
        view.addLineTypeAuxButtonListener(() -> paintContext.setLineTypeOfNewLines(OriLine.Type.AUX));

        byValueContext.addPropertyChangeListener(ByValueContext.ANGLE,
                e -> view.setByValueAngle((double) e.getNewValue()));
        byValueContext.addPropertyChangeListener(ByValueContext.LENGTH,
                e -> view.setByValueLength((double) e.getNewValue()));

    }

    public void addPlugins(final List<GraphicMouseActionPlugin> plugins) {
        for (var plugin : plugins) {
            var state = bindingFactory.createState(plugin);

            view.addMouseActionPluginListener(plugin.getName(), state::performActions, keyProcessing);
        }
        view.updatePluginPanel();
    }

}
