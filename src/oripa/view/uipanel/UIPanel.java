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

package oripa.view.uipanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import oripa.doc.TypeForChange;
import oripa.file.ImageResourceLoader;
import oripa.folder.Folder;
import oripa.geom.OriLine;
import oripa.paint.Globals;
import oripa.paint.LineTypeSetter;
import oripa.paint.byvalue.AngleMeasuringAction;
import oripa.paint.byvalue.AngleValueInputListener;
import oripa.paint.byvalue.LengthMeasuringAction;
import oripa.paint.byvalue.LengthValueInputListener;
import oripa.paint.byvalue.ValueDB;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.view.main.MainScreen;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;
import oripa.viewsetting.model.ModelFrameSettingDB;
import oripa.viewsetting.render.RenderFrameSettingDB;
import oripa.viewsetting.uipanel.ChangeOnByValueButtonSelected;
import oripa.viewsetting.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.uipanel.FromLineTypeItemListener;
import oripa.viewsetting.uipanel.ToLineTypeItemListener;
import oripa.viewsetting.uipanel.UIPanelSettingDB;

public class UIPanel extends JPanel 
implements ActionListener, PropertyChangeListener, Observer {


	private UIPanelSettingDB settingDB = UIPanelSettingDB.getInstance();
	ResourceHolder resources = ResourceHolder.getInstance();

	
	//---------------------------------------------------------------------------------------------------------------------------
	// Binding edit mode

	private BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();
	ButtonFactory buttonFactory = new PaintActionButtonFactory();

	JRadioButton editModeInputLineButton = (JRadioButton) viewChangeBinder.createButton(
			JRadioButton.class, new ChangeOnPaintInputButtonSelected(), StringID.UI.INPUT_LINE_ID);

	JRadioButton editModePickLineButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.SELECT_ID);

	JRadioButton editModeDeleteLineButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.DELETE_LINE_ID);

	JRadioButton editModeLineTypeButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.CHANGE_LINE_TYPE_ID);

	JRadioButton editModeAddVertex =(JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.ADD_VERTEX_ID);

	JRadioButton editModeDeleteVertex = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.DELETE_VERTEX_ID);


	//---------------------------------------------------------------------------------------------------------------------------
	// Binding how to enter the line


	JRadioButton lineInputDirectVButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.DIRECT_V_ID);

	JRadioButton lineInputOnVButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.ON_V_ID);

	JRadioButton lineInputVerticalLineButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.VERTICAL_ID);

	JRadioButton lineInputAngleBisectorButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.BISECTOR_ID);

	JRadioButton lineInputTriangleSplitButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.TRIANGLE_ID);

	JRadioButton lineInputSymmetricButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.SYMMETRIC_ID);

	JRadioButton lineInputMirrorButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.MIRROR_ID);

	JRadioButton lineInputByValueButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.BY_VALUE_ID);

	JRadioButton lineInputPBisectorButton = (JRadioButton) buttonFactory.create(
			this, JRadioButton.class, StringID.PERPENDICULAR_BISECTOR_ID);

	//---------------------------------------------------------------------------------------------------------------------------


	JRadioButton lineTypeSubButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.AUX_ID));
	JRadioButton lineTypeMountainButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MOUNTAIN_ID));
	JRadioButton lineTypeValleyButton = new JRadioButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.VALLEY_ID));

	//---------------------------------------------------------------------------------------------------------------------------


	ButtonGroup editModeGroup;
	// Text box
	JFormattedTextField textFieldLength;
	JFormattedTextField textFieldAngle;
	JFormattedTextField textFieldGrid;

	JButton buttonLength = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));
	JButton buttonAngle = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.MEASURE_ID));

	JButton buildButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.FOLD_ID));
	JButton resetButton = new JButton("Reset");

	JCheckBox dispGridCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_GRID_ID), true);
	
	JButton gridSmallButton = new JButton("x2");
	JButton gridLargeButton = new JButton("x1/2");
	JButton gridChangeButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.GRID_SIZE_CHANGE_ID));

	JPanel mainPanel = new JPanel();
	JPanel subPanel1 = new JPanel();
	JPanel subPanel2 = new JPanel();
	JPanel gridPanel = new JPanel();
	JPanel lineTypePanel = new JPanel();
	// AlterLineType
	JPanel alterLineTypePanel = new JPanel();


	TypeForChange[] alterLine_comboData_from = 
		{TypeForChange.EMPTY, TypeForChange.RIDGE, TypeForChange.VALLEY};
	TypeForChange[] alterLine_comboData_to = 
		{TypeForChange.RIDGE, TypeForChange.VALLEY, TypeForChange.AUX, 
			TypeForChange.CUT, TypeForChange.DELETE, TypeForChange.FLIP};

	JComboBox<TypeForChange> alterLine_combo_from = new JComboBox<>(alterLine_comboData_from);
	JComboBox<TypeForChange> alterLine_combo_to = new JComboBox<>(alterLine_comboData_to);

	JCheckBox dispMVLinesCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_MV_ID), true);
	JCheckBox dispAuxLinesCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_AUX_ID), true);
	JCheckBox dispVertexCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.SHOW_VERTICES_ID), false);
	JCheckBox doFullEstimationCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.UI.FULL_ESTIMATION_ID), false);
	JButton buttonCheckWindow = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.UI.CHECK_WINDOW_ID));
	MainScreen screen;


	//	private PaintContext context = PaintContext.getInstance();




	public UIPanel(MainScreen __screen) {

		//setModeButtonText();
		editModeInputLineButton.setSelected(true);

		this.screen = __screen;
		setPreferredSize(new Dimension(210, 400));

		settingDB.addObserver(this);
		screenDB.addObserver(this);

		//		alterLine_combo_from.setSelectedIndex(0);
		//		alterLine_combo_to.setSelectedIndex(0);
		//		alterLine_combo_from.actionPerformed(null);
		//		alterLine_combo_to.actionPerformed(null);

		// Edit mode
		editModeGroup = new ButtonGroup();
		editModeGroup.add(editModeInputLineButton);
		editModeGroup.add(editModePickLineButton);
		editModeGroup.add(editModeDeleteLineButton);
		editModeGroup.add(editModeLineTypeButton);
		editModeGroup.add(editModeAddVertex);
		editModeGroup.add(editModeDeleteVertex);

		JLabel l1 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.CHANGE_LINE_TYPE_FROM_ID));
		
		JLabel l2 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.CHANGE_LINE_TYPE_TO_ID));
		
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

		//      lineInputDirectVButton.setIcon(new ImageIcon(getClass().getResource("/icon/segment.gif")));

		ImageResourceLoader imgLoader = new ImageResourceLoader();
		lineInputDirectVButton.setIcon(imgLoader.loadAsIcon("icon/segment.gif"));
		lineInputDirectVButton.setSelectedIcon(imgLoader.loadAsIcon("icon/segment_p.gif"));

		lineInputOnVButton.setIcon(imgLoader.loadAsIcon("icon/line.gif"));
		lineInputOnVButton.setSelectedIcon(imgLoader.loadAsIcon("icon/line_p.gif"));

		lineInputPBisectorButton.setIcon(imgLoader.loadAsIcon("icon/pbisector.gif"));
		lineInputPBisectorButton.setSelectedIcon(imgLoader.loadAsIcon("icon/pbisector_p.gif") );

		lineInputAngleBisectorButton.setIcon(imgLoader.loadAsIcon("icon/bisector.gif") );
		lineInputAngleBisectorButton.setSelectedIcon(imgLoader.loadAsIcon("icon/bisector_p.gif"));

		lineInputTriangleSplitButton.setIcon(imgLoader.loadAsIcon("icon/incenter.gif") );
		lineInputTriangleSplitButton.setSelectedIcon(imgLoader.loadAsIcon("icon/incenter_p.gif"));

		lineInputVerticalLineButton.setIcon(imgLoader.loadAsIcon("icon/vertical.gif"));
		lineInputVerticalLineButton.setSelectedIcon(imgLoader.loadAsIcon("icon/vertical_p.gif"));

		lineInputSymmetricButton.setIcon(imgLoader.loadAsIcon("icon/symmetry.gif"));
		lineInputSymmetricButton.setSelectedIcon(imgLoader.loadAsIcon("icon/symmetry_p.gif"));

		lineInputMirrorButton.setIcon(imgLoader.loadAsIcon("icon/mirror.gif"));
		lineInputMirrorButton.setSelectedIcon(imgLoader.loadAsIcon("icon/mirror_p.gif"));

		lineInputByValueButton.setIcon(imgLoader.loadAsIcon("icon/by_value.gif"));
		lineInputByValueButton.setSelectedIcon(imgLoader.loadAsIcon("icon/by_value_p.gif"));



		setLayout(new FlowLayout());
		add(mainPanel);


		//------------------------------------
		// Panel input for length and angle
		//------------------------------------
		JLabel subLabel1 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.LENGTH_ID));

		JLabel subLabel2 = new JLabel(
				resources.getString(ResourceKey.LABEL, StringID.UI.ANGLE_ID));

//		subPanel1.setVisible(true);
//		subPanel2.setVisible(true);
		subPanel1.setVisible(false);
		subPanel2.setVisible(false);

		NumberFormat doubleValueFormat = NumberFormat.getNumberInstance(Locale.US);
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

		//------------------------------------
		// For the grid panel
		//------------------------------------
		JPanel divideNumSpecPanel = new JPanel();
		JLabel gridLabel1 = new JLabel(
			resources.getString(ResourceKey.LABEL, StringID.UI.GRID_DIVIDE_NUM_ID));

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
		gridPanel.setBorder(new EtchedBorder(BevelBorder.RAISED, getBackground().darker(), getBackground().brighter()));
		add(gridPanel);

		//------------------------------------
		// Buttons panel
		//------------------------------------
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


		addListenerToComponents();

		//-------------------------------------------------
		// Initialize selection
		//-------------------------------------------------

		// of paint command
		lineInputDirectVButton.doClick();
		
		// of line type on DB
		settingDB.setTypeFrom((TypeForChange)alterLine_combo_from.getSelectedItem());
		settingDB.setTypeTo((TypeForChange)alterLine_combo_to.getSelectedItem());

	}

	
	private void addPaintActionButtons(int gridWidth, int gridy_start){

		paintActionButtonCount = 0;
		// put operation buttons in order
		addPaintActionButton(lineInputDirectVButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputOnVButton,  gridWidth, gridy_start);
		addPaintActionButton(lineInputPBisectorButton,  gridWidth, gridy_start);
		addPaintActionButton(lineInputAngleBisectorButton,  gridWidth, gridy_start);
		addPaintActionButton(lineInputTriangleSplitButton,  gridWidth, gridy_start);
		addPaintActionButton(lineInputVerticalLineButton,  gridWidth, gridy_start);
		addPaintActionButton(lineInputSymmetricButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputMirrorButton, gridWidth, gridy_start);
		addPaintActionButton(lineInputByValueButton, gridWidth, gridy_start);
	}

	private int paintActionButtonCount = 0;
	private void addPaintActionButton(AbstractButton button, int gridWidth, int gridy){
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = paintActionButtonCount % gridWidth + 1;
		gridBagConstraints.gridy = gridy + paintActionButtonCount / gridWidth;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		mainPanel.add(button, gridBagConstraints);

		paintActionButtonCount++;
		
		
	}
	
	private void addListenerToComponents(){


		alterLine_combo_from.addItemListener(new FromLineTypeItemListener());
		alterLine_combo_to.addItemListener(new ToLineTypeItemListener());

		buttonLength.addActionListener(
				new PaintActionSetter(new LengthMeasuringAction()));
		buttonLength.addActionListener(
				new ViewChangeListener(new ChangeOnByValueButtonSelected()));

		buttonAngle.addActionListener(
				new PaintActionSetter(new AngleMeasuringAction()));
		buttonAngle.addActionListener(
				new ViewChangeListener(new ChangeOnByValueButtonSelected()));

		lineTypeMountainButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_RIDGE));

		lineTypeValleyButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_VALLEY));
			
		lineTypeSubButton.addActionListener(
				new LineTypeSetter(OriLine.TYPE_NONE));

		editModeInputLineButton.addActionListener(new InputCommandStatePopper());

		textFieldLength.getDocument().addDocumentListener(new LengthValueInputListener());
		textFieldAngle.getDocument().addDocumentListener(new AngleValueInputListener());


		dispGridCheckBox.addActionListener(this);
		gridSmallButton.addActionListener(this);
		gridLargeButton.addActionListener(this);
		buildButton.addActionListener(this);
		resetButton.addActionListener(this);
		dispVertexCheckBox.addActionListener(this);
		dispVertexCheckBox.setSelected(true);
		Globals.dispVertex = true;
		dispMVLinesCheckBox.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Globals.dispMVLines = dispMVLinesCheckBox.isSelected();
				screen.repaint();
			}
		});
		dispAuxLinesCheckBox.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Globals.dispAuxLines = dispAuxLinesCheckBox.isSelected();
				screen.repaint();
			}
		});

		doFullEstimationCheckBox.setSelected(true);
		Globals.bDoFullEstimation = true;
		doFullEstimationCheckBox.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Globals.bDoFullEstimation = doFullEstimationCheckBox.isSelected();
				screen.repaint();
			}
		});

		buttonCheckWindow.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ORIPA.doc.buildOrigami3(false);
				ORIPA.doc.checkPatternValidity();
				ORIPA.checkFrame.setVisible(true);
				ORIPA.checkFrame.repaint();
			}
		});

	}


	//	private GraphicMouseAction previousMouseAction = null;
	private MainScreenSettingDB screenDB = MainScreenSettingDB.getInstance();

	@Override
	public void actionPerformed(ActionEvent ae) {		

		ScreenUpdater screenUpdater = ScreenUpdater.getInstance();

		if (ae.getSource() == dispGridCheckBox) {
			screenDB.setGridVisible(dispGridCheckBox.isSelected());
			screenDB.notifyObservers();

			screenUpdater.updateScreen();			

		} else if (ae.getSource() == gridSmallButton) {
			if (Globals.gridDivNum < 65) {
				Globals.gridDivNum *= 2;
				textFieldGrid.setValue(new Integer(Globals.gridDivNum));

				screenUpdater.updateScreen();			
			}
		} else if (ae.getSource() == gridLargeButton) {
			if (Globals.gridDivNum > 3) {
				Globals.gridDivNum /= 2;
				textFieldGrid.setValue(new Integer(Globals.gridDivNum));

				screenUpdater.updateScreen();			
			}
		} else if (ae.getSource() == dispVertexCheckBox) {
			Globals.dispVertex = dispVertexCheckBox.isSelected();

			screenUpdater.updateScreen();			
		} else if (ae.getSource() == resetButton) {
		} else if (ae.getSource() == buildButton) {
			boolean buildOK = false;
			ORIPA.doc.sortedFaces.clear();
			if (ORIPA.doc.buildOrigami3(false)) {
				buildOK = true;
			} else {
				if (JOptionPane.showConfirmDialog(
						ORIPA.mainFrame, resources.getString(ResourceKey.WARNING, StringID.Warning.FOLD_FAILED_DUPLICATION_ID), 
						"Failed", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
						== JOptionPane.YES_OPTION) {
					if (ORIPA.doc.buildOrigami3(false)) {
						buildOK = true;
					} else {
						JOptionPane.showMessageDialog(
								ORIPA.mainFrame, resources.getString(ResourceKey.WARNING, StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID), 
								"Failed Level1",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}

			if (buildOK) {
				Folder folder = new Folder(ORIPA.doc);
				int answerNum = folder.fold();
				System.out.println("RenderFrame");
				if (answerNum != 0) {
					RenderFrameSettingDB renderSetting = RenderFrameSettingDB.getInstance();
					renderSetting.setFrameVisible(true);
					renderSetting.notifyObservers();
				}

			} else {
				ORIPA.doc.foldWithoutLineType();
			}

			ModelFrameSettingDB modelSetting = ModelFrameSettingDB.getInstance();
			modelSetting.setFrameVisible(true);
			modelSetting.notifyObservers();

			//			screen.modeChanged();

		} else if (ae.getSource() == gridChangeButton) {
			int value;
			try {
				value = Integer.valueOf(textFieldGrid.getText());
				System.out.println("type");

				if (value < 128 && value > 2) {
					textFieldGrid.setValue(value);
					Globals.gridDivNum = value;
					screenUpdater.updateScreen();			
				}
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}



	}


	@Override
	public void propertyChange(PropertyChangeEvent e) {
		//        if (e.getSource() == textFieldLength) {
		//            textFieldLength.setValue(java.lang.Double.valueOf(textFieldLength.getText()));
		//        } else if (e.getSource() == textFieldAngle) {
		//            textFieldAngle.setValue(java.lang.Double.valueOf(textFieldAngle.getText()));
		//        }
	}


	/**
	 * observes DB for reflecting the changes to views.
	 * toString() of given DB has to return a unique value among DB classes.
	 * @param o Observable class which implements toString() 
	 *          to return its class name.
	 * @param arg A parameter to specify the action 
	 *        for the given Observable object.
	 */
	@Override
	public void update(Observable o, Object arg) {

		//System.out.println(o.toString());

		if(o.toString().equals(ValueDB.getInstance().toString())){
			// update text field of values
			ValueDB valueDB = (ValueDB) o;
			textFieldAngle.setValue(valueDB.getAngle());
			textFieldLength.setValue(valueDB.getLength());
		}
		else if(settingDB.hasGivenName(o.toString())){
			// update GUI
			UIPanelSettingDB setting = (UIPanelSettingDB) o;

			updateEditModeButtonSelection(setting);
			
			subPanel1.setVisible(setting.isValuePanelVisible());
			subPanel2.setVisible(setting.isValuePanelVisible());

			alterLineTypePanel.setVisible(setting.isAlterLineTypePanelVisible());


			lineTypeMountainButton.setEnabled(setting.isMountainButtonEnabled());
			lineTypeValleyButton.setEnabled(setting.isValleyButtonEnabled());
			lineTypeSubButton.setEnabled(setting.isAuxButtonEnabled());

			repaint();
		}
		else if(screenDB.hasGivenName(o.toString())){
			if(screenDB.isGridVisible() != dispGridCheckBox.isSelected()){
				dispGridCheckBox.setSelected(screenDB.isGridVisible());

			}

			repaint();
		}

	}

	private void updateEditModeButtonSelection(UIPanelSettingDB setting){
		switch(setting.getSelectedMode()){
		case INPUT:
			selectEditModeButton(editModeInputLineButton);
			break;
		case SELECT:
			selectEditModeButton(editModePickLineButton);
			break;
		}
		
	}
	
	private void selectEditModeButton(AbstractButton modeButton){
		editModeGroup.setSelected(modeButton.getModel(), true);

	}

}
