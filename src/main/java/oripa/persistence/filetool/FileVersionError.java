package oripa.persistence.filetool;

public class FileVersionError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5043619691306291060L;

	public FileVersionError() {
		super("Failed to load the file. That file is compatible with a new version. "
				+ "Please obtain the latest version of ORIPA");
	}
}
