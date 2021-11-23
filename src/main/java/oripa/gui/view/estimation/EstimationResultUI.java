/**
 * ORIPA - Origami Pattern Editor Copyright (C) 2005-2009 Jun Mitani
 * http://mitani.cs.tsukuba.ac.jp/
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.view.estimation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.estimation.EstimationResultFileAccess;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import oripa.gui.view.util.ColorRGBPanel;
import oripa.gui.view.util.Dialogs;
import oripa.gui.view.util.GridBagConstraintsBuilder;
import oripa.gui.view.util.TitledBorderFactory;
import oripa.persistence.entity.FoldedModelDAO;
import oripa.persistence.entity.FoldedModelFilterSelector;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class EstimationResultUI extends JPanel {
	private static final Logger logger = LoggerFactory.getLogger(EstimationResultUI.class);

	private static final long serialVersionUID = 1L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	private final String indexLabelString = resources.getString(ResourceKey.LABEL,
			StringID.EstimationResultUI.INDEX_ID);

	// JPanel drawing the model estimation
	private FoldedModelScreen screen;

	// setup components used
	private final JLabel indexLabel = new JLabel();

	private final JButton nextAnswerButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.NEXT_RESULT_ID));
	private final JButton prevAnswerButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.PREV_RESULT_ID));

	private final JCheckBox orderCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.ORDER_FLIP_ID));
	private final JCheckBox shadowCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.SHADOW_ID));
	private final JCheckBox useColorCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.USE_COLOR_ID));
	private final JCheckBox edgeCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.EDGE_ID));
	private final JCheckBox fillFaceCheckBox = new JCheckBox(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.FILL_FACE_ID));

	private final ColorRGBPanel frontColorRGBPanel = new ColorRGBPanel(this, DefaultColors.FRONT,
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.FACE_COLOR_FRONT_ID));
	private final ColorRGBPanel backColorRGBPanel = new ColorRGBPanel(this, DefaultColors.BACK,
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.FACE_COLOR_BACK_ID));

	private final JButton exportButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.EXPORT_ID));

	private final TitledBorderFactory titledBorderFactory = new TitledBorderFactory();

	private String lastFilePath = null;

	private FoldedModel foldedModel;
	private OverlapRelationList overlapRelationList = null;

	/**
	 * This is the default constructor
	 */
	public EstimationResultUI() {
		super();
		try {
			initialize();
		} catch (Exception e) {
			Dialogs.showErrorDialog(this, "initializatiton error (folded model UI)", e);
		}
	}

	public void setScreen(final FoldedModelScreen s) {
		screen = s;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(216, 200));

		var gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.FIRST_LINE_START)
				.setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0.0);

		add(createAnswerShiftPanel(), gbBuilder.getLineField());
		add(createConfigPanel(), gbBuilder.getLineField());
		add(createColorPanel(), gbBuilder.getLineField());

		gbBuilder.setWeight(1, 1).setFill(GridBagConstraints.BOTH);
		add(new JPanel(), gbBuilder.getLineField());

		gbBuilder.setWeight(1, 0.0).setFill(GridBagConstraints.HORIZONTAL)
				.setAnchor(GridBagConstraints.LAST_LINE_START);
		add(exportButton, gbBuilder.getLineField());

		updateIndexLabel();
		initialCheckBoxSetting();
		addActionListenersToComponents();
	}

	/**
	 * register listeners with all used components
	 */
	private void addActionListenersToComponents() {
		nextAnswerButton.addActionListener(e -> {
			overlapRelationList.setNextIndex();
			screen.redrawOrigami();
			updateIndexLabel();
		});
		prevAnswerButton.addActionListener(e -> {
			overlapRelationList.setPrevIndex();
			screen.redrawOrigami();
			updateIndexLabel();
		});

		orderCheckBox.addItemListener(e -> {
			screen.flipFaces(e.getStateChange() == ItemEvent.SELECTED);
		});
		shadowCheckBox.addItemListener(e -> {
			screen.shadeFaces(e.getStateChange() == ItemEvent.SELECTED);
		});
		useColorCheckBox.addItemListener(e -> {
			screen.setUseColor(e.getStateChange() == ItemEvent.SELECTED);
		});
		edgeCheckBox.addItemListener(e -> {
			screen.drawEdge(e.getStateChange() == ItemEvent.SELECTED);
		});
		fillFaceCheckBox.addItemListener(e -> {
			screen.setFillFace(e.getStateChange() == ItemEvent.SELECTED);
		});

		ChangeListener colorChangeListener = (e) -> {
			screen.setColors(
					frontColorRGBPanel.getColor(),
					backColorRGBPanel.getColor());
			screen.redrawOrigami();
		};
		frontColorRGBPanel.addChangeListener(colorChangeListener);
		backColorRGBPanel.addChangeListener(colorChangeListener);

		exportButton.addActionListener(e -> export());
	}

	private void initialCheckBoxSetting() {
		useColorCheckBox.setSelected(true);
		edgeCheckBox.setSelected(true);
		fillFaceCheckBox.setSelected(true);
	}

	/**
	 * @param overlapRelationList
	 */
	public void setModel(final FoldedModel foldedModel) {
		this.foldedModel = foldedModel;
		this.overlapRelationList = foldedModel.getOverlapRelationList();
	}

	private JPanel createAnswerShiftPanel() {
		var answerShiftPanel = new JPanel();

		answerShiftPanel.setLayout(new GridBagLayout());
		answerShiftPanel.setBorder(titledBorderFactory.createTitledBorderFrame(this,
				resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.ANSWERS_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(2).setAnchor(GridBagConstraints.CENTER)
				.setWeight(0.5, 1.0);

		answerShiftPanel.add(prevAnswerButton, gbBuilder.getNextField());
		answerShiftPanel.add(nextAnswerButton, gbBuilder.getNextField());

		answerShiftPanel.add(indexLabel, gbBuilder.getLineField());

		return answerShiftPanel;
	}

	private JPanel createConfigPanel() {
		var configPanel = new JPanel();

		configPanel.setLayout(new GridBagLayout());
		configPanel.setBorder(titledBorderFactory.createTitledBorderFrame(this,
				resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.DRAWING_CONFIG_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(2).setAnchor(GridBagConstraints.CENTER)
				.setWeight(0.5, 0.5);

		configPanel.add(orderCheckBox, gbBuilder.getNextField());
		configPanel.add(shadowCheckBox, gbBuilder.getNextField());
		configPanel.add(useColorCheckBox, gbBuilder.getNextField());
		configPanel.add(edgeCheckBox, gbBuilder.getNextField());
		configPanel.add(fillFaceCheckBox, gbBuilder.getNextField());

		return configPanel;
	}

	private JPanel createColorPanel() {
		var colorPanel = new JPanel();

		colorPanel.setLayout(new GridBagLayout());
		colorPanel.setBorder(titledBorderFactory.createTitledBorderFrame(this,
				resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.FACE_COLOR_PANEL_ID)));

		var gbBuilder = new GridBagConstraintsBuilder(1).setAnchor(GridBagConstraints.CENTER)
				.setWeight(1.0, 0.0);

		colorPanel.add(frontColorRGBPanel, gbBuilder.getNextField());

		colorPanel.add(backColorRGBPanel, gbBuilder.getNextField());

		return colorPanel;
	}

	/**
	 * update the label showing which estimation is currently shown on screen
	 */
	private void updateIndexLabel() {

		if (overlapRelationList == null) {
			return;
		}

		indexLabel.setText("Folded model ["
				+ (overlapRelationList.getCurrentIndex() + 1) + "/"
				+ overlapRelationList.getCount() + "]");

	}

	/**
	 * open export dialog for current folded estimation
	 */
	private void export() {
		try {
			var filterSelector = new FoldedModelFilterSelector(screen.isFaceOrderFlipped());
			final FoldedModelDAO dao = new FoldedModelDAO(filterSelector);
			EstimationResultFileAccess fileAccess = new EstimationResultFileAccess(dao);
			lastFilePath = fileAccess.saveFile(foldedModel, lastFilePath, this,
					filterSelector.getSavables());
		} catch (Exception ex) {
			logger.error("error: ", ex);
			Dialogs.showErrorDialog(this, resources.getString(
					ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), ex);
		}
	}
}