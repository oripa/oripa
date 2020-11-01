package oripa.persistent.filetool;

public class FileAccessSupportFilter<Data>
		extends javax.swing.filechooser.FileFilter
		implements Comparable<FileAccessSupportFilter<Data>> {

	/**
	 *
	 * @author OUCHI Koji
	 *
	 */

	private final FileTypeProperty fileType;
	private final String msg;

	private AbstractSavingAction<Data> savingAction = null;

	private AbstractLoadingAction<Data> loadingAction = null;

	/**
	 *
	 * Constructor.
	 *
	 * @param fileType
	 *            specifies what to filter
	 * @param msg
	 *            message in filter box
	 */
	public FileAccessSupportFilter(final FileTypeProperty fileType, final String msg) {
		this.fileType = fileType;
		this.msg = msg;
	}

	/**
	 *
	 * Constructor.
	 *
	 * @param fileType
	 *            specifies what to filter
	 * @param msg
	 *            message in filter box
	 * @param savingAction
	 *            default action of saving
	 */
	public FileAccessSupportFilter(final FileTypeProperty fileType, final String msg,
			final AbstractSavingAction<Data> savingAction) {
		this.fileType = fileType;
		this.msg = msg;
		this.savingAction = savingAction;
	}

	private String acceptedPath = null;

	public AbstractLoadingAction<Data> getLoadingAction() {
		if (loadingAction != null) {
			return loadingAction.setPath(acceptedPath);
		}
		return loadingAction;
	}

	/**
	 *
	 * @param action
	 *            how to load
	 */
	public void setLoadingAction(final AbstractLoadingAction<Data> action) {
		this.loadingAction = action;
	}

	/**
	 *
	 * @param action
	 *            how to save
	 */
	public void setSavingAction(final AbstractSavingAction<Data> action) {
		savingAction = action;
	}

	/**
	 *
	 * @return object which can save the data
	 */
	public AbstractSavingAction<Data> getSavingAction() {
		if (savingAction != null) {
			return savingAction.setPath(acceptedPath);
		}
		return savingAction;
	}

	/**
	 *
	 * @return acceptable extensions
	 */
	public String[] getExtensions() {
		return fileType.getExtensions();
	}

	@Override
	public boolean accept(final java.io.File f) {
		if (f.isDirectory()) {
			return true;
		}

		for (String extension : fileType.getExtensions()) {

			if (f.getName().endsWith(extension)) {
				acceptedPath = f.getAbsolutePath();
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return msg;
	}

	public FileTypeProperty getTargetType() {
		return fileType;
	}

	/**
	 * order property is the most prior, the second is msg property.
	 */
	@Override
	public int compareTo(final FileAccessSupportFilter<Data> o) {
		int cmp = fileType.getOrder().compareTo(o.fileType.getOrder());
		if (cmp == 0) {
			return msg.compareTo(o.msg);
		}

		return cmp;
	}

	/**
	 *
	 * @param type
	 *            file type
	 * @param explanation
	 * @return in the style of "(*.extension1, *.extension2, ...)
	 *         ${type.getKeytext()} ${explanation}"
	 */
	public static String createDefaultDescription(final FileTypeProperty type,
			final String explanation) {
		String[] extensions = type.getExtensions();

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for (int i = 0; i < extensions.length; i++) {
			if (i > 0) {
				builder.append(",");
			}
			builder.append("*");
			builder.append(extensions[i]);
		}
		builder.append(") ");
		builder.append(explanation);

		return builder.toString();
	}
}
