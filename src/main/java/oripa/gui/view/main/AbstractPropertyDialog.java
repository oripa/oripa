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

import java.awt.GridBagConstraints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oripa.doc.Property;

// TODO: Use label resource.
public abstract class AbstractPropertyDialog extends JDialog implements
		ComponentListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -5864700666603644379L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	public JTextField TitleTextField = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JTextField EditorNameTextField = null;
	private JTextField OriginalAuthorTextField = null;
	private JTextField ReferenceTextField = null;
	private JTextArea MemoTextArea = null;
	public JButton OKButton = null;

	private final Property property;

	/**
	 * This is the default constructor
	 */
	public AbstractPropertyDialog(final JFrame parent, final Property prop) {
		super(parent);
		initialize();

		property = prop;
	}

	public void setValue() {
		TitleTextField.setText(property.getTitle());
		EditorNameTextField.setText(property.getEditorName());
		OriginalAuthorTextField.setText(property.getOriginalAuthorName());
		ReferenceTextField.setText(property.getReference());
		MemoTextArea.setText(property.getMemo());
	}

	private Property getEditedProperty() {
		Property prop = new Property(property.getDataFilePath());
		prop.setTitle(TitleTextField.getText());
		prop.setEditorName(EditorNameTextField.getText());
		prop.setOriginalAuthorName(OriginalAuthorTextField.getText());
		prop.setReference(ReferenceTextField.getText());
		prop.setMemo(MemoTextArea.getText());

		return prop;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.addComponentListener(this);
		this.setSize(420, 278);
		this.setContentPane(getJContentPane());
		this.setTitle("Model Information");
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 5;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 4;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 4;
			jLabel4 = new JLabel();
			jLabel4.setText("Memo");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("Source");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("Model creator");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Author data");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 1;
			jLabel = new JLabel();
			jLabel.setText("Title");
			jLabel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			jContentPane = new JPanel();
			jContentPane.setLayout(new java.awt.GridBagLayout());
			jContentPane.add(getOKButton(), gridBagConstraints8);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, gridBagConstraints1);
			jContentPane.add(jLabel2, gridBagConstraints2);
			jContentPane.add(jLabel3, gridBagConstraints11);
			jContentPane.add(jLabel4, gridBagConstraints21);
			jContentPane.add(getTitleTextField(), gridBagConstraints);
			jContentPane.add(getEditorNameTextField(), gridBagConstraints3);
			jContentPane.add(getOriginalAuthorTextField(), gridBagConstraints4);
			jContentPane.add(getOriginTextField(), gridBagConstraints5);
			jContentPane.add(getMemoTextArea(), gridBagConstraints6);
		}
		return jContentPane;
	}

	/**
	 * This method initializes TitleTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTitleTextField() {
		if (TitleTextField == null) {
			TitleTextField = new JTextField();
		}
		return TitleTextField;
	}

	/**
	 * This method initializes EditorNameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getEditorNameTextField() {
		if (EditorNameTextField == null) {
			EditorNameTextField = new JTextField();
		}
		return EditorNameTextField;
	}

	/**
	 * This method initializes OriginalAuthorTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getOriginalAuthorTextField() {
		if (OriginalAuthorTextField == null) {
			OriginalAuthorTextField = new JTextField();
		}
		return OriginalAuthorTextField;
	}

	/**
	 * This method initializes OriginTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getOriginTextField() {
		if (ReferenceTextField == null) {
			ReferenceTextField = new JTextField();
		}
		return ReferenceTextField;
	}

	/**
	 * This method initializes MemoTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getMemoTextArea() {
		if (MemoTextArea == null) {
			MemoTextArea = new JTextArea();
		}
		return MemoTextArea;
	}

	/**
	 * This method initializes OKButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOKButton() {
		if (OKButton == null) {
			OKButton = new JButton();
			OKButton.setText("OK");
			OKButton.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					onClickOKButton(getEditedProperty());
					dispose();
				}
			});
		}
		return OKButton;
	}

	protected abstract void onClickOKButton(Property newProperty);

	@Override
	public void componentResized(final ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(final ComponentEvent arg0) {

	}

	@Override
	public void componentShown(final ComponentEvent arg0) {
		OKButton.requestFocusInWindow();
	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {

	}
}
