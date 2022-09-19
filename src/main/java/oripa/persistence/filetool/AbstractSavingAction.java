package oripa.persistence.filetool;

import java.io.IOException;

public abstract class AbstractSavingAction<Data> {
	private String path;

	public final AbstractSavingAction<Data> setPath(final String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

	public final boolean save(final Data data) throws IOException, IllegalArgumentException {
		beforeSave(data);
		var result = saveImpl(data);
		afterSave(data);
		return result;
	}

	protected abstract void beforeSave(Data data);

	protected abstract void afterSave(Data data);

	protected abstract boolean saveImpl(Data data) throws IOException, IllegalArgumentException;

}