package oripa.persistence.filetool;

public class FileAccessSupport<Data>
		implements Comparable<FileAccessSupport<Data>> {

	private final FileTypeProperty<Data> fileType;
	// TODO description is not related to persistence responsibility.
	// this should be removed.
	private final String description;
	private AbstractLoadingAction<Data> loadingAction;
	private AbstractSavingAction<Data> savingAction;

	/**
	 *
	 * Constructor.
	 *
	 * @param fileType
	 *            specifies what to filter
	 * @param description
	 *            message in filter box
	 */
	public FileAccessSupport(final FileTypeProperty<Data> fileType, final String description) {
		this.fileType = fileType;
		this.description = description;

		var exporter = fileType.getExporter();
		if (exporter != null) {
			savingAction = new SavingActionTemplate<>(exporter);
		}

		var loader = fileType.getLoader();
		if (loader != null) {
			loadingAction = new LoadingActionTemplate<>(loader);
		}
	}

	/**
	 *
	 * @return acceptable extensions
	 */
	public String[] getExtensions() {
		return fileType.getExtensions();
	}

	public FileTypeProperty<Data> getTargetType() {
		return fileType;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * The order property is the most prior, the second is the description
	 * property.
	 */
	@Override
	public int compareTo(final FileAccessSupport<Data> o) {
		int cmp = fileType.getOrder().compareTo(o.fileType.getOrder());
		if (cmp == 0) {
			return description.compareTo(o.description);
		}

		return cmp;
	}

	/**
	 * @return loadingAction
	 */
	public AbstractLoadingAction<Data> getLoadingAction() {
		return loadingAction;
	}

	/**
	 * @param loadingAction
	 *            Sets loadingAction
	 */
	public void setLoadingAction(final AbstractLoadingAction<Data> loadingAction) {
		this.loadingAction = loadingAction;
	}

	/**
	 * @return savingAction
	 */
	public AbstractSavingAction<Data> getSavingAction() {
		return savingAction;
	}

	/**
	 * @param savingAction
	 *            Sets savingAction
	 */
	public void setSavingAction(final AbstractSavingAction<Data> savingAction) {
		this.savingAction = savingAction;
	}

}
