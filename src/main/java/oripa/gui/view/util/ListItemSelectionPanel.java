/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.gui.view.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class ListItemSelectionPanel<Item> extends JPanel {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private final JLabel titleLabel = new JLabel();

	private final JButton nextButton = new JButton(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.NEXT_MODEL_ID));
	private final JButton prevButton = new JButton(
			resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.PREV_MODEL_ID));
	private final JLabel selectedItemIndexLabel = new JLabel();

	private List<Item> items;
	private int selectionIndex = -1;
	private Item item = null;

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	public final static String ITEM = "ITEM";
	public final static String INDEX = "INDEX";

	public ListItemSelectionPanel(final String title) {
		titleLabel.setText(title);
		build();
	}

	/**
	 * calls {@link #selectItem(int)} with the parameter = 0 if the
	 * {@code items} is not empty.
	 *
	 * @param items
	 */
	public void setItems(final List<Item> items) {
		this.items = items;
		if (items.isEmpty()) {
			item = null;
		} else {
			selectItem(0);
		}
	}

	@Override
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	private void build() {
		add(titleLabel);
		add(prevButton);
		add(selectedItemIndexLabel);
		add(nextButton);

		prevButton.addActionListener(e -> {
			if (selectionIndex == 0) {
				return;
			}
			selectItem(selectionIndex - 1);
		});

		nextButton.addActionListener(e -> {
			if (selectionIndex == items.size() - 1) {
				return;
			}
			selectItem(selectionIndex + 1);
		});
	}

	public void selectItem(final int index) {
		setSelectionIndex(index);
		setItem(items.get(index));
		updateSelectionIndexLabel();
	}

	private void updateSelectionIndexLabel() {
		selectedItemIndexLabel.setText((selectionIndex + 1) + "/" + items.size());
	};

	private void setSelectionIndex(final int index) {
		if (index < 0 || index >= items.size()) {
			throw new IndexOutOfBoundsException(index);
		}

		int old = selectionIndex;
		selectionIndex = index;
		support.firePropertyChange(INDEX, old, index);
	}

	private void setItem(final Item item) {
		var old = this.item;
		this.item = item;
		support.firePropertyChange(ITEM, old, item);
	}

}
