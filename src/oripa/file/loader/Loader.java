package oripa.file.loader;

import oripa.Doc;
import oripa.file.FileVersionError;

public interface Loader {
    public Doc load(String filePath) throws FileVersionError;
}
