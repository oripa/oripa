package oripa.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.inject.Singleton;

/**
 * A singleton for resources.
 *
 * @author koji
 *
 */
@Singleton
public class ResourceHolder {

	private final HashMap<ResourceKey, ResourceBundle> resources = new HashMap<>();

//----------------------------------------------------------

	public ResourceHolder() {
		synchronized ("resourceHolderInitialize") {
			load();
		}
	}

//----------------------------------------------------------

	private static final String resourcePackage = "oripa.resource";

	private void load() {
		addResource(ResourceKey.EXPLANATION,
				createResource(resourcePackage + ".ExplanationStringResource_en"));
		addResource(ResourceKey.LABEL,
				createResource(resourcePackage + ".LabelStringResource_en"));
		addResource(ResourceKey.DEFAULT,
				createResource(resourcePackage + ".DefaultStringResource_en"));
		addResource(ResourceKey.INFO,
				createResource(resourcePackage + ".InformationStringResource_en"));
		addResource(ResourceKey.WARNING,
				createResource(resourcePackage + ".WarningStringResource_en"));
		addResource(ResourceKey.ERROR,
				createResource(resourcePackage + ".ErrorStringResource_en"));
		addResource(ResourceKey.APP_INFO,
				createResource(resourcePackage + ".AppInfoResource"));
	}

	private ResourceBundle createResource(final String classPath) {
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

	private void addResource(final ResourceKey key, final ResourceBundle resource) {
		resources.put(key, resource);
	}

	public ResourceBundle getResource(final ResourceKey key) {
		return resources.get(key);
	}

	public String getString(final ResourceKey key, final String id) {
		return getResource(key).getString(id);
	}
}
