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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oripa.doc.Property;
import oripa.gui.view.util.GridBagConstraintsBuilder;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public abstract class AbstractPropertyDialog extends JDialog implements
		ComponentListener {

	private static final long serialVersionUID = -5864700666603644379L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final JTextField titleTextField = new JTextField();
	private final JTextField editorNameTextField = new JTextField();
	private final JTextField originalAuthorTextField = new JTextField();
	private final JTextField referenceTextField = new JTextField();
	private final JTextArea memoTextArea = new JTextArea();

	private final JButton okButton = new JButton(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_OK_ID));

	private final Property property;

	public AbstractPropertyDialog(final JFrame parent, final Property prop) {
		super(parent);

		this.property = prop;

		setTitle(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_TITLE_ID));
		setSize(420, 278);
		addComponentListener(this);

		// setup ScrollPane and word wrapping in memoTextArea
		memoTextArea.setLineWrap(true);
		memoTextArea.setWrapStyleWord(true);
		JScrollPane memoScrollPane = new JScrollPane(memoTextArea);
		memoScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		memoScrollPane.setPreferredSize(new Dimension(250, 250));

		// add action listener to OK button
		okButton.addActionListener(e -> {
			onClickOKButton(getEditedProperty());
			dispose();
		});

		// Add Components to contentPane
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// left column should not take more space than needed
		// remaining space given to input fields
		var leftWeight = 0.0;
		var rightWeight = 1.0;
		var gbcBuilder = new GridBagConstraintsBuilder(2).setInsets(2, 5, 2, 5).setWeight(leftWeight, 0.0)
				.setAnchor(GridBagConstraints.CENTER);

		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_MODEL_TITLE_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(titleTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_AUTHOR_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(editorNameTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_CREATOR_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(originalAuthorTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_SOURCE_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(referenceTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIAL_MEMO_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 1.0).setFill(GridBagConstraints.BOTH);
		contentPane.add(memoScrollPane, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0).setFill(GridBagConstraints.NONE);
		contentPane.add(okButton, gbcBuilder.getLineField());
	}

	public void setValue() {
		titleTextField.setText(property.getTitle());
		editorNameTextField.setText(property.getEditorName());
		originalAuthorTextField.setText(property.getOriginalAuthorName());
		referenceTextField.setText(property.getReference());
		memoTextArea.setText(property.getMemo());
	}

	private Property getEditedProperty() {
		Property prop = new Property(property.getDataFilePath());
		prop.setTitle(titleTextField.getText());
		prop.setEditorName(editorNameTextField.getText());
		prop.setOriginalAuthorName(originalAuthorTextField.getText());
		prop.setReference(referenceTextField.getText());
		prop.setMemo(memoTextArea.getText());

		return prop;
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
		okButton.requestFocusInWindow();
	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {
	}
}
