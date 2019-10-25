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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

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

import oripa.Config;
import oripa.ORIPA;
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
import oripa.domain.paint.ScreenUpdaterInterface;
import oripa.domain.paint.byvalue.AngleMeasuringAction;
import oripa.domain.paint.byvalue.AngleValueInputListener;
import oripa.domain.paint.byvalue.LengthMeasuringAction;
import oripa.domain.paint.byvalue.LengthValueInputListener;
import oripa.domain.paint.byvalue.ValueDB;
import oripa.domain.paint.core.PaintConfig;
import oripa.domain.paint.util.LineTypeSetter;
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
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;
import oripa.viewsetting.main.uipanel.ChangeOnByValueButtonSelected;
import oripa.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.main.uipanel.FromLineTypeItemListener;
import oripa.viewsetting.main.uipanel.ToLineTypeItemListener;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class UIPanel extends JPanel
		implements ActionListener, Observer {

	private final UIPanelSettingDB settingDB = UIPanelSettingDB.getInstance();
	ResourceHolder resources = ResourceHolder.getInstance();

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

	private final JRadioButton lineTypeSubButton = new JRadioButton(
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
	private final JButton resetButton = new JButton("Reset");

	private final JCheckBox dispGridCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_GRID_ID),
			true);

	private final JButton gridSmallButton = new JButton("x2");
	private final JButton gridLargeButton = new JButton("x1/2");
	private final JButton gridChangeButton = new JButton(
			resources.getString(ResourceKey.LABEL,
					StringID.UI.GRID_SIZE_CHANGE_ID));

	private final JPanel mainPanel = new JPanel();
	private final JPanel subPanel1 = new JPanel();
	private final JPanel subPanel2 = new JPanel();
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
	private final ScreenUpdater screenUpdater;

	private final PaintContextInterface paintContext;

	private final MouseActionHolder actionHolder;

	private final EstimationEntityHolder estimationHolder;
	private final SheetCutOutlinesHolder cutOutlinesHolder;

	public UIPanel(final ScreenUpdater screenUpdater,
			final MouseActionHolder actionHolder,
			final PaintContextInterface aContext,
			final EstimationEntityHolder anEstimationHolder,
			final SheetCutOutlinesHolder aCutOutlinesHolder) {

		this.screenUpdater = screenUpdater;

		this.actionHolder = actionHolder;

		paintContext = aContext;
		estimationHolder = anEstimationHolder;
		cutOutlinesHolder = aCutOutlinesHolder;

		constructButtons(paintContext);

		// setModeButtonText();
		editModeInputLineButton.setSelected(true);

		setPreferredSize(new Dimension(210, 400));

		settingDB.addObserver(this);

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
		lineTypeGroup.add(lineTypeSubButton);

		lineTypePanel.setLayout(new GridBagLayout());
		lineTypePanel.add(lineTypeMountainButton);
		lineTypePanel.add(lineTypeValleyButton);
		lineTypePanel.add(lineTypeSubButton);

		lineTypeMountainButton.setSelected(true);

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
		subPanel1.setVisible(false);
		subPanel2.setVisible(false);

		NumberFormat doubleValueFormat = NumberFormat
				.getNumberInstance(Locale.US);
		doubleValueFormat.setMinimumFractionDigits(3);

		textFieldLength = new JFormattedTextField(doubleValueFormat);
		textFieldAngle = new JFormattedTextField(doubleValueFormat);

		textFieldLength.setColumns(4);
		textFieldAngle.setColumns(4);
		textFieldLength.setValue(new java.lang.Double(0.0));
		textFieldAngle.setValue(new java.lang.Double(0.0));

		textFieldLength.setHorizontalAlignment(JTextField.RIGHT);
		textFieldAngle.setHorizontalAlignment(JTextField.RIGHT);

		subPanel1.setLayout(new FlowLayout());
		subPanel2.setLayout(new FlowLayout());
		subPanel1.add(subLabel1);
		subPanel1.add(textFieldLength);
		subPanel1.add(buttonLength);
		subPanel2.add(subLabel2);
		subPanel2.add(textFieldAngle);
		subPanel2.add(buttonAngle);

		add(subPanel1);
		add(subPanel2);

		// ------------------------------------
		// For the grid panel
		// ------------------------------------
		JPanel divideNumSpecPanel = new JPanel();
		JLabel gridLabel1 = new JLabel(
				resources.getString(ResourceKey.LABEL,
						StringID.UI.GRID_DIVIDE_NUM_ID));

		textFieldGrid = new JFormattedTextField(new DecimalFormat("#"));
		textFieldGrid.setColumns(2);
		textFieldGrid.setValue(new Integer(Config.DEFAULT_GRID_DIV_NUM));
		textFieldGrid.setHorizontalAlignment(JTextField.RIGHT);
		gridChangeButton.addActionListener(this);

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
		lineTypeSubButton.setMnemonic('A');
		lineTypeMountainButton.setMnemonic('M');
		lineTypeValleyButton.setMnemonic('V');

		ValueDB.getInstance().addObserver(this);

		addPropertyChangeListenersToSetting();
		addListenerToComponents(paintContext);

		// -------------------------------------------------
		// Initialize selection
		// -------------------------------------------------

		// of paint command
		lineInputDirectVButton.doClick();

		// of line type on DB
		settingDB.setTypeFrom((TypeForChange) alterLine_combo_from
				.getSelectedItem());
		settingDB.setTypeTo((TypeForChange) alterLine_combo_to
				.getSelectedItem());

	}

	private void constructButtons(final PaintContextInterface context) {
		BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();
		ButtonFactory buttonFactory = new PaintActionButtonFactory(context);

		editModeInputLineButton = (JRadioButton) viewChangeBinder
				.createButton(
						JRadioButton.class, new ChangeOnPaintInputButtonSelected(),
						StringID.UI.INPUT_LINE_ID,
						screenUpdater.getKeyListener());

		editModePickLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.SELECT_ID,
				screenUpdater.getKeyListener());

		editModeDeleteLineButton = (JRadioButton) buttonFactory
				.create(
						this, JRadioButton.class, actionHolder, StringID.DELETE_LINE_ID,
						screenUpdater.getKeyListener());

		editModeLineTypeButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.CHANGE_LINE_TYPE_ID,
				screenUpdater.getKeyListener());

		editModeAddVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.ADD_VERTEX_ID,
				screenUpdater.getKeyListener());

		editModeDeleteVertex = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.DELETE_VERTEX_ID,
				screenUpdater.getKeyListener());

		// ---------------------------------------------------------------------------------------------------------------------------
		// Binding how to enter the line

		lineInputDirectVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.DIRECT_V_ID,
				screenUpdater.getKeyListener());

		lineInputOnVButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.ON_V_ID,
				screenUpdater.getKeyListener());

		lineInputVerticalLineButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.VERTICAL_ID,
				screenUpdater.getKeyListener());

		lineInputAngleBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.BISECTOR_ID,
				screenUpdater.getKeyListener());

		lineInputTriangleSplitButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.TRIANGLE_ID,
				screenUpdater.getKeyListener());

		lineInputSymmetricButton = (JRadioButton) buttonFactory
				.create(
						this, JRadioButton.class, actionHolder, StringID.SYMMETRIC_ID,
						screenUpdater.getKeyListener());

		lineInputMirrorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.MIRROR_ID,
				screenUpdater.getKeyListener());

		lineInputByValueButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.BY_VALUE_ID,
				screenUpdater.getKeyListener());

		lineInputPBisectorButton = (JRadioButton) buttonFactory.create(
				this, JRadioButton.class, actionHolder, StringID.PERPENDICULAR_BISECTOR_ID,
				screenUpdater.getKeyListener());

	}

	private void addPaintActionButtons(final int gridWidth, final int gridy_start) {

		paintActionButtonCount = 0;
		// put operation buttons in order
		addPaintActionButton(lineInputDirectVButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputOnVButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputPBisectorButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputAngleBisectorButton, gridWidth,
				gridy_start);
		addPaintActionButton(lineInputTriangleSplitButton, gridWidth,
				gridy_start);
		addPaintActionButton(lineInputVerticalLineButton, gridWidth,
				gridy_start);
		addPaintActionButton(lineInputSymmetricButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputMirrorButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputByValueButton, gridWidth, gridy_start);
	}

	private int paintActionButtonCount = 0;

	private void addPaintActionButton(final AbstractButton button, final int gridWidth,
			final int gridy) {

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = paintActionButtonCount % gridWidth + 1;
		gridBagConstraints.gridy = gridy + paintActionButtonCount / gridWidth;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		mainPanel.add(button, gridBagConstraints);

		paintActionButtonCount++;

	}

	private void addListenerToComponents(final PaintContextInterface context) {

		alterLine_combo_from.addItemListener(new FromLineTypeItemListener());
		alterLine_combo_to.addItemListener(new ToLineTypeItemListener());

		buttonLength
				.addActionListener(
						new PaintActionSetter(actionHolder, new LengthMeasuringAction(), context));
		buttonLength.addActionListener(
				new ViewChangeListener(new ChangeOnByValueButtonSelected()));

		buttonAngle
				.addActionListener(
						new PaintActionSetter(actionHolder, new AngleMeasuringAction(), context));
		buttonAngle.addActionListener(
				new ViewChangeListener(new ChangeOnByValueButtonSelected()));

		lineTypeMountainButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_RIDGE));

		lineTypeValleyButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_VALLEY));

		lineTypeSubButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_NONE));

		editModeInputLineButton
				.addActionListener(new InputCommandStatePopper());

		textFieldLength.getDocument().addDocumentListener(
				new LengthValueInputListener());
		textFieldAngle.getDocument().addDocumentListener(
				new AngleValueInputListener());

		dispGridCheckBox.addActionListener(this);
		gridSmallButton.addActionListener(this);
		gridLargeButton.addActionListener(this);
		buildButton.addActionListener(this);
		resetButton.addActionListener(this);
		dispVertexCheckBox.addActionListener(this);
		dispVertexCheckBox.setSelected(true);
		PaintConfig.dispVertex = true;
		dispMVLinesCheckBox
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
						PaintConfig.dispMVLines = dispMVLinesCheckBox
								.isSelected();
						screenUpdater.updateScreen();
					}
				});
		dispAuxLinesCheckBox
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
						PaintConfig.dispAuxLines = dispAuxLinesCheckBox
								.isSelected();
						screenUpdater.updateScreen();
					}
				});

		doFullEstimationCheckBox.setSelected(true);
		PaintConfig.bDoFullEstimation = true;
		doFullEstimationCheckBox
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
						PaintConfig.bDoFullEstimation = doFullEstimationCheckBox
								.isSelected();
						screenUpdater.updateScreen();
					}
				});

		buttonCheckWindow
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
						OrigamiModel origamiModel;
						CreasePatternInterface creasePattern = context
								.getCreasePattern();

						OrigamiModelFactory modelFactory = new OrigamiModelFactory();
						origamiModel = modelFactory.createOrigamiModel(
								creasePattern, creasePattern.getPaperSize());

						// document.setOrigamiModel(origamiModel);
						// boolean isValidPattern =
						// folderTool.checkPatternValidity(
						// origamiModel.getEdges(), origamiModel.getVertices(),
						// origamiModel.getFaces() );

						FoldabilityCheckFrameFactory checkerFactory = new FoldabilityCheckFrameFactory();
						JFrame checker = checkerFactory.createFrame(
								UIPanel.this, origamiModel, creasePattern);
						checker.setVisible(true);
					}
				});

	}

	private final MainScreenSettingDB screenDB = MainScreenSettingDB
			.getInstance();

	@Override
	public void actionPerformed(final ActionEvent ae) {

		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();

		// TODO decompose this long long if-else.
		if (ae.getSource() == dispGridCheckBox) {
			screenDB.setGridVisible(dispGridCheckBox.isSelected());

			screenUpdater.updateScreen();

		} else if (ae.getSource() == gridSmallButton) {
			if (PaintConfig.gridDivNum < 65) {
				PaintConfig.gridDivNum *= 2;
				textFieldGrid.setValue(new Integer(PaintConfig.gridDivNum));

				screenUpdater.updateScreen();
			}
		} else if (ae.getSource() == gridLargeButton) {
			if (PaintConfig.gridDivNum > 3) {
				PaintConfig.gridDivNum /= 2;
				textFieldGrid.setValue(new Integer(PaintConfig.gridDivNum));

				screenUpdater.updateScreen();
			}
		} else if (ae.getSource() == dispVertexCheckBox) {
			PaintConfig.dispVertex = dispVertexCheckBox.isSelected();

			screenUpdater.updateScreen();
		} else if (ae.getSource() == resetButton) {
		} else if (ae.getSource() == buildButton) {
			boolean buildOK = false;
			CreasePatternInterface creasePattern = paintContext.getCreasePattern();

			// if (document.buildOrigami3(origamiModel, false)) {
			OrigamiModelFactory modelFactory = new OrigamiModelFactory();
			OrigamiModel origamiModel = modelFactory.createOrigamiModel(
					creasePattern, creasePattern.getPaperSize());

			FoldedModelInfo foldedModelInfo = estimationHolder.getFoldedModelInfo();

			if (origamiModel.isProbablyFoldable()) {
				buildOK = true;
			} else {
				if (JOptionPane.showConfirmDialog(
						ORIPA.mainFrame, resources.getString(
								ResourceKey.WARNING,
								StringID.Warning.FOLD_FAILED_DUPLICATION_ID),
						"Failed", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

					origamiModel = modelFactory
							.createOrigamiModelNoDuplicateLines(
									creasePattern, creasePattern.getPaperSize());
					if (origamiModel.isProbablyFoldable()) {
						buildOK = true;
					} else {
						JOptionPane
								.showMessageDialog(
										ORIPA.mainFrame,
										resources
												.getString(
														ResourceKey.WARNING,
														StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID),
										"Failed Level1",
										JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}

			Folder folder = new Folder();

			if (buildOK) {
				folder.fold(origamiModel, foldedModelInfo);
				estimationHolder.setOrigamiModel(origamiModel);

				// TODO move this block out of if(buildOK) statement.
				if (foldedModelInfo.getFoldablePatternCount() != 0) {
					System.out.println("RenderFrame");

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

			ModelViewFrameFactory modelViewFactory = new ModelViewFrameFactory();
			JFrame modelView = modelViewFactory.createFrame(this, origamiModel,
					cutOutlinesHolder, () -> screenUpdater.updateScreen());

			modelView.setVisible(true);
			modelView.repaint();
			// ModelFrameSettingDB modelSetting =
			// ModelFrameSettingDB.getInstance();
			// modelSetting.setFrameVisible(true);
			// modelSetting.notifyObservers();

			// screen.modeChanged();

		} else if (ae.getSource() == gridChangeButton) {
			int value;
			try {
				value = Integer.valueOf(textFieldGrid.getText());
				System.out.println("type");

				if (value < 128 && value > 2) {
					textFieldGrid.setValue(value);
					PaintConfig.gridDivNum = value;
					screenUpdater.updateScreen();
				}
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

	}

	private void addPropertyChangeListenersToSetting() {
		screenDB.addPropertyChangeListener(MainScreenSettingDB.GRID_VISIBLE,
				(e) -> {
					dispGridCheckBox.setSelected((boolean) e.getNewValue());
					repaint();
				});

		// if (e.getSource() == textFieldLength) {
		// textFieldLength.setValue(java.lang.Double.valueOf(textFieldLength.getText()));
		// } else if (e.getSource() == textFieldAngle) {
		// textFieldAngle.setValue(java.lang.Double.valueOf(textFieldAngle.getText()));
		// }
	}

	/**
	 * observes DB for reflecting the changes to views. toString() of given DB
	 * has to return a unique value among DB classes.
	 *
	 * @param o
	 *            Observable class which implements toString() to return its
	 *            class name.
	 * @param arg
	 *            A parameter to specify the action for the given Observable
	 *            object.
	 */
	@Override
	public void update(final Observable o, final Object arg) {

		// System.out.println(o.toString());

		if (o.toString().equals(ValueDB.getInstance().toString())) {
			// update text field of values
			ValueDB valueDB = (ValueDB) o;
			textFieldAngle.setValue(valueDB.getAngle());
			textFieldLength.setValue(valueDB.getLength());
		} else if (settingDB.hasGivenName(o.toString())) {
			// update GUI
			UIPanelSettingDB setting = (UIPanelSettingDB) o;

			updateEditModeButtonSelection(setting);

			subPanel1.setVisible(setting.isValuePanelVisible());
			subPanel2.setVisible(setting.isValuePanelVisible());

			alterLineTypePanel
					.setVisible(setting.isAlterLineTypePanelVisible());

			lineTypeMountainButton
					.setEnabled(setting.isMountainButtonEnabled());
			lineTypeValleyButton.setEnabled(setting.isValleyButtonEnabled());
			lineTypeSubButton.setEnabled(setting.isAuxButtonEnabled());

			repaint();
		}
	}

	private void updateEditModeButtonSelection(final UIPanelSettingDB setting) {
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

}
