package oripa.viewsetting.main.uipanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.TypeForChange;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.byvalue.ValueSetting;
import oripa.domain.paint.linetype.TypeForChangeGettable;

public class UIPanelSetting implements TypeForChangeGettable {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelSetting.class);

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private boolean byValuePanelVisible = false;
	public static final String BY_VALUE_PANEL_VISIBLE = "by-value panel visible";

	private boolean alterLineTypePanelVisible = true;
	public static final String ALTER_LINE_TYPE_PANEL_VISIBLE = "alter-line-type panel visible";

	private boolean mountainButtonEnabled = true;
	public static final String MOUNTAIN_BUTTON_ENABLED = "mountain button enabled";

	private boolean valleyButtonEnabled = true;
	public static final String VALLEY_BUTTON_ENABLED = "valley button enabled";

	private boolean auxButtonEnabled = true;
	public static final String AUX_BUTTON_ENABLED = "aux button enabled";

	private EditMode selectedMode = EditMode.NONE;
	public static final String SELECTED_MODE = "selected mode";

	private TypeForChange typeFrom = TypeForChange.EMPTY;
	public static final String TYPE_FROM = "line type of 'from' box";

	private TypeForChange typeTo = TypeForChange.EMPTY;
	public static final String TYPE_TO = "line type of 'to' box";

	private boolean angleStepVisible = false;
	public static final String ANGLE_STEP_VISIBLE = "angle step visible";

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

	public boolean isMountainButtonEnabled() {
		return mountainButtonEnabled;
	}

	public boolean isValleyButtonEnabled() {
		return valleyButtonEnabled;
	}

	public boolean isAuxButtonEnabled() {
		return auxButtonEnabled;
	}

	public void setByValuePanelVisible(final boolean byValuePanelVisible) {
		logger.info("set by-value panel visible: " + byValuePanelVisible);
		var old = this.byValuePanelVisible;
		this.byValuePanelVisible = byValuePanelVisible;
		support.firePropertyChange(BY_VALUE_PANEL_VISIBLE, old, byValuePanelVisible);
	}

	public void setAlterLineTypePanelVisible(final boolean alterLineTypePanelVisible) {
		var old = this.alterLineTypePanelVisible;
		this.alterLineTypePanelVisible = alterLineTypePanelVisible;
		support.firePropertyChange(ALTER_LINE_TYPE_PANEL_VISIBLE, old, alterLineTypePanelVisible);
	}

	public void setMountainButtonEnabled(final boolean mountainButtonEnabled) {
		var old = this.mountainButtonEnabled;
		this.mountainButtonEnabled = mountainButtonEnabled;
		support.firePropertyChange(MOUNTAIN_BUTTON_ENABLED, old, mountainButtonEnabled);
	}

	public void setValleyButtonEnabled(final boolean valleyButtonEnabled) {
		var old = this.valleyButtonEnabled;
		this.valleyButtonEnabled = valleyButtonEnabled;
		support.firePropertyChange(VALLEY_BUTTON_ENABLED, old, valleyButtonEnabled);
	}

	public void setAuxButtonEnabled(final boolean auxButtonEnabled) {
		var old = this.auxButtonEnabled;
		this.auxButtonEnabled = auxButtonEnabled;
		support.firePropertyChange(AUX_BUTTON_ENABLED, old, auxButtonEnabled);
	}

	private void setSelectedMode(final EditMode mode) {
		var old = selectedMode;
		selectedMode = mode;
		support.firePropertyChange(SELECTED_MODE, old, mode);
	}

	public void selectInputMode() {
		setSelectedMode(EditMode.INPUT);
	}

	public void selectChangeLineTypeMode() {
		setSelectedMode(EditMode.CHANGE_TYPE);
	}

	public void selectSelectMode() {
		setSelectedMode(EditMode.SELECT);
	}

	public void selectDeleteLineMode() {
		setSelectedMode(EditMode.DELETE_LINE);
	}

	public void selectAddVertexMode() {
		setSelectedMode(EditMode.VERTEX);
	}

	public void selectDeleteVertexMode() {
		setSelectedMode(EditMode.VERTEX);
	}

	public EditMode getSelectedMode() {
		EditMode ret = selectedMode;

		selectedMode = EditMode.NONE; // a hack to trigger an update every time.

		return ret;
	}

	public void setAngleStepVisible(final boolean visible) {
		var old = angleStepVisible;
		angleStepVisible = visible;
		support.firePropertyChange(ANGLE_STEP_VISIBLE, old, visible);
	}

	public ValueSetting getValueSetting() {
		return valueSetting;
	}
}
