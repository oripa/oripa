package oripa.persistence.filetool;

import java.io.IOException;

public interface Loader<Data> {
	public Data load(String filePath)
			throws FileVersionError, IOException, WrongDataFormatException;
}
