package oripa.doc;

import oripa.file.FileVersionError;

public interface Loader<Data> {
	public Data load(String filePath) throws FileVersionError;
}
