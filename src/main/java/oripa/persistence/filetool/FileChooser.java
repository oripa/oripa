package oripa.persistence.filetool;

///**
// *
// * @author OUCHI Koji
// *
// */
//@Deprecated
//public class FileChooser extends JFileChooser implements FileChooserView {
//
//	private static Logger logger = LoggerFactory.getLogger(FileChooser.class);
//
//	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 4700305827321319095L;
//
//	FileChooser() {
//
//		super();
//	}
//
//	FileChooser(final String path) {
//		super(path);
//
//		File file = new File(path);
//		this.setSelectedFile(file);
//	}
//
//	private String replaceExtension(final String path, final String ext) {
//
//		String path_new;
//
//		// drop the old extension
//		path_new = path.replaceAll("\\.\\w+$", "");
//
//		// append the new extension
//		path_new += "." + ext;
//
//		return path_new;
//	}
//
//	/**
//	 * this method does not change {@code path}.
//	 *
//	 * @param path
//	 * @param extensions
//	 *            ex) ".png"
//	 * @return path string with new extension
//	 */
//	private String correctExtension(final String path, final String[] extensions) {
//
//		String path_new = new String(path);
//
//		var filtered = Arrays.asList(extensions).stream()
//				.filter(ext -> path.endsWith(ext))
//				.collect(Collectors.toList());
//
//		// the path's extension is not in the targets.
//		if (filtered.isEmpty()) {
//			path_new = replaceExtension(path_new, extensions[0]);
//		}
//
//		return path_new;
//	}
//
//	public AbstractSavingAction<Data> getActionForSavingFile(
//			final Component parent)
//			throws FileChooserCanceledException, IllegalStateException {
//
//		if (this.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
//			throw new FileChooserCanceledException();
//		}
//
//		FileFilter rawFilter = this.getFileFilter();
//		if (!(rawFilter instanceof FileAccessSupport<?>)) {
//			throw new RuntimeException("Wrong Implementation!");
//		}
//
//		@SuppressWarnings("unchecked")
//		FileAccessSupport<Data> filter = (FileAccessSupport<Data>) rawFilter;
//
//		String[] extensions = filter.getExtensions();
//
//		File file = this.getSelectedFile();
//		String filePath = null;
//		try {
//			filePath = correctExtension(file.getCanonicalPath(), extensions);
//		} catch (IOException e) {
//			throw new IllegalStateException("Failed to get canonical path.");
//		}
//		if (file.exists()) {
//			if (JOptionPane.showConfirmDialog(null,
//					resourceHolder.getString(ResourceKey.WARNING,
//							StringID.Warning.SAME_FILE_EXISTS_ID),
//					resourceHolder.getString(ResourceKey.WARNING,
//							StringID.Warning.SAVE_TITLE_ID),
//					JOptionPane.YES_NO_OPTION,
//					JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
//				throw new FileChooserCanceledException();
//			}
//		}
//
//		var savingAction = filter.getSavingAction();
//
//		if (savingAction == null) {
//			throw new IllegalStateException("The filter is not for saving the file.");
//		}
//
//		return savingAction.setPath(filePath);
//	}
//
//	/*
//	 * (non Javadoc)
//	 *
//	 * @see oripa.persistent.filetool.FileAccessActionProvider#
//	 * getActionForLoadingFile (java.awt.Component)
//	 */
//	@Override
//	public AbstractLoadingAction<Data> getActionForLoadingFile(
//			final Component parent)
//			throws FileChooserCanceledException, FileNotFoundException, IllegalStateException {
//
//		if (this.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
//			throw new FileChooserCanceledException();
//		}
//
//		FileFilter rawFilter = this.getFileFilter();
//		if (!(rawFilter instanceof FileAccessSupport<?>)) {
//			throw new RuntimeException("Wrong Implementation!");
//		}
//
//		@SuppressWarnings("unchecked")
//		FileAccessSupport<Data> filter = (FileAccessSupport<Data>) rawFilter;
//
//		File file = this.getSelectedFile();
//		if (!file.exists()) {
//			throw new FileNotFoundException("Selected file doesn't exist.");
//		}
//
//		String filePath = null;
//		try {
//			filePath = file.getCanonicalPath();
//		} catch (IOException e) {
//			throw new IllegalStateException("Failed to get canonical path.");
//		}
//
//		logger.debug("preparing loadingAction for: " + filePath);
//
//		AbstractLoadingAction<Data> loadingAction = null;
//
//		try {
//			if (filter instanceof MultiTypeAcceptableFileLoadingSupport<?>) {
//				MultiTypeAcceptableFileLoadingSupport<Data> multiFilter = (MultiTypeAcceptableFileLoadingSupport<Data>) filter;
//				loadingAction = multiFilter.getLoadingAction(filePath);
//			} else {
//				loadingAction = filter.getLoadingAction();
//			}
//		} catch (IllegalArgumentException e) {
//			logger.error("error on getting a loadingAction: ", e);
//			Dialogs.showErrorDialog(this, resourceHolder.getString(
//					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), e);
//		}
//
//		if (loadingAction == null) {
//			throw new IllegalStateException("The filter is not for loading the file.");
//		}
//
//		return loadingAction.setPath(filePath);
//	}
//}
