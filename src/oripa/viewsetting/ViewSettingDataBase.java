package oripa.viewsetting;

import java.util.Observable;

public abstract class ViewSettingDataBase extends Observable {

	public abstract String getName();
	

	
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * A tool to be identified by observers.
	 * @param name
	 * @return
	 */
	public boolean hasGivenName(String name){
		return getName().equals(name);
	}
}
