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
package oripa.gui.view.main;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oripa.gui.view.View;

/**
 * @author OUCHI Koji
 *
 */
public interface UIPanelView extends View {

	// UIPanelSetting getUIPanelSetting();

	void initializeButtonSelection(String angleStep, String typeFrom, String typeTo);

	void addItemOfAlterLineComboFrom(String item);

	void addItemOfAlterLineComboTo(String item);

	void addItemOfAngleStepCombo(String item);

	void addGridSmallButtonListener(Runnable listener);

	void addGridLargeButtonListener(Runnable listener);

	void addGridChangeButtonListener(Consumer<Integer> listener);

	void setGridDivNum(int gridDivNum);

	void setEstimationResultColors(Color front, Color back);

	void setBuildButtonEnabled(boolean enabled);

	void setByValueAngle(double angle);

	void setByValueLength(double length);

	boolean isFullEstimation();

	Color getEstimationResultFrontColor();

	Color getEstimationResultBackColor();

	void addEditModeInputLineButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addEditModeLineSelectionButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addEditModeDeleteLineButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addEditModeLineTypeButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addAlterLineComboFromSelectionListener(Consumer<String> listener);

	void addAlterLineComboToSelectionListener(Consumer<String> listener);

	void addEditModeAddVertexButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addEditModeDeleteVertexButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addSelectionButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addEnlargementButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputDirectVButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputOnVButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputVerticalLineButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputAngleBisectorButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputTriangleSplitButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputSymmetricButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputMirrorButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputByValueButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLengthButtonListener(Runnable listener);

	void addAngleButtonListener(Runnable listener);

	void addLengthTextFieldListener(Consumer<Double> listener);

	void addAngleTextFieldListener(Consumer<Double> listener);

	void addLineInputPBisectorButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addLineInputAngleSnapButtonListener(Runnable listener, KeyProcessing keyProcessing);

	void addAngleStepComboListener(Consumer<String> listener);

	void addLineTypeMountainButtonListener(Runnable listener);

	void addLineTypeValleyButtonListener(Runnable listener);

	void addLineTypeAuxButtonListener(Runnable listener);

	void addDispGridCheckBoxListener(Consumer<Boolean> listener);

	void addDispVertexCheckBoxListener(Consumer<Boolean> listener);

	void addDispMVLinesCheckBoxListener(Consumer<Boolean> listener);

	void addDispAuxLinesCheckBoxListener(Consumer<Boolean> listener);

	void addZeroLineWidthCheckBoxListener(Consumer<Boolean> listener);

	void addCheckWindowButtonListener(Runnable listener);

	BiConsumer<Color, Color> getEstimationResultSaveColorsListener();

	PropertyChangeListener getPaperDomainOfModelChangeListener();

	public void showNoAnswerMessage();

	public boolean showCleaningUpDuplicationDialog();

	public void showCleaningUpMessage();

	public void showFoldFailureMessage();

	public void setModelComputationListener(Runnable listener);

	public void setShowFoldedModelWindowsListener(Runnable listener);

	public void showErrorMessage(Exception e);
}
