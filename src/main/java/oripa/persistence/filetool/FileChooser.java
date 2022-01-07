package oripa.persistence.filetool;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.view.util.Dialogs;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 *
 * @author OUCHI Koji
 *
 */

public class FileChooser<Data> extends JFileChooser implements FileAccessActionProvider<Data> {

	private static Logger logger = LoggerFactory.getLogger(FileChooser.class);

	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 *
	 */
	private static final long serialVersionUID = 4700305827321319095L;

	FileChooser() {

		super();
	}

	FileChooser(final String path) {
		super(path);

		File file = new File(path);
		this.setSelectedFile(file);
	}

	/**
	 * don't use this!
	 */
	@Override
	@Deprecated
	public void addChoosableFileFilter(final FileFilter filter) {

	}

	public void addChoosableFileFilter(
			final FileAccessSupportFilter<Data> filter) {
		super.addChoosableFileFilter(filter);
	}

	private String replaceExtension(final String path, final String ext) {

		String path_new;

		// drop the old extension
		path_new = path.replaceAll("\\.\\w+$", "");

		// append the new extension
		path_new += ext;

		return path_new;
	}

	/**
	 * this method does not change {@code path}.
	 *
	 * @param path
	 * @param extensions
	 *            ex) ".png"
	 * @return path string with new extension
	 */
	private String correctExtension(final String path, final String[] extensions) {

		String path_new = new String(path);

		var filtered = Arrays.asList(extensions).stream()
				.filter(ext -> path.endsWith(ext))
				.collect(Collectors.toList());

		// the path's extension is not in the targets.
		if (filtered.isEmpty()) {
			path_new = replaceExtension(path_new, extensions[0]);
		}

		return path_new;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.persistent.filetool.FileAccessActionProvider#getActionForSavingFile
	 * (java.awt.Component)
	 */
	@Override
	public AbstractSavingAction<Data> getActionForSavingFile(
			final Component parent)
			throws FileChooserCanceledException, IllegalStateException {

		if (this.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
			throw new FileChooserCanceledException();
		}

		FileFilter rawFilter = this.getFileFilter();
		if (!(rawFilter instanceof FileAccessSupportFilter<?>)) {
			throw new RuntimeException("Wrong Implementation!");
		}

		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Data> filter = (FileAccessSupportFilter<Data>) rawFilter;

		String[] extensions = filter.getExtensions();

		File file = this.getSelectedFile();
		String filePath = null;
		try {
			filePath = correctExtension(file.getCanonicalPath(), extensions);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to get canonical path.");
		}
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(null,
					resourceHolder.getString(ResourceKey.WARNING,
							StringID.Warning.SAME_FILE_EXISTS_ID),
					resourceHolder.getString(ResourceKey.WARNING,
							StringID.Warning.SAVE_TITLE_ID),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
				throw new FileChooserCanceledException();
			}
		}

		var savingAction = filter.getSavingAction();

		if (savingAction == null) {
			throw new IllegalStateException("The filter is not for saving the file.");
		}

		return savingAction.setPath(filePath);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.filetool.FileAccessActionProvider#
	 * getActionForLoadingFile (java.awt.Component)
	 */
	@Override
	public AbstractLoadingAction<Data> getActionForLoadingFile(
			final Component parent)
			throws FileChooserCanceledException, FileNotFoundException, IllegalStateException {

		if (this.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			throw new FileChooserCanceledException();
		}

		FileFilter rawFilter = this.getFileFilter();
		if (!(rawFilter instanceof FileAccessSupportFilter<?>)) {
			throw new RuntimeException("Wrong Implementation!");
		}

		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Data> filter = (FileAccessSupportFilter<Data>) rawFilter;

		File file = this.getSelectedFile();
		if (!file.exists()) {
			throw new FileNotFoundException("Selected file doesn't exist.");
		}

		String filePath = null;
		try {
			filePath = file.getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to get canonical path.");
		}

		logger.debug("preparing loadingAction for: " + filePath);

		AbstractLoadingAction<Data> loadingAction = null;

		try {
			if (filter instanceof MultiTypeAcceptableFileLoadingFilter<?>) {
				MultiTypeAcceptableFileLoadingFilter<Data> multiFilter = (MultiTypeAcceptableFileLoadingFilter<Data>) filter;
				loadingAction = multiFilter.getLoadingAction(filePath);
			} else {
				loadingAction = filter.getLoadingAction();
			}
		} catch (IllegalArgumentException e) {
			logger.error("error on getting a loadingAction: ", e);
			Dialogs.showErrorDialog(this, resourceHolder.getString(
					ResourceKey.ERROR, StringID.Error.LOAD_FAILED_ID), e);
		}

		if (loadingAction == null) {
			throw new IllegalStateException("The filter is not for loading the file.");
		}

		return loadingAction.setPath(filePath);
	}
}
