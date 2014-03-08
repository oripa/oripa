package oripa.persistent.filetool;

public abstract class AbstractSavingAction<Data> {
	private String path;

	public final AbstractSavingAction<Data> setPath(final String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

//	public boolean targetClassMatches(Data data) {
//		return this.targetClass.isInstance(data);
//	}

	public abstract boolean save(Data data);

}