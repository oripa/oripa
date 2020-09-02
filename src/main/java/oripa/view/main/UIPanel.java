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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.AbstractButton;
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
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.Config;
import oripa.appstate.InputCommandStatePopper;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.bind.binder.BinderInterface;
import oripa.bind.binder.ViewChangeBinder;
import oripa.bind.state.action.PaintActionSetter;
import oripa.domain.cptool.TypeForChange;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.BoundBox;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.Folder;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.fold.OrigamiModelFactory;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.byvalue.AngleMeasuringAction;
import oripa.domain.paint.byvalue.AngleValueInputListener;
import oripa.domain.paint.byvalue.LengthMeasuringAction;
import oripa.domain.paint.byvalue.LengthValueInputListener;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.file.ImageResourceLoader;
import oripa.persistent.doc.EstimationEntityHolder;
import oripa.persistent.doc.SheetCutOutlinesHolder;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.value.OriLine;
import oripa.view.estimation.EstimationResultFrameFactory;
import oripa.view.estimation.FoldabilityCheckFrameFactory;
import oripa.view.model.ModelViewFrameFactory;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.MainFrameSetting;
import oripa.viewsetting.main.MainScreenSetting;
import oripa.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.main.uipanel.FromLineTypeItemListener;
import oripa.viewsetting.main.uipanel.ToLineTypeItemListener;
import oripa.viewsetting.main.uipanel.UIPanelSetting;

public class UIPanel extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(UIPanel.class);

	private final UIPanelSetting setting = new UIPanelSetting();
	private final ValueSetting valueDB = setting.getValueSetting();
	private final MainScreenSetting mainScreenSetting;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private boolean fullEstimation = true;
	// ---------------------------------------------------------------------------------------------------------------------------
	// Binding edit mode

	private JRadioButton editModeInputLineButton;
	private JRadioButton editModePickLineButton;
	private JRadioButton editModeDeleteLineButton;
	private JRadioButton editModeLineTypeButton;
	private JRadioButton editModeAddVertex;
	private JRadioButton editModeDeleteVertex;
	// ---------------------------------------------------------------------------------------------------------------------------
	// Binding how to enter the line

	private JRadioButton lineInputDirectVButton;
	private JRadioButton lineInputOnVButton;
	private JRadioButton lineInputVerticalLineButton;
	private JRadioButton lineInputAngleBisectorButton;
	private JRadioButton lineInputTriangleSplitButton;
	private JRadioButton lineInputSymmetricButton;
	private JRadioButton lineInputMirrorButton;
	private JRadioButton lineInputByValueButton;
	private JRadioButton lineInputPBisectorButton;
	// ---------------------------------------------------------------------------------------------------------------------------

	private final JRadioButton lineTypeAuxButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.AUX_ID));
	private final JRadioButton lineTypeMountainButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MOUNTAIN_ID));
	private final JRadioButton lineTypeValleyButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.VALLEY_ID));

	// ---------------------------------------------------------------------------------------------------------------------------

	private final ButtonGroup editModeGroup;
	// Text box
	private final JFormattedTextField textFieldLength;
	private final JFormattedTextField textFieldAngle;
	private final JFormattedTextField textFieldGrid;

	private final JButton buttonLength = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));
	private final JButton buttonAngle = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));

	private final JButton buildButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.FOLD_ID));

	private final JCheckBox dispGridCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_GRID_ID),
			true);

	private final JButton gridSmallButton = new JButton("x2");
	private final JButton gridLargeButton = new JButton("x1/2");
	private final JButton gridChangeButton = new JButton(
			resources.getString(ResourceKey.LABEL,
					StringID.UI.GRID_SIZE_CHANGE_ID));

	private final JPanel mainPanel = new JPanel();
	private final JPanel byValueLengthPanel = new JPanel();
	private final JPanel byValueAnglePanel = new JPanel();
	private final JPanel gridPanel = new JPanel();
	private final JPanel lineTypePanel = new JPanel();
	// AlterLineType
	private final JPanel alterLineTypePanel = new JPanel();

	private final TypeForChange[] alterLine_comboData_from = {
			TypeForChange.EMPTY, TypeForChange.RIDGE, TypeForChange.VALLEY };
	private final TypeForChange[] alterLine_comboData_to = {
			TypeForChange.RIDGE, TypeForChange.VALLEY, TypeForChange.AUX,
			TypeForChange.CUT, TypeForChange.DELETE, TypeForChange.FLIP };

	private final JComboBox<TypeForChange> alterLine_combo_from = new JComboBox<>(
			alterLine_comboData_from);
	private final JComboBox<TypeForChange> alterLine_combo_to = new JComboBox<>(
			alterLine_comboData_to);

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
	private final JButton buttonCheckWindow = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.CHECK_WINDOW_ID));
	private final ViewScreenUpdater screenUpdater;

	private final PaintContextInterface paintContext;

	private final MouseActionHolder actionHolder;

	private final EstimationEntityHolder estimationHolder;
	private final SheetCutOutlinesHolder cutOutlinesHolder;

	public UIPanel(final ViewScreenUpdater screenUpdater,
			final MouseActionHolder actionHolder,
			final PaintContextInterface aContext,
			final EstimationEntityHolder anEstimationHolder,
			final SheetCutOutlinesHolder aCutOutlinesHolder,
			final MainFrameSetting mainFrameSetting,
			final MainScreenSetting mainScreenSetting) {

		this.screenUpdater = screenUpdater;

		this.actionHolder = actionHolder;

		paintContext = aContext;
		estimationHolder = anEstimationHolder;
		cutOutlinesHolder = aCutOutlinesHolder;

		this.mainScreenSetting = mainScreenSetting;

		constructButtons(mainFrameSetting);

		// setModeButtonText();
		editModeInputLineButton.setSelected(true);

		setPreferredSize(new Dimension(210, 400));

		// alterLine_combo_from.setSelectedIndex(0);
		// alterLine_combo_to.setSelectedIndex(0);
		// alterLine_combo_from.actionPerformed(null);
		// alterLine_combo_to.actionPerformed(null);

		// Edit mode
		editModeGroup = new ButtonGroup();
		editModeGroup.add(editModeInputLineButton);
		editModeGroup.add(editModePickLineButton);
		editModeGroup.add(editModeDeleteLineButton);
		editModeGroup.add(editModeLineTypeButton);
		editModeGroup.add(editModeAddVertex);
		editModeGroup.add(editModeDeleteVertex);

		JLabel l1 = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.CHANGE_LINE_TYPE_FROM_ID));

		JLabel l2 = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.CHANGE_LINE_TYPE_TO_ID));

		alterLineTypePanel.add(l1);
		alterLineTypePanel.add(alterLine_combo_from);
		alterLineTypePanel.add(l2);
		alterLineTypePanel.add(alterLine_combo_to);
		alterLineTypePanel.setVisible(false);

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

		ButtonGroup lineTypeGroup = new ButtonGroup();
		lineTypeGroup.add(lineTypeMountainButton);
		lineTypeGroup.add(lineTypeValleyButton);
		lineTypeGroup.add(lineTypeAuxButton);

		lineTypePanel.setLayout(new GridBagLayout());
		lineTypePanel.add(lineTypeMountainButton);
		lineTypePanel.add(lineTypeValleyButton);
		lineTypePanel.add(lineTypeAuxButton);

		mainPanel.setLayout(new GridBagLayout());

		int n = 0;
		GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
		gridBagConstraints0.gridx = 1;
		gridBagConstraints0.gridy = 0;
		gridBagConstraints0.gridwidth = 4;
		gridBagConstraints0.anchor = java.awt.GridBagConstraints.WEST;

		mainPanel.add(editModeInputLineButton, gridBagConstraints0);
		n++;

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.gridwidth = 4;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;

		mainPanel.add(lineTypePanel, gridBagConstraints1);
		n++;

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.gridwidth = 4;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(editModePickLineButton, gridBagConstraints2);
		n++;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.gridy = 3;
		gridBagConstraints3.gridwidth = 4;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(editModeDeleteLineButton, gridBagConstraints3);
		n++;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 1;
		gridBagConstraints4.gridy = 4;
		gridBagConstraints4.gridwidth = 4;
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(editModeLineTypeButton, gridBagConstraints4);
		n++;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 5;
		gridBagConstraints5.gridwidth = 4;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(alterLineTypePanel, gridBagConstraints5);
		n++;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 6;
		gridBagConstraints6.gridwidth = 4;
		gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(editModeAddVertex, gridBagConstraints6);
		n++;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 7;
		gridBagConstraints7.gridwidth = 4;
		gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(editModeDeleteVertex, gridBagConstraints7);
		n++;
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 8;
		gridBagConstraints8.gridwidth = 4;
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;

		JLabel label1 = new JLabel("Command (1...9)");
		label1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		mainPanel.add(label1, gridBagConstraints8);
		n++;

		addPaintActionButtons(4, 9);

		lineInputDirectVButton.setSelected(true);

		// lineInputDirectVButton.setIcon(new
		// ImageIcon(getClass().getResource("/icon/segment.gif")));

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		lineInputDirectVButton
				.setIcon(imgLoader.loadAsIcon("icon/segment.gif"));
		lineInputDirectVButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/segment_p.gif"));

		lineInputOnVButton.setIcon(imgLoader.loadAsIcon("icon/line.gif"));
		lineInputOnVButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/line_p.gif"));

		lineInputPBisectorButton.setIcon(imgLoader
				.loadAsIcon("icon/pbisector.gif"));
		lineInputPBisectorButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/pbisector_p.gif"));

		lineInputAngleBisectorButton.setIcon(imgLoader
				.loadAsIcon("icon/bisector.gif"));
		lineInputAngleBisectorButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/bisector_p.gif"));

		lineInputTriangleSplitButton.setIcon(imgLoader
				.loadAsIcon("icon/incenter.gif"));
		lineInputTriangleSplitButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/incenter_p.gif"));

		lineInputVerticalLineButton.setIcon(imgLoader
				.loadAsIcon("icon/vertical.gif"));
		lineInputVerticalLineButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/vertical_p.gif"));

		lineInputSymmetricButton.setIcon(imgLoader
				.loadAsIcon("icon/symmetry.gif"));
		lineInputSymmetricButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/symmetry_p.gif"));

		lineInputMirrorButton.setIcon(imgLoader.loadAsIcon("icon/mirror.gif"));
		lineInputMirrorButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/mirror_p.gif"));

		lineInputByValueButton.setIcon(imgLoader
				.loadAsIcon("icon/by_value.gif"));
		lineInputByValueButton.setSelectedIcon(imgLoader
				.loadAsIcon("icon/by_value_p.gif"));

		setLayout(new FlowLayout());
		add(mainPanel);

		// ------------------------------------
		// Panel input for length and angle
		// ------------------------------------
		JLabel subLabel1 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.LENGTH_ID));

		JLabel subLabel2 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.ANGLE_ID));

		// subPanel1.setVisible(true);
		// subPanel2.setVisible(true);
		byValueLengthPanel.setVisible(false);
		byValueAnglePanel.setVisible(false);

		NumberFormat doubleValueFormat = NumberFormat
				.getNumberInstance(Locale.US);
		doubleValueFormat.setMinimumFractionDigits(3);

		textFieldLength = new JFormattedTextField(doubleValueFormat);
		textFieldAngle = new JFormattedTextField(doubleValueFormat);

		textFieldLength.setColumns(4);
		textFieldAngle.setColumns(4);
		textFieldLength.setValue(java.lang.Double.valueOf(0.0));
		textFieldAngle.setValue(java.lang.Double.valueOf(0.0));

		textFieldLength.setHorizontalAlignment(JTextField.RIGHT);
		textFieldAngle.setHorizontalAlignment(JTextField.RIGHT);

		byValueLengthPanel.setLayout(new FlowLayout());
		byValueAnglePanel.setLayout(new FlowLayout());
		byValueLengthPanel.add(subLabel1);
		byValueLengthPanel.add(textFieldLength);
		byValueLengthPanel.add(buttonLength);
		byValueAnglePanel.add(subLabel2);
		byValueAnglePanel.add(textFieldAngle);
		byValueAnglePanel.add(buttonAngle);

		add(byValueLengthPanel);
		add(byValueAnglePanel);

		// ------------------------------------
		// For the grid panel
		// ------------------------------------
		JPanel divideNumSpecPanel = new JPanel();
		JLabel gridLabel1 = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.GRID_DIVIDE_NUM_ID));

		textFieldGrid = new JFormattedTextField(new DecimalFormat("#"));
		textFieldGrid.setColumns(2);
		textFieldGrid.setValue(Integer.valueOf(Config.DEFAULT_GRID_DIV_NUM));
		textFieldGrid.setHorizontalAlignment(JTextField.RIGHT);

		divideNumSpecPanel.add(gridLabel1);
		divideNumSpecPanel.add(textFieldGrid);
		divideNumSpecPanel.add(gridChangeButton);

		JPanel gridButtonsPanel = new JPanel();
		gridButtonsPanel.add(gridSmallButton);
		n++;
		gridButtonsPanel.add(gridLargeButton);
		n++;

		n = 0;
		gridPanel.add(dispGridCheckBox);
		n++;
		gridPanel.add(divideNumSpecPanel);
		n++;
		gridPanel.add(gridButtonsPanel);
		n++;
		gridPanel.setLayout(new GridLayout(n, 1, 10, 2));
		gridPanel.setBorder(new EtchedBorder(BevelBorder.RAISED,
				getBackground().darker(), getBackground().brighter()));
		add(gridPanel);

		// ------------------------------------
		// Buttons panel
		// ------------------------------------
		JPanel buttonsPanel = new JPanel();
		n = 0;
		buttonsPanel.add(dispMVLinesCheckBox);
		n++;
		buttonsPanel.add(dispAuxLinesCheckBox);
		n++;
		buttonsPanel.add(dispVertexCheckBox);
		n++;
		buttonsPanel.add(buttonCheckWindow);
		n++;
		buttonsPanel.add(buildButton);
		n++;
		buttonsPanel.add(doFullEstimationCheckBox);
		n++;
		buttonsPanel.setLayout(new GridLayout(n, 1, 10, 2));

		add(buttonsPanel);

		// Shortcut
		// How to enter the line
		lineInputDirectVButton.setMnemonic('1');
		lineInputOnVButton.setMnemonic('2');
		lineInputPBisectorButton.setMnemonic('3');
		lineInputAngleBisectorButton.setMnemonic('4');
		lineInputTriangleSplitButton.setMnemonic('5');
		lineInputVerticalLineButton.setMnemonic('6');
		lineInputSymmetricButton.setMnemonic('7');
		lineInputMirrorButton.setMnemonic('8');
		lineInputByValueButton.setMnemonic('9');

		editModeInputLineButton.setMnemonic('I');
		editModePickLineButton.setMnemonic('S');
		editModeDeleteLineButton.setMnemonic('D');
		editModeLineTypeButton.setMnemonic('T');
		editModeDeleteVertex.setMnemonic('L');
		lineTypeAuxButton.setMnemonic('A');
		lineTypeMountainButton.setMnemonic('M');
		lineTypeValleyButton.setMnemonic('V');

		addPropertyChangeListenersToSetting();
		addActionListenersToComponents();

		// -------------------------------------------------
		// Initialize selection
		// -------------------------------------------------

		// of paint command
		lineInputDirectVButton.doClick();

		// of line type on DB
		setting.setTypeFrom((TypeForChange) alterLine_combo_from
				.getSelectedItem());
		setting.setTypeTo((TypeForChange) alterLine_combo_to
				.getSelectedItem());

		doFullEstimationCheckBox.setSelected(true);
		lineTypeMountainButton.doClick();

	}

	private void constructButtons(final MainFrameSetting mainFrameSetting) {
		BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();
		ButtonFactory buttonFactory = new PaintActionButtonFactory(paintContext, mainFrameSetting,
				setting);

		editModeInputLineButton = (JRadioButton) viewChangeBinder
				.createButton(
						JRadioButton.class, new ChangeOnPaintInputButtonSelected(setting),
						StringID.UI.INPUT_LINE_ID,
						screenUpdater.getKeyListener());

		editModePickLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.SELECT_ID,
				screenUpdater.getKeyListener());

		editModeDeleteLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater,
				StringID.DELETE_LINE_ID,
				screenUpdater.getKeyListener());

		editModeLineTypeButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.CHANGE_LINE_TYPE_ID,
				screenUpdater.getKeyListener());

		editModeAddVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.ADD_VERTEX_ID,
				screenUpdater.getKeyListener());

		editModeDeleteVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.DELETE_VERTEX_ID,
				screenUpdater.getKeyListener());

		// ---------------------------------------------------------------------------------------------------------------------------
		// Binding how to enter the line

		lineInputDirectVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.DIRECT_V_ID,
				screenUpdater.getKeyListener());

		lineInputOnVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.ON_V_ID,
				screenUpdater.getKeyListener());

		lineInputVerticalLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.VERTICAL_ID,
				screenUpdater.getKeyListener());

		lineInputAngleBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.BISECTOR_ID,
				screenUpdater.getKeyListener());

		lineInputTriangleSplitButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.TRIANGLE_ID,
				screenUpdater.getKeyListener());

		lineInputSymmetricButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater,
				StringID.SYMMETRIC_ID,
				screenUpdater.getKeyListener());

		lineInputMirrorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.MIRROR_ID,
				screenUpdater.getKeyListener());

		lineInputByValueButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater, StringID.BY_VALUE_ID,
				screenUpdater.getKeyListener());

		lineInputPBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, screenUpdater,
				StringID.PERPENDICULAR_BISECTOR_ID,
				screenUpdater.getKeyListener());

	}

	private void addPaintActionButtons(final int gridWidth, final int gridy_start) {

		int paintActionButtonCount = 0;
		// put operation buttons in order
		addPaintActionButton(lineInputDirectVButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputOnVButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputPBisectorButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputAngleBisectorButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputTriangleSplitButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputVerticalLineButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputSymmetricButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputMirrorButton, gridWidth, gridy_start,
				paintActionButtonCount++);
		addPaintActionButton(lineInputByValueButton, gridWidth, gridy_start,
				paintActionButtonCount++);
	}

	private void addPaintActionButton(final AbstractButton button, final int gridWidth,
			final int gridy, final int paintActionButtonCount) {

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = paintActionButtonCount % gridWidth + 1;
		gridBagConstraints.gridy = gridy + paintActionButtonCount / gridWidth;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		mainPanel.add(button, gridBagConstraints);
	}

	private void addActionListenersToComponents() {

		alterLine_combo_from.addItemListener(new FromLineTypeItemListener(setting));
		alterLine_combo_to.addItemListener(new ToLineTypeItemListener(setting));

		buttonLength.addActionListener(
				new PaintActionSetter(actionHolder, new LengthMeasuringAction(valueDB),
						screenUpdater, paintContext));

		buttonAngle.addActionListener(
				new PaintActionSetter(actionHolder, new AngleMeasuringAction(valueDB),
						screenUpdater, paintContext));

		lineTypeMountainButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.RIDGE));

		lineTypeValleyButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.VALLEY));

		lineTypeAuxButton.addActionListener(
				e -> paintContext.setLineTypeOfNewLines(OriLine.Type.NONE));

		editModeInputLineButton
				.addActionListener(new InputCommandStatePopper());

		textFieldLength.getDocument().addDocumentListener(
				new LengthValueInputListener(valueDB));
		textFieldAngle.getDocument().addDocumentListener(
				new AngleValueInputListener(valueDB));

		dispGridCheckBox.addActionListener(e -> {
			mainScreenSetting.setGridVisible(dispGridCheckBox.isSelected());
			screenUpdater.updateScreen();
		});

		gridSmallButton.addActionListener(e -> makeGridSizeHalf());

		gridLargeButton.addActionListener(e -> makeGridSizeTwiceLarge());

		gridChangeButton.addActionListener(e -> setGridDivNum());

		dispVertexCheckBox.addActionListener(e -> {
			paintContext.setVertexVisible(dispVertexCheckBox.isSelected());
			screenUpdater.updateScreen();
		});
		dispVertexCheckBox.setSelected(true);
		paintContext.setVertexVisible(true);

		dispMVLinesCheckBox
				.addActionListener(e -> {
					paintContext.setMVLineVisible(dispMVLinesCheckBox.isSelected());
					screenUpdater.updateScreen();
				});
		dispAuxLinesCheckBox
				.addActionListener(e -> {
					paintContext.setAuxLineVisible(dispAuxLinesCheckBox.isSelected());
					screenUpdater.updateScreen();
				});

		doFullEstimationCheckBox
				.addActionListener(e -> {
					fullEstimation = doFullEstimationCheckBox.isSelected();
				});

		buttonCheckWindow.addActionListener(e -> showCheckerWindow(paintContext));

		buildButton.addActionListener(e -> showFoldedModelWindows());

	}

	private void showCheckerWindow(final PaintContextInterface context) {
		OrigamiModel origamiModel;
		CreasePatternInterface creasePattern = context.getCreasePattern();

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());

		FoldabilityCheckFrameFactory checkerFactory = new FoldabilityCheckFrameFactory();
		JFrame checker = checkerFactory.createFrame(
				UIPanel.this, origamiModel, creasePattern);
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
			logger.debug("gird division num: " + value);

			if (value < 128 && value > 2) {
				textFieldGrid.setValue(value);
				paintContext.setGridDivNum(value);
				screenUpdater.updateScreen();
			}
		} catch (Exception ex) {
			logger.error("failed to get grid division num.", ex);
		}
	}

	private void showFoldedModelWindows() {
		CreasePatternInterface creasePattern = paintContext.getCreasePattern();
		FoldedModelInfo foldedModelInfo = estimationHolder.getFoldedModelInfo();

		Folder folder = new Folder();

		OrigamiModel origamiModel = buildOrigamiModel(creasePattern);

		if (origamiModel.isProbablyFoldable()) {
			final int foldableModelCount = folder.fold(
					origamiModel, foldedModelInfo, fullEstimation);
			estimationHolder.setOrigamiModel(origamiModel);

			if (foldableModelCount == -1) {

			} else if (foldableModelCount == 0) {
				JOptionPane.showMessageDialog(
						null, "No answer was found", "ORIPA",
						JOptionPane.DEFAULT_OPTION);
			}
//			else if (foldedModelInfo.getFoldablePatternCount() > 0) {
			else if (foldableModelCount > 0) {
				logger.info("foldable layer layout is found.");

				EstimationResultFrameFactory resultFrameFactory = new EstimationResultFrameFactory();
				JFrame frame = resultFrameFactory.createFrame(this,
						origamiModel, foldedModelInfo);
				frame.setVisible(true);
			}

		} else {
			BoundBox boundBox = folder.foldWithoutLineType(origamiModel);
			foldedModelInfo.setBoundBox(boundBox);
			estimationHolder.setOrigamiModel(origamiModel);
		}

		ModelViewFrameFactory modelViewFactory = new ModelViewFrameFactory(mainScreenSetting);
		JFrame modelView = modelViewFactory.createFrame(this, origamiModel,
				cutOutlinesHolder, () -> screenUpdater.updateScreen());

		modelView.setVisible(true);
		modelView.repaint();

	}

	private OrigamiModel buildOrigamiModel(final CreasePatternInterface creasePattern) {
		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		OrigamiModel origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());

		if (origamiModel.isProbablyFoldable()) {
			return origamiModel;
		}

		if (JOptionPane.showConfirmDialog(
				this, resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FOLD_FAILED_DUPLICATION_ID),
				"Failed", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
			return origamiModel;
		}

		origamiModel = modelFactory
				.createOrigamiModelNoDuplicateLines(
						creasePattern, creasePattern.getPaperSize());
		if (origamiModel.isProbablyFoldable()) {
			return origamiModel;
		}

		JOptionPane.showMessageDialog(
				this,
				resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID),
				"Failed Level1",
				JOptionPane.INFORMATION_MESSAGE);

		return origamiModel;
	}

	private void addPropertyChangeListenersToSetting() {
		mainScreenSetting.addPropertyChangeListener(
				MainScreenSetting.GRID_VISIBLE, e -> {
					dispGridCheckBox.setSelected((boolean) e.getNewValue());
					repaint();
				});

		valueDB.addPropertyChangeListener(
				ValueSetting.ANGLE, e -> textFieldAngle.setValue(e.getNewValue()));

		valueDB.addPropertyChangeListener(
				ValueSetting.LENGTH, e -> textFieldLength.setValue(e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.SELECTED_MODE, this::onChangeEditModeButtonSelection);

		setting.addPropertyChangeListener(
				UIPanelSetting.BY_VALUE_PANEL_VISIBLE, e -> {
					byValueLengthPanel.setVisible((boolean) e.getNewValue());
					byValueAnglePanel.setVisible((boolean) e.getNewValue());
				});

		setting.addPropertyChangeListener(
				UIPanelSetting.ALTER_LINE_TYPE_PANEL_VISIBLE,
				e -> alterLineTypePanel.setVisible((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.MOUNTAIN_BUTTON_ENABLED,
				e -> lineTypeMountainButton.setEnabled((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.VALLEY_BUTTON_ENABLED,
				e -> lineTypeValleyButton.setEnabled((boolean) e.getNewValue()));

		setting.addPropertyChangeListener(
				UIPanelSetting.AUX_BUTTON_ENABLED,
				e -> lineTypeAuxButton.setEnabled((boolean) e.getNewValue()));
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
