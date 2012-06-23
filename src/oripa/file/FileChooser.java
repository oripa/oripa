package oripa.file;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import oripa.ORIPA;

public class FileChooser extends JFileChooser {
	
	public FileChooser() {
	
		super();
	}
	
	public FileChooser(String path) {
		super(path);
		
	}

	/**
	 * don't use this!
	 */
	@Deprecated
	public void addChoosableFileFilter(FileFilter filter) {
	
	}
	
	public void addChoosableFileFilter(FileFilterEx filter) {
		// TODO Auto-generated method stub
		super.addChoosableFileFilter(filter);			
	}
	
	
	/**
	 * this method does not change {@code path}.
	 * @param path 
	 * @param ext ex) ".png"
	 * @return path string with new extension
	 */
	public static String correctExtension(String path, String[] extensions){
		
		String path_new = new String(path);

		if(extensions.length == 1){
			String ext = extensions[0];
	
			path_new.replaceAll("\\.\\w+$", "");
			path_new += ext;
		}
		else {
			boolean isCorrect = false;
			for (int i = 0; i < extensions.length; i++) {
				if(path.endsWith(extensions[i])){
					isCorrect = true;
					break;
				}
			}
			
			if(isCorrect == false){
				return null;
			}
		}
		
		
		return path_new;
	}
	

	public String saveFile(Component parent) {

		if (JFileChooser.APPROVE_OPTION != this.showSaveDialog(parent)) {
        	return null;
        }
        
        String filePath = null;
        
		try {

			
			FileFilterEx filter = ((FileFilterEx)this.getFileFilter());
			String[] extensions = filter.getExtensions();

			
			filePath = correctExtension(
					this.getSelectedFile().getPath(), extensions);
			
			if(filePath == null){
				throw new IllegalArgumentException("wrong extension of selected name");
			}

			File file = new File(filePath);
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(
						null, ORIPA.res.getString("Warning_SameNameFileExist"), 
						ORIPA.res.getString("DialogTitle_FileSave"),
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
					return null;
				}
			}
			
			filter.getSavingAction().save(filePath);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					parent, e.toString(), ORIPA.res.getString("Error_FileSaveFailed"),
					JOptionPane.ERROR_MESSAGE);
		}

		return filePath;
	}
	
}
