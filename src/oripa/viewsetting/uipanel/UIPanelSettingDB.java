package oripa.viewsetting.uipanel;

import oripa.doc.TypeForChange;
import oripa.paint.EditMode;
import oripa.viewsetting.ViewSettingDataBase;

public class UIPanelSettingDB extends ViewSettingDataBase{
	//-------------------------------------------------------
		// singleton pattern
		
		private static UIPanelSettingDB settingDB = null;
		
		private UIPanelSettingDB(){}
		
		public static UIPanelSettingDB getInstance(){
			if(settingDB == null){
				settingDB = new UIPanelSettingDB();
			}
			
			return settingDB;
		}
	//-------------------------------------------------------

		
	private boolean subPanelVisible = false;
	private boolean alterLineTypePanelVisible = true; 
	private boolean mountainButtonEnabled = true;
	private boolean valleyButtonEnabled = true;
	private boolean auxButtonEnabled = true;
	
	private EditMode selectedMode = EditMode.NONE;

	private int lineTypeFromIndex;
	private int lineTypeToIndex;
	
	private TypeForChange typeFrom = TypeForChange.EMPTY;
	private TypeForChange typeTo = TypeForChange.EMPTY;


	public TypeForChange getTypeFrom() {
		return typeFrom;
	}

	public void setTypeFrom(TypeForChange typeFrom) {
		this.typeFrom = typeFrom;
	}

	public TypeForChange getTypeTo() {
		return typeTo;
	}

	public void setTypeTo(TypeForChange typeTo) {
		this.typeTo = typeTo;
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


	
	public boolean isValuePanelVisible() {
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

	public void setValuePanelVisible(boolean subPanelVisible) {
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


	public void selectInputMode(){
		selectedMode = EditMode.INPUT;
		this.setChanged();
	}
	
	public void selectChangeLineTypeMode(){
		selectedMode = EditMode.CHANGE_TYPE;
		this.setChanged();
	}
	

	public void selectSelectMode(){
		selectedMode = EditMode.SELECT;
		this.setChanged();
	}
	
	public void selectDeleteLineMode(){
		selectedMode = EditMode.DELETE_LINE;
		this.setChanged();
	}
	
	public void selectAddVertexMode(){
		selectedMode = EditMode.ADD_VERTEX;
		this.setChanged();
	}

	public void selectDeleteVertexMode(){
		selectedMode = EditMode.DELETE_VERTEX;
		this.setChanged();
	}

	
	public EditMode getSelectedMode(){
		EditMode ret = selectedMode;

		selectedMode = EditMode.NONE;
		
		return ret;
	}
	
	@Override
	public String getName() {
		return this.getClass().getName();
	}
}
