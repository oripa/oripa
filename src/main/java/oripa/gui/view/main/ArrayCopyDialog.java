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

package oripa.gui.view.main;

import java.awt.Rectangle;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class ArrayCopyDialog extends JDialog implements ArrayCopyDialogView {

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextField jTextFieldRow = null;
	private JLabel jLabelRow = null;
	private JLabel jLabelCol = null;
	private JTextField jTextFieldCol = null;
	private JCheckBox jCheckBoxFill = null;
	private JLabel jLabelInterval = null;
	private JLabel jLabelIntX = null;
	private JLabel jLabelIntY = null;
	private JTextField jTextFieldIntX = null;
	private JTextField jTextFieldIntY = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;

	private final JFrame owner;

	/**
	 * @param owner
	 */
	public ArrayCopyDialog(final JFrame owner) {
		super(owner);
		this.owner = owner;

		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(123, 249);
		this.setLocation(owner.getLocation().x + 200,
				owner.getLocation().y + 100);
		this.setTitle(resources.getString(ResourceKey.LABEL, StringID.Main.ARRAY_COPY_DIALOG_TITLE_ID));
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelIntY = new JLabel();
			jLabelIntY.setBounds(new Rectangle(5, 130, 26, 21));
			jLabelIntY.setText("Y");
			jLabelIntY.setEnabled(false);

			jLabelIntX = new JLabel();
			jLabelIntX.setBounds(new Rectangle(5, 105, 26, 21));
			jLabelIntX.setText("X");
			jLabelIntX.setEnabled(false);

			jLabelInterval = new JLabel();
			jLabelInterval.setBounds(new Rectangle(5, 80, 59, 21));
			jLabelInterval.setText("Interval");

			jLabelCol = new JLabel();
			jLabelCol.setBounds(new Rectangle(5, 55, 51, 21));
			jLabelCol.setText("Col");
			jLabelCol.setEnabled(false);

			jLabelRow = new JLabel();
			jLabelRow.setBounds(new Rectangle(5, 30, 51, 21));
			jLabelRow.setText("Row");
			jLabelRow.setEnabled(false);

			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextFieldRow(), null);
			jContentPane.add(jLabelRow, null);
			jContentPane.add(jLabelCol, null);
			jContentPane.add(getJTextFieldCol(), null);
			jContentPane.add(getJCheckBoxFill(), null);
			jContentPane.add(jLabelInterval, null);
			jContentPane.add(jLabelIntX, null);
			jContentPane.add(jLabelIntY, null);
			jContentPane.add(getJTextFieldIntX(), null);
			jContentPane.add(getJTextFieldIntY(), null);
			jContentPane.add(getJButtonOK(), null);
			jContentPane.add(getJButtonCancel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextFieldX
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldRow() {
		if (jTextFieldRow == null) {
			jTextFieldRow = new JTextField();
			jTextFieldRow.setBounds(new Rectangle(60, 30, 41, 21));
			jTextFieldRow.setHorizontalAlignment(JTextField.RIGHT);
			jTextFieldRow.setEnabled(false);
		}
		return jTextFieldRow;
	}

	/**
	 * This method initializes jTextFieldY
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldCol() {
		if (jTextFieldCol == null) {
			jTextFieldCol = new JTextField();
			jTextFieldCol.setBounds(new Rectangle(60, 55, 41, 21));
			jTextFieldCol.setHorizontalAlignment(JTextField.RIGHT);
			jTextFieldCol.setEnabled(false);
		}
		return jTextFieldCol;
	}

	/**
	 * This method initializes jCheckBoxFill
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxFill() {
		if (jCheckBoxFill == null) {
			jCheckBoxFill = new JCheckBox();
			jCheckBoxFill.setBounds(new Rectangle(5, 5, 91, 22));
			jCheckBoxFill.setSelected(true);
			jCheckBoxFill.setText("Fill Sheet");
			jCheckBoxFill.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					jTextFieldRow.setEnabled(!jCheckBoxFill.isSelected());
					jTextFieldCol.setEnabled(!jCheckBoxFill.isSelected());
					jLabelRow.setEnabled(!jCheckBoxFill.isSelected());
					jLabelCol.setEnabled(!jCheckBoxFill.isSelected());

					jTextFieldIntX.setEnabled(!jCheckBoxFill.isSelected());
					jTextFieldIntY.setEnabled(!jCheckBoxFill.isSelected());
					jLabelIntX.setEnabled(!jCheckBoxFill.isSelected());
					jLabelIntY.setEnabled(!jCheckBoxFill.isSelected());
				}
			});
		}
		return jCheckBoxFill;
	}

	/**
	 * This method initializes jTextFieldIntX
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldIntX() {
		if (jTextFieldIntX == null) {
			jTextFieldIntX = new JTextField();
			jTextFieldIntX.setBounds(new Rectangle(35, 105, 66, 21));
			jTextFieldIntX.setHorizontalAlignment(JTextField.RIGHT);
			jTextFieldIntX.setEnabled(false);
		}
		return jTextFieldIntX;
	}

	/**
	 * This method initializes jTextFieldIntY
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldIntY() {
		if (jTextFieldIntY == null) {
			jTextFieldIntY = new JTextField();
			jTextFieldIntY.setBounds(new Rectangle(35, 130, 66, 21));
			jTextFieldIntY.setHorizontalAlignment(JTextField.RIGHT);
			jTextFieldIntY.setEnabled(false);
		}
		return jTextFieldIntY;
	}

	/**
	 * This method initializes jButtonOK
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(10, 160, 96, 21));
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
			jButtonCancel.setBounds(new Rectangle(10, 185, 96, 21));
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(e -> dispose());
		}
		return jButtonCancel;
	}

	@Override
	public int getRowCount() {
		try {
			return Integer.valueOf(jTextFieldRow.getText());
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void setRowCount(final int rowCount) {
		jTextFieldRow.setText(Integer.toString(rowCount));
	}

	@Override
	public int getColumnCount() {
		try {
			return Integer.valueOf(jTextFieldCol.getText());
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void setColumnCount(final int columnCount) {
		jTextFieldCol.setText(Integer.toString(columnCount));
	}

	@Override
	public double getIntervalX() {
		try {
			return Double.valueOf(jTextFieldIntX.getText());
		} catch (Exception ex) {
			return 0;
		}
	}

	@Override
	public void setIntervalX(final double intervalX) {
		jTextFieldIntX.setText(Double.toString(intervalX));
	}

	@Override
	public double getIntervalY() {
		try {
			return Double.valueOf(jTextFieldIntY.getText());
		} catch (Exception ex) {
			return 0;
		}
	}

	@Override
	public void setIntervalY(final double intervalY) {
		jTextFieldIntY.setText(Double.toString(intervalY));
	}

	@Override
	public boolean shouldFillUp() {
		return jCheckBoxFill.isSelected();
	}

	@Override
	public void setFillUp(final boolean fillUp) {
		jCheckBoxFill.setSelected(fillUp);
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
	public void setViewVisible(final boolean visible) {
		setVisible(visible);
	}

	@Override
	public void showWrongInputMessage() {
		JOptionPane.showMessageDialog(
				owner, "Specify non-Zero value to Row and Col.",
				"Array Copy",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
