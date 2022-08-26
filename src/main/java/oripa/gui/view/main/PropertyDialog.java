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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oripa.gui.view.util.GridBagConstraintsBuilder;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * Provides JDialog with input fields for {@link oripa.doc.Property} values.
 *
 */
public class PropertyDialog extends JDialog implements
		PropertyDialogView, ComponentListener {

	private static final long serialVersionUID = -5864700666603644379L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final JTextField titleTextField = new JTextField();
	private final JTextField editorNameTextField = new JTextField();
	private final JTextField originalAuthorTextField = new JTextField();
	private final JTextField referenceTextField = new JTextField();
	private final JTextArea memoTextArea = new JTextArea();

	private final List<Runnable> okListeners = new ArrayList<>();

	private final JButton okButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_OK_ID));

	public PropertyDialog(final JFrame parent) {
		super(parent);

		build();

		okButton.addActionListener(e -> {
			okListeners.forEach(listener -> listener.run());
			dispose();
		});
	}

	private void build() {
		setTitle(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_TITLE_ID));
		setSize(420, 278);
		addComponentListener(this);

		// setup ScrollPane and word wrapping in memoTextArea
		memoTextArea.setLineWrap(true);
		memoTextArea.setWrapStyleWord(true);
		JScrollPane memoScrollPane = new JScrollPane(memoTextArea);
		memoScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		memoScrollPane.setPreferredSize(new Dimension(250, 250));

		// Add Components to contentPane
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// left column should not take more space than needed
		// remaining space given to input fields
		var leftWeight = 0.0;
		var rightWeight = 1.0;
		var gbcBuilder = new GridBagConstraintsBuilder(2).setInsets(2, 5, 2, 5).setWeight(leftWeight, 0.0)
				.setAnchor(GridBagConstraints.CENTER);

		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_MODEL_TITLE_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(titleTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_AUTHOR_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(editorNameTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_CREATOR_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(originalAuthorTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_SOURCE_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 0.0);
		contentPane.add(referenceTextField, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0);
		contentPane.add(new JLabel(resources.getString(ResourceKey.LABEL, StringID.Main.PROP_DIALOG_MEMO_ID)),
				gbcBuilder.getNextField());
		gbcBuilder.setWeight(rightWeight, 1.0).setFill(GridBagConstraints.BOTH);
		contentPane.add(memoScrollPane, gbcBuilder.getNextField());

		gbcBuilder.setWeight(leftWeight, 0.0).setFill(GridBagConstraints.NONE);
		contentPane.add(okButton, gbcBuilder.getLineField());

	}

	@Override
	public void addOKButtonListener(final Runnable listener) {
		okButton.addActionListener(e -> listener.run());
	}

	@Override
	public String getModelTitle() {
		return titleTextField.getText();
	}

	@Override
	public void setModelTitle(final String title) {
		titleTextField.setText(title);
	}

	@Override
	public String getEditorName() {
		return editorNameTextField.getText();
	}

	@Override
	public void setEditorName(final String editorName) {
		editorNameTextField.setText(editorName);
	}

	@Override
	public String getOriginalAuthor() {
		return originalAuthorTextField.getText();
	}

	@Override
	public void setOriginalAutor(final String originalAuthor) {
		originalAuthorTextField.setText(originalAuthor);
	}

	@Override
	public String getReference() {
		return referenceTextField.getText();
	}

	@Override
	public void setReference(final String reference) {
		referenceTextField.setText(reference);
	}

	@Override
	public String getMemo() {
		return memoTextArea.getText();
	}

	@Override
	public void setMemo(final String memo) {
		memoTextArea.setText(memo);
	}

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

	@Override
	public void setViewVisible(final boolean visible) {
		setVisible(visible);
	}
}
