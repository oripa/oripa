package oripa.persistence.filetool;

import java.io.IOException;
import java.util.Optional;

public interface Loader<Data> {
    Optional<Data> load(String filePath)
            throws FileVersionError, IOException, WrongDataFormatException;
}
