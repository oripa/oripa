package oripa.persistent.filetool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccessSupportFilter<Data>
		extends javax.swing.filechooser.FileFilter
		implements Comparable<FileAccessSupportFilter<Data>> {

	private static final Logger logger = LoggerFactory
			.getLogger(FileAccessSupportFilter.class);

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
	public FileAccessSupportFilter(final FileTypeProperty<Data> fileType, final String msg) {
		this.fileType = fileType;
		this.msg = msg;

		var exporter = fileType.getExporter();
		if (exporter != null) {
			savingAction = new SavingActionTemplate<>(exporter);
		}

		var loader = fileType.getLoader();
		if (loader != null) {
			loadingAction = new LoadingActionTemplate<>(fileType.getLoader());
		}
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

		for (String extension : getExtensions()) {

			if (f.getName().endsWith(extension)) {
				logger.debug("accepted file: " + f);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return msg;
	}

	public FileTypeProperty<Data> getTargetType() {
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
	 *         ${explanation}"
	 */
	public static String createDefaultDescription(final FileTypeProperty<?> type,
			final String explanation) {
		String[] extensions = type.getExtensions();

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append("*");
		builder.append(String.join(",*", extensions));
		builder.append(") ");
		builder.append(explanation);

		return builder.toString();
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
