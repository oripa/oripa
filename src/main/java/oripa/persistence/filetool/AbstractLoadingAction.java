package oripa.persistence.filetool;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractLoadingAction<Data> {
	private String path;

	public final AbstractLoadingAction<Data> setPath(final String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

	public abstract Optional<Data> load() throws FileVersionError, IOException, WrongDataFormatException;

}