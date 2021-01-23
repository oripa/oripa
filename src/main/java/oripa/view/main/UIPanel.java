/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.view.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.OrigamiModelInteractiveBuilder;
import oripa.appstate.InputCommandStatePopper;
import oripa.appstate.StateManager;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.bind.binder.BinderInterface;
import oripa.bind.binder.ViewChangeBinder;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.bind.state.action.PaintActionSetterFactory;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.Folder;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.fold.OrigamiModelFactory;
import oripa.domain.paint.AngleStep;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.byvalue.AngleMeasuringAction;
import oripa.domain.paint.byvalue.AngleValueInputListener;
import oripa.domain.paint.byvalue.LengthMeasuringAction;
import oripa.domain.paint.byvalue.LengthValueInputListener;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.file.ImageResourceLoader;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.gui.ChildFrameManager;
import oripa.util.gui.GridBagConstraintsBuilder;
import oripa.value.OriLine;
import oripa.view.estimation.EstimationResultFrameFactory;
import oripa.view.foldability.FoldabilityCheckFrameFactory;
import oripa.view.model.ModelViewFrameFactory;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainFrameSetting;
import oripa.viewsetting.main.MainScreenSetting;
import oripa.viewsetting.main.uipanel.FromLineTypeItemListener;
import oripa.viewsetting.main.uipanel.ToLineTypeItemListener;
import oripa.viewsetting.main.uipanel.UIPanelSetting;

public class UIPanel extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(UIPanel.class);

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final UIPanelSetting setting = new UIPanelSetting();
	private final ValueSetting valueSetting = setting.getValueSetting();
	private ChildFrameManager childFrameManager;

	private final ViewScreenUpdater screenUpdater;
	private final PaintContextInterface paintContext;

	private boolean fullEstimation = true;

	// main three panels
	private final JPanel editModePanel = new JPanel();
	private final JPanel toolSettingsPanel = new JPanel();
	private final JPanel generalSettingsPanel = new JPanel();

	// ---------------------------------------------------------------------------------------------------------------------------
	// Binding edit mode
	private final ButtonGroup editModeGroup;

	private JRadioButton editModeInputLineButton;
	private JRadioButton editModePickLineButton;
	private JRadioButton editModeDeleteLineButton;
	private JRadioButton editModeLineTypeButton;
	private JRadioButton editModeAddVertex;
	private JRadioButton editModeDeleteVertex;

	// Insert Line Tools Panel
	private final JPanel lineInputPanel = new JPanel();

	private JRadioButton lineInputDirectVButton;
	private JRadioButton lineInputOnVButton;
	private JRadioButton lineInputVerticalLineButton;
	private JRadioButton lineInputAngleBisectorButton;
	private JRadioButton lineInputTriangleSplitButton;
	private JRadioButton lineInputSymmetricButton;
	private JRadioButton lineInputMirrorButton;
	private JRadioButton lineInputByValueButton;
	private JRadioButton lineInputPBisectorButton;
	private JRadioButton lineInputAngleSnapButton;

	// lineTypePanel
	private final JPanel lineTypePanel = new JPanel();

	private final JRadioButton lineTypeAuxButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.AUX_ID));
	private final JRadioButton lineTypeMountainButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MOUNTAIN_ID));
	private final JRadioButton lineTypeValleyButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.VALLEY_ID));

	// byValuePanel for length and angle
	private final JPanel byValuePanel = new JPanel();

	private JFormattedTextField textFieldLength;
	private final JButton buttonLength = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));
	private JFormattedTextField textFieldAngle;
	private final JButton buttonAngle = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));

	// AlterLineTypePanel
	private final JPanel alterLineTypePanel = new JPanel();

	private final TypeForChange[] alterLine_comboData_from = {
			TypeForChange.EMPTY, TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.AUX,
			TypeForChange.CUT };
	private final TypeForChange[] alterLine_comboData_to = {
			TypeForChange.MOUNTAIN, TypeForChange.VALLEY, TypeForChange.AUX,
			TypeForChange.CUT, TypeForChange.DELETE, TypeForChange.FLIP };

	private final JComboBox<TypeForChange> alterLine_combo_from = new JComboBox<>(
			alterLine_comboData_from);
	private final JComboBox<TypeForChange> alterLine_combo_to = new JComboBox<>(
			alterLine_comboData_to);

	// Angle Step Panel
	private final JPanel angleStepComboPanel = new JPanel();

	private final JComboBox<AngleStep> angleStepCombo = new JComboBox<>(
			AngleStep.values());

	// gridPanel
	private final JPanel gridPanel = new JPanel();

	private final JCheckBox dispGridCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_GRID_ID),
			true);
	private JFormattedTextField textFieldGrid;
	private final JButton gridSmallButton = new JButton("x2");
	private final JButton gridLargeButton = new JButton("x1/2");
	private final JButton gridChangeButton = new JButton(
			resources.getString(ResourceKey.LABEL,
					StringID.UI.GRID_SIZE_CHANGE_ID));

	// view Panel
	private final JPanel viewPanel = new JPanel();

	private final JCheckBox dispMVLinesCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_MV_ID),
			true);
	private final JCheckBox dispAuxLinesCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_AUX_ID),
			true);
	private final JCheckBox dispVertexCheckBox = new JCheckBox(
			resources
					.getString(ResourceKey.LABEL, StringID.UI.SHOW_VERTICES_ID),
			false);
	private final JCheckBox doFullEstimationCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL,
					StringID.UI.FULL_ESTIMATION_ID),
			false);
	private final JCheckBox zeroLineWidthCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.ZERO_LINE_WIDTH_ID));

	// ActionButtons Panel
	private final JPanel buttonsPanel = new JPanel();

	private final JButton buildButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.FOLD_ID));
	private final JButton buttonCheckWindow = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.CHECK_WINDOW_ID));

	public UIPanel(
			final StateManager stateManager,
			final ViewScreenUpdater screenUpdater,
			final MouseActionHolder actionHolder,
			final PaintContextInterface aContext,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final MainFrameSetting mainFrameSetting,
			final MainScreenSetting mainScreenSetting) {

		this.screenUpdater = screenUpdater;
		this.paintContext = aContext;

		constructButtons(stateManager, actionHolder, mainFrameSetting, mainScreenSetting);

		// setPreferredSize(new Dimension(230, 800));

		// edit mode Selection panel
		editModeGroup = new ButtonGroup();
		editModeGroup.add(editModeInputLineButton);
		editModeGroup.add(editModePickLineButton);
		editModeGroup.add(editModeDeleteLineButton);
		editModeGroup.add(editModeLineTypeButton);
		editModeGroup.add(editModeAddVertex);
		editModeGroup.add(editModeDeleteVertex);

		editModePanel.setBorder(createTitledBorderFrame(
				resources.getString(ResourceKey.LABEL, StringID.UI.TOOL_PANEL_ID)));
		editModePanel.setLayout(new GridBagLayout());

		var gbBuilder = new GridBagConstraintsBuilder(1)
				.setAnchor(GridBagConstraints.LINE_START)
				.setInsets(0, 5, 0, 0);

		editModePanel.add(editModeInputLineButton, gbBuilder.getLineField());
		editModePanel.add(editModePickLineButton, gbBuilder.getLineField());
		editModePanel.add(editModeDeleteLineButton, gbBuilder.getLineField());
		editModePanel.add(editModeLineTypeButton, gbBuilder.getLineField());
		editModePanel.add(editModeAddVertex, gbBuilder.getLineField());
		editModePanel.add(editModeDeleteVertex, gbBuilder.getLineField());

		// Tool settings panel
		createLineInputPanel();
		createAlterLineTypePanel();
		createSetAngleStepPanel();
		createEditByValuePanel();

		toolSettingsPanel.setLayout(new GridBagLayout());
		toolSettingsPanel.setBorder(createTitledBorderFrame(
				resources.getString(ResourceKey.LABEL, StringID.UI.TOOL_SETTINGS_PANEL_ID)));

		gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.PAGE_START)
				.setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1);

		toolSettingsPanel.add(lineInputPanel, gbBuilder.getLineField());
		toolSettingsPanel.add(alterLineTypePanel, gbBuilder.getLineField());
		toolSettingsPanel.add(byValuePanel, gbBuilder.getLineField());
		toolSettingsPanel.add(angleStepComboPanel, gbBuilder.getLineField());

		// general settings panel
		createGridPanel();
		createViewPanel();
		createButtonsPanel();

		generalSettingsPanel.setLayout(new GridBagLayout());
		generalSettingsPanel.setBorder(createTitledBorderFrame(
				resources.getString(ResourceKey.LABEL, StringID.UI.GENERAL_SETTINGS_ID)));

		gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.PAGE_START)
				.setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0.5);

		generalSettingsPanel.add(gridPanel, gbBuilder.getLineField());
		generalSettingsPanel.add(viewPanel, gbBuilder.getLineField());
		generalSettingsPanel.add(buttonsPanel, gbBuilder.getLineField());

		// the main UIPanel
		setLayout(new GridBagLayout());

		gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.FIRST_LINE_START)
				.setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0.0);
		System.out.printf(String.format("weightX %,.2f : weightY %,.2f \n\n", gbBuilder.weightX,
				gbBuilder.weightY));
		add(editModePanel, gbBuilder.getLineField());

		gbBuilder.setWeight(1, 1).setFill(GridBagConstraints.BOTH);
		add(toolSettingsPanel, gbBuilder.getLineField());

		gbBuilder.setWeight(1, 0.0).setFill(GridBagConstraints.HORIZONTAL)
				.setAnchor(GridBagConstraints.LAST_LINE_START);
		add(generalSettingsPanel, gbBuilder.getLineField());

		// add listeners
		addPropertyChangeListenersToSetting(mainScreenSetting);
		addActionListenersToComponents(stateManager, actionHolder, cutOutlinesHolder,
				mainScreenSetting);

		// -------------------------------------------------
		// Initialize selection
		// -------------------------------------------------
		editModeInputLineButton.setSelected(true);
		angleStepCombo.setSelectedItem(AngleStep.PI_OVER_8);

		// of paint command
		lineInputDirectVButton.doClick();

		// of line type on setting
		setting.setTypeFrom((TypeForChange) alterLine_combo_from
				.getSelectedItem());
		setting.setTypeTo((TypeForChange) alterLine_combo_to
				.getSelectedItem());

		doFullEstimationCheckBox.setSelected(true);
		lineTypeMountainButton.doClick();
	}

	/**
	 * panel containing line input methods and line type selection
	 */
	private void createLineInputPanel() {
		// extra panel just for line types
		ButtonGroup lineTypeGroup = new ButtonGroup();
		lineTypeGroup.add(lineTypeMountainButton);
		lineTypeGroup.add(lineTypeValleyButton);
		lineTypeGroup.add(lineTypeAuxButton);

		lineTypePanel.setLayout(new BoxLayout(lineTypePanel, BoxLayout.LINE_AXIS));
		lineTypePanel.add(lineTypeMountainButton);
		lineTypePanel.add(lineTypeValleyButton);
		lineTypePanel.add(lineTypeAuxButton);

		// How to enter the line
		ButtonGroup lineInputGroup = new ButtonGroup();
		lineInputGroup.add(lineInputDirectVButton);
		lineInputGroup.add(lineInputOnVButton);
		lineInputGroup.add(lineInputTriangleSplitButton);
		lineInputGroup.add(lineInputAngleBisectorButton);
		lineInputGroup.add(lineInputVerticalLineButton);
		lineInputGroup.add(lineInputSymmetricButton);
		lineInputGroup.add(lineInputMirrorButton);
		lineInputGroup.add(lineInputByValueButton);
		lineInputGroup.add(lineInputPBisectorButton);
		lineInputGroup.add(lineInputAngleSnapButton);

		// put layout together
		lineInputPanel.setLayout(new GridBagLayout());
		lineInputPanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.LINE_INPUT_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(4) // 4 columns used
				.setAnchor(GridBagConstraints.CENTER) // anchor items in the
														// center of their Box
				.setWeight(0.5, 1.0); // distribute evenly accross both axis,
										// 1.0 is needed to force max size
										// (maybe?)

		var lineTypeLabel = new JLabel("Line Type");
		lineTypeLabel.setHorizontalAlignment(JLabel.CENTER);
		lineInputPanel.add(lineTypeLabel, gbBuilder.getLineField());

		lineInputPanel.add(lineTypePanel, gbBuilder.getLineField());

		var commandsLabel = new JLabel("Command (Alt + 1...9)");
		commandsLabel.setHorizontalAlignment(JLabel.CENTER);
		lineInputPanel.add(commandsLabel, gbBuilder.getLineField());

		// needs to move into seperate Panel later
		lineInputPanel.add(angleStepCombo, gbBuilder.getLineField());

		gbBuilder.setWeight(0.5, 0.5);
		// put operation buttons in order
		lineInputPanel.add(lineInputDirectVButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputOnVButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputPBisectorButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputAngleBisectorButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputTriangleSplitButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputVerticalLineButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputSymmetricButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputMirrorButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputByValueButton, gbBuilder.getNextField());
		lineInputPanel.add(lineInputAngleSnapButton, gbBuilder.getNextField());

		setButtonIcons();
	}

	/**
	 * display combobox for angle step line drawing tool
	 */
	private void createSetAngleStepPanel() {
		angleStepComboPanel.setLayout(new GridBagLayout());
		angleStepComboPanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.ANGLE_STEP_ID)));

		angleStepComboPanel.add(angleStepCombo, new GridBagConstraintsBuilder(1)
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.getLineField());

		angleStepComboPanel.setVisible(false);
	}

	/**
	 * change line type tool settings panel
	 */
	private void createAlterLineTypePanel() {
		var fromLabel = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.CHANGE_LINE_TYPE_FROM_ID));

		var toLabel = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.CHANGE_LINE_TYPE_TO_ID));

		alterLineTypePanel.setLayout(new GridBagLayout());
		alterLineTypePanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.ALTER_LINE_TYPE_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(2);

		alterLineTypePanel.add(fromLabel, gbBuilder.getNextField());
		alterLineTypePanel.add(alterLine_combo_from, gbBuilder.getNextField());
		alterLineTypePanel.add(toLabel, gbBuilder.getNextField());
		alterLineTypePanel.add(alterLine_combo_to, gbBuilder.getNextField());

		alterLineTypePanel.setVisible(false);
	}

	/**
	 * input line by value tool
	 */
	private void createEditByValuePanel() {
		var lengthLabel = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.LENGTH_ID));

		var angleLabel = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.ANGLE_ID));

		NumberFormat doubleValueFormat = NumberFormat
				.getNumberInstance(Locale.US);
		doubleValueFormat.setMinimumFractionDigits(3);

		textFieldLength = new JFormattedTextField(doubleValueFormat);
		textFieldAngle = new JFormattedTextField(doubleValueFormat);

		textFieldLength.setColumns(5);
		textFieldLength.setValue(java.lang.Double.valueOf(0.0));
		textFieldLength.setHorizontalAlignment(JTextField.RIGHT);

		textFieldAngle.setColumns(5);
		textFieldAngle.setValue(java.lang.Double.valueOf(0.0));
		textFieldAngle.setHorizontalAlignment(JTextField.RIGHT);

		byValuePanel.setLayout(new GridBagLayout());
		byValuePanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.INSERT_BY_VALUE_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(3)
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.NONE)
				.setWeight(0, 1);

		byValuePanel.add(lengthLabel, gbBuilder.getNextField());
		byValuePanel.add(textFieldLength, gbBuilder.getNextField());
		byValuePanel.add(buttonLength, gbBuilder.getNextField());

		byValuePanel.add(angleLabel, gbBuilder.getNextField());
		byValuePanel.add(textFieldAngle, gbBuilder.getNextField());
		byValuePanel.add(buttonAngle, gbBuilder.getNextField());

		byValuePanel.setVisible(false);
	}

	/**
	 * grid size settings panel
	 */
	private void createGridPanel() {
		var gridDivideLabel = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.GRID_DIVIDE_NUM_ID));

		textFieldGrid = new JFormattedTextField(new DecimalFormat("#"));
		textFieldGrid.setColumns(2);
		textFieldGrid.setValue(Integer.valueOf(paintContext.getGridDivNum()));
		textFieldGrid.setHorizontalAlignment(JTextField.RIGHT);

		gridPanel.setLayout(new GridBagLayout());
		gridPanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.GRID_SETTINGS_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(3);

		gridPanel.add(dispGridCheckBox, gbBuilder.getLineField());

		gridPanel.add(gridDivideLabel, gbBuilder.getNextField());
		gridPanel.add(textFieldGrid, gbBuilder.setWeight(1, 0.5).getNextField());
		gridPanel.add(gridChangeButton, gbBuilder.setWeight(0.5, 0.5).getNextField());

		gridPanel.add(gridSmallButton, gbBuilder.getNextField());
		gbBuilder.getNextField(); // empty field
		gridPanel.add(gridLargeButton, gbBuilder.getNextField());

	}

	/**
	 * view/display settings panel
	 */
	private void createViewPanel() {
		viewPanel.setLayout(new GridBagLayout());

		viewPanel.setBorder(createTitledBorder(
				resources.getString(ResourceKey.LABEL, StringID.UI.VIEW_SETTINGS_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(3);

		viewPanel.add(zeroLineWidthCheckBox, gbBuilder.getLineField());

		viewPanel.add(dispMVLinesCheckBox, gbBuilder.getLineField());
		viewPanel.add(dispAuxLinesCheckBox, gbBuilder.getLineField());
		viewPanel.add(dispVertexCheckBox, gbBuilder.getLineField());
	}

	private void createButtonsPanel() {
		buttonsPanel.setLayout(new GridBagLayout());

		buttonsPanel.setBorder(new MatteBorder(1, 0, 0, 0,
				getBackground().darker().darker()));

		var gbBuilder = new GridBagConstraintsBuilder(3);

		buttonsPanel.add(doFullEstimationCheckBox, gbBuilder.getLineField());

		buttonsPanel.add(buttonCheckWindow, gbBuilder.getLineField());
		buttonsPanel.add(buildButton, gbBuilder.getLineField());
	}

	public void setChildFrameManager(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	private TitledBorder createTitledBorder(final String text) {
		TitledBorder border = new TitledBorder(text);
		border.setBorder(new LineBorder(getBackground().darker().darker(), 2));
		border.setBorder(new MatteBorder(1, 0, 0, 0,
				getBackground().darker().darker()));
		return border;
	}

	private TitledBorder createTitledBorderFrame(final String text) {
		TitledBorder border = new TitledBorder(text);
		border.setBorder(new LineBorder(getBackground().darker().darker()));
		return border;
	}

	private void constructButtons(final StateManager stateManager,
			final MouseActionHolder actionHolder,
			final MainFrameSetting mainFrameSetting,
			final MainScreenSetting mainScreenSetting) {

		BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();

		var stateFactory = new PaintBoundStateFactory(stateManager, mainFrameSetting, setting,
				mainScreenSetting.getSelectionOriginHolder());

		ButtonFactory buttonFactory = new PaintActionButtonFactory(
				stateFactory, paintContext, actionHolder, screenUpdater);

		editModeInputLineButton = (JRadioButton) viewChangeBinder
				.createButton(
						JRadioButton.class, null,
						StringID.UI.INPUT_LINE_ID,
						screenUpdater.getKeyListener());
		editModeInputLineButton.setMnemonic(KeyEvent.VK_I);

		editModePickLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.SELECT_ID,
				screenUpdater.getKeyListener());
		editModePickLineButton.setMnemonic(KeyEvent.VK_S);

		editModeDeleteLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.DELETE_LINE_ID,
				screenUpdater.getKeyListener());
		editModeDeleteLineButton.setMnemonic(KeyEvent.VK_D);

		editModeLineTypeButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.CHANGE_LINE_TYPE_ID,
				screenUpdater.getKeyListener());
		editModeLineTypeButton.setMnemonic(KeyEvent.VK_T);

		editModeAddVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.ADD_VERTEX_ID,
				screenUpdater.getKeyListener());

		editModeDeleteVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.DELETE_VERTEX_ID,
				screenUpdater.getKeyListener());
		editModeDeleteVertex.setMnemonic(KeyEvent.VK_L);

		// ---------------------------------------------------------------------------------------------------------------------------
		// Binding how to enter the line
		lineInputDirectVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.DIRECT_V_ID,
				screenUpdater.getKeyListener());
		lineInputDirectVButton.setMnemonic(KeyEvent.VK_1);

		lineInputOnVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.ON_V_ID,
				screenUpdater.getKeyListener());
		lineInputOnVButton.setMnemonic(KeyEvent.VK_2);

		lineInputVerticalLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.VERTICAL_ID,
				screenUpdater.getKeyListener());
		lineInputVerticalLineButton.setMnemonic(KeyEvent.VK_3);

		lineInputAngleBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.BISECTOR_ID,
				screenUpdater.getKeyListener());
		lineInputAngleBisectorButton.setMnemonic(KeyEvent.VK_4);

		lineInputTriangleSplitButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.TRIANGLE_ID,
				screenUpdater.getKeyListener());
		lineInputTriangleSplitButton.setMnemonic(KeyEvent.VK_5);

		lineInputSymmetricButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.SYMMETRIC_ID,
				screenUpdater.getKeyListener());
		lineInputSymmetricButton.setMnemonic(KeyEvent.VK_6);

		lineInputMirrorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.MIRROR_ID,
				screenUpdater.getKeyListener());
		lineInputMirrorButton.setMnemonic(KeyEvent.VK_7);

		lineInputByValueButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.BY_VALUE_ID,
				screenUpdater.getKeyListener());
		lineInputByValueButton.setMnemonic(KeyEvent.VK_8);

		lineInputPBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.PERPENDICULAR_BISECTOR_ID,
				screenUpdater.getKeyListener());
		lineInputPBisectorButton.setMnemonic(KeyEvent.VK_9);

		lineInputAngleSnapButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, StringID.ANGLE_SNAP_ID,
				screenUpdater.getKeyListener());
		lineInputAngleSnapButton.setMnemonic(KeyEvent.VK_0);
	}

	private void setButtonIcons() {
		setButtonIcon(lineInputDirectVButton, "icon/segment.gif", "icon/segment_p.gif");
		setButtonIcon(lineInputOnVButton, "icon/line.gif", "icon/line_p.gif");
		setButtonIcon(lineInputPBisectorButton, "icon/pbisector.gif", "icon/pbisector_p.gif");
		setButtonIcon(lineInputAngleBisectorButton, "icon/bisector.gif", "icon/bisector_p.gif");
		setButtonIcon(lineInputTriangleSplitButton, "icon/incenter.gif", "icon/incenter_p.gif");
		setButtonIcon(lineInputVerticalLineButton, "icon/vertical.gif", "icon/vertical_p.gif");
		setButtonIcon(lineInputSymmetricButton, "icon/symmetry.gif", "icon/symmetry_p.gif");
		setButtonIcon(lineInputMirrorButton, "icon/mirror.gif", "icon/mirror_p.gif");
		setButtonIcon(lineInputByValueButton, "icon/by_value.gif", "icon/by_value_p.gif");
		setButtonIcon(lineInputAngleSnapButton, "icon/angle.gif", "icon/angle_p.gif");
	}

	private void setButtonIcon(final AbstractButton button, final String iconPath,
			final String selectedIconPath) {
		ImageResourceLoader imgLoader = new ImageResourceLoader();
		button.setIcon(imgLoader.loadAsIcon(iconPath));
		button.setSelectedIcon(imgLoader.loadAsIcon(selectedIconPath));
	}

	private void addActionListenersToComponents(final StateManager stateManager,
			final MouseActionHolder actionHolder,
			final CutModelOutlinesHolder cutOutlinesHolder,
			final MainScreenSetting mainScreenSetting) {
		PaintActionSetterFactory setterFactory = new PaintActionSetterFactory(
				actionHolder, screenUpdater, paintContext);

		// edit mode line input radio button
		editModeInputLineButton
				.addActionListener(new InputCommandStatePopper(stateManager));

		// change line type tool
		alterLine_combo_from.addItemListener(new FromLineTypeItemListener(setting));
		alterLine_combo_to.addItemListener(new ToLineTypeItemListener(setting));

		// draw line by value tool
		buttonLength.addActionListener(
				setterFactory.create(new LengthMeasuringAction(valueSetting)));
		buttonAngle.addActionListener(
				setterFactory.create(new AngleMeasuringAction(valueSetting)));

		textFieldLength.getDocument().addDocumentListener(
				new LengthValueInputListener(valueSetting));
		textFieldAngle.getDocument().addDocumentListener(
				new AngleValueInputListener(valueSetting));

		// angle step tool
		angleStepCombo.addItemListener(e -> paintContext.setAngleStep((AngleStep) e.getItem()));

		// line type radio buttons
		lineTypeMountainButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.MOUNTAIN));
		lineTypeMountainButton.setMnemonic(KeyEvent.VK_M);

		lineTypeValleyButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.VALLEY));
		lineTypeValleyButton.setMnemonic(KeyEvent.VK_V);

		lineTypeAuxButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.AUX));
		lineTypeAuxButton.setMnemonic(KeyEvent.VK_A);

		// grid settings
		dispGridCheckBox.addActionListener(e -> {
			mainScreenSetting.setGridVisible(dispGridCheckBox.isSelected());
			screenUpdater.updateScreen();
		});

		gridSmallButton.addActionListener(e -> makeGridSizeHalf());

		gridLargeButton.addActionListener(e -> makeGridSizeTwiceLarge());

		gridChangeButton.addActionListener(e -> setGridDivNum());

		textFieldGrid.addActionListener(e -> setGridDivNum());

		// display/view settings
		dispVertexCheckBox.addActionListener(e -> {
			logger.debug("vertexVisible at listener: " + dispVertexCheckBox.isSelected());
			mainScreenSetting.setVertexVisible(dispVertexCheckBox.isSelected());
		});

		dispMVLinesCheckBox.addActionListener(e -> {
			logger.debug("mvLineVisible at listener: " + dispMVLinesCheckBox.isSelected());
			mainScreenSetting.setMVLineVisible(dispMVLinesCheckBox.isSelected());
		});

		dispAuxLinesCheckBox.addActionListener(e -> {
			logger.debug("auxLineVisible at listener: " + dispAuxLinesCheckBox.isSelected());
			mainScreenSetting.setAuxLineVisible(dispAuxLinesCheckBox.isSelected());
		});

		zeroLineWidthCheckBox.addActionListener(e -> {
			mainScreenSetting.setZeroLineWidth(zeroLineWidthCheckBox.isSelected());
			screenUpdater.updateScreen();
		});

		// buttons panel
		doFullEstimationCheckBox.addActionListener(e -> {
			fullEstimation = doFullEstimationCheckBox.isSelected();
		});

		buttonCheckWindow.addActionListener(e -> showCheckerWindow(paintContext));

		buildButton.addActionListener(
				e -> showFoldedModelWindows(cutOutlinesHolder, mainScreenSetting));
	}

	/**
	 * display window with foldability checks
	 *
	 * @param context
	 *            the cp data to be used
	 */
	private void showCheckerWindow(final PaintContextInterface context) {
		OrigamiModel origamiModel;
		CreasePatternInterface creasePattern = context.getCreasePattern();

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());

		FoldabilityCheckFrameFactory checkerFactory = new FoldabilityCheckFrameFactory(
				childFrameManager);
		JFrame checker = checkerFactory.createFrame(
				UIPanel.this, origamiModel, creasePattern, context.isZeroLineWidth());
		checker.repaint();
		checker.setVisible(true);
	}

	private void makeGridSizeHalf() {
		if (paintContext.getGridDivNum() < 65) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() * 2);
			textFieldGrid.setValue(Integer.valueOf(paintContext.getGridDivNum()));

			screenUpdater.updateScreen();
		}
	}

	private void makeGridSizeTwiceLarge() {
		if (paintContext.getGridDivNum() > 3) {
			paintContext.setGridDivNum(paintContext.getGridDivNum() / 2);
			textFieldGrid.setValue(Integer.valueOf(paintContext.getGridDivNum()));

			screenUpdater.updateScreen();
		}
	}

	private void setGridDivNum() {
		int value;
		try {
			value = Integer.valueOf(textFieldGrid.getText());
			logger.debug("grid division num: " + value);

			if (value < 128 && value > 2) {
				paintContext.setGridDivNum(value);
				screenUpdater.updateScreen();
			}
		} catch (Exception ex) {
			logger.error("failed to get grid division num.", ex);
		}
	}

	/**
	 * open window with folded model
	 *
	 * @param cutOutlinesHolder
	 * @param mainScreenSetting
	 */
	private void showFoldedModelWindows(
			final CutModelOutlinesHolder cutOutlinesHolder,
			final MainScreenSetting mainScreenSetting) {
		CreasePatternInterface creasePattern = paintContext.getCreasePattern();
		FoldedModelInfo foldedModelInfo = new FoldedModelInfo();

		Folder folder = new Folder();

		OrigamiModel origamiModel = buildOrigamiModel(creasePattern);

		if (origamiModel.isProbablyFoldable()) {
			final int foldableModelCount = folder.fold(
					origamiModel, foldedModelInfo, fullEstimation);

			if (foldableModelCount == -1) {

			} else if (foldableModelCount == 0) {
				JOptionPane.showMessageDialog(
						this,
						resources.getString(ResourceKey.INFO, StringID.Information.NO_ANSWER_ID),
						resources.getString(ResourceKey.INFO,
								StringID.Information.FOLD_ALGORITHM_TITLE_ID),
						JOptionPane.INFORMATION_MESSAGE);
			} else if (foldableModelCount > 0) {
				logger.info("foldable layer layout is found.");

				EstimationResultFrameFactory resultFrameFactory = new EstimationResultFrameFactory(
						childFrameManager);
				JFrame frame = resultFrameFactory.createFrame(this,
						origamiModel, foldedModelInfo);
				frame.repaint();
				frame.setVisible(true);
			}
		} else {
			folder.foldWithoutLineType(origamiModel);
		}

		ModelViewFrameFactory modelViewFactory = new ModelViewFrameFactory(
				mainScreenSetting,
				childFrameManager);
		JFrame modelView = modelViewFactory.createFrame(this, origamiModel,
				cutOutlinesHolder, () -> screenUpdater.updateScreen());

		modelView.repaint();
		modelView.setVisible(true);
	}

	/**
	 * try building the creasepattern and ask for additional measures to help
	 * clean it
	 *
	 * @param creasePattern
	 * @return folded Origami model
	 */
	private OrigamiModel buildOrigamiModel(final CreasePatternInterface creasePattern) {
		var builder = new OrigamiModelInteractiveBuilder();

		return builder.build(creasePattern,
				// ask if ORIPA should try to remove duplication.
				() -> JOptionPane.showConfirmDialog(
						this,
						resources.getString(
								ResourceKey.WARNING,
								StringID.Warning.FOLD_FAILED_DUPLICATION_ID),
						resources.getString(
								ResourceKey.WARNING,
								StringID.Warning.FAILED_TITLE_ID),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION,
				// clean up the crease pattern
				() -> JOptionPane.showMessageDialog(
						this,
						resources.getString(ResourceKey.INFO,
								StringID.Information.SIMPLIFYING_CP_ID),
						resources.getString(ResourceKey.INFO,
								StringID.Information.SIMPLIFYING_CP_TITLE_ID),
						JOptionPane.INFORMATION_MESSAGE),
				// folding failed.
				() -> JOptionPane.showMessageDialog(
						this,
						resources.getString(
								ResourceKey.WARNING,
								StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID),
						resources.getString(
								ResourceKey.WARNING,
								StringID.Warning.FAILED_TITLE_ID),
						JOptionPane.WARNING_MESSAGE));
	}

	private void addPropertyChangeListenersToSetting(final MainScreenSetting mainScreenSetting) {
		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.ZERO_LINE_WIDTH, e -> {
					zeroLineWidthCheckBox.setSelected((boolean) e.getNewValue());
				});

		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.GRID_VISIBLE, e -> {
					dispGridCheckBox.setSelected((boolean) e.getNewValue());
					repaint();
				});

		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.VERTEX_VISIBLE, e -> {
					logger.debug("vertexVisible property change: " + e.getNewValue());
					dispVertexCheckBox.setSelected((boolean) e.getNewValue());
				});

		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.MV_LINE_VISIBLE, e -> {
					logger.debug("mvLineVisible property change: " + e.getNewValue());
					dispMVLinesCheckBox.setSelected((boolean) e.getNewValue());
				});

		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.AUX_LINE_VISIBLE, e -> {
					logger.debug("auxLineVisible property change: " + e.getNewValue());
					dispAuxLinesCheckBox.setSelected((boolean) e.getNewValue());
				});

		valueSetting.addPropertyChangeListener(
				ValueSetting.ANGLE, e -> textFieldAngle.setValue(e.getNewValue()));

		valueSetting.addPropertyChangeListener(
				ValueSetting.LENGTH, e -> textFieldLength.setValue(e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.SELECTED_MODE, this::onChangeEditModeButtonSelection);

		setting.addPropertyChangeListener(
				UIPanelSetting.BY_VALUE_PANEL_VISIBLE,
				e -> byValuePanel.setVisible((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.ALTER_LINE_TYPE_PANEL_VISIBLE,
				e -> alterLineTypePanel.setVisible((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(UIPanelSetting.LINE_INPUT_PANEL_VISIBLE,
				e -> lineInputPanel.setVisible((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.MOUNTAIN_BUTTON_ENABLED,
				e -> lineTypeMountainButton.setEnabled((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.VALLEY_BUTTON_ENABLED,
				e -> lineTypeValleyButton.setEnabled((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.AUX_BUTTON_ENABLED,
				e -> lineTypeAuxButton.setEnabled((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.ANGLE_STEP_PANEL_VISIBLE,
				e -> angleStepComboPanel.setVisible((boolean) e.getNewValue()));
	}

	private void onChangeEditModeButtonSelection(final PropertyChangeEvent e) {
		switch (setting.getSelectedMode()) {
		case INPUT:
			selectEditModeButton(editModeInputLineButton);
			break;
		case SELECT:
			selectEditModeButton(editModePickLineButton);
			break;
		default:
			break;
		}
	}

	private void selectEditModeButton(final AbstractButton modeButton) {
		editModeGroup.setSelected(modeButton.getModel(), true);
	}

	public UIPanelSetting getUIPanelSetting() {
		return setting;
	}
}
