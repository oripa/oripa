package oripa.persistence.filetool;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractLoadingAction<Data> {
	public abstract Optional<Data> load(String path) throws FileVersionError, IOException, WrongDataFormatException;

}