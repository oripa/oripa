package oripa.file;

public abstract class AbstractSavingAction<Data> {
	private String path;

	private final Class<? extends Object> targetClass;

	public AbstractSavingAction(Class<? extends Object> targetClass) {
		this.targetClass = targetClass;
	}

	public final AbstractSavingAction<Data> setPath(String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

	public boolean targetClassMatches(Data data) {
		return this.targetClass.isInstance(data);
	}

	public abstract boolean save(Data data);

}