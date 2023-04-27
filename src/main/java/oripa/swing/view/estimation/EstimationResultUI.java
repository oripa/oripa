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
package oripa.swing.view.estimation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.subface.SubFace;
import oripa.gui.view.View;
import oripa.gui.view.estimation.DefaultColors;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.swing.view.util.ColorRGBPanel;
import oripa.swing.view.util.Dialogs;
import oripa.swing.view.util.GridBagConstraintsBuilder;
import oripa.swing.view.util.ListItemSelectionPanel;
import oripa.swing.view.util.TitledBorderFactory;
import oripa.util.Pair;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;

public class EstimationResultUI extends JPanel implements EstimationResultUIView {
	private static final Logger logger = LoggerFactory.getLogger(EstimationResultUI.class);

	private static final long serialVersionUID = 1L;

	private final ResourceHolder resources = ResourceHolder.getInstance();

	// JPanel drawing the model estimation
	private FoldedModelScreen screen;

	// setup components used
	private final ListItemSelectionPanel answerSelectionPanel = new ListItemSelectionPanel("");

	private final JCheckBox filterEnabledCheckBox = new JCheckBox("Use subface filter");
	private final JComboBox<Integer> subfaceIndexCombo = new JComboBox<>();
	private final JComboBox<Integer> suborderIndexCombo = new JComboBox<Integer>();
	private final JCheckBox subfaceVisibleCheckBox = new JCheckBox("Show subface");

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

	private final JButton saveColorsButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.SAVE_COLORS_ID));

	private final JButton saveSVGConfigButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.SAVE_SVG_CONFIG_ID));

	private final JButton exportButton = new JButton(
			resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.EXPORT_ID));

	private final JLabel svgFaceStrokeWidthLabel = new JLabel("face stroke-width:");
	private final JTextField svgFaceStrokeWidthField = new JTextField();
	private final JLabel svgPrecreaseStrokeWidthLabel = new JLabel("crease stroke-width:");
	private final JTextField svgPrecreaseStrokeWidthField = new JTextField();

	private final TitledBorderFactory titledBorderFactory = new TitledBorderFactory();

	private FoldedModel foldedModel;
	private OverlapRelation overlapRelation;

	private List<OverlapRelation> filteredOverlapRelations;

	/**
	 * < index of selected subface, index of selected suborder >
	 */
	private final Map<Integer, Integer> filterSelectionMap = new HashMap<>();

	/**
	 * < index of subface, map< index of order, overlap relation indices > >
	 */
	private Map<Integer, Map<Integer, Set<Integer>>> subfaceToOverlapRelationIndices;

	private BiConsumer<Color, Color> saveColorsListener;

	/**
	 * This is the default constructor
	 */
	public EstimationResultUI() {
		super();
		try {
			initialize();
		} catch (Exception e) {
			Dialogs.showErrorDialog(this,
					resources.getString(ResourceKey.ERROR, StringID.Error.ER_UI_INIT_FAILED_ID), e);
		}
	}

	/**
	 * set Screen displaying the folded Model Estimation
	 *
	 * @param s
	 *            {@code FoldedModelScreen} to be used
	 */
	public void setScreen(final FoldedModelScreen s) {
		screen = s;
	}

	/* (non Javadoc)
	 * @see oripa.gui.view.estimation.EstimationResultUIView#setModel(oripa.domain.fold.FoldedModel)
	 */
	@Override
	public void setModel(final FoldedModel foldedModel) {
		this.foldedModel = foldedModel;

		setOverlapRelations(foldedModel.getOverlapRelations());
	}

	private void initializeFilterComponents() {
		var worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				subfaceToOverlapRelationIndices = createSubfaceToOverlapRelationIndices(foldedModel);
				subfaceToOverlapRelationIndices.forEach((s, indicesMap) -> {
					filterSelectionMap.put(s, 0);
				});
				return null;
			}
		};

		worker.execute();

		try {
			worker.get();
			prepareSubfaceIndexCombo();
			prepareSuborderIndexCombo(0);

			subfaceIndexCombo.setSelectedIndex(0);
			suborderIndexCombo.setSelectedIndex(0);
		} catch (Exception e) {
		}

	}

	private void prepareSubfaceIndexCombo() {
		subfaceIndexCombo.removeAllItems();
		subfaceToOverlapRelationIndices.forEach((s, relationIndicesMap) -> {
			if (relationIndicesMap.size() > 1) {
				subfaceIndexCombo.addItem(s);
			}
		});
	}

	private void prepareSuborderIndexCombo(final int subfaceIndex) {
		suborderIndexCombo.removeAllItems();
		subfaceToOverlapRelationIndices.get(subfaceIndex)
				.forEach((order, indices) -> suborderIndexCombo.addItem(order));
	}

	private void setSubfaceToScreen(final SubFace subface) {
		if (subface == null) {
			screen.setSelectedSubface(null);
			return;
		}

		screen.setSelectedSubface(subfaceVisibleCheckBox.isSelected() ? subface.getOutline() : null);
	}

	private void setOverlapRelations(final List<OverlapRelation> overlapRelations) {
		filteredOverlapRelations = overlapRelations;
		answerSelectionPanel.setItemCount(overlapRelations.size());
	}

	private void selectOverlapRelation(final int index) {
		overlapRelation = filteredOverlapRelations.get(index);
		screen.setOverlapRelation(overlapRelation);
	}

	@Override
	public FoldedModel getModel() {
		return foldedModel;
	}

	private class OrderValue extends Pair<List<Integer>, Byte> {

		public OrderValue(final int i, final int j, final byte value) {
			super(List.of(i, j), value);
		}
	}

	private Map<Integer, Map<Integer, Set<Integer>>> createSubfaceToOverlapRelationIndices(
			final FoldedModel foldedModel) {

		var watch = new StopWatch(true);
		logger.debug("createSubfaceToOverlapRelationIndices() start");

		var map = new ConcurrentHashMap<Integer, Map<Integer, Set<Integer>>>();
		var orders = new ConcurrentHashMap<Integer, Map<Set<OrderValue>, Set<Integer>>>();

		var subfaces = foldedModel.getSubfaces();
		var overlapRelations = foldedModel.getOverlapRelations();

		// initialize
		for (int s = 0; s < subfaces.size(); s++) {
			orders.put(s, new ConcurrentHashMap<>());
		}

		IntStream.range(0, overlapRelations.size()).parallel().forEach(k -> {
			var overlapRelation = overlapRelations.get(k);

			IntStream.range(0, subfaces.size()).forEach(s -> {
				var subface = subfaces.get(s);
				Set<OrderValue> order = CollectionUtil.newConcurrentHashSet();

				for (int i = 0; i < subface.getParentFaceCount(); i++) {
					var face_i = subface.getParentFace(i);
					for (int j = i + 1; j < subface.getParentFaceCount(); j++) {
						var face_j = subface.getParentFace(j);

						var smallerIndex = Math.min(face_i.getFaceID(), face_j.getFaceID());
						var largerIndex = Math.max(face_i.getFaceID(), face_j.getFaceID());
						var relation = overlapRelation.get(smallerIndex, largerIndex);

						var value = new OrderValue(smallerIndex, largerIndex, relation);

						order.add(value);
					}
				}

				var indices = orders.get(s).get(order);
				if (indices == null) {
					indices = CollectionUtil.newConcurrentHashSet();
					orders.get(s).put(order, indices);
				}
				indices.add(k);
			});
		});

		IntStream.range(0, subfaces.size()).parallel().forEach(s -> {
			var orderToOverlapRelationIndices = orders.get(s);
			map.put(s, Collections.synchronizedMap(new HashMap<>()));

			int index = 0;
			for (var order : orderToOverlapRelationIndices.keySet()) {
				var indices = orderToOverlapRelationIndices.get(order);
				map.get(s).put(index++, indices);
			}
		});

		logger.debug("createSubfaceToOverlapRelationIndices() end: {}[ms]", watch.getMilliSec());

		return map;
	}

	private List<OverlapRelation> filter(final Integer subfaceIndex, final Integer suborderIndex) {

		return subfaceToOverlapRelationIndices.get(subfaceIndex).get(suborderIndex).stream()
				.map(k -> foldedModel.getOverlapRelations().get(k)).collect(Collectors.toList());
	}

	private void setFilterEnabled(final boolean enabled) {
		subfaceIndexCombo.setEnabled(enabled);
		suborderIndexCombo.setEnabled(enabled);
		subfaceVisibleCheckBox.setEnabled(enabled);

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

		add(createSVGConfigPanel(), gbBuilder.getLineField());

		gbBuilder.setWeight(1, 1).setFill(GridBagConstraints.BOTH);
		add(new JPanel(), gbBuilder.getLineField());

		gbBuilder.setWeight(1, 0.0).setFill(GridBagConstraints.HORIZONTAL)
				.setAnchor(GridBagConstraints.LAST_LINE_START);
		add(exportButton, gbBuilder.getLineField());

		initializeComponentSetting();
		addActionListenersToComponents();
	}

	/**
	 * register listeners with all used components
	 */
	private void addActionListenersToComponents() {
		answerSelectionPanel.addPropertyChangeListener(ListItemSelectionPanel.INDEX,
				e -> {
					selectOverlapRelation((int) e.getNewValue());
				});

		filterEnabledCheckBox.addActionListener(e -> {
			if (filterEnabledCheckBox.isSelected()) {
				initializeFilterComponents();

				setFilterEnabled(true);

				setOverlapRelations(filter(0, 0));
				selectOverlapRelation(0);

				var subface = foldedModel.getSubfaces().get(0);
				setSubfaceToScreen(subface);
			} else {
				setFilterEnabled(false);

				setOverlapRelations(foldedModel.getOverlapRelations());
				selectOverlapRelation(0);

				setSubfaceToScreen(null);
			}
		});

		subfaceIndexCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				var subfaceIndex = (Integer) e.getItem();
				var suborderIndex = filterSelectionMap.get(subfaceIndex);

				setOverlapRelations(filter(subfaceIndex, suborderIndex));
				selectOverlapRelation(0);

				var subface = foldedModel.getSubfaces().get(subfaceIndex);
				setSubfaceToScreen(subface);

				prepareSuborderIndexCombo(subfaceIndex);
				suborderIndexCombo.setSelectedItem(suborderIndex);
			}
		});

		suborderIndexCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				var subfaceIndex = (Integer) subfaceIndexCombo.getSelectedItem();
				var suborderIndex = (Integer) e.getItem();
				filterSelectionMap.put(subfaceIndex, suborderIndex);

				setOverlapRelations(filter(subfaceIndex, suborderIndex));
				selectOverlapRelation(0);
			}
		});

		subfaceVisibleCheckBox.addActionListener(e -> {
			var subfaceIndex = (Integer) subfaceIndexCombo.getSelectedItem();
			var subface = foldedModel.getSubfaces().get(subfaceIndex);

			setSubfaceToScreen(subface);
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

		ChangeListener colorRGBChangeListener = (e) -> {
			var frontColor = frontColorRGBPanel.getColor();
			var backColor = backColorRGBPanel.getColor();
			screen.setColors(frontColor, backColor);
			screen.redrawOrigami();
		};
		frontColorRGBPanel.addChangeListener(colorRGBChangeListener);
		backColorRGBPanel.addChangeListener(colorRGBChangeListener);

		saveColorsButton.addActionListener(
				e -> saveColorsListener.accept(frontColorRGBPanel.getColor(), backColorRGBPanel.getColor()));
	}

	private void initializeComponentSetting() {
		filterEnabledCheckBox.setSelected(false);
		setFilterEnabled(false);

		subfaceVisibleCheckBox.setSelected(true);

		useColorCheckBox.setSelected(true);
		edgeCheckBox.setSelected(true);
		fillFaceCheckBox.setSelected(true);
	}

	private JPanel createAnswerShiftPanel() {
		var answerShiftPanel = new JPanel();

		var gbBuilder = new GridBagConstraintsBuilder(1);

		answerShiftPanel.setLayout(new GridBagLayout());
		answerShiftPanel.setBorder(titledBorderFactory.createTitledBorderFrame(this,
				resources.getString(ResourceKey.LABEL, StringID.EstimationResultUI.ANSWERS_PANEL_ID)));

		answerShiftPanel.add(answerSelectionPanel, gbBuilder.getNextField());

		var filterPanel = new JPanel();
		filterPanel.add(subfaceIndexCombo);
		filterPanel.add(suborderIndexCombo);

		answerShiftPanel.add(filterEnabledCheckBox, gbBuilder.getNextField());
		answerShiftPanel.add(subfaceVisibleCheckBox, gbBuilder.getNextField());
		answerShiftPanel.add(filterPanel, gbBuilder.getNextField());

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

		colorPanel.add(saveColorsButton, gbBuilder.getNextField());
		return colorPanel;
	}

	private JPanel createSVGConfigPanel() {
		var svgPanel = new JPanel();

		svgPanel.setLayout(new GridBagLayout());
		svgPanel.setBorder(titledBorderFactory.createTitledBorderFrame(this, "SVG config"));

		var gbBuilder = new GridBagConstraintsBuilder(2).setAnchor(GridBagConstraints.CENTER)
				.setWeight(1.0, 0.0);

		svgPanel.add(svgFaceStrokeWidthLabel, gbBuilder.getNextField());
		svgPanel.add(svgFaceStrokeWidthField, gbBuilder.getNextField());

		svgPanel.add(svgPrecreaseStrokeWidthLabel, gbBuilder.getNextField());
		svgPanel.add(svgPrecreaseStrokeWidthField, gbBuilder.getNextField());

		svgPanel.add(saveSVGConfigButton, gbBuilder.getLineField());

		return svgPanel;
	}

	public void setColors(final Color front, final Color back) {
		logger.debug("Front color = {}", front);
		logger.debug("Back color = {}", back);
		frontColorRGBPanel.setColor(front == null ? DefaultColors.FRONT : front);
		backColorRGBPanel.setColor(back == null ? DefaultColors.BACK : back);
	}

	public void setSaveColorsListener(final BiConsumer<Color, Color> listener) {
		saveColorsListener = listener;
	}

	@Override
	public void addSaveSVGCofigButtonListener(final Runnable listener) {
		addButtonListener(saveSVGConfigButton, listener);
	}

	@Override
	public void addExportButtonListener(final Runnable listener) {
		addButtonListener(exportButton, listener);
	}

	private void addButtonListener(final AbstractButton button, final Runnable listener) {
		button.addActionListener(e -> listener.run());
	}

	@Override
	public OverlapRelation getOverlapRelation() {
		return overlapRelation;
	}

	@Override
	public int getOverlapRelationIndex() {
		return foldedModel.getOverlapRelations().indexOf(overlapRelation);
	}

	@Override
	public boolean isFaceOrderFlipped() {
		return screen.isFaceOrderFlipped();
	}

	@Override
	public double getSVGFaceStrokeWidth() {
		return Double.parseDouble(svgFaceStrokeWidthField.getText());
	}

	@Override
	public void setSVGFaceStrokeWidth(final double strokeWidth) {
		svgFaceStrokeWidthField.setText(Double.toString(strokeWidth));
	}

	@Override
	public double getSVGPrecreaseStrokeWidth() {
		return Double.parseDouble(svgPrecreaseStrokeWidthField.getText());
	}

	@Override
	public void setSVGPrecreaseStrokeWidth(final double strokeWidth) {
		svgPrecreaseStrokeWidthField.setText(Double.toString(strokeWidth));
	}

	@Override
	public void showExportErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resources.getString(
				ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID), e);
	}

	@Override
	public void showErrorMessage(final Exception e) {
		Dialogs.showErrorDialog(this, resources.getString(
				ResourceKey.ERROR, StringID.Error.DEFAULT_TITLE_ID), e);
	}

	@Override
	public View getTopLevelView() {
		return (View) SwingUtilities.getWindowAncestor(this);
	}
}