package oripa.persistent.filetool;

import java.io.IOException;

import oripa.persistent.doc.WrongDataFormatException;

public interface Loader<Data> {
	public Data load(String filePath)
			throws FileVersionError, IOException, WrongDataFormatException;
}
