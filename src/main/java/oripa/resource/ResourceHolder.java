package oripa.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A singleton for resources.
 * All resources are loaded at the beginning.
 * 
 * @author koji
 *
 */
public class ResourceHolder {
	
	
	private HashMap<ResourceKey, ResourceBundle> resources = new HashMap<>();
	
//----------------------------------------------------------	
	private static ResourceHolder instance = null;
	
	private ResourceHolder(){}
	
	public static ResourceHolder getInstance(){
		if(instance == null){
			instance = new ResourceHolder();
			instance.load();
		}
		
		return instance;		
	}
//----------------------------------------------------------	

	private static final String resourcePackage = "oripa.resource";

	private void load(){
        ResourceHolder resources = ResourceHolder.getInstance();
        resources.addResource(ResourceKey.EXPLANATION, 
        		createResource(resourcePackage + ".ExplanationStringResource_en") );
        resources.addResource(ResourceKey.LABEL, 
        		createResource(resourcePackage + ".LabelStringResource_en") );
        resources.addResource(ResourceKey.WARNING, 
        		createResource(resourcePackage + ".WarningStringResource_en") );
        

	}
	

	
	public static ResourceBundle createResource(String classPath){
		ResourceBundle bundle;
		
		// get a resource for the location
        try {
            bundle = ResourceBundle.getBundle(classPath,
                    Locale.getDefault());
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(classPath,
                    Locale.ENGLISH);
        }
        
        // sorry for forcing English...
        bundle = ResourceBundle.getBundle(classPath, Locale.ENGLISH);
		
		
		return bundle;
	}

	
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
