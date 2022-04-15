package oripa.gui.viewsetting.main.uipanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.TypeForChange;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.domain.paint.linetype.TypeForChangeGettable;
import oripa.gui.presenter.creasepattern.EditMode;

public class UIPanelSetting implements TypeForChangeGettable {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelSetting.class);

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private boolean lineSelectionPanelVisible = false;
	public static final String LINE_SELECTION_PANEL_VISIBLE = "line-selection-panel-visible";

	private boolean lineInputPanelVisible = true;
	public static final String LINE_INPUT_PANEL_VISIBLE = "line-input-panel-visible";

	private boolean byValuePanelVisible = false;
	public static final String BY_VALUE_PANEL_VISIBLE = "by-value panel visible";

	private boolean alterLineTypePanelVisible = true;
	public static final String ALTER_LINE_TYPE_PANEL_VISIBLE = "alter-line-type panel visible";

	private boolean angleStepPanelVisible = false;
	public static final String ANGLE_STEP_PANEL_VISIBLE = "angle step panel visible";

	private EditMode selectedMode = EditMode.NONE;
	public static final String SELECTED_MODE = "selected mode";

	private TypeForChange typeFrom = TypeForChange.EMPTY;
	public static final String TYPE_FROM = "line type of 'from' box";

	private TypeForChange typeTo = TypeForChange.EMPTY;
	public static final String TYPE_TO = "line type of 'to' box";

	private final ValueSetting valueSetting = new ValueSetting();

	public void addPropertyChangeListener(
			final String propertyName, final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public TypeForChange getTypeFrom() {
		return typeFrom;
	}

	public void setTypeFrom(final TypeForChange typeFrom) {
		this.typeFrom = typeFrom;
	}

	@Override
	public TypeForChange getTypeTo() {
		return typeTo;
	}

	public void setTypeTo(final TypeForChange typeTo) {
		this.typeTo = typeTo;
	}

	public boolean isByValuePanelVisible() {
		return byValuePanelVisible;
	}

	public boolean isAlterLineTypePanelVisible() {
		return alterLineTypePanelVisible;
	}

	public boolean isSelectLinePanelVisible() {
		return lineSelectionPanelVisible;
	}

	public boolean isLineInputPanelVisible() {
		return lineInputPanelVisible;
	}

	public boolean isAngleStepPanelVisible() {
		return angleStepPanelVisible;
	}

	public void setByValuePanelVisible(final boolean byValuePanelVisible) {
		logger.info("set by-value panel visible: " + byValuePanelVisible);
		var old = this.byValuePanelVisible;
		this.byValuePanelVisible = byValuePanelVisible;
		support.firePropertyChange(BY_VALUE_PANEL_VISIBLE, old, byValuePanelVisible);
	}

	public void setAlterLineTypePanelVisible(final boolean alterLineTypePanelVisible) {
		logger.info("set alter line type panel visible: " + alterLineTypePanelVisible);
		var old = this.alterLineTypePanelVisible;
		this.alterLineTypePanelVisible = alterLineTypePanelVisible;
		support.firePropertyChange(ALTER_LINE_TYPE_PANEL_VISIBLE, old, alterLineTypePanelVisible);
	}

	public void setLineSelectionPanelVisible(final boolean lineSelectionPanelVisible) {
		logger.info("set line selection panel visible: " + lineSelectionPanelVisible);
		var old = this.lineSelectionPanelVisible;
		this.lineSelectionPanelVisible = lineSelectionPanelVisible;
		support.firePropertyChange(LINE_SELECTION_PANEL_VISIBLE, old, lineSelectionPanelVisible);
	}

	public void setLineInputPanelVisible(final boolean lineInputPanelVisible) {
		logger.info("set line input panel visible: " + lineInputPanelVisible);
		var old = this.lineInputPanelVisible;
		this.lineInputPanelVisible = lineInputPanelVisible;
		support.firePropertyChange(LINE_INPUT_PANEL_VISIBLE, old, lineInputPanelVisible);
	}

	public void setAngleStepPanelVisible(final boolean angleStepVisible) {
		logger.info("set angle step panel visible: " + angleStepVisible);
		var old = this.angleStepPanelVisible;
		this.angleStepPanelVisible = angleStepVisible;
		support.firePropertyChange(ANGLE_STEP_PANEL_VISIBLE, old, angleStepVisible);
	}

	private void setSelectedMode(final EditMode mode) {
		logger.info("set selected mode to: " + mode.name());
		var old = selectedMode;
		selectedMode = mode;
		support.firePropertyChange(SELECTED_MODE, old, mode);
	}

	public void selectInputMode() {
		setSelectedMode(EditMode.INPUT);
	}

	public void selectSelectMode() {
		setSelectedMode(EditMode.SELECT);
	}

	public EditMode getSelectedMode() {
		EditMode ret = selectedMode;

		selectedMode = EditMode.NONE; // a hack to trigger an update every time.

		return ret;
	}

	public ValueSetting getValueSetting() {
		return valueSetting;
	}
}
