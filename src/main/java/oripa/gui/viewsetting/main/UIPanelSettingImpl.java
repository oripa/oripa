package oripa.gui.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.view.main.UIPanelSetting;

public class UIPanelSettingImpl implements UIPanelSetting {
	private static final Logger logger = LoggerFactory.getLogger(UIPanelSettingImpl.class);

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private boolean lineSelectionPanelVisible = false;
	private boolean lineInputPanelVisible = true;
	private boolean byValuePanelVisible = false;
	private boolean alterLineTypePanelVisible = true;
	private boolean angleStepPanelVisible = false;
	private EditMode selectedMode = EditMode.NONE;
	@Override
	public void addPropertyChangeListener(
			final String propertyName, final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public boolean isByValuePanelVisible() {
		return byValuePanelVisible;
	}

	@Override
	public boolean isAlterLineTypePanelVisible() {
		return alterLineTypePanelVisible;
	}

	@Override
	public boolean isSelectLinePanelVisible() {
		return lineSelectionPanelVisible;
	}

	@Override
	public boolean isLineInputPanelVisible() {
		return lineInputPanelVisible;
	}

	@Override
	public boolean isAngleStepPanelVisible() {
		return angleStepPanelVisible;
	}

	@Override
	public void setByValuePanelVisible(final boolean byValuePanelVisible) {
		logger.info("set by-value panel visible: " + byValuePanelVisible);
		var old = this.byValuePanelVisible;
		this.byValuePanelVisible = byValuePanelVisible;
		support.firePropertyChange(BY_VALUE_PANEL_VISIBLE, old, byValuePanelVisible);
	}

	@Override
	public void setAlterLineTypePanelVisible(final boolean alterLineTypePanelVisible) {
		logger.info("set alter line type panel visible: " + alterLineTypePanelVisible);
		var old = this.alterLineTypePanelVisible;
		this.alterLineTypePanelVisible = alterLineTypePanelVisible;
		support.firePropertyChange(ALTER_LINE_TYPE_PANEL_VISIBLE, old, alterLineTypePanelVisible);
	}

	@Override
	public void setLineSelectionPanelVisible(final boolean lineSelectionPanelVisible) {
		logger.info("set line selection panel visible: " + lineSelectionPanelVisible);
		var old = this.lineSelectionPanelVisible;
		this.lineSelectionPanelVisible = lineSelectionPanelVisible;
		support.firePropertyChange(LINE_SELECTION_PANEL_VISIBLE, old, lineSelectionPanelVisible);
	}

	@Override
	public void setLineInputPanelVisible(final boolean lineInputPanelVisible) {
		logger.info("set line input panel visible: " + lineInputPanelVisible);
		var old = this.lineInputPanelVisible;
		this.lineInputPanelVisible = lineInputPanelVisible;
		support.firePropertyChange(LINE_INPUT_PANEL_VISIBLE, old, lineInputPanelVisible);
	}

	@Override
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

	@Override
	public void selectInputMode() {
		setSelectedMode(EditMode.INPUT);
	}

	@Override
	public void selectSelectMode() {
		setSelectedMode(EditMode.SELECT);
	}

	@Override
	public String getSelectedModeString() {
		EditMode ret = selectedMode;

		selectedMode = EditMode.NONE; // a hack to trigger an update every time.

		return ret.toString();
	}
}
