package oripa.file;

import oripa.ORIPA;

public class FileAccessSupportFilter<Data> extends
		javax.swing.filechooser.FileFilter {

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

	// public FileFilterEx(String[] extensions, String msg, Exporter exporter) {
	// this.extensions = extensions;
	// this.msg = msg;
	// this.exporter = exporter;
	//
	// // this.savingAction = new SavingAction() {
	// //
	// // @Override
	// // public boolean save(String path) {
	// // boolean success = false;
	// // try {
	// // success = FileFilterEx.this.exporter.export(ORIPA.doc, path);
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // return success;
	// // }
	// // };
	//
	// }

	// public boolean save(String path) throws Exception{
	//
	// boolean success = false;
	//
	// if(savingAction != null){
	// success = savingAction.save(path);
	// }
	// // else if(exporter != null){
	// // success = exporter.export(doc, path);
	// // }
	//
	//
	// return success;
	// }
	//
	//
	//
	// public boolean load(String path) throws Exception{
	//
	// boolean success = false;
	//
	// if(loadingAction != null){
	// success = loadingAction.load(path);
	// }
	//
	// return success;
	// }
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
			builder.append(", *.");
			builder.append(extensions[i]);
		}
		builder.append(") " + type.getKeyText() + ORIPA.res.getString("File"));

		return builder.toString();
	}

}
