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

package oripa;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import oripa.file.ImageResourceLoader;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.line.TwoPointLineAction;
import oripa.paint.pbisec.TwoPointBisectorAction;
import oripa.paint.segment.TwoPointSegmentAction;
import oripa.paint.vertical.VerticalLineAction;

public class UIPanel extends JPanel implements ActionListener, PropertyChangeListener, KeyListener {
    // Edit mode

    JRadioButton editModeInputLineButton = new JRadioButton("InputLine", true);
    public JRadioButton editModePickLineButton = new JRadioButton("Select");
    JRadioButton editModeDeleteLineButton = new JRadioButton("DeleteLine");
    JRadioButton editModeLineTypeButton = new JRadioButton("AlterLineType");
    JRadioButton editModeAddVertex = new JRadioButton("AddVertex");
    JRadioButton editModeDeleteVertex = new JRadioButton("DeleteVertex");
    JRadioButton lineTypeSubButton = new JRadioButton("Aux");
    JRadioButton lineTypeMountainButton = new JRadioButton("Mountain");
    JRadioButton lineTypeValleyButton = new JRadioButton("Valley");
    // How to enter the line
    JRadioButton lineInputDirectVButton = new JRadioButton();
    JRadioButton lineInputVerticalLineButton = new JRadioButton();
    JRadioButton lineInputOnVButton = new JRadioButton();
    JRadioButton lineInputOverlapVButton = new JRadioButton();
    JRadioButton lineInputOverlapEButton = new JRadioButton();
    JRadioButton lineInputBisectorButton = new JRadioButton();
    JRadioButton lineInputTriangleSplitButton = new JRadioButton();
    JRadioButton lineInputSymmetricButton = new JRadioButton();
    JRadioButton lineInputMirrorButton = new JRadioButton();
    JRadioButton lineInputByValueButton = new JRadioButton();
    JRadioButton lineInputPBisectorButton = new JRadioButton(); // perpendicular bisector
    ButtonGroup editModeGroup;
    // Text box
    JFormattedTextField textFieldLength;
    JFormattedTextField textFieldAngle;
    JFormattedTextField textFieldGrid;
    JButton buttonLength = new JButton(ORIPA.res.getString("UI_Mesure"));
    JButton buttonAngle = new JButton(ORIPA.res.getString("UI_Mesure"));
    JButton buildButton = new JButton(ORIPA.res.getString("UI_Fold"));
    JButton resetButton = new JButton("Reset");
    JCheckBox dispGridCheckBox = new JCheckBox(ORIPA.res.getString("UI_ShowGrid"), true);
    JButton gridSmallButton = new JButton("x2");
    JButton gridLargeButton = new JButton("x1/2");
    JButton gridChangeButton = new JButton(ORIPA.res.getString("UI_GridSizeChange"));
    JPanel mainPanel = new JPanel();
    JPanel subPanel1 = new JPanel();
    JPanel subPanel2 = new JPanel();
    JPanel gridPanel = new JPanel();
    JPanel lineTypePanel = new JPanel();
    // AlterLineType
    JPanel alterLineTypePanel = new JPanel();
    String[] alterLine_comboData_from = {"-", "M", "V"};
    String[] alterLine_comboData_to = {"M", "V", "Aux", "Cut", "Del", "Flip"};
    JComboBox alterLine_combo_from = new JComboBox(alterLine_comboData_from);
    JComboBox alterLine_combo_to = new JComboBox(alterLine_comboData_to);
    JCheckBox dispMVLinesCheckBox = new JCheckBox("Show M/V Lines", true);
    JCheckBox dispAuxLinesCheckBox = new JCheckBox("Show Aux Lines", true);
    JCheckBox dispVertexCheckBox = new JCheckBox("Show Vertices", false);
    JCheckBox doFullEstimationCheckBox = new JCheckBox("Full Estimation", false);
    JButton buttonCheckWindow = new JButton("Check Window");
    MainScreen screen;

    public UIPanel(MainScreen __screen) {
        this.screen = __screen;
        setPreferredSize(new Dimension(210, 400));

        // Edit mode
        editModeGroup = new ButtonGroup();
        editModeGroup.add(editModeInputLineButton);
        editModeGroup.add(editModePickLineButton);
        editModeGroup.add(editModeDeleteLineButton);
        editModeGroup.add(editModeLineTypeButton);
        editModeGroup.add(editModeAddVertex);
        editModeGroup.add(editModeDeleteVertex);

        JLabel l1 = new JLabel("  from");
        JLabel l2 = new JLabel("to");
        alterLineTypePanel.add(l1);
        alterLineTypePanel.add(alterLine_combo_from);
        alterLineTypePanel.add(l2);
        alterLineTypePanel.add(alterLine_combo_to);
        alterLineTypePanel.setVisible(false);

        // How to enter the line
        ButtonGroup lineInputGroup = new ButtonGroup();
        lineInputGroup.add(lineInputDirectVButton);
        lineInputGroup.add(lineInputOnVButton);
        lineInputGroup.add(lineInputOverlapVButton);
        lineInputGroup.add(lineInputTriangleSplitButton);
        lineInputGroup.add(lineInputBisectorButton);
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
        lineTypeMountainButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Globals.inputLineType = OriLine.TYPE_RIDGE;
            }
        });
        lineTypeValleyButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Globals.inputLineType = OriLine.TYPE_VALLEY;
            }
        });
        lineTypeSubButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Globals.inputLineType = OriLine.TYPE_NONE;
            }
        });


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

        int gridy_base = 9;
        GridBagConstraints gridBagConstraints_i0 = new GridBagConstraints();
        gridBagConstraints_i0.gridx = 1;
        gridBagConstraints_i0.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i1 = new GridBagConstraints();
        gridBagConstraints_i1.gridx = 2;
        gridBagConstraints_i1.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i2 = new GridBagConstraints();
        gridBagConstraints_i2.gridx = 3;
        gridBagConstraints_i2.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i3 = new GridBagConstraints();
        gridBagConstraints_i3.gridx = 4;
        gridBagConstraints_i3.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i4 = new GridBagConstraints();

        gridy_base++;

        gridBagConstraints_i4.gridx = 1;
        gridBagConstraints_i4.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i5 = new GridBagConstraints();
        gridBagConstraints_i5.gridx = 2;
        gridBagConstraints_i5.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i6 = new GridBagConstraints();
        gridBagConstraints_i6.gridx = 3;
        gridBagConstraints_i6.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i7 = new GridBagConstraints();
        gridBagConstraints_i7.gridx = 4;
        gridBagConstraints_i7.gridy = gridy_base;
        GridBagConstraints gridBagConstraints_i8 = new GridBagConstraints();
        gridy_base++;
        gridBagConstraints_i8.gridx = 1;
        gridBagConstraints_i8.gridy = gridy_base;

        gridBagConstraints_i0.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i7.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_i8.fill = java.awt.GridBagConstraints.HORIZONTAL;


        // put operation buttons in order
        mainPanel.add(lineInputDirectVButton, gridBagConstraints_i0);
        n++;
        mainPanel.add(lineInputOnVButton, gridBagConstraints_i1);
        n++;
        mainPanel.add(lineInputPBisectorButton, gridBagConstraints_i2);
        n++;
        mainPanel.add(lineInputBisectorButton, gridBagConstraints_i3);
        n++;
        mainPanel.add(lineInputTriangleSplitButton, gridBagConstraints_i4);
        n++;
        mainPanel.add(lineInputVerticalLineButton, gridBagConstraints_i5);
        n++;
        mainPanel.add(lineInputSymmetricButton, gridBagConstraints_i6);
        n++;
        mainPanel.add(lineInputMirrorButton, gridBagConstraints_i7);
        n++;
        mainPanel.add(lineInputByValueButton, gridBagConstraints_i8);
        n++;


        lineInputDirectVButton.setSelected(true);

        //      lineInputDirectVButton.setIcon(new ImageIcon(getClass().getResource("/icon/segment.gif")));

		ImageResourceLoader imgLoader = new ImageResourceLoader();
        lineInputDirectVButton.setIcon(imgLoader.loadAsIcon("icon/segment.gif"));
        lineInputDirectVButton.setSelectedIcon(imgLoader.loadAsIcon("icon/segment_p.gif"));

        lineInputOnVButton.setIcon(imgLoader.loadAsIcon("icon/line.gif"));
        lineInputOnVButton.setSelectedIcon(imgLoader.loadAsIcon("icon/line_p.gif"));

        lineInputPBisectorButton.setIcon(imgLoader.loadAsIcon("icon/pbisector.gif"));
        lineInputPBisectorButton.setSelectedIcon(imgLoader.loadAsIcon("icon/pbisector_p.gif") );

        lineInputBisectorButton.setIcon(imgLoader.loadAsIcon("icon/bisector.gif") );
        lineInputBisectorButton.setSelectedIcon(imgLoader.loadAsIcon("icon/bisector_p.gif"));

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


        lineInputDirectVButton.addActionListener(this);
        lineInputOnVButton.addActionListener(this);
        lineInputOverlapVButton.addActionListener(this);
        lineInputOverlapEButton.addActionListener(this);
        lineInputTriangleSplitButton.addActionListener(this);
        lineInputBisectorButton.addActionListener(this);
        lineInputVerticalLineButton.addActionListener(this);
        lineInputSymmetricButton.addActionListener(this);
        lineInputMirrorButton.addActionListener(this);
        lineInputByValueButton.addActionListener(this);
        lineInputPBisectorButton.addActionListener(this);

        editModeInputLineButton.addActionListener(this);
        editModePickLineButton.addActionListener(this);
        editModeDeleteLineButton.addActionListener(this);
//        editModeDivideLineButton.addActionListener(this);
        editModeLineTypeButton.addActionListener(this);
//        editModeSelectFaceButton.addActionListener(this);
        editModeAddVertex.addActionListener(this);
        editModeDeleteVertex.addActionListener(this);

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

        setLayout(new FlowLayout());
        add(mainPanel);

        //------------------------------------
        // Panel input for length and angle
        //------------------------------------
        JLabel subLabel1 = new JLabel(ORIPA.res.getString("UI_Length"));
        JLabel subLabel2 = new JLabel(ORIPA.res.getString("UI_Angle"));
        subPanel1.setVisible(false);
        subPanel2.setVisible(false);

        NumberFormat doubleValueFormat = NumberFormat.getNumberInstance(Locale.US);
        doubleValueFormat.setMinimumFractionDigits(3);

        textFieldLength = new JFormattedTextField(doubleValueFormat);
        textFieldAngle = new JFormattedTextField(doubleValueFormat);

        textFieldLength.addKeyListener(this);
        textFieldAngle.addKeyListener(this);

        textFieldLength.setColumns(4);
        textFieldAngle.setColumns(4);
        textFieldLength.setValue(new Double(0.0));
        textFieldAngle.setValue(new Double(0.0));

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
        buttonLength.addActionListener(this);
        buttonAngle.addActionListener(this);

        add(subPanel1);
        add(subPanel2);

        //------------------------------------
        // For the grid panel
        //------------------------------------
        JPanel divideNumSpecPanel = new JPanel();
        JLabel gridLabel1 = new JLabel(ORIPA.res.getString("UI_GridDivideNum"));
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
        lineInputBisectorButton.setMnemonic('4');
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


    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    	MouseContext.getInstance().clear();
    	Globals.mouseAction = null;
    	
    	
    	if (ae.getSource() == lineInputDirectVButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.DIRECT_V;
            
            Globals.mouseAction = new TwoPointSegmentAction();
            
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } 
        else if (ae.getSource() == lineInputOnVButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.ON_V;
            
            Globals.mouseAction = new TwoPointLineAction();
            
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } 
        else if (ae.getSource() == lineInputPBisectorButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.PBISECTOR;
            
            Globals.mouseAction = new TwoPointBisectorAction();
            
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
            
        } else if (ae.getSource() == lineInputVerticalLineButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.VERTICAL_LINE;

            Globals.mouseAction = new VerticalLineAction();
            
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
            
        } 
        
        else if (ae.getSource() == lineInputOverlapVButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.OVERLAP_V;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } else if (ae.getSource() == lineInputOverlapEButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.OVERLAP_E;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();

        } else if (ae.getSource() == lineInputBisectorButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.BISECTOR;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } else if (ae.getSource() == lineInputTriangleSplitButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.TRIANGLE_SPLIT;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();

        } else if (ae.getSource() == lineInputSymmetricButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.SYMMETRIC_LINE;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } else if (ae.getSource() == lineInputMirrorButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            Globals.lineInputMode = Constants.LineInputMode.MIRROR;
            editModeGroup.setSelected(editModeInputLineButton.getModel(), true);
            modeChanged();
        } else if (ae.getSource() == editModeInputLineButton) {
            Globals.editMode = Constants.EditMode.INPUT_LINE;
            modeChanged();
        } else if (ae.getSource() == editModePickLineButton) {
            Globals.editMode = Constants.EditMode.PICK_LINE;
            modeChanged();
        } else if (ae.getSource() == editModeDeleteLineButton) {
            Globals.editMode = Constants.EditMode.DELETE_LINE;
            modeChanged();
        } else if (ae.getSource() == editModeLineTypeButton) {
            Globals.editMode = Constants.EditMode.CHANGE_LINE_TYPE;
            modeChanged();
        } else if (ae.getSource() == editModeAddVertex) {
            Globals.editMode = Constants.EditMode.ADD_VERTEX;
            modeChanged();
        } else if (ae.getSource() == editModeDeleteVertex) {
            Globals.editMode = Constants.EditMode.DELETE_VERTEX;
            modeChanged();
        } else if (ae.getSource() == dispGridCheckBox) {
            screen.setDispGrid(dispGridCheckBox.isSelected());
        } else if (ae.getSource() == gridSmallButton) {
            if (Globals.gridDivNum < 65) {
                Globals.gridDivNum *= 2;
                textFieldGrid.setValue(new Integer(Globals.gridDivNum));
                screen.repaint();
            }
        } else if (ae.getSource() == gridLargeButton) {
            if (Globals.gridDivNum > 3) {
                Globals.gridDivNum /= 2;
                textFieldGrid.setValue(new Integer(Globals.gridDivNum));
                screen.repaint();
            }
        } else if (ae.getSource() == dispVertexCheckBox) {
            Globals.dispVertex = dispVertexCheckBox.isSelected();
            screen.repaint();
        } else if (ae.getSource() == resetButton) {
        } else if (ae.getSource() == buildButton) {
            boolean buildOK = false;
            ORIPA.doc.sortedFaces.clear();
            if (ORIPA.doc.buildOrigami3(false)) {
                buildOK = true;
            } else {
                if (JOptionPane.showConfirmDialog(
                        ORIPA.mainFrame, ORIPA.res.getString("Warning_foldFail1"), "Failed",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                        == JOptionPane.YES_OPTION) {
                    if (ORIPA.doc.buildOrigami3(false)) {
                        buildOK = true;
                    } else {
                        JOptionPane.showMessageDialog(
                                ORIPA.mainFrame, ORIPA.res.getString("Warning_foldFail2"), 
                                "Failed Level1",JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

            if (buildOK) {
                Folder folder = new Folder(ORIPA.doc);
                int answerNum = folder.fold();
                System.out.println("RenderFrame");
                if (answerNum != 0) {
                    ORIPA.renderFrame.screen.resetViewMatrix();
                    ORIPA.renderFrame.screen.redrawOrigami();
                    ORIPA.renderFrame.ui.updateLabel();
                    ORIPA.renderFrame.setVisible(true);
                }
                
            } else {
                ORIPA.doc.foldWithoutLineType();
            }

            ORIPA.modelFrame.setVisible(true);
            ORIPA.modelFrame.screen.resetViewMatrix();
            ORIPA.modelFrame.menuItemSlideFaces.setSelected(false);
            ORIPA.modelFrame.repaint();
            screen.modeChanged();
        } else if (ae.getSource() == buttonLength) {
            Globals.subLineInputMode = Constants.SubLineInputMode.PICK_LENGTH;
            modeChanged();
        } else if (ae.getSource() == buttonAngle) {
            Globals.subLineInputMode = Constants.SubLineInputMode.PICK_ANGLE;
            modeChanged();
        } else if (ae.getSource() == lineInputByValueButton) {
            Globals.lineInputMode = Constants.LineInputMode.BY_VALUE;
            Globals.subLineInputMode = Constants.SubLineInputMode.NONE;
            modeChanged();
        } else if (ae.getSource() == gridChangeButton) {
            int value;
            try {
                value = Integer.valueOf(textFieldGrid.getText());
                System.out.println("type");

                if (value < 128 && value > 2) {
                    textFieldGrid.setValue(value);
                    Globals.gridDivNum = value;
                    screen.repaint();
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

    }

    public void modeChanged() {
        boolean bDispSubPanel = false;
        if (Globals.editMode == Constants.EditMode.INPUT_LINE) {
            if (Globals.lineInputMode == Constants.LineInputMode.BY_VALUE) {
                bDispSubPanel = true;
            }
        }
        subPanel1.setVisible(bDispSubPanel);
        subPanel2.setVisible(bDispSubPanel);

        alterLineTypePanel.setVisible(Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE);
        screen.modeChanged();

        MainFrame.updateHint();

        lineTypeMountainButton.setEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);
        lineTypeValleyButton.setEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);
        lineTypeSubButton.setEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);

        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == textFieldLength) {
            textFieldLength.setValue(Double.valueOf(textFieldLength.getText()));
        } else if (e.getSource() == textFieldAngle) {
            textFieldAngle.setValue(Double.valueOf(textFieldAngle.getText()));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
