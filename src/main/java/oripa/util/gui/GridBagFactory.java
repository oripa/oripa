package oripa.util.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.view.main.MainFrame;

public class GridBagFactory {
	private int gridX, gridY;
	private final int gridWidth;
	private int anchor;

	private double weightX, weightY;
	private int fill;
	private Insets insets;

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	public GridBagFactory(final int gridWidth) {
		gridX = 0;
		gridY = 0;

		anchor = GridBagConstraints.LINE_START;
		weightX = 0.5;
		weightY = 0.5;
		insets = new Insets(1, 1, 1, 1);
		fill = GridBagConstraints.HORIZONTAL;

		this.gridWidth = gridWidth;
	}

	public GridBagFactory setInsets(final int top, final int left, final int bottom,
			final int right) {
		insets = new Insets(top, left, bottom, right);
		return this;

	}

	public GridBagFactory setFill(final int fill) {
		this.fill = fill;
		return this;
	}

	public GridBagFactory setWeight(final double weightX, final double weightY) {
		this.weightX = weightX;
		this.weightY = weightY;
		return this;
	}

	public GridBagFactory setAnchor(final int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GridBagConstraints getLineField() {
		gridX = 0;
		return getField(gridX, gridY++, gridWidth);
	}

	public GridBagConstraints getNextField() {
		var f = getField(gridX++, gridY, 1);
		if (gridX == gridWidth) {
			gridX = 0;
			gridY++;
		}
		return f;
	}

	public GridBagConstraints fillLineField() {
		var f = getField(gridX, gridY++, gridWidth - gridX);
		gridX = 0;
		return f;
	}

	public GridBagConstraints fillFieldCount(int width) {
		if (width + gridX > gridWidth) {
			width = gridWidth - gridX;
			logger.debug("fill Field Count width too big");
		}
		logger.debug("fill Field Count: width {} x  {} y {}", width, gridX, gridY);
		var f = getField(gridX, gridY, width);
		gridX += width;

		if (gridX == gridWidth) {
			gridX = 0;
			gridY++;
		}
		return f;
	}

	private GridBagConstraints getField(final int gridX, final int gridY,
			final int gridWidth) {
		var gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridx = gridX;
		gridBagConstraints.gridy = gridY;
		gridBagConstraints.gridwidth = gridWidth;
		gridBagConstraints.insets = insets;

		gridBagConstraints.weightx = weightX;
		gridBagConstraints.weighty = weightY;
		gridBagConstraints.fill = fill;
		gridBagConstraints.anchor = anchor;

		return gridBagConstraints;
	}
}