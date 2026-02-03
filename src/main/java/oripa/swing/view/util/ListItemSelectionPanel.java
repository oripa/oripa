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
package oripa.swing.view.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.stream.Stream;

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
public class ListItemSelectionPanel extends JPanel {

    private final JLabel titleLabel = new JLabel();

    private final JButton nextButton;
    private final JButton prevButton;
    private final JLabel selectedItemIndexLabel = new JLabel();

    private int itemCount = 0;
    private int selectionIndex = -1;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    public final static String INDEX = "INDEX";

    public ListItemSelectionPanel(final String title, final ResourceHolder resourceHolder) {

        nextButton = new JButton(
                resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.NEXT_MODEL_ID));
        prevButton = new JButton(
                resourceHolder.getString(ResourceKey.LABEL, StringID.ModelUI.PREV_MODEL_ID));

        titleLabel.setText(title);
        build();
    }

    /**
     * calls {@link #selectItem(int)} with the parameter = 0 if the
     * {@code itemCount} is larger than zero.
     *
     * @param itemCount
     */
    public void setItemCount(final int itemCount) {
        this.itemCount = itemCount;
        if (itemCount > 0) {
            selectItem(0);
        } else {
            selectionIndex = -1;
            updateSelectionIndexLabel();
        }
    }

    /**
     * Possible property names:
     * <ul>
     * <li>{@link #INDEX}</li>
     * </ul>
     */
    @Override
    public void addPropertyChangeListener(final String propertyName,
            final PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListeners(final String propertyName) {
        Stream.of(support.getPropertyChangeListeners(propertyName))
                .forEach(listener -> support.removePropertyChangeListener(listener));
    }

    private void build() {
        add(titleLabel);
        add(prevButton);
        add(selectedItemIndexLabel);
        add(nextButton);

        prevButton.addActionListener(e -> {
            if (selectionIndex <= 0) {
                return;
            }
            selectItem(selectionIndex - 1);
        });

        nextButton.addActionListener(e -> {
            if (selectionIndex == itemCount - 1) {
                return;
            }
            selectItem(selectionIndex + 1);
        });
    }

    public void selectItem(final int index) {
        setSelectionIndex(index);
        updateSelectionIndexLabel();
    }

    private void updateSelectionIndexLabel() {
        selectedItemIndexLabel.setText((selectionIndex + 1) + "/" + itemCount);
    };

    private void setSelectionIndex(final int index) {
        if (index < 0 || index >= itemCount) {
            throw new IndexOutOfBoundsException(index);
        }

        int old = selectionIndex;
        selectionIndex = index;
        support.firePropertyChange(INDEX, old, index);
    }
}
