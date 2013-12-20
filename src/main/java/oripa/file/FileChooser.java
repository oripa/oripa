package oripa.file;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import oripa.ORIPA;

/**
 * 
 * @author OUCHI Koji
 * 
 */

public class FileChooser<Data> extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700305827321319095L;

	public FileChooser() {

		super();
	}

	public FileChooser(String path) {
		super(path);
		String trimmedPath = replaceExtension(path, "");

		// File file = new File(trimmedPath);
		File file = new File(path);
		this.setSelectedFile(file);

		System.out.println(path);
	}

	/**
	 * don't use this!
	 */
	@Override
	@Deprecated
	public void addChoosableFileFilter(FileFilter filter) {

	}

	public void addChoosableFileFilter(FileAccessSupportFilter<Data> filter) {
		// TODO Auto-generated method stub
		super.addChoosableFileFilter(filter);
	}

	public String replaceExtension(String path, String ext) {

		String path_new;

		path_new = path.replaceAll("\\.\\w+$", "");
		path_new += ext;

		return path_new;
	}

	/**
	 * this method does not change {@code path}.
	 * 
	 * @param path
	 * @param ext
	 *            ex) ".png"
	 * @return path string with new extension
	 */
	public String correctExtension(String path, String[] extensions) {

		String path_new = new String(path);

		boolean isCorrect = false;
		for (int i = 0; i < extensions.length; i++) {
			if (path.endsWith(extensions[i])) {
				isCorrect = true;
				break;
			}
		}

		if (isCorrect == false) {
			path_new = replaceExtension(path_new, extensions[0]);
		}

		return path_new;
	}

	/**
	 * Opens chooser dialog and return saver object for the chosen file.
	 * 
	 * @param parent
	 *            parent GUI component
	 * @return saver object.
	 */
	public AbstractSavingAction<Data> getActionForSavingFile(Component parent) {

		if (JFileChooser.APPROVE_OPTION != this.showSaveDialog(parent)) {
			return null;
		}

		String filePath = null;

		try {

			FileAccessSupportFilter<Data> filter = (FileAccessSupportFilter<Data>) (this
					.getFileFilter());
			String[] extensions = filter.getExtensions();

			filePath = correctExtension(this.getSelectedFile().getPath(),
					extensions);

			if (filePath == null) {
				throw new IllegalArgumentException(
						"wrong extension of selected name");
			}

			File file = new File(filePath);
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(null,
						ORIPA.res.getString("Warning_SameNameFileExist"),
						ORIPA.res.getString("DialogTitle_FileSave"),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
					return null;
				}
			}

			return filter.getSavingAction().setPath(filePath);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, e.toString(),
					ORIPA.res.getString("Error_FileSaveFailed"),
					JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/**
	 * Opens chooser dialog and returns loader object for the chosen file.
	 * 
	 * @param parent
	 *            parent GUI component
	 * @return loader object.
	 */
	public AbstractLoadingAction<Data> getActionForLoadingFile(
			Component parent) {

		if (JFileChooser.APPROVE_OPTION == this.showOpenDialog(parent)) {
			try {
				String filePath = this.getSelectedFile().getPath();
				FileAccessSupportFilter<Data> filter = (FileAccessSupportFilter<Data>) (this
						.getFileFilter());

				return filter.getLoadingAction().setPath(filePath);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.toString(),
						ORIPA.res.getString("Error_FileLoadFailed"),
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
			// } catch (FileVersionError v_err) {
			// JOptionPane.showMessageDialog(this,
			// "This file is compatible with a new version. "
			// + "Please obtain the latest version of ORIPA",
			// "Failed to load the file", JOptionPane.ERROR_MESSAGE);
			// return null;
		}

		return null;
	}

	// /*
	// * (non Javadoc)
	// *
	// * @see javax.swing.JFileChooser#getFileFilter()
	// */
	// @SuppressWarnings("unchecked")
	// @Override
	// public FileAccessSupportFilter<Data> getFileFilter() {
	// // TODO 自動生成されたメソッド・スタブ
	// return (FileAccessSupportFilter<Data>) super.getFileFilter();
	// }

}
