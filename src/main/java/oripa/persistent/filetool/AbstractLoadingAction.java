package oripa.persistent.filetool;

public abstract class AbstractLoadingAction<Data> {
	private String path;

	public final AbstractLoadingAction<Data> setPath(String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

	public abstract Data load() throws FileVersionError;

}