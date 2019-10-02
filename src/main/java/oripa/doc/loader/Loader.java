package oripa.doc.loader;

import oripa.doc.Doc;
import oripa.file.FileVersionError;

public interface Loader {
    Doc load(String filePath) throws FileVersionError;
}
