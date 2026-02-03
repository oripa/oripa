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

package oripa.swing.view.main;

import java.awt.Rectangle;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oripa.gui.view.main.CircleCopyDialogView;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class CircleCopyDialog extends JDialog implements CircleCopyDialogView {

    private final ResourceHolder resourceHolder;

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JButton jButtonOK = null;
    private JButton jButtonCancel = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JTextField jTextFieldCX = null;
    private JTextField jTextFieldCY = null;
    private JTextField jTextFieldAngle = null;
    private JLabel jLabel3 = null;
    private JTextField jTextFieldNum = null;

    private final JFrame owner;

    /**
     * @param owner
     */
    public CircleCopyDialog(final JFrame owner, final ResourceHolder resourceHolder) {
        super(owner);

        this.owner = owner;

        this.resourceHolder = resourceHolder;

        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(160, 171);
        this.setTitle(resourceHolder.getString(ResourceKey.LABEL, StringID.Main.CIRCLE_COPY_DIALOG_TITLE_ID));
        this.setLocation(
                owner.getLocation().x + 200,
                owner.getLocation().y + 100);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jLabel3 = new JLabel();
            jLabel3.setBounds(new Rectangle(5, 80, 71, 21));
            jLabel3.setText("Number");
            jLabel2 = new JLabel();
            jLabel2.setBounds(new Rectangle(5, 55, 71, 21));
            jLabel2.setText("Angle(deg)");
            jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(5, 30, 61, 21));
            jLabel1.setText("Center Y");
            jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(5, 5, 61, 21));
            jLabel.setToolTipText("");
            jLabel.setText("Center X");
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJButtonOK(), null);
            jContentPane.add(getJButtonCancel(), null);
            jContentPane.add(jLabel, null);
            jContentPane.add(jLabel1, null);
            jContentPane.add(jLabel2, null);
            jContentPane.add(getJTextFieldCX(), null);
            jContentPane.add(getJTextFieldCY(), null);
            jContentPane.add(getJTextFieldAngle(), null);
            jContentPane.add(jLabel3, null);
            jContentPane.add(getJTextFieldNum(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jButtonOK
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonOK() {
        if (jButtonOK == null) {
            jButtonOK = new JButton();
            jButtonOK.setBounds(new Rectangle(5, 110, 56, 21));
            jButtonOK.setText("OK");
        }
        return jButtonOK;
    }

    /**
     * This method initializes jButtonCancel
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCancel() {
        if (jButtonCancel == null) {
            jButtonCancel = new JButton();
            jButtonCancel.setBounds(new Rectangle(65, 110, 81, 21));
            jButtonCancel.setText("Cancel");
            jButtonCancel.addActionListener(e -> dispose());
        }
        return jButtonCancel;
    }

    /**
     * This method initializes jTextFieldCX
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldCX() {
        if (jTextFieldCX == null) {
            jTextFieldCX = new JTextField();
            jTextFieldCX.setBounds(new Rectangle(80, 5, 66, 21));
            jTextFieldCX.setText("0");
            jTextFieldCX.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldCX;
    }

    /**
     * This method initializes jTextFieldCY
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldCY() {
        if (jTextFieldCY == null) {
            jTextFieldCY = new JTextField();
            jTextFieldCY.setBounds(new Rectangle(80, 30, 66, 21));
            jTextFieldCY.setText("0");
            jTextFieldCY.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldCY;
    }

    /**
     * This method initializes jTextFieldAngle
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldAngle() {
        if (jTextFieldAngle == null) {
            jTextFieldAngle = new JTextField();
            jTextFieldAngle.setBounds(new Rectangle(80, 55, 66, 21));
            jTextFieldAngle.setText("30");
            jTextFieldAngle.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldAngle;
    }

    /**
     * This method initializes jTextFieldNum
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldNum() {
        if (jTextFieldNum == null) {
            jTextFieldNum = new JTextField();
            jTextFieldNum.setBounds(new Rectangle(80, 80, 66, 21));
            jTextFieldNum.setText("1");
            jTextFieldNum.setHorizontalAlignment(JTextField.RIGHT);
        }
        return jTextFieldNum;
    }

    @Override
    public double getCenterX() {
        try {
            return Double.valueOf(jTextFieldCX.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void setCenterX(final double centerX) {
        jTextFieldCX.setText(Double.toString(centerX));
    }

    @Override
    public double getCenterY() {
        try {
            return Double.valueOf(jTextFieldCY.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void setCenterY(final double centerY) {
        jTextFieldCY.setText(Double.toString(centerY));
    }

    @Override
    public double getAngleDegree() {
        try {
            return Double.valueOf(jTextFieldAngle.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void setAngleDegree(final double angleDegree) {
        jTextFieldAngle.setText(Double.toString(angleDegree));
    }

    @Override
    public int getCopyCount() {
        try {
            return Integer.valueOf(jTextFieldNum.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void setCopyCount(final int copyCount) {
        jTextFieldNum.setText(Integer.toString(copyCount));
    }

    @Override
    public void setOKButtonListener(final Supplier<Boolean> listener) {
        Stream.of(jButtonOK.getActionListeners()).forEach(l -> jButtonOK.removeActionListener(l));
        jButtonOK.addActionListener(e -> {
            if (listener.get()) {
                dispose();
            }
        });
    }

    @Override
    public void showWrongCopyCountMessage() {
        JOptionPane.showMessageDialog(
                owner, "Specify positive integer to Number.",
                "Circle Copy",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
