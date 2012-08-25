package oripa.file;

import oripa.Doc;

public interface Loader {
    public Doc load(String filePath) throws FileVersionError;
}
