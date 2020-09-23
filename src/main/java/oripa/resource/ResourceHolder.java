package oripa.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A singleton for resources. All resources are loaded at the beginning.
 *
 * @author koji
 *
 */
public class ResourceHolder {

	private final HashMap<ResourceKey, ResourceBundle> resources = new HashMap<>();

//----------------------------------------------------------
	private static ResourceHolder instance = null;

	private ResourceHolder() {
	}

	public static ResourceHolder getInstance() {
		if (instance == null) {
			instance = new ResourceHolder();
			instance.load();
		}

		return instance;
	}

//----------------------------------------------------------

	private static final String resourcePackage = "oripa.resource";

	private void load() {
		ResourceHolder holder = ResourceHolder.getInstance();
		holder.addResource(ResourceKey.EXPLANATION,
				createResource(resourcePackage + ".ExplanationStringResource_en"));
		holder.addResource(ResourceKey.LABEL,
				createResource(resourcePackage + ".LabelStringResource_en"));
		holder.addResource(ResourceKey.WARNING,
				createResource(resourcePackage + ".WarningStringResource_en"));
		holder.addResource(ResourceKey.APP_INFO,
				createResource(resourcePackage + ".AppInfoResource"));

	}

	private static ResourceBundle createResource(final String classPath) {
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

	public void addResource(final ResourceKey key, final ResourceBundle resource) {
		resources.put(key, resource);
	}

	public ResourceBundle getResource(final ResourceKey key) {
		return resources.get(key);
	}

	public String getString(final ResourceKey key, final String id) {
		return getResource(key).getString(id);
	}

}
