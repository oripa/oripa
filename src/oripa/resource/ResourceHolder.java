package oripa.resource;

import java.util.HashMap;
import java.util.ResourceBundle;

public class ResourceHolder {
	
	
	private HashMap<ResourceKey, ResourceBundle> resources = new HashMap<>();
	
//----------------------------------------------------------	
	private static ResourceHolder instance = null;
	
	private ResourceHolder(){}
	
	public static ResourceHolder getInstance(){
		if(instance == null){
			instance = new ResourceHolder();
		}
		
		return instance;		
	}
//----------------------------------------------------------	

	public void addResource(ResourceKey key, ResourceBundle resource){
		resources.put(key, resource);
	}
	
	public ResourceBundle getResource(ResourceKey key){
		return resources.get(key);
	}
	
	public String getString(ResourceKey key, String id){
		return getResource(key).getString(id);
	}

}
