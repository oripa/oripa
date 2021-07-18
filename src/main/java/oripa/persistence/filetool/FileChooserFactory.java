package oripa.persistence.filetool;

public class FileChooserFactory<Data> {

	@SafeVarargs
	public final FileChooser<Data>
			createChooser(final String path, final FileAccessSupportFilter<Data>... filters) {

		FileChooser<Data> fileChooser;

		if (path != null) {
			fileChooser = new FileChooser<Data>(path);
		}
		else {
			fileChooser = new FileChooser<Data>();
		}

		for (int i = 0; i < filters.length; i++) {
			fileChooser.addChoosableFileFilter(filters[i]);
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(filters[0]);

		return fileChooser;
	}

}
