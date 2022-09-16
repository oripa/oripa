package oripa.persistence.filetool;

public class FileAccessSupport<Data>
		implements Comparable<FileAccessSupport<Data>> {

//	private static final Logger logger = LoggerFactory
//			.getLogger(FileAccessSupportFilter.class);

	/**
	 *
	 * @author OUCHI Koji
	 *
	 */

	private final FileTypeProperty<Data> fileType;
	private final String msg;
	private AbstractLoadingAction<Data> loadingAction;
	private AbstractSavingAction<Data> savingAction;

	/**
	 *
	 * Constructor.
	 *
	 * @param fileType
	 *            specifies what to filter
	 * @param msg
	 *            message in filter box
	 */
	public FileAccessSupport(final FileTypeProperty<Data> fileType, final String msg) {
		this.fileType = fileType;
		this.msg = msg;

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
		return msg;
	}

	/**
	 * order property is the most prior, the second is msg property.
	 */
	@Override
	public int compareTo(final FileAccessSupport<Data> o) {
		int cmp = fileType.getOrder().compareTo(o.fileType.getOrder());
		if (cmp == 0) {
			return msg.compareTo(o.msg);
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
