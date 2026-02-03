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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Simple color chooser for RGB.
 *
 * @author OUCHI Koji
 *
 */
public class ColorRGBPanel extends JPanel {
    private final JSpinner red = new JSpinner();
    private final JSpinner green = new JSpinner();
    private final JSpinner blue = new JSpinner();

    private final JPanel pallete = new JPanel();

    private final List<ChangeListener> listeners = new ArrayList<>();

    private final TitledBorderFactory titledBorderFactory = new TitledBorderFactory();

    public ColorRGBPanel(final JComponent parent, final Color initialColor, final String title) {
        build(parent, initialColor, title);
    }

    public Color getColor() {
        return new Color((Integer) red.getValue(), (Integer) green.getValue(), (Integer) blue.getValue());
    }

    public void setColor(final Color color) {
        red.setValue(color.getRed());
        green.setValue(color.getGreen());
        blue.setValue(color.getBlue());

        pallete.setBackground(color);
    }

    private class ColorSpinnerChangeListener implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent e) {
            pallete.setBackground(getColor());

            listeners.forEach(l -> l.stateChanged(e));
        }
    }

    private void build(final JComponent parent, final Color initialColor, final String title) {

        setLayout(new GridBagLayout());
        setBorder(titledBorderFactory.createTitledBorder(parent, title));

        var gbBuilder = new GridBagConstraintsBuilder(2).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(0.5, 1.0);

        var listener = new ColorSpinnerChangeListener();
        red.addChangeListener(listener);
        green.addChangeListener(listener);
        blue.addChangeListener(listener);

        pallete.setBackground(initialColor);

        setColorSpinnerModel(red, initialColor.getRed());
        setColorSpinnerModel(green, initialColor.getGreen());
        setColorSpinnerModel(blue, initialColor.getBlue());

        gbBuilder.setFill(GridBagConstraints.BOTH);
        add(pallete, gbBuilder.getNextField());

        gbBuilder.setFill(GridBagConstraints.HORIZONTAL);
        gbBuilder.setAnchor(GridBagConstraints.EAST);
        add(createRGBPanel(), gbBuilder.getNextField());
    }

    private JPanel createRGBPanel() {

        var rgbPanel = new JPanel();

        rgbPanel.setLayout(new GridBagLayout());

        var gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.EAST)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 1.0);

        rgbPanel.add(createTitledColorSpinner(red, "R"), gbBuilder.getNextField());
        rgbPanel.add(createTitledColorSpinner(green, "G"), gbBuilder.getNextField());
        rgbPanel.add(createTitledColorSpinner(blue, "B"), gbBuilder.getNextField());

        return rgbPanel;
    }

    private JPanel createTitledColorSpinner(final JSpinner spinner, final String title) {
        var panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        var gbBuilder = new GridBagConstraintsBuilder(2).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(0.5, 0.5);

        panel.add(new JLabel(title, SwingConstants.RIGHT), gbBuilder.getNextField());

        gbBuilder.setFill(GridBagConstraints.EAST);
        panel.add(spinner, gbBuilder.getNextField());

        return panel;
    }

    private void setColorSpinnerModel(final JSpinner spinner, final int initialValue) {
        spinner.setModel(new SpinnerNumberModel(initialValue, 0, 255, 1));
    }

    public void addChangeListener(final ChangeListener listener) {
        listeners.add(listener);
    }
}
