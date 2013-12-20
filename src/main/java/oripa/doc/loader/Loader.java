package oripa.doc.loader;

import oripa.file.FileVersionError;

public interface Loader<Data> {
	public Data load(String filePath) throws FileVersionError;
}
