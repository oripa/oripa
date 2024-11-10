package oripa.persistence.filetool;

import java.io.IOException;
import java.util.Optional;

public class LoadingAction<Data> {

	private final Loader<Data> loader;

	public LoadingAction(final Loader<Data> l) {
		loader = l;
	}

	public Optional<Data> load(final String path) throws FileVersionError, IOException, WrongDataFormatException {
		return loader.load(path);
	}

}