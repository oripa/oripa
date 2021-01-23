package oripa.util.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.view.main.MainFrame;

/**
 * remove gridBagConstraint management from UI Code by providing simple
 * functions to return GridBagConstraints of the same layout definitions
 *
 * @author lifesbest23
 *
 */
public class GridBagConstraintsBuilder {
	private int gridX, gridY;
	private final int gridWidth;
	private int anchor;

	private double weightX, weightY;
	private int fill;
	private Insets insets;

	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	/**
	 *
	 * Constructor
	 *
	 * @param gridWidth
	 *            the maximum width of the grid
	 */
	public GridBagConstraintsBuilder(final int gridWidth) {
		gridX = 0;
		gridY = 0;

		anchor = GridBagConstraints.LINE_START;
		weightX = 0.5;
		weightY = 0.5;
		insets = new Insets(1, 1, 1, 1);
		fill = GridBagConstraints.HORIZONTAL;

		this.gridWidth = gridWidth;
	}

	/**
	 * the empty space in pixels around the component
	 *
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 * @return changed instance of the Builder
	 */
	public GridBagConstraintsBuilder setInsets(final int top, final int left, final int bottom,
			final int right) {
		insets = new Insets(top, left, bottom, right);
		return this;

	}

	/**
	 * set the component fill expansion direction
	 *
	 * @param fill
	 *            uses {@link java.awt.GridBagConstraints} constants
	 * @return changed instance of the Builder
	 */
	public GridBagConstraintsBuilder setFill(final int fill) {
		this.fill = fill;
		return this;
	}

	/**
	 * set the relative spacing in either row or column
	 *
	 * @param weightX
	 *            value between 0.0 and 1.0
	 * @param weightY
	 *            value between 0.0 and 1.0
	 * @return changed instance of the Builder
	 */
	public GridBagConstraintsBuilder setWeight(final double weightX, final double weightY) {
		this.weightX = weightX;
		this.weightY = weightY;
		return this;
	}

	/**
	 * set the anchor direction of components
	 *
	 * @param anchor
	 *            uses {@link java.awt.GridBagConstraints} constants
	 * @return changed instance of the Builder
	 */
	public GridBagConstraintsBuilder setAnchor(final int anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * fill the entire current line
	 *
	 * @apiNote ignores previously added cells in this line
	 * @see {@link oripa.util.gui.GridBagConstraintsBuilder.fillLineField}
	 *
	 * @return constraint filling the entire line
	 */
	public GridBagConstraints getLineField() {
		gridX = 0;
		return getField(gridX, gridY++, gridWidth);
	}

	/**
	 * only one field
	 *
	 * @apiNote automatically wraps line
	 *
	 * @return constraint for the next field
	 */
	public GridBagConstraints getNextField() {
		var f = getField(gridX++, gridY, 1);
		if (gridX == gridWidth) {
			gridX = 0;
			gridY++;
		}
		return f;
	}

	/**
	 * fill the line until the end
	 *
	 * @return constraint until the end of the current line
	 */
	public GridBagConstraints fillLineField() {
		var f = getField(gridX, gridY++, gridWidth - gridX);
		gridX = 0;
		return f;
	}

	/**
	 * Supposedly fills the next {@code width} fields
	 *
	 * @param width
	 *            amount of fields to fill
	 * @return constraint filling the right amount of fields
	 *
	 *         TODO: did not work so far for some reason.
	 */
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