package oripa.persistent.filetool;

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

	public abstract boolean save(Data data) throws IOException, IllegalArgumentException;

}