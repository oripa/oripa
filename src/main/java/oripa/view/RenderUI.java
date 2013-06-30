/**
 * ORIPA - Origami Pattern Editor Copyright (C) 2005-2009 Jun Mitani
 * http://mitani.cs.tsukuba.ac.jp/
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.io.File;
import javax.swing.*;

import oripa.ORIPA;
import oripa.doc.exporter.Exporter;
import oripa.doc.exporter.ExporterORmat;
import oripa.doc.exporter.ExporterSVG;
import oripa.file.FileFilterEx;

public class RenderUI extends JPanel {

    private static final long serialVersionUID = 1L;
    private JButton jButtonNextAnswer = null;
    private JButton jButtonPrevAnswer = null;
    private JCheckBox jCheckBoxOrder = null;
    private JCheckBox jCheckBoxShadow = null;
    private JLabel jLabel = null;
    private RenderScreen2 screen;
    private JCheckBox jCheckBoxUseColor = null;
    private JCheckBox jCheckBoxEdge = null;
    private JCheckBox jCheckBoxFillFace = null;
    private JButton jButtonExport = null;

    /**
     * This is the default constructor
     */
    public RenderUI() {
        super();
        initialize();
    }

    public void setScreen(RenderScreen2 s) {
        screen = s;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        jLabel = new JLabel();
        jLabel.setBounds(new Rectangle(15, 45, 181, 16));
        this.setLayout(null);
        this.setSize(216, 256);
        this.setPreferredSize(new Dimension(216, 200));
        this.add(getJButtonPrevAnswer(), null);
        this.add(getJCheckBoxOrder(), null);
        this.add(getJButtonNextAnswer(), null);
        this.add(getJCheckBoxShadow(), null);
        this.add(jLabel, null);
        this.add(getJCheckBoxUseColor(), null);
        this.add(getJCheckBoxEdge(), null);
        this.add(getJCheckBoxFillFace(), null);
        this.add(getJButtonExport(), null);
        updateLabel();
    }

    public void updateLabel() {
        jLabel.setText("Folded model [" + (ORIPA.doc.currentORmatIndex + 1) + "/"
                + ORIPA.doc.overlapRelations.size() + "]");

    }

    /**
     * This method initializes jButtonNextAnswer
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonNextAnswer() {
        if (jButtonNextAnswer == null) {
            jButtonNextAnswer = new JButton();
            jButtonNextAnswer.setText("Next");
            jButtonNextAnswer.setBounds(new Rectangle(109, 4, 87, 27));

            jButtonNextAnswer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ORIPA.doc.setNextORMat();
                    screen.redrawOrigami();
                    updateLabel();
                }
            });
        }
        return jButtonNextAnswer;
    }

    /**
     * This method initializes jButtonPrevAnswer
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonPrevAnswer() {
        if (jButtonPrevAnswer == null) {
            jButtonPrevAnswer = new JButton();
            jButtonPrevAnswer.setText("Prev");
            jButtonPrevAnswer.setBounds(new Rectangle(15, 4, 89, 27));

            jButtonPrevAnswer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ORIPA.doc.setPrevORMat();
                    screen.redrawOrigami();
                    updateLabel();
                }
            });
        }
        return jButtonPrevAnswer;
    }

    /**
     * This method initializes jCheckBoxOrder
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxOrder() {
        if (jCheckBoxOrder == null) {
            jCheckBoxOrder = new JCheckBox();
            jCheckBoxOrder.setBounds(new Rectangle(15, 75, 91, 31));
            jCheckBoxOrder.setText("Flip");
            jCheckBoxOrder.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    screen.flipFaces(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return jCheckBoxOrder;
    }

    /**
     * This method initializes jCheckBoxShadow
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxShadow() {
        if (jCheckBoxShadow == null) {
            jCheckBoxShadow = new JCheckBox();
            jCheckBoxShadow.setBounds(new Rectangle(105, 75, 80, 31));
            jCheckBoxShadow.setText("Shade");

            jCheckBoxShadow.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    screen.shadeFaces(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return jCheckBoxShadow;
    }

    /**
     * This method initializes jCheckBoxUseColor
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxUseColor() {
        if (jCheckBoxUseColor == null) {
            jCheckBoxUseColor = new JCheckBox();
            jCheckBoxUseColor.setBounds(new Rectangle(15, 120, 80, 31));
            jCheckBoxUseColor.setSelected(true);
            jCheckBoxUseColor.setText("Use Color");

            jCheckBoxUseColor.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    screen.setUseColor(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return jCheckBoxUseColor;
    }

    /**
     * This method initializes jCheckBoxEdge
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxEdge() {
        if (jCheckBoxEdge == null) {
            jCheckBoxEdge = new JCheckBox();
            jCheckBoxEdge.setBounds(new Rectangle(105, 120, 93, 31));
            jCheckBoxEdge.setSelected(true);
            jCheckBoxEdge.setText("Draw Edge");

            jCheckBoxEdge.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    screen.drawEdge(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return jCheckBoxEdge;
    }

    /**
     * This method initializes jCheckBoxFillFace
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxFillFace() {
        if (jCheckBoxFillFace == null) {
            jCheckBoxFillFace = new JCheckBox();
            jCheckBoxFillFace.setBounds(new Rectangle(15, 165, 93, 21));
            jCheckBoxFillFace.setSelected(true);
            jCheckBoxFillFace.setText("FillFace");

            jCheckBoxFillFace.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    screen.setFillFace(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        }
        return jCheckBoxFillFace;
    }

    /**
     * This method initializes jButtonExport
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonExport() {
        if (jButtonExport == null) {
            jButtonExport = new JButton();
            jButtonExport.setBounds(new Rectangle(15, 206, 92, 26));
            jButtonExport.setText("Export");
            jButtonExport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String ext1 = "ormat";
                    String ext2 = "svg";
                    JFileChooser fileChooser = new JFileChooser();
                    FileFilterEx f1 = new FileFilterEx(new String[]{"." + ext1},
                            "(*." + ext1 + ")" + ext1 + ORIPA.res.getString("File"));
                    FileFilterEx f2 = new FileFilterEx(new String[]{"." + ext2},
                            "(*." + ext2 + ")" + ext2 + ORIPA.res.getString("File"));
                    fileChooser.addChoosableFileFilter(f1);
                    fileChooser.addChoosableFileFilter(f2);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    fileChooser.setFileFilter(f2);
                    if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(ORIPA.mainFrame)) {
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
                            if (fileChooser.getFileFilter().equals(f1)) {
                                if (!filePath.endsWith("." + ext1)) {
                                    filePath += "." + ext1;
                                }
                                Exporter exporter = new ExporterORmat();
                                exporter.export(ORIPA.doc, filePath);
                            } else if (fileChooser.getFileFilter().equals(f2)) {
                                if (!filePath.endsWith("." + ext2)) {
                                    filePath += "." + ext2;
                                }
                                ExporterSVG.exportModel(ORIPA.doc, filePath);
                            }

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                    ORIPA.mainFrame, ex.toString(),
                                    ORIPA.res.getString("Error_FileSaveFaild"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }
        return jButtonExport;
    }
}  //  @jve:decl-index=0:visual-constraint="8,8"
