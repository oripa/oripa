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
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oripa.doc.Property;
import oripa.gui.view.util.GridBagConstraintsBuilder;

// TODO: Use label resource.
public abstract class AbstractPropertyDialog extends JDialog implements
		ComponentListener {

	private static final long serialVersionUID = -5864700666603644379L;

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

	public AbstractPropertyDialog(final JFrame parent, final Property prop) {
		super(parent);

		this.property = prop;

		setTitle("Model Information");
		setSize(420, 278);
		addComponentListener(this);
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		jLabel = new JLabel();
		jLabel.setText("Title");
		jLabel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
		jLabel1 = new JLabel();
		jLabel1.setText("Author data");
		jLabel2 = new JLabel();
		jLabel2.setText("Model creator");
		jLabel3 = new JLabel();
		jLabel3.setText("Source");
		jLabel4 = new JLabel();
		jLabel4.setText("Memo");

		var leftWeight = 0.0;
		var rightWeight = 1.0;

		var gbcBuilder = new GridBagConstraintsBuilder(2).setInsets(2, 5, 2, 5).setWeight(leftWeight, 0.0)
				.setAnchor(GridBagConstraints.CENTER);

		contentPane.add(jLabel, gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(getTitleTextField(), gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(jLabel1, gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(getEditorNameTextField(), gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(jLabel2, gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(getOriginalAuthorTextField(), gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(jLabel3, gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(getOriginTextField(), gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(jLabel4, gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 1.0).setFill(GridBagConstraints.BOTH);
		contentPane.add(getMemoTextArea(), gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0).setFill(GridBagConstraints.NONE);
		contentPane.add(getOKButton(), gbcBuilder.getLineField());
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
