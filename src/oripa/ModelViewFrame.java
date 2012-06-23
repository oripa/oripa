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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import javax.swing.*;

import oripa.file.FileFilterEx;

public class ModelViewFrame extends JFrame implements ActionListener, AdjustmentListener {

    ModelViewScreen screen;
    private JMenu menuDisp = new JMenu(ORIPA.res.getString("MENU_Disp"));
    private JMenu menuFile = new JMenu(ORIPA.res.getString("File"));
    private JMenuItem menuItemExportDXF = new JMenuItem(ORIPA.res.getString("MENU_ExportModelLine_DXF"));
    private JMenuItem menuItemExportOBJ = new JMenuItem("Export to OBJ file");
    private JMenuItem menuItemFlip = new JMenuItem(ORIPA.res.getString("MENU_Invert"));
    private JCheckBoxMenuItem menuItemCrossLine = new JCheckBoxMenuItem("Show Cross-Line", false);
    public JCheckBoxMenuItem menuItemSlideFaces = new JCheckBoxMenuItem(ORIPA.res.getString("MENU_SlideFaces"), false);
    public JLabel hintLabel = new JLabel(ORIPA.res.getString("Direction_Basic"));
    private JMenu dispSubMenu = new JMenu(ORIPA.res.getString("MENU_DispType"));
    private JRadioButtonMenuItem menuItemFillColor = new JRadioButtonMenuItem(ORIPA.res.getString("MENU_FillColor"));
    private JRadioButtonMenuItem menuItemFillWhite = new JRadioButtonMenuItem(ORIPA.res.getString("MENU_FillWhite"));
    private JRadioButtonMenuItem menuItemFillAlpha = new JRadioButtonMenuItem(ORIPA.res.getString("MENU_FillAlpha"));
    private JRadioButtonMenuItem menuItemFillNone = new JRadioButtonMenuItem(ORIPA.res.getString("MENU_DrawLines"));
    private JScrollBar scrollBarAngle = new JScrollBar(JScrollBar.HORIZONTAL, 90, 5, 0, 185);
    private JScrollBar scrollBarPosition = new JScrollBar(JScrollBar.VERTICAL, 0, 5, -150, 150);

    public ModelViewFrame() {
        setTitle(ORIPA.res.getString("ExpectedFoldedOrigami"));
        screen = new ModelViewScreen();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(screen, BorderLayout.CENTER);
        getContentPane().add(hintLabel, BorderLayout.SOUTH);
        getContentPane().add(scrollBarAngle, BorderLayout.NORTH);
        getContentPane().add(scrollBarPosition, BorderLayout.WEST);

        // Construct menu bar
        JMenuBar menuBar = new JMenuBar();

        menuFile.add(menuItemExportDXF);
        menuFile.add(menuItemExportOBJ);
        menuDisp.add(menuItemFlip);

        menuDisp.add(dispSubMenu);
        menuDisp.add(menuItemCrossLine);
        ButtonGroup dispGroup = new ButtonGroup();
        dispGroup.add(menuItemFillAlpha);
        dispSubMenu.add(menuItemFillAlpha);
        dispGroup.add(menuItemFillNone);
        dispSubMenu.add(menuItemFillNone);
        menuItemFillAlpha.setSelected(true);
        menuItemFlip.addActionListener(this);
        menuItemSlideFaces.addActionListener(this);
        menuItemFillColor.addActionListener(this);
        menuItemFillWhite.addActionListener(this);
        menuItemFillAlpha.addActionListener(this);
        menuItemFillNone.addActionListener(this);
        menuItemExportDXF.addActionListener(this);
        menuItemExportOBJ.addActionListener(this);

        menuItemCrossLine.addActionListener(this);
        menuBar.add(menuFile);
        menuBar.add(menuDisp);

        setJMenuBar(menuBar);

        scrollBarAngle.addAdjustmentListener(this);
        scrollBarPosition.addAdjustmentListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemFlip) {
            ORIPA.doc.filpAll();
            screen.repaint();
        } else if (e.getSource() == menuItemSlideFaces) {
            ORIPA.doc.setFacesOutline(menuItemSlideFaces.isSelected());
            screen.repaint();
        } else if (e.getSource() == menuItemCrossLine) {
            Globals.bDispCrossLine = menuItemCrossLine.isSelected();
            if (menuItemCrossLine.isSelected()) {
                screen.recalcCrossLine();
            } else {
                screen.repaint();
                ORIPA.mainFrame.mainScreen.repaint();
            }
        } else if (e.getSource() == menuItemExportDXF) {
            exportFile("dxf");
        } else if (e.getSource() == menuItemExportOBJ) {
            exportFile("obj");
        } else if (e.getSource() == menuItemFillColor
                || e.getSource() == menuItemFillWhite
                || e.getSource() == menuItemFillAlpha
                || e.getSource() == menuItemFillNone) {
            if (menuItemFillColor.isSelected()) {
                Globals.modelDispMode = Constants.ModelDispMode.FILL_COLOR;
            } else if (menuItemFillWhite.isSelected()) {
                Globals.modelDispMode = Constants.ModelDispMode.FILL_WHITE;
            } else if (menuItemFillAlpha.isSelected()) {
                Globals.modelDispMode = Constants.ModelDispMode.FILL_ALPHA;
            } else if (menuItemFillNone.isSelected()) {
                Globals.modelDispMode = Constants.ModelDispMode.FILL_NONE;
            }

            System.out.println("fillMode" + Globals.modelDispMode);
            screen.repaint();
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == scrollBarAngle) {
            screen.setCrossLineAngle(e.getValue());
        } else if (e.getSource() == scrollBarPosition) {
            screen.setCrossLinePosition(e.getValue());
        }

    }

    private void exportFile(String ext) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilterEx(new String[]{"." + ext}, 
                "(*." + ext + ")" + ext + ORIPA.res.getString("File"), null));
        if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                File file = new File(filePath);
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(
                            null, ORIPA.res.getString("Warning_SameNameFileExist"), 
                            ORIPA.res.getString("DialogTitle_FileSave"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) 
                            != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                if (!filePath.endsWith("." + ext)) {
                    filePath += "." + ext;
                }
                switch (ext) {
                    case "dxf":
                        ExporterDXF.exportModel(ORIPA.doc, filePath);
                        break;
                    case "obj":
                        ExporterOBJ2.export(ORIPA.doc, filePath);
                        break;
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this, e.toString(), ORIPA.res.getString("Error_FileSaveFailed"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
