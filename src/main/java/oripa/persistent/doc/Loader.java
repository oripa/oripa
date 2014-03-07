package oripa.persistent.doc;

import oripa.persistent.filetool.FileVersionError;

public interface Loader<Data> {
	public Data load(String filePath) throws FileVersionError;
}
