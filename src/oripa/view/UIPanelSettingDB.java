package oripa.view;

import java.util.Observable;

import oripa.Constants;
import oripa.paint.Globals;

public class UIPanelSettingDB extends Observable{
	private boolean subPanelVisible = false;
	private boolean alterLineTypePanelVisible = true; 
	private boolean mountainButtonEnabled = true;
	private boolean valleyButtonEnabled = true;
	private boolean auxButtonEnabled = true;
	private int lineTypeFromIndex;
	private int lineTypeToIndex;
	
	// singleton pattern
	
	private static UIPanelSettingDB settingDB = null;
	
	public static UIPanelSettingDB getInstance(){
		if(settingDB == null){
			settingDB = new UIPanelSettingDB();
		}
		
		return settingDB;
	}


	
	public int getLineTypeFromIndex() {
		return lineTypeFromIndex;
	}


	public void setLineTypeFromIndex(int lineTypeFromIndex) {

		this.lineTypeFromIndex = lineTypeFromIndex;
		this.setChanged();
	}


	public int getLineTypeToIndex() {
		return lineTypeToIndex;
	}


	public void setLineTypeToIndex(int lineTypeToIndex) {
		this.lineTypeToIndex = lineTypeToIndex;

		this.setChanged();
	}


	
	public boolean isSubPanelVisible() {
		return subPanelVisible;
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

	public void setSubPanelVisible(boolean subPanelVisible) {
		this.subPanelVisible = subPanelVisible;
		this.setChanged();
	}
	public void setAlterLineTypePanelVisible(boolean alterLineTypePanelVisible) {
		this.alterLineTypePanelVisible = alterLineTypePanelVisible;
		this.setChanged();
	}
	public void setMountainButtonEnabled(boolean mountainButtonEnabled) {
		this.mountainButtonEnabled = mountainButtonEnabled;
		this.setChanged();
	}
	public void setValleyButtonEnabled(boolean valleyButtonEnabled) {
		this.valleyButtonEnabled = valleyButtonEnabled;
		this.setChanged();
	}
	public void setAuxButtonEnabled(boolean auxButtonEnabled) {
		this.auxButtonEnabled = auxButtonEnabled;
		this.setChanged();
	}

	public void updateUIPanel(){
		boolean bDispSubPanel = false;
		if (Globals.editMode == Constants.EditMode.INPUT_LINE) {
			if (Globals.lineInputMode == Constants.LineInputMode.BY_VALUE) {
				bDispSubPanel = true;
			}
		}
		
		setSubPanelVisible(bDispSubPanel);
		setAlterLineTypePanelVisible(Globals.editMode == Constants.EditMode.CHANGE_LINE_TYPE);
		setMountainButtonEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);
		setValleyButtonEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);
		setAuxButtonEnabled(Globals.editMode == Constants.EditMode.INPUT_LINE);

		
		
		notifyObservers();

	}
	
	
	/**
	 * @return full-path class name
	 */
	@Override
	public String toString() {
		return this.getClass().getName();
	}
	
}
