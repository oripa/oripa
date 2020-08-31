package oripa.persistent.doc;

import java.io.IOException;

import oripa.persistent.filetool.FileVersionError;

public interface Loader<Data> {
	public Data load(String filePath) throws FileVersionError, IOException;
}
