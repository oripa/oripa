package oripa.persistent.filetool;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class FileAccessSupportFilter<Data>
		extends javax.swing.filechooser.FileFilter
		implements Comparable<FileAccessSupportFilter<Data>> {

	/**
	 * 
	 * @author OUCHI Koji
	 * 
	 */

	private final FileTypeProperty		fileType;
	private final String				msg;

	private AbstractSavingAction<Data>	savingAction	= null;

	private AbstractLoadingAction<Data>	loadingAction	= null;

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

	private String	acceptedPath	= null;

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

	// public Loader getLoader() {
	// return loader;
	// }
	//
	// public void setLoader(Loader loader) {
	// this.loader = loader;
	// }

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

	// public Exporter getExporter() {
	// return exporter;
	// }
	//
	// public void setExporter(Exporter exporter) {
	// this.exporter = exporter;
	// }

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
	 * @param suffix
	 * @return in the style of
	 *         "(*.extension1, *.extension2, ...) ${type.getKeytext()} ${suffix}"
	 */
	public static String createDefaultDescription(final FileTypeProperty type,
			final String suffix) {
		String[] extensions = type.getExtensions();

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(extensions[0]);
		for (int i = 1; i < extensions.length; i++) {
			builder.append(", *");
			builder.append(extensions[i]);
		}
		builder.append(") " + type.getKeyText()
				+ ResourceHolder.getInstance().getString(
						ResourceKey.LABEL, StringID.Main.FILE_ID));

		return builder.toString();
	}
}
