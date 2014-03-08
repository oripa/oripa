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

	private final FileTypeProperty fileType;
	private final String msg;

	private AbstractSavingAction<Data> savingAction = null;

	private AbstractLoadingAction<Data> loadingAction = null;

	public FileAccessSupportFilter(FileTypeProperty fileType, String msg) {
		this.fileType = fileType;
		this.msg = msg;
	}

	public FileAccessSupportFilter(FileTypeProperty fileType, String msg,
			AbstractSavingAction<Data> savingAction) {
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

	public void setLoadingAction(AbstractLoadingAction<Data> loadingAction) {
		this.loadingAction = loadingAction;
	}

	// public Loader getLoader() {
	// return loader;
	// }
	//
	// public void setLoader(Loader loader) {
	// this.loader = loader;
	// }

	public void setSavingAction(AbstractSavingAction<Data> s) {
		savingAction = s;
	}

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

	public String[] getExtensions() {
		return fileType.getExtensions();
	}

	@Override
	public boolean accept(java.io.File f) {
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

	/*
	 * (non Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FileAccessSupportFilter<Data> o) {
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
	public static String createDefaultDescription(FileTypeProperty type,
			String suffix) {
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
		// ORIPA.res.getString("File"));

		return builder.toString();
	}
}
