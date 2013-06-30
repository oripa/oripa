package oripa.doc.loader;

import oripa.doc.Doc;
import oripa.file.FileVersionError;

public interface Loader {
    public Doc load(String filePath) throws FileVersionError;
}
